package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth;

import android.bluetooth.BluetoothSocket;

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

    private static final String MESSAGE_ID = "**PING_PHASE**";

    private static final String PING = "PING";

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
                byte[] challengeSent = SecurityUtil.generateSecureRandom(SecurityUtil.NONCE_BYTES_SIZE);
                sessionKey = SecurityUtil.getAesKeyFromBytes(sessionKeyBytes);
                writer.write(MESSAGE_ID + "||1||" + client.bluetooth.getAdapterMacAddress() + "||" +
                        SecurityUtil.byteToBase64(SecurityUtil.encrypt(sessionKeyBytes, client.getKek())) + "||" +
                        SecurityUtil.byteToBase64(SecurityUtil.encrypt(challengeSent, sessionKey)) + "\n");
                writer.flush();

                while ((line = reader.readLine()) != null){
                    System.out.println(line);
                    String lines[] = line.split("\\|\\|");
                    if(lines[0].equals(PING)){
                        byte[] ping = SecurityUtil.base64ToByte(lines[1]);
                        byte[] pingDecrypted = SecurityUtil.decrypt(ping,sessionKey);
                        byte[] pingTransformation = SecurityUtil.nonceTransformation(pingDecrypted);
                        byte[] pingTransformationEncrypted = SecurityUtil.encrypt(pingTransformation, sessionKey);
                        writer.write(MESSAGE_ID + "||3||" + SecurityUtil.byteToBase64(pingTransformationEncrypted) + "\n");
                        writer.flush();
                    }else if(lines.length == 5){
                        byte[] responseEncrypted = SecurityUtil.base64ToByte(lines[1]);
                        byte[] responseDecrypted = SecurityUtil.decrypt(responseEncrypted,sessionKey);
                        byte[] challengeEncrypted = SecurityUtil.base64ToByte(lines[2]);
                        byte[] challengeDecrypted = SecurityUtil.decrypt(challengeEncrypted, sessionKey);

                        byte[] timeStampEncrypted = SecurityUtil.base64ToByte(lines[3]);
                        byte[] timeStampDecrypted = SecurityUtil.decrypt(timeStampEncrypted,sessionKey);
                        byte[] hashtimeStampEncrypted = SecurityUtil.base64ToByte(lines[4]);
                        byte[] hashtimeStampDecrypted = SecurityUtil.decrypt(hashtimeStampEncrypted, sessionKey);

                        byte[] challengeTransformation = SecurityUtil.nonceTransformation(challengeDecrypted);
                        byte[] challengeTransformationEncrypted = SecurityUtil.encrypt(challengeTransformation,sessionKey);
                        if (Arrays.equals(hashtimeStampDecrypted, SecurityUtil.hashBytes(timeStampDecrypted))) {
                            if (Arrays.equals(responseDecrypted, SecurityUtil.nonceTransformation(challengeSent))) {
                                System.out.println("Challenge correct");
                                if (client.getFileKey() == null) {
                                    client.setFileKey(SecurityUtil.getAesKeyFromBytes(SecurityUtil.generateRandomAESKey()));
                                }
                                byte[] fileKeyMac = SecurityUtil.encrypt(SecurityUtil.hashBytes(client.getFileKey().getEncoded()), sessionKey);
                                byte[] encryptedFileKey = SecurityUtil.encrypt(client.getFileKey().getEncoded(), sessionKey);

                                //new Kfile
                                client.setFileKey(SecurityUtil.getAesKeyFromBytes(SecurityUtil.generateRandomAESKey()));
                                byte[] newEncryptedFileKey = SecurityUtil.encrypt(client.getFileKey().getEncoded(), sessionKey);
                                byte[] newFileKeyMac = SecurityUtil.encrypt(SecurityUtil.hashBytes(client.getFileKey().getEncoded()), sessionKey);

                                writer.write(MESSAGE_ID + "||2||" + SecurityUtil.byteToBase64(challengeTransformationEncrypted)
                                        + "||" + SecurityUtil.byteToBase64(encryptedFileKey) + "||" +
                                        SecurityUtil.byteToBase64(fileKeyMac) + "||" +
                                        SecurityUtil.byteToBase64(newEncryptedFileKey) + "||" +
                                        SecurityUtil.byteToBase64(newFileKeyMac) + "||" +
                                        lines[3] + "||" +
                                        lines[4] + "||" +
                                        "\n");
                                writer.flush();
                            } else {
                                System.out.println("Wrong challenge response aborting..");
                                cancel();
                                return;
                            }
                        }else{
                            System.out.println("TimeStamp tampering, aborting..");
                            cancel();
                            return;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        cancel();
    }
}
