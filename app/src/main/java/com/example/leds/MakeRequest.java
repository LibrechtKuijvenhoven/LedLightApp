package com.example.leds;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;

public class MakeRequest extends AsyncTask<String, Void, URL> {

    @Override
    protected URL doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            System.out.println(url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            System.out.println(con.getResponseCode());
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
