package com.example.iotgreenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;




import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	TextView TextID01,TextID02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "https://api.thingspeak.com/channels/449926/feeds/last.json?api_key=";
        String apikey = "TC8NQYVPK8SUDANY";
        final UriApi uriapi01 = new UriApi();

        uriapi01.setUri(url,apikey);
        Timer timer = new Timer();
        TimerTask tasknew = new TimerTask(){
            public void run() {
                LoadJSON task = new LoadJSON();
                task.execute(uriapi01.getUri());
            }
        };
        timer.scheduleAtFixedRate(tasknew,15*1000,15*1000);
        Button temp=(Button)findViewById(R.id.button1);
        temp.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View arg0) {
        		// TODO Auto-generated method stub
        	Intent it=new Intent(MainActivity.this,MessageActivity.class);
        	startActivity(it);
        	}
        });
        Button humidity=(Button)findViewById(R.id.button2);
        humidity.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View arg0) {
        		// TODO Auto-generated method stub
        	Intent it=new Intent(MainActivity.this,MoistureActivity.class);
        	startActivity(it);
        	}
        });
    }

    private class UriApi {

        private String uri,url,apikey;

        protected void setUri(String url, String apikey){
            this.url = url;
            this.apikey = apikey;
            this.uri = url + apikey;
        }

        protected  String getUri(){
            return uri;
        }

    }

    private class LoadJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return getText(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            /*TextView textview = (TextView) findViewById(R.id.textJSON);
            textview.setText(result);*/
            String msg01 = "";
            String msg02 = "";
           // String msg03 = "";

            try {
                JSONObject json = new JSONObject(result);
                System.out.println("^%^%$%^$%"+msg01+msg02);

                msg01 = String.format("%s", json.getString("field1"));
               msg02 = String.format("%s", json.getString("field2"));

              // msg03 = String.format("%s", json.getString("field3"));

                System.out.println("^%^%$%^$%"+msg01+msg02);

            } catch (JSONException e) {
                e.printStackTrace();
            }
             TextID01 = (TextView) findViewById(R.id.textView1);
             TextID02 = (TextView) findViewById(R.id.textView2);
            // TextID03 = (TextView) findViewById(R.id.textView3);

             TextID01.setText("Temparature  :"+Integer.parseInt(msg01));

             TextID02.setText("Humidity  :"+Integer.parseInt(msg02));

             //TextID03.setText("Light  :"+Integer.parseInt(msg03));
             int  aaa=Integer.parseInt(msg02);
             if(aaa>=40){
             	NotificationCompat.Builder builder =
             	         new NotificationCompat.Builder(MainActivity.this)
             	         .setSmallIcon(R.drawable.ic_launcher)
             	         .setContentTitle("Weather  Monitoring Alert")
             	         .setContentText("Humidity level above 40");

             	      Intent notificationIntent = new Intent(MainActivity.this, MainActivity.class);
             	      PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, notificationIntent,
             	         PendingIntent.FLAG_UPDATE_CURRENT);
             	      builder.setContentIntent(contentIntent);

             	      // Add as notification
             	      NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
             	      manager.notify(0, builder.build());

             }else{
             	
             }


        }
    }

    private String getText(String strUrl) {
        String strResult = "";
        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            strResult = readStream(con.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResult;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
