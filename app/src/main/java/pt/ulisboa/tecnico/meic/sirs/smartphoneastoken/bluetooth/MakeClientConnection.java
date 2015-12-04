package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.MainActivity;
import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.business.Client;


public class MakeClientConnection extends Thread{

    private BluetoothDevice device;

    private BluetoothSocket socket;

    private Client client;

    private MainActivity currentActivity;

    private String targetClass = "";



    public MakeClientConnection(MainActivity activity, BluetoothDevice device,UUID uuid,Client client, String targetClassName) {
        this.currentActivity = activity;
        this.device = device;
        this.client = client;
        this.targetClass = targetClassName;
        BluetoothSocket tempSocket = null;
        try {
            tempSocket = device.createRfcommSocketToServiceRecord(uuid);
        } catch (Exception e) {
            e.printStackTrace(); //TODO
        }
        socket = tempSocket;
    }

    @Override
    public void run(){
        try{
            socket.connect();
        }catch(IOException e){
            e.printStackTrace();
            try{
                socket.close();
            }catch(IOException e2){
                //TODO
            }
            return;
        }
        ManageClientConnection conn;
        if (targetClass.equals(MessageConnection.class.getName())) {
            conn = new MessageConnection(this.currentActivity, socket, client);
        } else {
            conn = new RegisterConnection(this.currentActivity, socket, client, device);
        }
        conn.start();
        System.out.println("Connection Established");
    }

}
