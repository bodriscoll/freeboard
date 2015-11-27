package com.workday.freeboard.service;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

class PostMessageToSlackTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = "PostMessageToSlackTask";
    private static final String URL = "https://slack.com/api/chat.postMessage";
    private static final String TOKEN = "########";
    private static final String USERNAME = "Table Tennis Bot";
    private static final String CHANNEL = "#dublin-table-tennis";
    private static final String UTF_8 = "UTF-8";

    protected String doInBackground(String... params) {
        Log.i(TAG, "Received slack message.");
        HttpURLConnection connection = null;
        try {

            String query = String.format("token=%s&channel=%s&username=%s&text=%s",
                    URLEncoder.encode(TOKEN, UTF_8),
                    URLEncoder.encode(CHANNEL, UTF_8),
                    URLEncoder.encode(USERNAME, UTF_8),
                    URLEncoder.encode(params[0], UTF_8));

            connection = (HttpURLConnection) new URL(URL).openConnection();
            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("Accept-Charset", UTF_8);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + UTF_8);

            OutputStream output = connection.getOutputStream();
            output.write(query.getBytes(UTF_8));

            String responseStatus = connection.getResponseMessage() + " " + connection.getResponseCode();

            InputStream response = connection.getInputStream();
            String responseBody = getStringFromInputStream(response);
            Log.d(TAG, "Response status: " + responseStatus);
            Log.d(TAG, "Input stream: " + responseBody);

            return responseStatus;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return "bad error happened";
    }

    protected void onPostExecute(String result) {
        Log.d(TAG, result);
    }

    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
}