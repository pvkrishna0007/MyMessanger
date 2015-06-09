package com.example.krishna.mymessanger;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by krishna on 15/5/15.
 */
public class ClientThread extends Thread {
    public static final String MSG = "msg";
    private Socket socket;
    private Handler handler;
    private final String ipAddress;
    private final int portNum;
    private boolean isServer;
    private String clientIpAddr;

    public ClientThread(Socket socket, Handler handler, String ipAddress, int portNum, boolean isServer) {
        this.socket = socket;
        this.handler = handler;
        this.ipAddress = ipAddress;
        this.portNum = portNum;
        this.isServer = isServer;
    }

    @Override
    public void run() {
        super.run();
        try {

            if (!isServer) {
                socket = new Socket(ipAddress, portNum);
                clientIpAddr = socket.getInetAddress().getHostAddress();
                sendMessage("----Connected to server : " + ipAddress + "----");
                Log.d("ClientThread", "run (Line:45) :" + "----Connected to server : " + ipAddress + "----");
            }
            InputStream inputStream = socket.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while (true) {
                String line = bufferedReader.readLine();
                sendMessage(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void sendMessage(String msg) {
        Message message = new Message();
        message.what = 123;
        Bundle bundle = new Bundle();
        bundle.putString(MSG, msg);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public void sendData(String message) {
        try {
            if (socket == null) {
                Log.d("ClientThread", "sendData (Line:73) :" + "Client Socket is null");
            } else {
                Log.d("ClientThread", "sendData (Line:76) :" + "Client Socket is not a null");
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.write(message + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (socket != null
                        )
                    socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientThread that = (ClientThread) o;

        if (clientIpAddr != null ? !clientIpAddr.equals(that.clientIpAddr) : that.clientIpAddr != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return clientIpAddr != null ? clientIpAddr.hashCode() : 0;
    }
}
