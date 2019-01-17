package com.example.quxian.brainwave.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.quxian.brainwave.R;

import java.util.ArrayList;

public class LeDeviceListAdapter extends BaseAdapter {
    private LayoutInflater mInflator;
    private ArrayList<BluetoothDevice> mLeDevices;


    public LeDeviceListAdapter(Context context) {
        super();
        mLeDevices = new ArrayList<>();
        mInflator = LayoutInflater.from(context);
    }


    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = mInflator.inflate(R.layout.item_device, null);
            viewHolder = new ViewHolder();

            viewHolder.deviceName = view.findViewById(R.id.item_device_name);
            viewHolder.connectState =  view.findViewById(R.id.item_connect_state);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getAddress();
        if (deviceName != null && deviceName.length() > 0) {
//            if(deviceName.equals("00:68:A7:03:F9:FE")){
//                viewHolder.deviceName.setText("脑电波柔性传感器");
//            }else
                viewHolder.deviceName.setText(deviceName);
        }
        else
            viewHolder.deviceName.setText(R.string.unknown_device);

        viewHolder.connectState.setText(R.string.not_connected);
        return view;
    }

    class ViewHolder{
        TextView deviceName;
        TextView connectState;
    }
}
