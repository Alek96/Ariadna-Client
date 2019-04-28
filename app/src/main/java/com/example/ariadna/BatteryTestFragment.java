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

public class BatteryTestFragment extends Fragment {
    private static final String TAG = "BatteryTestFragment";


    // Layout Views
    private ListView mBatteryLevelView;
    private Button mRefreshButton;

    private BluetoothConnectionService mBluetoothService;
    private ArrayAdapter<String> mBatteryLevelViewAdepter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_battery_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mBatteryLevelView = (ListView) view.findViewById(R.id.battery_level_view);
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
                    Log.d(TAG, "mRefreshButton: onClick: Sending throw bluetooth msg: " + Constants.GET_BATTERY_LEVEL_MSG);
                    mBluetoothService.write(Constants.GET_BATTERY_LEVEL_MSG.getBytes());
                }
            }
        });

        mBatteryLevelViewAdepter = new ArrayAdapter<String>(getActivity(), R.layout.message);
        mBatteryLevelView.setAdapter(mBatteryLevelViewAdepter);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "Handler: Started");
            if (msg.what == BluetoothConnectionService.MESSAGE_READ) {
                byte[] readBuf = (byte[]) msg.obj;
                String readMessage = new String(readBuf, 0, msg.arg1);

                mBatteryLevelViewAdepter.add("Battery level:  " + readMessage);
                Log.d(TAG, "Handler: readMessage is " + readMessage);
            }
            return false;
        }
    });
}
