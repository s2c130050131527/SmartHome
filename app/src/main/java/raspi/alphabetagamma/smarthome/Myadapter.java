package raspi.alphabetagamma.smarthome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shreyngd on 1/4/17.
 */

public class Myadapter extends SimpleAdapter {
    private Context ctx;
    private String ipAddress;



    /**
     * Constructor
     *  @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
 *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
*                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     * @param ipAddress
     */
    public Myadapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, String ipAddress) {
        super(context, data, resource, from, to);
        ctx=context;
        this.ipAddress=ipAddress;

    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View v=super.getView(position,convertView,parent);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //do nothing;
            }
        });

            Button onOff= (Button) v.findViewById(R.id.onoff);


        ImageButton setTimer= (ImageButton) v.findViewById(R.id.settime);


                onOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String onoff="/smarthome/switch.php?id=";
                String id="";
                try {
                    id = ((Map) getItem(position)).get("ID").toString();
                }catch(Exception e){
                    Toast.makeText(ctx,e.toString(),Toast.LENGTH_SHORT).show();
                }
                String URL="http://"+ipAddress+onoff+id;
                SwitchTask switchTask=new SwitchTask();
                switchTask.execute(URL);
//                Toast.makeText(ctx,URL,Toast.LENGTH_SHORT).show();
                if(ctx instanceof MainActivity){
                    ((MainActivity)ctx).updateList();
                }

            }
        });

        setTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b=new Bundle();
                b.putString("id",((Map) getItem(position)).get("ID").toString());
                b.putString("ip",ipAddress);
                Intent i=new Intent(ctx,TimerSetter.class);
                i.putExtras(b);
                ((MainActivity)ctx).startActivity(i);



            }
        });
    return v;
    }

}
