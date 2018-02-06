package com.alexandruro.recyclinghelper;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class BackgroundTask extends AsyncTask<String, Void, String> {

    String ip;
    String method;
    String parameter;
    String value;

    TextView resultTextView;


    public BackgroundTask(String ip, String method, TextView resultTextView) {
        this.ip = ip;
        this.method = method;
        this.resultTextView = resultTextView;
    }

    public BackgroundTask(String ip, String method, String parameter, String value, TextView resultTextView) {
        this.ip = ip;
        this.method = method;
        this.parameter = parameter;
        this.value = value;
        this.resultTextView = resultTextView;
    }

    protected void onPreExecute(){}

    protected String doInBackground(String... arg0) {

        try {

            URL url = new URL(ip);
            //URL url = new URL("https://aa5af3de-bdce-4b9c-b09f-90678071cc22.mock.pstmn.io");

            JSONObject postDataParams = new JSONObject();
            postDataParams.put(parameter, value);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoInput(true);

            if(method.equals("POST")) {
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
            }
            else {
                conn.connect();
            }

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK || responseCode==HttpsURLConnection.HTTP_CREATED) {

                BufferedReader in=new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));

                StringBuilder sb = new StringBuilder("");
                String line="";

                while((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                Log.d("ok", "doInBackground: " + sb.toString());
                return sb.toString();

            }
            else {
                return new String("false : "+responseCode);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return new String("Exception: " + e.getMessage());
        }

    }

    @Override
    protected void onPostExecute(String result) {
        resultTextView.setText(result);
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
