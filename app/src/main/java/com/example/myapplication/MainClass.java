package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by Ирина on 03.05.2016.
 */
public class MainClass extends Activity
{
    HTMLTask objHtml;
    String prefix = "";

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final TextView url = (TextView)findViewById(R.id.TextUrl);
        final TextView html = (TextView) findViewById (R.id.TextHTML);
        final Button butGetHTML = (Button)findViewById(R.id.buttonGO);

        RadioGroup group = (RadioGroup)findViewById(R.id.RadGroup);

        final RadioButton rb1 = (RadioButton)findViewById(R.id.RButHttp);
        final RadioButton rb2 = (RadioButton)findViewById(R.id.RButHttps);

        ImageButton imButton = (ImageButton)findViewById(R.id.imageButton);



        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.RButHttp:
                        prefix = "http://";
                        break;

                    case R.id.RButHttps:
                        prefix = "https://";
                        break;

                    default:
                        break;
                }
            }
        });


        imButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                url.setText("");
                html.setText("");
            }
        });

        butGetHTML.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(!url.getText().equals(""))
                {
                    if( internetAvailable() )
                    {
                        if( rb1.isChecked() || rb2.isChecked())
                        {
                            objHtml = new HTMLTask();
                            System.out.println("prefix = " + prefix);
                            objHtml.execute(prefix + url.getText().toString().trim());

                            try {
                                html.setText(objHtml.get());

                            } catch (Exception ex) {
                                System.out.println("ex " + ex);

                            }
                        }
                        else
                        {
                            html.setText("Необходимо указать протокол: http или https");
                        }
                    }
                    else
                    {

                        html.setText("Проверьте наличие интернет соединения");
                    }
                }
                else
                {

                    url.setText("Введите url!");
                }
            }
        });
    }
    class HTMLTask extends AsyncTask<String, Integer, String>
    {
        protected String doInBackground(String... params)
        {
            String url = "";
            if( params.length > 0 )
            {
                url = params[0];
            }
            StringBuilder sb = new StringBuilder();


            try
            {
                URL pageURL = new URL(url);
                System.out.println("url = "+url);
                String inputLine;
                URLConnection uc = pageURL.openConnection();

                BufferedReader buff = new BufferedReader(new InputStreamReader(uc.getInputStream()));

                while ((inputLine = buff.readLine()) != null)
                {
                    sb.append(inputLine);
                }
                buff.close();

            }

            catch (Exception ex)
            {
                System.out.println("ex request "+ex);
            }

            return sb.toString();
        }

    }



    public Boolean internetAvailable() {
        ConnectivityManager connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean internetAvailable = (connectManager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        return internetAvailable;
    }
}
