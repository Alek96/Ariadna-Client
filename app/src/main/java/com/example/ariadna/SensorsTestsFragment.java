package com.example.ariadna;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SensorsTestsFragment extends Fragment {
    private static final String TAG = "SensorsTestsFragment";

    // Layout Views
    private ListView mSensorsValueView;
    private Button mRefreshButton;

    private BluetoothConnectionService mBluetoothService;
    private ArrayAdapter<String> mSensorsValueViewAdepter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensors_test_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSensorsValueView = (ListView) view.findViewById(R.id.sensors_value_view);
        mRefreshButton = (Button) view.findViewById(R.id.button_refresh);
    }

    @Override
    public void onStart() {
        super.onStart();
        setUp();
        mBluetoothService.setHandler(mHandler);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBluetoothService.setHandler(mHandler);
    }

    private void setUp() {
        Log.d(TAG, "setUp: Started");

        mBluetoothService = ((MainActivity) getActivity()).getBluetoothService();

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "mRefreshButton: onClick: Started");
                if (mBluetoothService.isConnected()) {
                    Log.d(TAG, "mRefreshButton: onClick: Sending throw bluetooth msg: " + Constants.GET_SENSORS_VALUE_MSG);
                    mBluetoothService.write(Constants.GET_SENSORS_VALUE_MSG.getBytes());
                }
            }
        });

        mSensorsValueViewAdepter = new ArrayAdapter<String>(getActivity(), R.layout.message);
        mSensorsValueView.setAdapter(mSensorsValueViewAdepter);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "Handler: Started");
            if (msg.what == BluetoothConnectionService.MESSAGE_READ) {
                byte[] readBuf = (byte[]) msg.obj;
                String readMessage = new String(readBuf, 0, msg.arg1);

                mSensorsValueViewAdepter.add("Sensors value:  " + readMessage);
                Log.d(TAG, "Handler: readMessage is " + readMessage);
            }
            return false;
        }
    });
}
