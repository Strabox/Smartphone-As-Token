package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.business.Client;

/**
 * Created by André on 16-11-2015.
 */
public class ManageClientConnection extends Thread{

    private final BluetoothSocket socket;

    private final InputStream in;

    private final OutputStream out;

    private final Client client;

    /**
     * Default constructor
     * @param socket
     * @param client
     */
    public ManageClientConnection(BluetoothSocket socket,Client client){
        this.socket = socket;
        this.client = client;
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
    public void run(){
        // Keep listening to the InputStream until an exception occurs
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        while (true) {
            try {
                String line;
                write(("1||"+ client.bluetooth.getAdapterMacAddress() +"\n").getBytes());

                while ((line = reader.readLine()) != null){
                    System.out.println(line);

                }

            } catch (IOException e) {
                break;
            }catch (Exception e) {
                break;
            }
        }
        cancel();
    }

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
