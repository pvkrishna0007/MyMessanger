package com.example.krishna.mymessanger;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.String;
import java.net.Socket;


public class ClientActivity extends ActionBarActivity implements View.OnClickListener {

    public static final int PORT = 3344;
    private TextView tvCommMsgs;
    private EditText etMessage;
    private EditText etIpAddr;
    private Socket socket;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String message = data.getString(ClientThread.MSG);
            tvCommMsgs.append("\n Server: "+message);
        }
    };
    private ClientThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        initialization();
    }

    private void initialization() {
        etMessage = (EditText) findViewById(R.id.cet_message);
        tvCommMsgs = (TextView) findViewById(R.id.ctv_comm);
        etIpAddr = (EditText) findViewById(R.id.cet_ip_address);

        findViewById(R.id.cbtn_start).setOnClickListener(this);
        findViewById(R.id.cbtn_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cbtn_start:
                String ip = etIpAddr.getText().toString().trim();
                Log.d("ClientActivity", "onClick (Line:55) :"+" "+(socket==null)+" "+(handler==null) +" "+ip+" "+PORT);
                thread = new ClientThread(socket, handler, ip, PORT, false);

                thread.start();
                break;
            case R.id.cbtn_send:
                Log.d("ClientActivity", "onClick (Line:63) :"+"data sending....");
                String msg = etMessage.getText().toString();
                thread.sendData(msg);
                tvCommMsgs.append("\n Client: "+msg);
                etMessage.getText().clear();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
