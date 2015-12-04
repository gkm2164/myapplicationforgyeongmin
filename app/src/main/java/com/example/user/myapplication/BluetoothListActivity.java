package com.example.user.myapplication;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2015-12-04.
 */
public class BluetoothListActivity extends ListActivity {
    private LayoutInflater mLayoutInflater;
    private BluetoothListAdapter bla;
    private final static String TAG = BluetoothListAdapter.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.listactivity_bluetooth);

        this.setListAdapter(bla);

        mLayoutInflater = this.getLayoutInflater();

        bla = new BluetoothListAdapter();

        BluetoothManager bm = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter ba = bm.getAdapter();
        final BluetoothGattCallback bgc = new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                Log.d(TAG, descriptor.getUuid().toString());
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                super.onReliableWriteCompleted(gatt, status);
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);
            }
        };
        final BluetoothAdapter.LeScanCallback mLeScanCallback =
                new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bla.addDevice(device);
                                bla.notifyDataSetChanged();
                                Log.d(TAG, device.getName() + " == " + device.getAddress());
                                String devname = device.getName();
                                if (devname != null && devname.equals("Surge")) {
                                    device.connectGatt(BluetoothListActivity.this, true, bgc);
                                }
                            }
                        });

                    }
                };

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ba.stopLeScan(mLeScanCallback);
            }
        }, 10000);

        ba.startLeScan(mLeScanCallback);
    }

    private class BluetoothListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mBDevices;

        public BluetoothListAdapter () {
            mBDevices = new ArrayList<BluetoothDevice> ();
        }

        @Override
        public int getCount() {
            return mBDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return mBDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.bl_row, null);
            }
            BluetoothDevice bd = (BluetoothDevice) getItem(position);
            if(bd != null) {
                TextView devname = (TextView) convertView.findViewById(R.id.bl_row_devname_tv);
                TextView devaddr = (TextView) convertView.findViewById(R.id.bl_row_devaddr_tv);

                if (devname != null) {
                    devname.setText("!!!");
                }

                if (devaddr != null) {
                    devaddr.setText(bd.getAddress());
                }
            }

            return convertView;
        }

        public void addDevice(BluetoothDevice dev) {
            for (BluetoothDevice device: mBDevices) {
                if (dev.getAddress().equals(device.getAddress())) {
                    Log.d(TAG, "Same device occured");
                    return;
                }
            }
            mBDevices.add(dev);
        }
    };
}
