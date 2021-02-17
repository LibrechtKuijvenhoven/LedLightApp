package com.example.leds.handlers;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPHandler extends AsyncTask<String, Void, URL> {

    /**
     * Makes http request to raspberry pi
     * with given url
     *
     * @param strings : String[]
     * @return null
     */
    @Override
    protected URL doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            System.out.println(url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            System.out.println(con.getResponseCode());
            Thread.sleep(200);
        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
