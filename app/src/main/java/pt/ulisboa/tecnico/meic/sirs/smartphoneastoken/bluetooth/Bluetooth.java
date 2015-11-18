package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;


import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by André on 27-10-2015.
 */
public class Bluetooth {

    public static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Activity currentActivity;

    private BluetoothAdapter adapter;

    private List<BluetoothDevice> nearDevices;

    /**
     * Listener objects, receives asynchronous calls from discovering.
     */
    private final BroadcastReceiver bluetoothListener = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                nearDevices.add(device);
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                new AlertDialog.Builder(context).setTitle("Discovery").setMessage("Discovery Finished").show();
            }
        }
    };

    public Bluetooth(Activity activity){
        this.currentActivity = activity;
        nearDevices = new LinkedList<BluetoothDevice>();
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void tryConnect(){
        for (BluetoothDevice device : nearDevices){
            System.out.println(device.getName());
            ParcelUuid[] uuids = device.getUuids();
            if(uuids != null) {
                for (ParcelUuid u : uuids) {
                    System.out.println(u);
                    if(u.getUuid().equals(Bluetooth.uuid))
                        new MakeClientConnection(device,Bluetooth.uuid).run();
                }
            }
        }
    }

    public void turnOnBluetooth(){
        if (adapter.isEnabled()) {
            adapter.disable();
        }
        else {
            adapter.enable();
        }
    }

    public void getNearDevice() throws InterruptedException {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        currentActivity.registerReceiver(bluetoothListener, filter);
        nearDevices.clear();
        adapter.startDiscovery();
    }

}
