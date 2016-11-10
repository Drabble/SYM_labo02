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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Activity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        final TextView jsonSent = (TextView)findViewById(R.id.json_sent);
        final TextView jsonReceived = (TextView)findViewById(R.id.json_received);

        final TextView xmlSent = (TextView)findViewById(R.id.xml_sent);
        final TextView xmlReceived = (TextView)findViewById(R.id.xml_received);

        CommunicationManager communicationManagerJSON = new CommunicationManager();
        communicationManagerJSON.setCommunicationEventListener(new CommunicationEventListener() {
            @Override
            public boolean handleServerResponse(String response) {
                JsonParser parser = new JsonParser();
                JsonObject jsonResponse = parser.parse(response).getAsJsonObject();
                Gson gson = new Gson();
                User user = gson.fromJson(jsonResponse, User.class);
                Log.i(Activity1.class.getName(), "User received from echo server : " + user);
                Toast.makeText(Activity3.this, "JSON User received from echo server ! ", Toast.LENGTH_SHORT).show();
                jsonReceived.setText(user.toString());
                return true;
            }
        });
        try {
            Gson g = new Gson();
            User user = new User(1, "antoine", "1234", "antoine.drabble@heig-vd.ch");
            String jsonRequest = g.toJson(user);
            communicationManagerJSON.sendRequest(jsonRequest, "http://sym.dutoit.email/rest/json", "CSD", "application/json");
            jsonSent.setText(user.toString());
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        CommunicationManager communicationManagerXML = new CommunicationManager();
        communicationManagerXML.setCommunicationEventListener(new CommunicationEventListener() {
            @Override
            public boolean handleServerResponse(String response) {
                Log.i(Activity1.class.getName(), "Message received from echo server : " + response);
                if(response.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<!DOCTYPE directory SYSTEM \"http://sym.dutoit.email/directory.dtd\">\n")){
                    Toast.makeText(Activity3.this, "XML Message received from echo server ! ", Toast.LENGTH_SHORT).show();
                    xmlReceived.setText(response);
                }
                else{
                    Toast.makeText(Activity3.this, "Error in the data XML received from the echo server ! ", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;
            docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("User");
            User user = new User(1, "antoine", "1234", "antoine.drabble@heig-vd.ch");
            //doc.appendChild(user);
            communicationManagerXML.sendRequest("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<!DOCTYPE directory SYSTEM \"http://sym.dutoit.email/directory.dtd\">\n" +
                    "<directory />\n", "http://sym.dutoit.email/rest/xml", "CSD", "application/xml");
            xmlSent.setText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<!DOCTYPE directory SYSTEM \"http://sym.dutoit.email/directory.dtd\">\n" +
                    "<directory />\n");
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
