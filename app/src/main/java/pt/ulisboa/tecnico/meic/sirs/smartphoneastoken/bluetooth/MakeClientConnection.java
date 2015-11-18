package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by André on 16-11-2015.
 */
public class MakeClientConnection implements Runnable{

    protected BluetoothDevice device;

    protected BluetoothSocket socket;

    public MakeClientConnection(BluetoothDevice device,UUID uuid) {
        this.device = device;
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
        manageConnection(socket);
    }

    public void cancel(){
        try{
            socket.close();
        }catch(IOException e){
            e.printStackTrace(); //TODO
        }
    }

    /**
     * Main frame for the connection.
     */
    private void manageConnection(BluetoothSocket socket){
        ManageClientConnection conn = new ManageClientConnection(socket);
        Thread thread = new Thread(conn);
    }
}
