/**
 * Project: Labo 02 SYM
 * Authors: Antoine Drabble & Patrick Djomo
 * Date: 28.11.2016
 */
package com.heig.sym.sym_labo02.communications;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Abstract generic class to implement the bases of every type of HTTP requests used by the messaging
 * application to communicate with the server and the corresponding callback.
 */
public class CommunicationManager  {
    // Class TAG for logging
    private final static String TAG = CommunicationManager.class.getSimpleName();

    private static CommunicationManager instance = null;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    protected CommunicationManager() {
        // Exists only to defeat instantiation.
    }

    public static CommunicationManager getInstance() {
        if(instance == null) {
            instance = new CommunicationManager();
        }
        return instance;
    }

    /**
     * Send a new POST request with the given arguments
     *
     * @param request
     * @param url
     * @param xNetwork
     * @param contentType
     * @param xContentEncoding
     */
    public void sendRequest(Activity activity, String request, String url, String xNetwork, String contentType, boolean xContentEncoding, int expectedHttpStatus, CommunicationEventListener communicationEventListener){
        Log.i(TAG, "New request POST on " + url);

        executor.schedule(new PostRequest(activity, url, request, xNetwork, contentType, xContentEncoding, expectedHttpStatus, communicationEventListener), 0, TimeUnit.SECONDS);
    }

    /**
     * Async task to run the HTTP POST requests asynchronously
     */
    private class PostRequest implements Runnable {
        private Activity activity;
        private String url;
        private String request;
        private String xRequest;
        private String contentType;
        private boolean xContentEncoding;
        private int expectedHttpStatus;
        private CommunicationEventListener communicationEventListener;

        /**
         * Create a new post request with the specified arguments
         *
         * @param url
         * @param request
         * @param xRequest
         * @param contentType
         * @param xContentEncoding
         */
        public PostRequest(Activity activity, String url, String request, String xRequest, String contentType, boolean xContentEncoding, int expectedHttpStatus, CommunicationEventListener communicationEventListener){
            this.activity = activity;
            this.url = url;
            this.request = request;
            this.xRequest = xRequest;
            this.contentType = contentType;
            this.xContentEncoding = xContentEncoding;
            this.expectedHttpStatus = expectedHttpStatus;
            this.communicationEventListener = communicationEventListener;
        }

        @Override
        public void run() {
            try {
                Log.i(TAG, "Starting new request !");
                String body = null;
                URL url = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Type", contentType);
                connection.setRequestProperty("X-Network", xRequest);
                connection.setConnectTimeout(2000);

                BufferedWriter bw;
                if (xContentEncoding) {
                    connection.setRequestProperty("X-Content-Encoding", "deflate");
                    bw = new BufferedWriter(new OutputStreamWriter(new DeflaterOutputStream(connection.getOutputStream(), new Deflater(9, true)), "UTF-8"));
                } else {
                    bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                }
                bw.write(request);
                bw.flush();
                bw.close();

                int status = connection.getResponseCode();
                InputStream is = connection.getInputStream();
                Log.i(TAG, "HTTP status : " + String.valueOf(status));

                BufferedReader br;
                if (connection.getHeaderField("X-Content-Encoding") != null && connection.getHeaderField("X-Content-Encoding").equalsIgnoreCase("deflate")) {
                    br = new BufferedReader(new InputStreamReader(new InflaterInputStream(is, new Inflater(true)), "utf-8"));
                } else {
                    br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                }
                StringBuilder bodyBuilder = new StringBuilder();
                String line;
                body = "";
                while ((line = br.readLine()) != null) {
                    bodyBuilder.append(line);
                    bodyBuilder.append("\n");
                }
                br.close();

                final String response = bodyBuilder.toString();
                if (status != expectedHttpStatus) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            communicationEventListener.handleServerError(response);
                        }
                    });
                }
                else {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            communicationEventListener.handleServerResponse(response);
                        }
                    });
                }
            } catch (java.net.SocketTimeoutException e) {
                // Reschedule the task
                Log.i(TAG, "Reschedulding request after timeout!");
                executor.schedule(this, 10, TimeUnit.SECONDS);
            } catch(final Exception e){
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        communicationEventListener.handleServerError("Error running the request");
                    }
                });
            }
        }
    }
}