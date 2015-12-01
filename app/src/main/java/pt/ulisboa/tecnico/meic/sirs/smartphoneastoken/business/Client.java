package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.business;

import java.security.Key;

import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.MainActivity;
import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth.Bluetooth;

/**
 * Created by André on 26-11-2015.
 */
public class Client {

    public Bluetooth bluetooth;

    private Key kek;

    private String id;

    public Client(MainActivity activity){
        this.bluetooth = new Bluetooth(activity);
    }

    public Key getKek(){
        return kek;
    }

    public void setKek(Key k){
        this.kek = k;
    }

}
