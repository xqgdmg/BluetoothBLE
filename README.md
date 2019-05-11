# BluetoothBLE
原文：https://blog.csdn.net/Fight_0513/article/details/79855749 <br/><br/>
4.x开始的蓝牙我们称之为低功耗蓝牙也就是蓝牙ble，当然4.x版本的蓝牙也是向下兼容的。android手机必须系统版本4.3及以上才支持BLE API。低功耗蓝牙较传统蓝牙，传输速度更快，覆盖范围更广，安全性更高，延迟更短，耗电极低等等优点。（现在的穿戴设备都是使用BLE蓝牙技术的）<br/><br/>
BLE的三部分：Service，Characteristic，Descriptor 这三部分都用UUID作为唯一标识符。<br/>
UUID为这种格式：0000ffe1-0000-1000-8000-00805f9b34fb。比如有3个Service，那么就有三个不同的UUID与Service对应。<br/>
这些UUID都写在硬件里，我们通过BLE提供的API可以读取到。 <br/>
一个BLE终端可以包含多个Service， 一个Service可以包含多个Characteristic，一个Characteristic包含一个value和多个Descriptor，一个Descriptor包含一个Value。Characteristic是比较重要的，是手机与BLE终端交换数据的关键，读取设置数据等操作都是操作Characteristic的相关属性。<br/><br/><br/>

Android BLE API 简介<br/>
BluetoothAdapter <br/>
BluetoothAdapter 拥有基本的蓝牙操作，例如蓝牙扫描的开启和停止，使用已知的 MAC 地址 <br/>
（BluetoothAdapter . getRemoteDevice）实例化一个 BluetoothDevice 用于连接蓝牙设备的操作等等。<br/><br/>

BluetoothDevice <br/>
代表一个远程蓝牙设备。这个类可以让你连接所代表的蓝牙设备或者获取一些有关它的信息，例如它的名字，地址和绑定状态等等。<br/><br/>

BluetoothGatt <br/>
这个类提供了 Bluetooth GATT 的基本功能。例如重新连接蓝牙设备，发现蓝牙设备的 Service 等等。<br/><br/>

BluetoothGattService <br/>
这一个类通过 BluetoothGatt#getService 获得，如果当前服务不可见那么将返回一个 null。这一个类对应上面说过的 Service。我们可以通过这个类的 getCharacteristic(UUID uuid) 进一步获取 Characteristic 实现蓝牙数据的双向传输。<br/><br/>

BluetoothGattCharacteristic <br/>
这个类对应上面提到的 Characteristic。通过这个类定义需要往外围设备写入的数据和读取外围设备发送过来的数据。<br/><br/>

...

#总结：
* 1.device.getName()总是返回 null，手机蓝牙有问题。。。
