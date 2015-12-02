package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import javax.crypto.SecretKey;

import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.MainActivity;
import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.business.Client;
import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.security.SecurityUtil;

/**
 * Created by fcma on 02/12/2015.
 */
public class MessageConnection extends ManageClientConnection{

    private static final String MESSAGE_ID = "**MESSAGE_STAGE**";

    /**
     * Default constructor
     *
     * @param currentActivity
     * @param socket
     * @param client
     */
    public MessageConnection(MainActivity currentActivity, BluetoothSocket socket, Client client) {
        super(currentActivity, socket, client, null);
    }

    public void run(){
        byte[] sentNonce = null;
        String line = "";
        SecretKey sessionKey;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        while (true) {
            try {
                byte[] sessionKeyBytes = SecurityUtil.generateRandomAESKey();
                sessionKey = SecurityUtil.getAesKeyFromBytes(sessionKeyBytes);
                writer.write(MESSAGE_ID + "||1||" + client.bluetooth.getAdapterMacAddress() + "||" +
                        SecurityUtil.byteToBase64(SecurityUtil.encrypt(sessionKeyBytes, client.getKek())) + "||" +
                        SecurityUtil.byteToBase64(SecurityUtil.encrypt(SecurityUtil.generateSecureRandom(SecurityUtil.NONCE_BYTES_SIZE), sessionKey))+"\n");
                writer.flush();
                while ((line = reader.readLine()) != null){

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
}
