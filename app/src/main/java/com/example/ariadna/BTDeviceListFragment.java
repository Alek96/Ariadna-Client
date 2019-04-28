package com.example.ariadna;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class BTDeviceListFragment extends Fragment {
    private static final String TAG = "BTDeviceListFragment";
    public static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<BluetoothDevice> mPairedDeviceListAdepter;

    private ListView mPairedDeviceListView;
    private Button mRefreshPairedDevicesButton;

    public BTDeviceListFragment() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mPairedDevices = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_device_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: Started");
        mRefreshPairedDevicesButton = view.findViewById(R.id.button_refresh_paired_devices);
        mPairedDeviceListView = view.findViewById(R.id.paired_device_view);

        if (mBluetoothAdapter == null) {
            Log.d(TAG, "checkBT: Bluetooth is not available");
            Toast.makeText(getContext(), "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            Log.d(TAG, "checkBT: Bluetooth is available");
        }
    }

    private void checkBT() {
        Log.d(TAG, "checkBT: Started");

        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "checkBT: Bluetooth is not enable");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            Log.d(TAG, "checkBT: Bluetooth is enable");
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: Started");
        super.onStart();
        checkBT();
        setUpPairedDeviceList();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: Started");
        super.onResume();
    }

    private void setUpPairedDeviceList() {
        Log.d(TAG, "setUpPairedDeviceList: Started");
        mRefreshPairedDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshPairedDevicesList();
            }
        });

        mPairedDeviceListAdepter = new ArrayAdapter<BluetoothDevice>(getActivity(), 0, mPairedDevices) {
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                BluetoothDevice device = mPairedDevices.get(position);
                if (view == null)
                    view = getActivity().getLayoutInflater().inflate(R.layout.device_list_item, parent, false);
                TextView device_name = view.findViewById(R.id.device_name);
                TextView device_address = view.findViewById(R.id.device_address);
                device_name.setText(device.getName());
                device_address.setText(device.getAddress());
                return view;
            }
        };

        mPairedDeviceListView.setAdapter(mPairedDeviceListAdepter);
        mPairedDeviceListView.setOnItemClickListener(mPairedDeviceListener);
    }

    private void refreshPairedDevicesList() {
        Log.d(TAG, "refreshPairedDevicesList: Started");
        mPairedDevices.clear();
        for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
            if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE)
                mPairedDevices.add(device);
        }
        Collections.sort(mPairedDevices, BTDeviceListFragment::compareTo);
        mPairedDeviceListAdepter.notifyDataSetChanged();
    }

    private AdapterView.OnItemClickListener mPairedDeviceListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "mPairedDeviceListener: onItemClick: Started");
            BluetoothDevice device = mPairedDevices.get(position);

            ((MainActivity) getActivity()).createBluetoothService(device.getAddress());
            ((MainActivity) getActivity()).getBluetoothService().setHandler(mHandler);
        }
    };

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "Handler: Started");
            switch (msg.what) {
                case BluetoothConnectionService.STATE_NONE:
                    Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothConnectionService.STATE_CONNECTED:
                    Log.d(TAG, "mHandler: STATE_CONNECTED");
                    Toast.makeText(getActivity(), R.string.connected, Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "mHandler: Change fragment to HomeFragment");
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeFragment()).commit();

                    ((MainActivity) getActivity()).activeDrawer();
                    break;
            }
            return false;
        }
    });

    /**
     * sort by name, then address. sort named devices first
     */
    static int compareTo(BluetoothDevice a, BluetoothDevice b) {
        boolean aValid = a.getName() != null && !a.getName().isEmpty();
        boolean bValid = b.getName() != null && !b.getName().isEmpty();
        if (aValid && bValid) {
            int ret = a.getName().compareTo(b.getName());
            if (ret != 0) return ret;
            return a.getAddress().compareTo(b.getAddress());
        }
        if (aValid) return -1;
        if (bValid) return +1;
        return a.getAddress().compareTo(b.getAddress());
    }
}
