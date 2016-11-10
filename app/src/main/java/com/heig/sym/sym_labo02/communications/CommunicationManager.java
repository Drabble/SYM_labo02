/**
 * Project: Labo 02 SYM
 * Authors: Antoine Drabble & Patrick Djomo
 * Date: 28.11.2016
 */
package com.heig.sym.sym_labo02.communications;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;

/**
 * Abstract generic class to implement the bases of every type of HTTP requests used by the messaging
 * application to communicate with the server and the corresponding callback.
 */
public class CommunicationManager  {
    // Event listener which will be called upon success of the HTTP request
    private CommunicationEventListener communicationEventListener;

    // Class TAG for logging
    private final static String TAG = CommunicationManager.class.getSimpleName();

    /**
     * Async task to run the HTTP POST requests asynchronously
     */
    private class PostRequest extends AsyncTask<Void, Void, String>{
        private String url;
        private String request;
        private String xRequest;
        private String contentType;
        private boolean xContentEncoding;

        /**
         * Create a new post request with the specified arguments
         *
         * @param url
         * @param request
         * @param xRequest
         * @param contentType
         * @param xContentEncoding
         */
        public PostRequest(String url, String request, String xRequest, String contentType, boolean xContentEncoding){
            this.url = url;
            this.request = request;
            this.xRequest = xRequest;
            this.contentType = contentType;
            this.xContentEncoding = xContentEncoding;
        }

        /**
         * To execute in the background thread
         *
         * @param params required ellipse
         * @return the result
         */
        @Override
        protected String doInBackground(Void... params) {
            String ret = null;

            // Run the HTTP request and wait 10000 to remake the request if it didn't work
            while(true){
                try {
                    ret = communication();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return ret;
        }

        /**
         * After executing the request
         *
         * @param ret the request
         */
        @Override
        protected void onPostExecute(String ret) {
            communicationEventListener.handleServerResponse(ret);
        }

        /**
         * Signature of the actual execution method wich will be implemented by each type of reqeust
         *
         * @return the request
         * @throws IOException
         */
        protected String communication() throws IOException {
            String body = null;
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("X-Network", xRequest);
            connection.setConnectTimeout(2000);
            connection.setUseCaches(false);
            if(xContentEncoding){
                connection.setRequestProperty("X-Content-Encoding", "deflate");
                DeflaterOutputStream dos = new DeflaterOutputStream(connection.getOutputStream());
                dos.write(request.getBytes());
                dos.flush();
                dos.close();
            }else{
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                bw.write(request);
                bw.flush();
                bw.close();
            }

            int status = connection.getResponseCode();
            InputStream is;
            Log.i(TAG, "HTTP status : " + String.valueOf(status));
            if (status == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }

            /*System.out.println("ENCODING : " + connection.getHeaderField("X-Content-Encoding"));
            if(connection.getHeaderField("X-Content-Encoding") != null && connection.getHeaderField("X-Content-Encoding").equalsIgnoreCase("deflate")){
                DeflaterInputStream dis = new DeflaterInputStream(is);
                dis.read();
                body = "";
                while ((dis.read(line)) != null) {
                    body += line + "\n";
                }
                dis.close();
            } else {*/
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                String line;
                body = "";
                while ((line = br.readLine()) != null) {
                    body += line + "\n";
                }
                br.close();
            //}
            if (status != HttpURLConnection.HTTP_OK) {
                System.out.println("Body answer : " + body);
                throw new RuntimeException("Invalid HTTP response");
            }
            return body;
        }
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
    public void sendRequest(String request, String url, String xNetwork, String contentType, boolean xContentEncoding){
        Log.i(TAG, "New request POST on " + url);

        new PostRequest(url, request, xNetwork, contentType, xContentEncoding).execute();
    }

    /**
     * Set the communication event listener which will be called upon success of the HTTP POST request
     *
     * @param communicationEventListener
     */
    public void setCommunicationEventListener (CommunicationEventListener communicationEventListener){
        this.communicationEventListener = communicationEventListener;
    }
}