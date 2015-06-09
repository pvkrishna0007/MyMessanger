package com.example.krishna.mymessanger;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.ServerSocket;


public class ServerActivity extends ActionBarActivity implements View.OnClickListener {

    public static final int PORT = 3344;
    private TextView tvCommMsgs;
    private EditText etMessage;
    private ServerSocket serverSocket;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String message = data.getString(ClientThread.MSG);
            tvCommMsgs.append("\n Client: "+message);
        }
    };
    private ServerThread serverThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        initialization();

    }

    private void initialization() {
        etMessage = (EditText) findViewById(R.id.set_message);
        tvCommMsgs = (TextView) findViewById(R.id.stv_comm);
        TextView tvTitle = (TextView) findViewById(R.id.stv_title);

        findViewById(R.id.sbtn_start).setOnClickListener(this);
        findViewById(R.id.sbtn_send).setOnClickListener(this);

        try {
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            tvTitle.setText("Server IpAddress: " + ip);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sbtn_start:
                serverThread = new ServerThread(serverSocket, handler, PORT);
                serverThread.start();
                break;
            case R.id.sbtn_send:

                Log.d("ServerActivity", "onClick (Line:69) :"+"sending data from server");
                String msg = etMessage.getText().toString();
                serverThread.sendData(msg);
                tvCommMsgs.append("\n Server: "+msg);
                etMessage.getText().clear();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
