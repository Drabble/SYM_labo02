package com.heig.sym.sym_labo02.communications;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Abstract generic class to implement the bases of every type of HTTP requests used by the messaging
 * application to communicate with the server and the corresponding callback.
 */
public class CommunicationManager  {
    private CommunicationEventListener communicationEventListener;

    private Exception mException;

    private final static String TAG = CommunicationManager.class.getSimpleName();

    private class PostRequest extends AsyncTask<Void, Void, String>{
        private String url;
        private String request;

        public PostRequest(String url, String request){
            this.url = url;
            this.request = request;
        }

        /**
         * To execute in the background thread
         *
         * @param params required ellipse
         * @return the result
         */
        @Override
        protected String doInBackground(Void... params) {
            mException = null;
            return communication();
        }

        /**
         * After executing the request
         *
         * @param ret the request
         */
        @Override
        protected void onPostExecute(String ret) {
            if (mException == null) {
                while(!communicationEventListener.handleServerResponse(ret)){
                    new PostRequest(url, request).execute();
                }
            } else {
                new PostRequest(url, request).execute();
                //communicationEventListener.failure(mException);
            }
        }

        /**
         * Signature of the actual execution method wich will be implemented by each type of reqeust
         *
         * @return the request
         */
        protected String communication(){
            String body = null;
            try {
                URL url = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Type", "text/plain");
                //connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("X-Network", "CSD");
                connection.setConnectTimeout(2000);
                connection.setUseCaches(false);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                bw.write(request);
                bw.flush();
                bw.close();

                int status = connection.getResponseCode();
                InputStream is;
                Log.i(TAG, "HTTP status : " + String.valueOf(status));
                if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_NO_CONTENT || status == HttpURLConnection.HTTP_CREATED) {
                    is = connection.getInputStream();
                } else {
                    is = connection.getErrorStream();
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                String line;
                body = "";
                while ((line = br.readLine()) != null) {
                    body += line + "\n";
                }
                br.close();
                if (status != HttpURLConnection.HTTP_OK && status != HttpURLConnection.HTTP_NO_CONTENT && status != HttpURLConnection.HTTP_CREATED) {
                    setException(new Exception(body));
                }
            } catch (IOException e) {
                setException(e);
            }
            return body;
        }
    }




    public void sendRequest(String request, String url) throws Exception{
        Log.i(TAG, "New request POST on " + url);
        new PostRequest(url, request).execute();
    }

    public void setCommunicationEventListener (CommunicationEventListener communicationEventListener){
        this.communicationEventListener = communicationEventListener;
    }

    /**
     * When an exception has occured
     *
     * @param exception the Exception
     */
    protected void setException(Exception exception) {
        mException = exception;
    }
}