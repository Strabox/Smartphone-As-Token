package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by André on 16-11-2015.
 */
public class ManageClientConnection implements  Runnable{

    private final BluetoothSocket socket;

    private final InputStream in;

    private final OutputStream out;

    public ManageClientConnection(BluetoothSocket socket){
        this.socket = socket;
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

    @Override
    public void run(){
        byte[] buffer = new byte[1024];
        int bytes;

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = in.read(buffer);
                // Send the obtained bytes to the UI activity
                //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                //        .sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            out.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) { }
    }

}
