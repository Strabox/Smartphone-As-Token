package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.business;

import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.MainActivity;
import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth.Bluetooth;

/**
 * Created by André on 26-11-2015.
 */
public class Client {

    public Bluetooth bluetooth;

    private byte[] kek;

    private String id;

    public Client(MainActivity activity){
        this.bluetooth = new Bluetooth(activity);
    }

    public byte[] getKek(){
        return kek;
    }

}
