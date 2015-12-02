package pt.ulisboa.tecnico.meic.sirs.smartphoneastoken;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth.Bluetooth;
import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth.MakeClientConnection;
import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.bluetooth.MessageConnection;
import pt.ulisboa.tecnico.meic.sirs.smartphoneastoken.business.Client;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Client client;

    public MainActivity() {
        client = new Client(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setVisibility(View.GONE);
        spinner.setOnItemSelectedListener(this);
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        findViewById(R.id.textView).setVisibility(View.GONE);
        findViewById(R.id.textView2).setVisibility(View.GONE);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        if(client.bluetooth.getState()){
            toggle.setChecked(false);
        }
        else{
            toggle.setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*========================== GUI Events ==============================*/

    public void disableProgressBar(){
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    public void registersDiscoverFinished(){
        Toast.makeText(this,"Found " + client.bluetooth.getDevicesNames().length + " registers",
                Toast.LENGTH_LONG).show();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_selectable_list_item);
        adapter.add("Choose One");
        adapter.addAll(client.bluetooth.getDevicesNames());
        ((Spinner) findViewById(R.id.spinner)).setAdapter(adapter);
        findViewById(R.id.textView).setVisibility(View.VISIBLE);
        findViewById(R.id.spinner).setVisibility(View.VISIBLE);
    }

    public void successRegistration(BluetoothDevice btd){
        Toast.makeText(this,"Connected to " + btd.getName(), Toast.LENGTH_LONG).show();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_selectable_list_item);
        adapter.add("Devices:");
        adapter.addAll(btd.getName());
        ((Spinner) findViewById(R.id.spinner2)).setAdapter(adapter);
        findViewById(R.id.textView2).setVisibility(View.VISIBLE);
        findViewById(R.id.spinner2).setVisibility(View.VISIBLE);
        client.bluetooth.addConnectedDevice(btd);
    }

    public void connectToLaptop(View v){
        String deviceName = ((Spinner)findViewById(R.id.spinner2)).getSelectedItem().toString();
        if(!deviceName.equals("Devices:")){
            BluetoothDevice device = client.bluetooth.getConnectedDeviceByName(deviceName);
            if(device != null){
                new MakeClientConnection(this,device, Bluetooth.uuid,client,MessageConnection.class.getName()).start();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0){
        //Do nothing
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String selected = parent.getItemAtPosition(pos).toString();
        if(!selected.equals("Choose One")) {
            Toast.makeText(parent.getContext(), selected, Toast.LENGTH_SHORT).show();
            client.bluetooth.register(selected,client);
        }
    }


    /* ============================================================= */

    public void toggleBluetooth(View v){
        client.bluetooth.toggleBluetooth();
    }

    public void discoverOnClick(View v) throws InterruptedException {
        if(client.bluetooth.getState()) {
            client.bluetooth.getNearDevice();
            findViewById(R.id.textView).setVisibility(View.GONE);
            findViewById(R.id.spinner).setVisibility(View.GONE);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }
    }


}
