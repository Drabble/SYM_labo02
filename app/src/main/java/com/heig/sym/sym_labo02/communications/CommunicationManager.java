/**
 * Project: Labo 02 SYM
 * Authors: Antoine Drabble & Patrick Djomo
 * Date: 28.11.2016
 */
package com.heig.sym.sym_labo02.communications;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.heig.sym.sym_labo02.R;
import com.heig.sym.sym_labo02.activities.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Singleton class for communicating with a server
 * Implements the bases of POST HTTP requests to communicate with a server and sends the results
 * in the corresponding callback.
 * Uses a ScheduledExecutorService to handle differed requests.
 */
public class CommunicationManager  {
    // Class TAG for logging
    private final static String TAG = CommunicationManager.class.getSimpleName();

    // Instance of the singleton
    private static CommunicationManager instance = null;

    // Thread pool for differed requests
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    /**
     * Protected constructor for protecting the singleton instance
     */
    protected CommunicationManager() {
        // Exists only to defeat instantiation.
    }

    /**
     * Return the instance of the singleton and creates if necessary
     *
     * @return the singleton instance
     */
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
     * Thread used to run the HTTP POST requests asynchronously
     */
    private class PostRequest implements Runnable {
        private WeakReference<Activity> activity;
        private String url;
        private String request;
        private String xRequest;
        private String contentType;
        private boolean xContentEncoding;
        private int expectedHttpStatus;
        private CommunicationEventListener communicationEventListener;
        private Context context;

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
            this.activity = new WeakReference<>(activity);
            this.url = url;
            this.request = request;
            this.xRequest = xRequest;
            this.contentType = contentType;
            this.xContentEncoding = xContentEncoding;
            this.expectedHttpStatus = expectedHttpStatus;
            this.communicationEventListener = communicationEventListener;
            this.context = activity.getApplicationContext();
        }

        /**
         * Handles the success of a request
         *
         * @param response
         */
        public void success(final String response){
            // If the activity still exists, run the success callback on the UI thread
            if(activity != null) {
                activity.get().runOnUiThread(new Runnable() {
                    public void run() {
                        communicationEventListener.handleServerResponse(response);
                    }
                });
            }

            // Show a notification indicating that the POST request succeeded
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(context.getString(R.string.request_success))
                            .setContentText(context.getString(R.string.request_succeeded));

            // Set the MainActivity in case of a click on the notification
            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            // Show the notification
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }

        /**
         * Handles the error of the request
         *
         * @param response
         */
        public void error(final String response){
            // If the activity still exists, run the error callback on the UI thread
            if(activity != null) {
                activity.get().runOnUiThread(new Runnable() {
                    public void run() {
                        communicationEventListener.handleServerError(response);
                    }
                });
            }

            // Show a notification indicating that the POST request failed
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(context.getString(R.string.request_fail))
                            .setContentText(context.getString(R.string.request_failed));

            // Set the MainActivity in case of a click on the notification
            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            // Show the notification
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }

        /**
         * Method executed when the thread starts.
         * Runs a POST HTTP request on the server with the specified headers, deflation if specified
         * and with the given request.
         */
        @Override
        public void run() {
            try {
                Log.i(TAG, "Starting new request !");

                // Create the Http connection and set the headers
                URL url = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Type", contentType);
                connection.setRequestProperty("X-Network", xRequest);
                connection.setConnectTimeout(2000);

                // Write the request to the HTTP connection's output stream
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

                // Retrieve the status of the request and its input stream
                int status = connection.getResponseCode();
                InputStream is = connection.getInputStream();
                Log.i(TAG, "HTTP status : " + String.valueOf(status));

                // Read the response from the request's input stream
                BufferedReader br;
                if (connection.getHeaderField("X-Content-Encoding") != null && connection.getHeaderField("X-Content-Encoding").equalsIgnoreCase("deflate")) {
                    br = new BufferedReader(new InputStreamReader(new InflaterInputStream(is, new Inflater(true)), "utf-8"));
                } else {
                    br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                }
                StringBuilder bodyBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    bodyBuilder.append(line);
                    bodyBuilder.append("\n");
                }
                br.close();

                // Execute the methods for success if the expected HTTP status is right otherwise
                // execute the method to hande error.
                final String response = bodyBuilder.toString();
                if (status != expectedHttpStatus) {
                    error(response);
                }
                else {
                    success(response);
                }
            } catch (java.net.SocketTimeoutException e) {
                // Reschedule the task for 10 seconds later in case of a HTTP request timeout
                Log.i(TAG, "Reschedulding request after timeout!");
                executor.schedule(this, 10, TimeUnit.SECONDS);
            } catch(final Exception e){
                e.printStackTrace();
                // In case of another exception, run the method to handle the error
                error("Error running the request");
            }
        }
    }
}