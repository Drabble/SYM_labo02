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

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.net.HttpURLConnection;

/**
 * Sends a JSON and an XML request on the echo server and outputs the responses in the
 * corresponding fields
 */
public class Activity3 extends AppCompatActivity {

    /**
     * On creation of the activity, run the two POST requests and handle the response.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        // Retrieve the TextViews containing server requests and responses
        final TextView jsonSent = (TextView)findViewById(R.id.json_sent);
        final TextView jsonReceived = (TextView)findViewById(R.id.json_received);
        final TextView xmlSent = (TextView)findViewById(R.id.xml_sent);
        final TextView xmlReceived = (TextView)findViewById(R.id.xml_received);

        try {
            // Create a new User and serialize it to JSON with the Gson library
            Gson g = new Gson();
            User user = new User(1, "antoine", "1234", "antoine.drabble@heig-vd.ch");
            String jsonRequest = g.toJson(user);

            // Send the JSON request to the server
            CommunicationManager.getInstance().sendRequest(this, jsonRequest, "http://sym.dutoit.email/rest/json", "CSD", "application/json", false, HttpURLConnection.HTTP_OK, new CommunicationEventListener() {

                /**
                 * Handle the server response. Shows a success toast and update the response text view
                 *
                 * @param response
                 * @return
                 */
                @Override
                public boolean handleServerResponse(String response) {

                    // Deserialize the received JSON to a user and output its value
                    JsonParser parser = new JsonParser();
                    JsonObject jsonResponse = parser.parse(response).getAsJsonObject();
                    Gson gson = new Gson();
                    User user = gson.fromJson(jsonResponse, User.class);

                    Log.i(Activity1.class.getName(), "User received from echo server : " + user);

                    // Make a toast indicating the success and set the response text view
                    Toast.makeText(Activity3.this, R.string.json_user_received_success, Toast.LENGTH_SHORT).show();
                    jsonReceived.setText(user.toString());
                    return true;
                }

                /**
                 * Handle the server error by showing an error toast and set the response to the text view
                 *
                 * @param response
                 */
                @Override
                public void handleServerError(String response) {
                    Log.i(Activity1.class.getName(), "Error or wrong status code received from echo server : " + response);
                    Toast.makeText(Activity3.this, R.string.message_received_status_fail, Toast.LENGTH_SHORT).show();
                    jsonReceived.setText(response);
                }
            });
            // Set the request text view with the user
            jsonSent.setText(user.toString());
        } catch (Exception e) {
            // In the case of an exception, show an error toast
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try {
            // Create a new XML object
            Document doc = new Document();
            Element root = new Element("directory");
            doc.setRootElement(root);
            doc.setDocType(new DocType("directory", "http://sym.dutoit.email/directory.dtd"));

            // Transform the XML object to a String
            XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
            String xml = outputter.outputString(doc);

            // Set the text of the XML request in the text view
            xmlSent.setText(xml);

            // Send the XML request to the echo server
            CommunicationManager.getInstance().sendRequest(this, xml, "http://sym.dutoit.email/rest/xml", "CSD", "application/xml", false, HttpURLConnection.HTTP_OK, new CommunicationEventListener() {

                /**
                 * Handle the server response. Shows a success toast and update the response text view
                 *
                 * @param response
                 * @return
                 */
                @Override
                public boolean handleServerResponse(String response) {
                    Log.i(Activity1.class.getName(), "Message received from echo server : " + response);
                    Toast.makeText(Activity3.this, R.string.message_received_success, Toast.LENGTH_SHORT).show();
                    xmlReceived.setText(response);
                    return true;
                }

                /**
                 * Handle the server error by showing an error toast and set the response to the text view
                 *
                 * @param response
                 */
                @Override
                public void handleServerError(String response) {
                    Log.i(Activity1.class.getName(), "Error or wrong status code received from echo server : " + response);
                    Toast.makeText(Activity3.this, R.string.message_received_status_fail, Toast.LENGTH_SHORT).show();
                    xmlReceived.setText(response);
                }
            });
        } catch (Exception e) {
            // In the case of an exception, show an error toast
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
