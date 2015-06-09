package com.example.krishna.mymessanger;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by krishna on 15/5/15.
 */
public class ServerThread extends Thread {
    private ServerSocket serverSocket;
    private Handler handler;
    private final int portNum;

    private Map<String, ClientThread> mapClients = new HashMap<>();
    //private Set<ClientThread> listClients = new HashSet<>();

    public ServerThread(ServerSocket serverSocket, Handler handler, int portNum) {
        this.serverSocket = serverSocket;
        this.handler = handler;
        this.portNum = portNum;
    }

    @Override
    public void run() {
        super.run();
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(portNum));
            while (true) {
                Socket socket = serverSocket.accept();

                sendMessage("----Received request from client----");
                Log.d("ServerThread", "run (Line:35) :" + "Sended Request");
                ClientThread clientThread = new ClientThread(socket, handler, null, portNum, true);
                clientThread.start();
                String add = socket.getInetAddress().getHostAddress();
                System.out.println("ADDR:" + add);
                //listClients.add(clientThread);
                mapClients.put(add, clientThread);

            }
        } catch (IOException e) {
            e.printStackTrace();
            try {

                closeSockets();
                if (serverSocket != null) {
                    serverSocket.close();
                    Log.d("ServerThread", "run (Line:58) :" + "ServerScoket is closed");
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void sendData(String message) {
        Iterator it = mapClients.entrySet().iterator();

        Log.d("ServerThread", "sendData (Line:71) :"+" No. of clients: "+mapClients.size());
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            ClientThread thread = (ClientThread) pair.getValue();
            thread.sendData(message);
        }
    }

    public void closeSockets() {
        Iterator it = mapClients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println("Pair : "+pair.getKey() + " = " + pair.getValue());
            ClientThread thread = (ClientThread) pair.getValue();
            Socket socket = thread.getSocket();
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                it.remove();
            }
        }
    }

    public void removeSocket(String ipAddress) {
        ClientThread thread = mapClients.get(ipAddress);
        Socket socket = thread.getSocket();
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mapClients.remove(ipAddress);
        }
    }

    private void sendMessage(String msg) {
        Message message = new Message();
        message.what = 123;
        Bundle bundle = new Bundle();
        bundle.putString(ClientThread.MSG, msg);
        message.setData(bundle);
        handler.sendMessage(message);
    }

}
