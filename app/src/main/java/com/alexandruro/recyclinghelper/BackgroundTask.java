package com.alexandruro.recyclinghelper;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class BackgroundTask extends AsyncTask<String, Void, String> {

    private String ip;
    private String method;
    private String parameter;
    private String value;

    private WeakReference<ServerConnectionFragment> fragmentReference;

    BackgroundTask(String ip, String method, ServerConnectionFragment context) {
        this.ip = ip;
        this.method = method;
        this.fragmentReference = new WeakReference<>(context);
    }

    BackgroundTask(String ip, String method, String parameter, String value, ServerConnectionFragment context) {
        this.ip = ip;
        this.method = method;
        this.parameter = parameter;
        this.value = value;
        this.fragmentReference = new WeakReference<>(context);
    }

    protected void onPreExecute() {
    }

    protected String doInBackground(String... arg0) {

        try {

            URL url = new URL(ip);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoInput(true);

            if (method.equals("POST")) {
                JSONObject postDataParams = new JSONObject();
                postDataParams.put(parameter, value);

                String username = "admin";
                String password = "admin";

                byte[] message = (username + ":" + password).getBytes("UTF-8");
                String encoded = android.util.Base64.encodeToString(message, android.util.Base64.DEFAULT);
                conn.setRequestProperty("AUTHORIZATION", "Basic " + encoded);

                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
            } else
                conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_CREATED) {

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder("");
                String line;

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();
                return sb.toString();

            } else {
                return new String("false : " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new String("Exception: " + e.getMessage());
        }

    }

    @Override
    protected void onPostExecute(String result) {
        ServerConnectionFragment fragment = fragmentReference.get();
        if (fragment != null)
            ((TextView) fragment.getView().findViewById(R.id.resultTextView)).setText(result);
    }

    private String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
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
