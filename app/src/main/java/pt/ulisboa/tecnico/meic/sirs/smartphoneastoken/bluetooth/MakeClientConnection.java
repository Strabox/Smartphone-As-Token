package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.business.Client;

/**
 * Created by André on 16-11-2015.
 */
public class MakeClientConnection extends Thread{

    private BluetoothDevice device;

    private BluetoothSocket socket;

    private Client client;

    public MakeClientConnection(BluetoothDevice device,UUID uuid,Client client) {
        this.device = device;
        this.client = client;
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
        ManageClientConnection conn = new ManageClientConnection(socket,client);
        conn.start();
        System.out.println("Connection Established");
    }

}
