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
import android.widget.ImageButton;
import android.widget.Toast;

public class MotorsTestsFragment extends Fragment {
    private static final String TAG = "MotorsTestsFragment";

    private static final String MOTOR_LEFT_FORWARD_MSG = "motor_left_forward";
    private static final String MOTORS_FORWARD_MSG = "motors_forward";
    private static final String MOTOR_RIGHT_FORWARD_MSG = "motor_right_forward";
    private static final String MOTOR_LEFT_BACKWARD_MSG = "motor_left_backward";
    private static final String MOTORS_BACKWARD_MSG = "motors_backward";
    private static final String MOTOR_RIGHT_BACKWARD_MSG = "motor_right_backward";

    private ImageButton mMotorLeftForwardButton;
    private ImageButton mMotorForwardButton;
    private ImageButton mMotorRightForwardButton;
    private ImageButton MotorLeftBackward;
    private ImageButton mMotorBackwardButton;
    private ImageButton mMotorRightBackwardButton;

    private BluetoothConnectionService mBluetoothService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_motors_tests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mMotorLeftForwardButton = view.findViewById(R.id.ButtonMotorLeftForward);
        mMotorForwardButton = view.findViewById(R.id.ButtonMotorForward);
        mMotorRightForwardButton = view.findViewById(R.id.ButtonMotorRightForward);
        MotorLeftBackward = view.findViewById(R.id.ButtonMotorLeftBackward);
        mMotorBackwardButton = view.findViewById(R.id.ButtonMotorBackward);
        mMotorRightBackwardButton = view.findViewById(R.id.ButtonMotorRightBackward);
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

        mMotorLeftForwardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "mMotorLeftForwardButton: onClick: Started");
                Toast.makeText(getActivity(), "Left Forward", Toast.LENGTH_SHORT).show();
                if (mBluetoothService.isConnected()) {
                    Log.d(TAG, "mMotorLeftForwardButton: onClick: Sending throw bluetooth");
                    mBluetoothService.write(MOTOR_LEFT_FORWARD_MSG.getBytes());
                }
            }
        });

        mMotorForwardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "mMotorForwardButton: onClick: Started");
                Toast.makeText(getActivity(), "Forward", Toast.LENGTH_SHORT).show();
                if (mBluetoothService.isConnected()) {
                    Log.d(TAG, "mMotorForwardButton: onClick: Sending throw bluetooth");
                    mBluetoothService.write(MOTORS_FORWARD_MSG.getBytes());
                }
            }
        });

        mMotorRightForwardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "mMotorRightForwardButton: onClick: Started");
                Toast.makeText(getActivity(), "Right Forward", Toast.LENGTH_SHORT).show();
                if (mBluetoothService.isConnected()) {
                    Log.d(TAG, "mMotorRightForwardButton: onClick: Sending throw bluetooth");
                    mBluetoothService.write(MOTOR_RIGHT_FORWARD_MSG.getBytes());
                }
            }
        });

        MotorLeftBackward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "MotorLeftBackward: onClick: Started");
                Toast.makeText(getActivity(), "Left Backward", Toast.LENGTH_SHORT).show();
                if (mBluetoothService.isConnected()) {
                    Log.d(TAG, "MotorLeftBackward: onClick: Sending throw bluetooth");
                    mBluetoothService.write(MOTOR_LEFT_BACKWARD_MSG.getBytes());
                }
            }
        });

        mMotorBackwardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "mMotorBackwardButton: onClick: Started");
                Toast.makeText(getActivity(), "Backward", Toast.LENGTH_SHORT).show();
                if (mBluetoothService.isConnected()) {
                    Log.d(TAG, "mMotorBackwardButton: onClick: Sending throw bluetooth");
                    mBluetoothService.write(MOTORS_BACKWARD_MSG.getBytes());
                }
            }
        });

        mMotorRightBackwardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "mMotorRightBackwardButton: onClick: Started");
                Toast.makeText(getActivity(), "Right Backward", Toast.LENGTH_SHORT).show();
                if (mBluetoothService.isConnected()) {
                    Log.d(TAG, "mMotorRightBackwardButton: onClick: Sending throw bluetooth");
                    mBluetoothService.write(MOTOR_RIGHT_BACKWARD_MSG.getBytes());
                }
            }
        });
    }
}
