package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.business;

import java.security.Key;

import javax.crypto.SecretKey;

import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.MainActivity;
import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth.Bluetooth;


public class Client {

    public Bluetooth bluetooth;

    private SecretKey kek;

    private SecretKey fileKey;

    private String id;

    public Client(MainActivity activity) {
        this.bluetooth = new Bluetooth(activity);
    }

    public SecretKey getKek() {
        return kek;
    }

    public void setKek(SecretKey k) {
        this.kek = k;
    }

    public SecretKey getFileKey() {
        return fileKey;
    }

    public void setFileKey(SecretKey k) {
        this.fileKey = k;
    }

}
