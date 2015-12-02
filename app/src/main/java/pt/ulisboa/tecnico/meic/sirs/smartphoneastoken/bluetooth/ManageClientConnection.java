package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.MainActivity;
import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.business.Client;
import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.security.SecurityUtil;

/**
 * Created by André on 16-11-2015.
 */
public class ManageClientConnection extends Thread{

    private final BluetoothSocket socket;

    private MainActivity currentActivity;

    private final InputStream in;

    private final OutputStream out;

    private final Client client;

    private String hardcodedKek = "";

    /**
     * Default constructor
     * @param socket
     * @param client
     */
    public ManageClientConnection(MainActivity currentActivity, BluetoothSocket socket,Client client){
        this.currentActivity = currentActivity;
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
        byte[] sentNonce = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        while (true) {
            try {
                String line;
                hardcodedKek = SecurityUtil.byteToBase64(SecurityUtil.generateSecureRandom(8));
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        AlertDialog alertDialog = new AlertDialog.Builder(currentActivity).create();
                        alertDialog.setTitle("Insert kek in target Desktop");
                        alertDialog.setMessage(hardcodedKek);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "DONE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                });
                writer.write("1||" + client.bluetooth.getAdapterMacAddress() + "\n");
                writer.flush();
                client.setKek(SecurityUtil.getAesKeyFromBytes(SecurityUtil.Hash(hardcodedKek)));

                sentNonce = SecurityUtil.generateSecureRandom(SecurityUtil.NONCE_BYTES_SIZE);
                System.out.println("2||" + SecurityUtil.byteToBase64(sentNonce));
                writer.write("2||" + SecurityUtil.byteToBase64(SecurityUtil.encrypt(sentNonce, client.getKek())) + "\n");
                writer.flush();

                System.out.println("Challenge sent to laptop, waiting response....");
                while ((line = reader.readLine()) != null){
                    String[] lines = line.split("\\|\\|");
                    byte[] response = SecurityUtil.base64ToByte(lines[0]);
                    byte[] challenge = SecurityUtil.base64ToByte(lines[1]);

                    if(Arrays.equals(SecurityUtil.nonceTransformation(sentNonce),SecurityUtil.decrypt(response,client.getKek()))){
                        System.out.println("Challenge response match sending my response");
                        byte[] challengePlain = SecurityUtil.decrypt(challenge,client.getKek());
                        byte[] challengeTransform = SecurityUtil.nonceTransformation(challengePlain);
                        byte[] challengeEncrypted = SecurityUtil.encrypt(challengeTransform,client.getKek());
                        writer.write("3||" + SecurityUtil.byteToBase64(challengeEncrypted) + "\n");
                        writer.flush();
                    }
                    else{
                        System.out.println("Wrong response Registration Failed");
                        client.setKek(null);
                        cancel();
                        return;
                    }
                }

            } catch (IOException e) {
                System.err.println("Excepção de IO esta treta morreu!!!");
                e.printStackTrace();
                break;
            }catch (Exception e) {
                System.err.println("Outra excepção ainda mais inesperada!!!");
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
