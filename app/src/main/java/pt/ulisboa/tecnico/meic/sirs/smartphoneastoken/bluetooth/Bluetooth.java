package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

/**
 * Created by André on 27-10-2015.
 */
public class Bluetooth {

    private Activity currentActivity;

    private BluetoothAdapter adapter;

    public Bluetooth(Activity activity){
        this.currentActivity = activity;
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void turnOnBluetooth(){
        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        currentActivity.startActivityForResult(turnOn,0);
    }


}
