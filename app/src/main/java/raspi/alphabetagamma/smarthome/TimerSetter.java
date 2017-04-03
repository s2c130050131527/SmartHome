package raspi.alphabetagamma.smarthome;


import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
public class TimerSetter extends AppCompatActivity implements View.OnClickListener {

    int mYear, mMonth, mDay, mHour, mMinute;
    Button starttimer,endtimer,savetimer,cleartimer;
    EditText editstart,editend;
    String finalstart="";
    String finalend="";
    String id="";
    private Context ctx;
    String ipAddress="";
    String URL="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_setter);
        starttimer= (Button) findViewById(R.id.starttimer);
        endtimer= (Button) findViewById(R.id.endtimer);
        savetimer= (Button) findViewById(R.id.savetimer);
        cleartimer= (Button) findViewById(R.id.cleartimer);
        editstart= (EditText) findViewById(R.id.editstart);
        editend= (EditText) findViewById(R.id.editend);

        starttimer.setOnClickListener(this);
        endtimer.setOnClickListener(this);
        cleartimer.setOnClickListener(this);
        savetimer.setOnClickListener(this);

        Bundle b= getIntent().getExtras();
        id=b.getString("id");
        ipAddress=b.getString("ip");
        ctx=this;
        URL="http://"+ipAddress+"/smarthome/timerset.php";

    }
    public void onClick(View v) {

        if (v == starttimer) {

            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);


            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            editstart.setText(String.format("%02d:%02d", hourOfDay, minute));


                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();

        }
        if (v == endtimer) {


            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);


            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            editend.setText(String.format("%02d:%02d", hourOfDay, minute));
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        if(v==cleartimer){

            editstart.setText("");
            editend.setText("");
        }
        if(v==savetimer){
            if(!(editstart.getText().toString()).equals("")){
            String[] s=editstart.getText().toString().split(":");
            finalstart=s[0]+s[1];}
            if(!(editend.getText().toString()).equals("")){
            String[] s1=editend.getText().toString().split(":");
            finalend=s1[0]+s1[1];}
            Toast.makeText(this,"Timer set sucessfully",Toast.LENGTH_SHORT).show();
            editstart.setText("");
            editend.setText("");
            Log.d(finalstart, "onClick: "+finalend);
            URL+="?id="+id+"&start="+finalstart+"&end="+finalend;
            SetTimeTask setTimeTask =new SetTimeTask(ctx);
            setTimeTask.execute(URL);
            Log.d("JSON",URL);
            finish();
        }

    }
}
