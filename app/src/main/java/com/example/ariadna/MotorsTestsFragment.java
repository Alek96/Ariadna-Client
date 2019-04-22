package com.example.ariadna;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MotorsTestsFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_motors_tests, container, false);

        view.findViewById(R.id.ButtonMotorLeftForward).setOnClickListener(this);
        view.findViewById(R.id.ButtonMotorForward).setOnClickListener(this);
        view.findViewById(R.id.ButtonMotorRightForward).setOnClickListener(this);
        view.findViewById(R.id.ButtonMotorLeftBackward).setOnClickListener(this);
        view.findViewById(R.id.ButtonMotorBackward).setOnClickListener(this);
        view.findViewById(R.id.ButtonMotorRightBackward).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.ButtonMotorLeftForward) {
            Toast.makeText(getActivity(), "Left Forward", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.ButtonMotorForward) {
            Toast.makeText(getActivity(), "Forward", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.ButtonMotorRightForward) {
            Toast.makeText(getActivity(), "Right Forward", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.ButtonMotorLeftBackward) {
            Toast.makeText(getActivity(), "Left Backward", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.ButtonMotorBackward) {
            Toast.makeText(getActivity(), "Backward", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.ButtonMotorRightBackward) {
            Toast.makeText(getActivity(), "Right Backward", Toast.LENGTH_SHORT).show();
        }
    }
}
