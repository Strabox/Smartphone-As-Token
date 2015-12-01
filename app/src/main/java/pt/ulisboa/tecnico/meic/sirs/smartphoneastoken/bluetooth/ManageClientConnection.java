package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.business.Client;
import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.security.SecurityUtil;

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
        byte[] sentNonce = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        while (true) {
            try {
                String line, hardcodedKek = "RANDOMkek";
                write(("1||" + client.bluetooth.getAdapterMacAddress() + "\n").getBytes());
                client.setKek(SecurityUtil.read(SecurityUtil.Hash(hardcodedKek)));

                sentNonce = SecurityUtil.generateSecureRandom(1);
                System.out.println("2||" + SecurityUtil.byteToBase64(sentNonce));
                writer.write("2||" + SecurityUtil.byteToBase64(SecurityUtil.encrypt(sentNonce,client.getKek())) + "\n");
                writer.flush();
                System.out.println("Nonce sent");
                while ((line = reader.readLine()) != null){
                    if(sentNonce.equals(SecurityUtil.base64ToByte(line)))
                        System.out.println("FUCK YEA");
                    else
                        System.out.println("Nonce broken");
                }

            } catch (IOException e) {
                System.err.println("Excepção de IO esta treta morreu!!!!!!!!!!!!!!!!!!");
                e.printStackTrace();
                break;
            }catch (Exception e) {
                System.err.println("Outra excepção ainda mais inesperada!!!!!!!!!!!!!!!!!!!!!!!!!!");
                e.printStackTrace();
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
