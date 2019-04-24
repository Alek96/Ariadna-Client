package com.example.ariadna;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private static final String START_MSG = "start";
    private static final String STOP_MSG = "stop";

    private Button mRunButton;
    private Button mStopButton;

    private BluetoothConnectionService mBluetoothService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRunButton = view.findViewById(R.id.ButtonRun);
        mStopButton = view.findViewById(R.id.ButtonStop);
    }

    @Override
    public void onStart() {
        super.onStart();
        setUp();
        mBluetoothService.setHandler(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBluetoothService.setHandler(null);
    }

    private void setUp() {
        Log.d(TAG, "setUp: Started");

        mBluetoothService = ((MainActivity) getActivity()).getBluetoothService();

        mRunButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Run", Toast.LENGTH_SHORT).show();
                if (mBluetoothService.isConnected()) {
                    mBluetoothService.write(START_MSG.getBytes());
                }
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Stop", Toast.LENGTH_SHORT).show();
                if (mBluetoothService.isConnected()) {
                    mBluetoothService.write(STOP_MSG.getBytes());
                }
            }
        });
    }
}
