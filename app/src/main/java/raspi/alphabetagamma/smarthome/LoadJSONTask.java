package raspi.alphabetagamma.smarthome;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

/**
 * Created by shreyngd on 1/4/17.
 */

public class LoadJSONTask extends AsyncTask<String, Void, Response> {
    private Context ctx;
    private ProgressDialog progressDialog;
    public LoadJSONTask(Context ctx, Listener listener) {

        mListener = listener;
        this.ctx=ctx;
    }

    public interface Listener {

        void onLoaded(List<DeviceStats> deviceStatsList);

        void onError();
    }

    private Listener mListener;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog=new ProgressDialog(ctx);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }


    @Override
    protected Response doInBackground(String... params) {
        try {
            Log.d("ISIT", "doInBackground: ");
            String stringResponse = loadJSON(params[0]);
            Gson gson = new Gson();
            return gson.fromJson(stringResponse, Response.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }

    }

    protected void onPostExecute(Response response) {

        if (response != null) {

            mListener.onLoaded(response.getDevice());

        } else {
            Toast.makeText(ctx,"Error:Cant connect to system\nCheck Address",Toast.LENGTH_SHORT).show();

            mListener.onError();
        }
        progressDialog.dismiss();
    }


    private String loadJSON(String jsonURL) throws IOException {

        URL url = new URL(jsonURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();

        while ((line = in.readLine()) != null) {

            response.append(line);
        }

        in.close();
        return response.toString();
    }
}

