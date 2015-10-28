package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.IOException;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 * Created by André on 27-10-2015.
 */
public class Bluetooth {

    private Activity currentActivity;

    private BluetoothAdapter adapter;

    private HashMap<String,BluetoothDevice> nearDevices;

    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                nearDevices.put(device.getName(), device);
            }
        }
    };

    public Bluetooth(Activity activity){
        this.currentActivity = activity;
        nearDevices = new HashMap<String,BluetoothDevice>();
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public int getAllDevices(){
        return nearDevices.size();
    }

    public void turnOnBluetooth(){
        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        currentActivity.startActivityForResult(turnOn,0);
    }

    public void getNearDevice() throws InterruptedException {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        currentActivity.registerReceiver(mReceiver, filter);
        nearDevices.clear();
        adapter.startDiscovery();
        while (adapter.isDiscovering());
    }

    public void connectToServer(String name) throws IOException {
        BluetoothDevice device = nearDevices.get((String) name);
        if(device == null)
            return;
        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(new UUID(1111111111,1111111111));
        socket.connect();
    }



}
