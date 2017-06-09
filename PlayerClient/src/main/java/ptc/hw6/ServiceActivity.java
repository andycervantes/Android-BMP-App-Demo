package ptc.hw6;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Andy Cervantes on 11/26/2016.
 */
public class ServiceActivity extends AppCompatActivity implements ServiceConnection {
    private static final String TAG = ServiceActivity.class.getSimpleName();
    public Boolean isBound = false;
    public AudioClient audioClient = null;
    public ArrayAdapter<String> arrayAdapter = null;
    public List<String> audioListView = null;
//    public ListView audioListView;
    public ListView outputListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        audioListView = (ListView) findViewById(R.id.audioListView);
        outputListView = (ListView) findViewById(R.id.outputListView);

    }

    // handle case where we connect
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//        super.onServiceConnected();
        audioClient = AudioClient.Stub.asInterface(iBinder);
        isBound = true;
    }

    // handle case where we disconnect
    @Override
    public void onServiceDisconnected(ComponentName componentName) {
//        super.onServiceDisconnected();


        if (isBound) {
            audioClient = null;
            isBound = false;
        }

        arrayAdapter = new ArrayAdapter<String>(ServiceActivity.this, android.R.layout.simple_list_item_activated_1, audioListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onBind();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(this);
            isBound = false;
        }
    }

//    @Override
    protected void onBind() {
//        super.onBind();

        if (!isBound) {

            Log.d(TAG, "onBind: " + ServiceActivity.class.getName());

            Intent intent = new Intent("ptc.hw6.AudioServer.AudioService");

//*********************************
            // keeps crashing because when player client starts, the other app closes...
            // need to do seperate in two different projects
            // *********************************

            ResolveInfo resolveInfo = getPackageManager().resolveService(intent, Context.BIND_AUTO_CREATE);
            intent.setComponent(new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name));
            isBound = bindService(intent, this, Context.BIND_AUTO_CREATE);

            if (isBound && audioClient != null) {
                Log.d(TAG, "onBind: SUCCESSFUL");
            }
            else
                Log.d(TAG, "onBind: FAILED");
        }
    }
}
