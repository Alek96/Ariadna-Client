package com.example.ariadna;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ToolsFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, container, false);

        view.findViewById(R.id.ButtonLoad).setOnClickListener(this);
        view.findViewById(R.id.ButtonSave).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.ButtonLoad) {
            Toast.makeText(getActivity(), "Load", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.ButtonSave) {
            Toast.makeText(getActivity(), "Save", Toast.LENGTH_SHORT).show();
        }
    }
}
