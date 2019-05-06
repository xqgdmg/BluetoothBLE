package com.example.bluetoothble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bluetoothble.adapter.DeviceAdapter;
import com.example.bluetoothble.adapter.DeviceAdapter.OnItemClickListener;
import com.example.bluetoothble.bean.BleAdvertisedData;
import com.example.bluetoothble.bean.DeviceBean;
import com.example.bluetoothble.utils.BleUtil;

import java.util.ArrayList;
import java.util.UUID;

/*
 * 未完待续...
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH_ENABLE = 2;
    private static final long SCAN_PERIOD = 60000;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;// 是否正在扫描

    Handler scanHandler = new Handler();

    // 蓝牙 Gatt
    private BluetoothGatt mBluetoothGatt;

    // 蓝牙地址
    private String mBluetoothDeviceAddress;

    // 蓝牙连接状态
    private boolean mConnectionState;
    private RecyclerView mRecyclerView;
    private DeviceAdapter mDeviceAdapter;

    // 搜索到的蓝牙设备列表
    ArrayList<DeviceBean> mDeviceList = new ArrayList<DeviceBean>();
    ArrayList<DeviceBean> mDeviceListFinal = new ArrayList<DeviceBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        //1. 获取 BluetoothAdapter
        getBluetoothAdapter();

        //2. 判断当前蓝牙状态
        checkBluetoothStatus();


    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mDeviceAdapter = new DeviceAdapter(MainActivity.this, mDeviceListFinal);
        mRecyclerView.setAdapter(mDeviceAdapter);
        mRecyclerView.setHasFixedSize(true);

        mDeviceAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DeviceBean deviceBean = mDeviceListFinal.get(position);
                boolean connect = connect(deviceBean.getAdreess());
                Log.e("chris","connect=="+connect);
            }
        });
    }

    /*
     * 4. 连接蓝牙设备
     */
    private boolean connect(String address) {
        if (mBluetoothAdapter == null || address == null) {
            Toast.makeText(MainActivity.this,
                    "BluetoothAdapter未初始化或者连接的Adress不存在", Toast.LENGTH_SHORT).show();
            return false;
        }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Toast.makeText(MainActivity.this, "蓝牙设备未找到，无法建立连接", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 关闭之前的 Gatt，建立新的 Gatt
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        mBluetoothGatt = device.connectGatt(getApplication(), false, mGattCallback); //该函数才是真正的去进行连接
//        mBluetoothGatt.connect();
//        mlog.e("Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = true;
        Toast.makeText(MainActivity.this, device.getName()+"", Toast.LENGTH_SHORT).show();
        return true;
    }

    /*
     * 扫描之后的回调，这里的扫描有很多重复的
     */
    //  mBluetoothAdapter.startLeScan(mLeScanCallback); //开始搜索
    // mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索
    BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            // 处理蓝牙名称，并没有用
            final BleAdvertisedData badata = BleUtil.parseAdertisedData(scanRecord);
            String deviceName = device.getName();
            if (deviceName == null) {
                deviceName = badata.getName();
            }

            //扫描结果回调  不要在这里处理耗时动作
            Log.e("chris", "发现设备：name=" + deviceName + "address=" + device.getAddress());
//            Log.e("chris", "rssi=" + rssi);
//            for (byte stringRecord : scanRecord) {
//                Log.e("chris", "scanRecord=" + stringRecord);
//            }

            // 填充设备列表数据
            DeviceBean deviceBean = new DeviceBean(deviceName, device.getAddress());
            mDeviceList.add(deviceBean);


        }
    };

    /*
     * 3. 开始扫描和停止扫描
     * 扫描的方法有三种（现在基本使用第二种方法）
     * 1、是通过监听广播 mBluetoothAdapter.startDiscovery()
     * 2、直接调用.startLeScan(BluetoothAdapter.LeScanCallbackcallback)即可扫描出BLE设备，在callback中会回调。
     * 3、通过startScan(ScanCallbackcallback)
     *
     * enable=true开始扫描；enable=true停止扫描
     */
    private void scanLeDevice(boolean enable) {
        if (enable) {
            //10秒后停止扫描 不能长时间不间断的扫描 很耗费资源的
            scanHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 超出扫描时间后停止搜索
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                    // 对扫描出来的数据去重并且防止每扫描到一条就刷新列表 mDeviceListFinal
                    for (int i = 0; i < mDeviceList.size(); i++) {
                        DeviceBean deviceBean = mDeviceList.get(i);
                        if (!mDeviceListFinal.contains(deviceBean)) {
                            mDeviceListFinal.add(deviceBean);
                        }
                    }

                    Log.e("chris", "mDeviceList.size()==" + mDeviceList.size());
                    Log.e("chris", "mDeviceListFinal.size()==" + mDeviceListFinal.size());
                    // 展示发现的设备列表
                    Toast.makeText(MainActivity.this, "已发现蓝牙" + mDeviceListFinal.size() + "个", Toast.LENGTH_SHORT).show();
                    mDeviceAdapter.notifyDataSetChanged();
                }
            }, SCAN_PERIOD);

            // 开始搜索
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            Toast.makeText(MainActivity.this, "开始搜索，请等待" + SCAN_PERIOD + "毫秒", Toast.LENGTH_SHORT).show();
        } else {
            // 停止搜索
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            Toast.makeText(MainActivity.this, "停止搜索", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * 2. 判断当前蓝牙状态
     */
    private void checkBluetoothStatus() {
        if (mBluetoothAdapter.isEnabled()) {//判断是否打开蓝牙
            //mBluetoothAdapter.enable();//无交互打开蓝牙

            // 3. 开始扫描 或者 停止扫描
            scanLeDevice(true);

        } else {
            //系统提示用户选择是否打开蓝牙
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BLUETOOTH_ENABLE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //代开蓝牙回调
        if (requestCode == REQUEST_BLUETOOTH_ENABLE) {
            if (resultCode == RESULT_OK) {
                //打开成功
                Toast.makeText(MainActivity.this, "打开蓝牙成功", Toast.LENGTH_SHORT).show();

                // 3. 开始扫描 或者 停止扫描
                scanLeDevice(true);
            } else {
                Toast.makeText(MainActivity.this, "打开蓝牙失败，无法使用蓝牙功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * 1. 获取 BluetoothAdapter
     */
    private void getBluetoothAdapter() {
        // 这种获取方式最小api要求18
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "蓝牙不支持", Toast.LENGTH_SHORT).show();
        }

        //还有一种兼容的获取方法
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /*
     * 5. 连接的回调
     */
    private static UUID WRITESERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static UUID READERID = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    private static UUID WRITEID = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb");

    BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.e("chris", "蓝牙已连接");
                    mConnectionState = true;
                    mBluetoothGatt.discoverServices();//连上蓝牙就可以对服务进行查找发现
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("chris", "蓝牙未连接");
                    mConnectionState = false;
                    mBluetoothGatt.close();//关键 如果断开蓝牙时没有释放资源会导致 133 错误
                    break;
            }

            if (status == BluetoothGatt.GATT_FAILURE) {
                Toast.makeText(getBaseContext(), "onConnectionStateChange status=" + status, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Toast.makeText(MainActivity.this, "发现服务", Toast.LENGTH_SHORT).show();

            //打开读写服务
            BluetoothGattService readerService = mBluetoothGatt.getService(WRITESERVICE);
            //读的特征值
            BluetoothGattCharacteristic reader = readerService.getCharacteristic(READERID);
            //写的特征值
            BluetoothGattCharacteristic writer = readerService.getCharacteristic(WRITEID);

            // 遍历 descriptor（描述符）
            for (BluetoothGattDescriptor descriptor : reader.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
            //打开通知 才能接收到数据回调
            mBluetoothGatt.setCharacteristicNotification(reader, true);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Toast.makeText(MainActivity.this,
                    "onCharacteristicRead characteristic=" + characteristic, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            //写入会有回调
            //数据写入结果回调   characteristic为你写入的指令 这可以判断数据是否完整写入

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            //这里接收到的就是蓝牙设备反馈回来的数据
            for (int i = 0; i < characteristic.getValue().length; i++) {
                Log.e("TAG", "------------获取数据  value[" + i + "]: " + characteristic.getValue()[i]);
            }

        }
        //还有一些其他的回调基本用不上了
    };

//    /*
//     * 6. 接下来就是数据的读写了
//     * 读写数据主要还是要依据 蓝牙通讯协议 （这个硬件是会提供的）
//     * 这里提供一个简单的示例：下图是蓝牙电子秤的置零指令
//     */
//    //置零
//    private byte[] buf_reset;
//
//    public void zeroOdert() {
//        //这里的 VERIFY是协议定义的指令校验 是把前面的数进行异或运算得到的值（具体要看协议怎么定义）
//        byte VERIFY = 0;
//        buf_reset = new byte[]{0x4C, 0x77, 0x06, 0x01, 0x72, VERIFY, 0x04, 0x1C};
//
//        for (int i = 0; i < 5; i++) {
//            VERIFY ^= buf_paramOder[i];
//        }
//
//        writer.setValue( buf_reset );
//        mBluetoothGatt.writeCharacteristic( writer );
//    }
//
//
//    //对应的反馈和读操作  以下是BluetoothGattCallback回调里的操作
//    @Override
//    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//        super.onCharacteristicChanged( gatt, characteristic );
//
//        int reres = characteristic.getValue()[5];
//        if (reres == 0) {
//            //置零成功
//
//        } else if (reres == 1) {
//            //置零失败
//
//        }
//    }


}
