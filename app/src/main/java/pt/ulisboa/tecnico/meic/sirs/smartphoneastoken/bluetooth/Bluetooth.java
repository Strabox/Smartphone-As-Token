package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth;

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

import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.MainActivity;
import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.business.Client;


public class Bluetooth {

    public static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private MainActivity currentActivity;

    private BluetoothAdapter adapter;

    private List<BluetoothDevice> nearDevicesWithService;

    private List<BluetoothDevice> connectedDevicesWithService;


    /**
     * Listener objects, receives asynchronous calls from discovering.
     */
    private final BroadcastReceiver bluetoothListener = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                ParcelUuid[] uuids =  device.getUuids();
                if(uuids != null){
                    for (ParcelUuid u : uuids) {
                        System.out.println(u);
                        if(u.getUuid().equals(Bluetooth.uuid)) //If device has our service running.
                            nearDevicesWithService.add(device);
                    }
                }
            }
            //When the discover is finished
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                currentActivity.disableProgressBar();
                currentActivity.registersDiscoverFinished();
            }
        }
    };

    public Bluetooth(MainActivity activity){
        this.currentActivity = activity;
        nearDevicesWithService = new LinkedList<BluetoothDevice>();
        connectedDevicesWithService = new LinkedList<BluetoothDevice>();
        adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.isEnabled();
    }



    public synchronized String[] getDevicesNames(){
        String[] array = new String[nearDevicesWithService.size()];
        int i = 0;
        for (BluetoothDevice device : nearDevicesWithService) {
            array[i] = device.getName();
            i++;
        }
        return array;
    }

    public void register(String deviceName,Client client){
        BluetoothDevice deviceTemp = null;
        for (BluetoothDevice device : nearDevicesWithService) {
            if(device.getName().equals(deviceName))
                deviceTemp = device;
        }
        new MakeClientConnection(currentActivity, deviceTemp,uuid,client, RegisterConnection.class.getName()).start();
    }

    public synchronized boolean getState(){
        return adapter.isEnabled();
    }

    public String getAdapterMacAddress(){
        return adapter.getAddress();
    }

    public synchronized void toggleBluetooth(){
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
        nearDevicesWithService.clear();
        adapter.startDiscovery();       //Start discovery assynchronosly.
    }

    public BluetoothDevice getConnectedDeviceByName(String deviceName){
        for (BluetoothDevice b: connectedDevicesWithService){
            if(b.getName().equals(deviceName)){
                return b;
            }
        }
        return null;
    }

    public void addConnectedDevice(BluetoothDevice bdv){
        connectedDevicesWithService.add(bdv);
    }
}
