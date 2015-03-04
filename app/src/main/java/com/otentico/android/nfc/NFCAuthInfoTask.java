package com.otentico.android.nfc;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NFCAuthInfoTask extends AsyncTask<String, Integer, String> {

    private OnTaskCompleted listener;

    public NFCAuthInfoTask(OnTaskCompleted listener) {
        this.listener = listener;

    }

    @Override
    protected void onPreExecute() {

    }

    public String POST(String uid, String country, String locality, String identity, String androidID) {
        String url = Utils.HOST + "api/scans/";
        InputStream inputStream = null;
        String result = "";
        HttpPost httpPost;
        Log.d("REQUEST", url);
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpParams myParams = httpclient.getParams();
            Log.d("MyApp",
                    "" + myParams.getParameter("http.connection.timeout"));

            Log.d("MyApp", "" + myParams.getParameter("http.socket.timeout"));

            httpPost = new HttpPost(url);

            JSONObject obj = new JSONObject();
            obj.put("uid", uid);
            obj.put("country", country);
            obj.put("city", locality);
            obj.put("deviceID", identity);
            obj.put("android_id", androidID);

            httpPost.setEntity(new StringEntity(obj.toString()));

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            Log.d("REQUEST", "json param" + obj + "req: " + httpPost.toString());
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
                Log.d("RESULT", result);

            } else {
                Log.d("RESULT", ">>>>>> DID NOT WORK!!!!!");
                result = "Did not work!";
            }

        } catch (Exception e) {
            // Log.d("InputStream", e.getLocalizedMessage());
            Log.d("InputStream", "INPUT ERROR!!!!!! " + e.getMessage());
        }

        return result;
    }

    private String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    // Called to initiate the background activity
    @Override
    protected String doInBackground(String... args) {
        JSONObject json;
        String json_string = "";
        try {
            json_string = this.POST(args[0], args[1], args[2], args[3], args[4]);

            json = new JSONObject(json_string);

            Log.d("NFCAuthInfoTask", json_string);
            Log.d("NFCAuthInfoTask", json.length() > 0 ? "True" : "False");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            Log.d("SRANJE!!!!!", "SRANJE!!!!!");
            e.printStackTrace();

        }

        return json_string;
    }

    // Called once the background activity has completed
    @Override
    protected void onPostExecute(String result) {

        Log.d("NFCAuthInfoTask", "Result: " + result);

        listener.onTaskCompleted(result);
        // startActivity(new Intent(mContext,MainTabsActivity.class));

    }

}