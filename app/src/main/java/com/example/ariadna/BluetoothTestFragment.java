package com.example.ariadna;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class BluetoothTestFragment extends Fragment {
    private static final String TAG = "BluetoothTestFragment";
    private static final String mConnectedDeviceName = "LineFollower";

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    private BluetoothConnectionService mBluetoothService;
    private ArrayAdapter<String> mConversationArrayAdapter;
    private StringBuffer mOutStringBuffer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mConversationView = (ListView) view.findViewById(R.id.conversation_view);
        mOutEditText = (EditText) view.findViewById(R.id.edit_text_out);
        mSendButton = (Button) view.findViewById(R.id.button_send);
    }

    @Override
    public void onStart() {
        super.onStart();
        setupChat();
        mBluetoothService.setHandler(mHandler);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBluetoothService.setHandler(mHandler);
    }

    private void setupChat() {
        Log.d(TAG, "setupChat: Started");

        mBluetoothService = ((MainActivity) getActivity()).getBluetoothService();

        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);
        mConversationView.setAdapter(mConversationArrayAdapter);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View view = getView();
                if (null != view) {
                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
                    String message = textView.getText().toString();
                    sendMessage(message);
                }
            }
        });

        mOutStringBuffer = new StringBuffer("");
    }

    private void sendMessage(String message) {
        Log.d(TAG, "sendMessage: Started with: " + message);


        if (message.length() > 0) {
            byte[] send = message.getBytes();
            if (mBluetoothService.isConnected()) {
                mBluetoothService.write(send);
            }

            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            Log.d(TAG, "mWriteListener: onEditorAction: Started");
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                Log.d(TAG, "mWriteListener: onEditorAction: sendMessage with: " + message);
                sendMessage(message);
            }
            return true;
        }
    };

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "Handler: Started");
            switch (msg.what) {
                case BluetoothConnectionService.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    Log.d(TAG, "Handler: writeMessage is " + writeMessage);
                    break;
                case BluetoothConnectionService.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    Log.d(TAG, "Handler: readMessage is " + readMessage);
                    break;
            }
            return false;
        }
    });
}
