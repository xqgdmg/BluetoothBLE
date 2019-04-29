package com.example.bluetoothble.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bluetoothble.R;
import com.example.bluetoothble.adapter.DeviceAdapter.DeviceViewHolder;
import com.example.bluetoothble.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描出来的蓝牙设备列表
 *
 * @author cWX708605
 * @version [V6.0.0.1, 2019/4/29]
 * @since V6.0.0.1
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder> {

    private Context mContext;
    private List<DeviceBean> mData = new ArrayList<DeviceBean>();

    public DeviceAdapter(Context context, List<DeviceBean> data) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View itemView = View.inflate(mContext, R.layout.item_device,null);
        DeviceViewHolder deviceViewHolder = new DeviceViewHolder(itemView);
        return deviceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder viewHolder, int position) {
        DeviceBean deviceBean = mData.get(position);
        viewHolder.textView.setText("设备名称："+deviceBean.getName()+",设备Adress："+deviceBean.getAdreess());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}
