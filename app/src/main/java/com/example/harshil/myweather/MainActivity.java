package com.example.harshil.myweather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView resulttv;
    ImageView imageView;
    public void findweather(View view) throws UnsupportedEncodingException {
        Log.i("City name :",editText.getText().toString());
        InputMethodManager mgr=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);

        String encodecityname=URLEncoder.encode(editText.getText().toString(),"UTF-8");
        Log.i("encodecityname",encodecityname);
        DownloadTask task=new DownloadTask();
        task.execute("http://api.openweathermap.org/data/2.5/weather?q="+encodecityname+"&appid=43220716eb6045a9a33eac9808e34e58");
    }
    public class DownloadTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(strings[0]);
                urlConnection=(HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1)
                {
                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }
                return result;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Could not find Weather :(",Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                String message="";
                JSONObject jsonObject=new JSONObject(s);

                String wetherinfo=jsonObject.getString("weather");
                JSONArray arr=new JSONArray(wetherinfo);
                JSONObject jsonpart=arr.getJSONObject(0);
                String main="Main : "+jsonpart.getString("main");
                String description="\nDescription : "+jsonpart.getString("description");
                String icon=jsonpart.getString("icon");
                Log.i("icon",icon);

                int visibility=jsonObject.getInt("visibility");
                String visibilityString=Integer.toString(visibility);
//
                String maininfo=jsonObject.getString("main");
                JSONObject jsonObject1=new JSONObject(maininfo);
                int temp=jsonObject1.getInt("temp");String tempstring="\nTemperature : "+Integer.toString(temp);
                int tempmin=jsonObject1.getInt("temp_min");String tempminstring="\nMin Temp : "+Integer.toString(tempmin);
                int tempmax=jsonObject1.getInt("temp_max");String tempmaxstring="\nMax Temp : "+Integer.toString(tempmax);
                int preassure=jsonObject1.getInt("pressure");String preassurestring="\nPressure : "+Integer.toString(preassure);
                int humidity=jsonObject1.getInt("humidity");String humiditystring="\nHumidity : "+Integer.toString(humidity);
                Log.i("Testing ",tempstring+tempmaxstring+tempminstring+preassurestring+humiditystring);

                String windinfo=jsonObject.getString("wind");
                JSONObject jsonObject2=new JSONObject(windinfo);
                double speed=jsonObject2.getDouble("speed");String speedstring="\nWind Speed : "+Double.toString(speed);


                ImageDownloader task=new ImageDownloader();
                Bitmap bitmap;
                bitmap=task.execute("http://openweathermap.org/img/w/"+icon+".png").get();
                imageView.setImageBitmap(bitmap);
                String result="";

                    if(main!=""&&description!="")
                    {
                        message+=main+description+tempstring+tempminstring+tempmaxstring+preassurestring+humiditystring+speedstring;
                    }
                    if(message!=""){

                        resulttv.setText(message);
                    }else{
                        Toast.makeText(getApplicationContext(),"Could not find Weather :(",Toast.LENGTH_SHORT).show();

                    }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }
    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url=new URL(strings[0]);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.connect();
                InputStream inputStream=httpURLConnection.getInputStream();
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resulttv=(TextView)findViewById(R.id.resulttv);
        editText=(EditText)findViewById(R.id.edittext);
        Button button1=(Button)findViewById(R.id.button1);
        button1.setText("What's the Weather ?");
        imageView=(ImageView)findViewById(R.id.imageview);
    }
}
