package sintu.com.pidroid;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import sintu.com.pidroid.p2p.P2P;

public class MainActivity extends AppCompatActivity {
    private P2P p2p;
    private ListView lvDevices;
    private List peers = new ArrayList();
    private List peerNames = new ArrayList();
    private ArrayAdapter<String> peersAdapter = null;
    private Handler peerHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createView();
        setHandler();
        setListeners();
        p2p = new P2P(this, peerHandler);
    }

    private void createView() {
        setContentView(R.layout.activity_main);
        lvDevices = (ListView) findViewById(R.id.lvDevices);
        peersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, peerNames);
        lvDevices.setAdapter(peersAdapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setHandler() {
        peerHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                peers.clear();
                peers.addAll((List <WifiP2pDevice>) msg.obj );
                peerNames.clear();
                for (int i = 0; i < peers.size(); i++) {
                    WifiP2pDevice device = (WifiP2pDevice) peers.get(i);
                    peersAdapter.add(device.deviceName);
                }

                return true;
            }
        });
    }

    private void setListeners() {
        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                WifiP2pDevice device = (WifiP2pDevice) peers.get(pos);
                p2p.connect(device);
            }
        });
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        p2p.registerReceivers();
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        p2p.unregisterReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
