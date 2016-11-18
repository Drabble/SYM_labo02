/**
 * Project: Labo 02 SYM
 * Authors: Antoine Drabble & Patrick Djomo
 * Date: 28.11.2016
 */
package com.heig.sym.sym_labo02.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.heig.sym.sym_labo02.R;
import com.heig.sym.sym_labo02.communications.CommunicationEventListener;
import com.heig.sym.sym_labo02.communications.CommunicationManager;
import com.heig.sym.sym_labo02.model.User;

import java.net.HttpURLConnection;

/**
 * Send a JSON request to the server and specify that the request must be deflated (compressed)
 */
public class Activity4 extends AppCompatActivity {

    /**
     * On creation of the activity, run a deflated JSON POST request and handle the response.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4);

        // Retrieve the text views which will contain the JSON request and response
        final TextView jsonSent = (TextView)findViewById(R.id.json_sent);
        final TextView jsonReceived = (TextView)findViewById(R.id.json_received);

        try {
            // Create a new User and serialize it to JSON using the Gson library
            Gson g = new Gson();
            User user = new User(1, "antoine", "1234", "antoine.drabble@heig-vd.ch");
            String jsonRequest = g.toJson(user);

            // Send the deflated JSON request to the echo server
            CommunicationManager.getInstance().sendRequest(this, jsonRequest, "http://sym.dutoit.email/rest/json", "CSD", "application/json", true, HttpURLConnection.HTTP_OK, new CommunicationEventListener() {

                /**
                 * Handle the server response by updating the text field and showing a success toast
                 *
                 * @param response
                 * @return
                 */
                @Override
                public boolean handleServerResponse(String response) {
                    // Deserialize the response to a user
                    JsonParser parser = new JsonParser();
                    JsonObject jsonResponse = parser.parse(response).getAsJsonObject();
                    Gson gson = new Gson();
                    User user = gson.fromJson(jsonResponse, User.class);

                    // Show the success toast and update the response text view
                    Log.i(Activity1.class.getName(), "User received from echo server : " + user);
                    Toast.makeText(Activity4.this, R.string.json_user_received_success, Toast.LENGTH_SHORT).show();
                    jsonReceived.setText(user.toString());
                    return true;
                }

                @Override
                public void handleServerError(String response) {
                    // Show an error toast and update the response text view
                    Log.i(Activity1.class.getName(), "Error or wrong status code received from echo server : " + response);
                    Toast.makeText(Activity4.this, R.string.message_received_status_fail, Toast.LENGTH_SHORT).show();
                    jsonReceived.setText(response);
                }
            });

            // Update the JSON response text view with the request
            jsonSent.setText(user.toString());
        } catch (Exception e) {
            // In the case of an exception show an error toast
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
