package com.example.bluetoothble.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
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
 * RecyclerView子View宽度不能全屏的问题，在Adapter的onCreateViewHolder创建子view的时候要把parent传进去; 
 *
 * 正确写法
 *
 * LayoutInflater.from(context).inflate(R.layout.item_view,parent,false);
 *
 * 错误写法
 *
 * LayoutInflater.from(context).inflate(R.layout.item_view,null);
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
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_device,viewGroup,false);
        return new DeviceViewHolder(itemView);
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
