package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.MainActivity;
import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.business.Client;

/**
 * Created by André on 16-11-2015.
 */
public abstract class ManageClientConnection extends Thread{

    protected final BluetoothSocket socket;

    protected MainActivity currentActivity;

    protected final InputStream in;

    protected final OutputStream out;

    protected final Client client;

    protected BluetoothDevice device;

    protected String hardcodedKek = "";


    /**
     * Default constructor
     * @param socket
     * @param client
     */
    public ManageClientConnection(MainActivity currentActivity, BluetoothSocket socket,Client client, BluetoothDevice device){
        this.currentActivity = currentActivity;
        this.socket = socket;
        this.client = client;
        this.device = device;
        InputStream inTemp = null;
        OutputStream outTemp = null;
        try{
            inTemp = socket.getInputStream();
            outTemp = socket.getOutputStream();
        }catch(IOException e){
            e.printStackTrace(); //TODO
        }
        this.in = inTemp;
        this.out = outTemp;
    }

    /**
     * Default run
     */
    @Override
    public abstract void run();

    /**
     *
     * @param bytes
     */
    public void write(byte[] bytes) {
        try {
            out.write(bytes);
            out.flush();
        } catch (IOException e) { }
    }

    /**
     * Cancels
     */
    public void cancel() {
        try {
            socket.close();
            System.out.println("Connection Closed..");
        } catch (IOException e) { }
    }

}
