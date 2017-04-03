package raspi.alphabetagamma.smarthome;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoadJSONTask.Listener{

    private ListView mListView;
    private String URL;
    private List<HashMap<String, String>> mAndroidMapList ;

    private static final String KEY_NAME = "name";
    private static final String KEY_STATUS = "status";
    private static final String KEY_ID="ID";
    private Button checkStatus=null;
    private LoadJSONTask.Listener listener=this;
    private String ipAddress ="";
    private Switch smartSwitch;


    private Context ctx=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(0xFFFFFFFF);
        mAndroidMapList = new ArrayList<>();
        checkStatus=(Button) findViewById(R.id.checkstatus);
        mListView = (ListView) findViewById(R.id.list_view);
        smartSwitch= (Switch) findViewById(R.id.switch1);
        ipAddress=getIpAddress();
        URL=buildURL();
        Log.d("ISit", "onCreate: "+URL);
        LoadJSONTask task= new LoadJSONTask(ctx,listener);
        task.execute(URL);
        loadListView();

        checkStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateList();

            }
        });

        smartSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SwitchTask task=new SwitchTask();
                    task.execute("http://"+ipAddress+"/smarthome/smarton.php");
                }
                else{
                    SwitchTask task=new SwitchTask();
                    task.execute("http://"+ipAddress+"/smarthome/smartoff.php");
                }
            }
        });


    }

    private String buildURL() {
        String URIBuilder="http://"+ipAddress+"/smarthome/result.php";
        return URIBuilder;
    }

    private String getIpAddress() {

        try {
            InputStream inputStream = openFileInput("config.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                return stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    //abc


    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preference, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SetIPActivity.class));
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }

    public void updateList() {
        mAndroidMapList.clear();
        new LoadJSONTask(ctx, listener).execute(URL);
    }


    @Override
    public void onLoaded(List<DeviceStats> deviceStatsList) {

        for (DeviceStats deviceStats: deviceStatsList) {

            HashMap<String, String> map = new HashMap<>();

            map.put(KEY_ID,deviceStats.getId());
            map.put(KEY_NAME, deviceStats.getName());
            map.put(KEY_STATUS, deviceStats.getStatus());


            mAndroidMapList.add(map);
        }

        loadListView();

    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error !", Toast.LENGTH_SHORT).show();
    }




    private void loadListView() {

       ListAdapter adapter = new Myadapter(MainActivity.this, mAndroidMapList, R.layout.list_item,
                new String[] { KEY_NAME, KEY_STATUS },
                new int[] {R.id.name, R.id.status },ipAddress);

        mListView.setAdapter(adapter);

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) { // this is override method
        if(keyCode == KeyEvent.KEYCODE_BACK){
            showExitConfirmDialog(); // call the function below
        }
        return super.onKeyDown(keyCode, event);
    }
    public void showExitConfirmDialog(){ // just show an dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Exit?"); // set title
        dialog.setMessage("Are you sure to exit?"); // set message
        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity(); // when click OK button, finish current activity!
                    }
                });
        dialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show(); // just show a Toast, do nothing else
                    }
                });
        dialog.create().show();
    }
}
