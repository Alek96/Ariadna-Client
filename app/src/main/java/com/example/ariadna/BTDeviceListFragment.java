package com.example.ariadna;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.Set;

public class BTDeviceListFragment extends Fragment {
    private static final String TAG = "BTDeviceListFragment";
    public static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mPairedDevices;

    private Button mRefreshPairedDevicesButton;
    private ListView mPairedDeviceListView;
    private ArrayAdapter<String> mPairedDeviceListAdepter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_device_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRefreshPairedDevicesButton = view.findViewById(R.id.button_refresh_paired_devices);
        mPairedDeviceListView = view.findViewById(R.id.paired_device_view);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Log.d(TAG, "checkBT: Bluetooth is not available");
            Toast.makeText(getContext(), "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            Log.d(TAG, "checkBT: Bluetooth is available");
        }
    }

    private void checkBT() {
        Log.d(TAG, "checkBT: Starting.");

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
                fillPairedDevicesList();
            }
        });

        mPairedDeviceListAdepter = new ArrayAdapter<String>(getActivity(), R.layout.message);
        mPairedDeviceListView.setAdapter(mPairedDeviceListAdepter);
        mPairedDeviceListView.setOnItemClickListener(mPairedDeviceListener);
    }

    private void fillPairedDevicesList() {
        Log.d(TAG, "fillPairedDevicesList: Started");
        mPairedDevices = mBluetoothAdapter.getBondedDevices();

        if (mPairedDevices.size() > 0) {
            Log.d(TAG, "fillPairedDevicesList: Paired Bluetooth Devices Found.");
            for (BluetoothDevice bt : mPairedDevices) {
                mPairedDeviceListAdepter.add(bt.getName() + "\n" + bt.getAddress());
            }
        } else {
            Log.d(TAG, "fillPairedDevicesList: No Paired Bluetooth Devices Found.");
            Toast.makeText(getContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
    }

    private AdapterView.OnItemClickListener mPairedDeviceListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            Log.d(TAG, "mPairedDeviceListener: onItemClick: Started");

            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            ((MainActivity) getActivity()).createBluetoothService(address);
            ((MainActivity) getActivity()).active();

            Log.d(TAG, "mPairedDeviceListener: onItemClick: Change fragment to HomeFragment");
            getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();


        }
    };
}
