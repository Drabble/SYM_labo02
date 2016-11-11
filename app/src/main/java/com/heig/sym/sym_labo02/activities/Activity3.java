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

import org.jdom2.Attribute;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

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
            communicationManagerJSON.sendRequest(jsonRequest, "http://sym.dutoit.email/rest/json", "CSD", "application/json", false);
            jsonSent.setText(user.toString());
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        CommunicationManager communicationManagerXML = new CommunicationManager();
        communicationManagerXML.setCommunicationEventListener(new CommunicationEventListener() {
            @Override
            public boolean handleServerResponse(String response) {
                Log.i(Activity1.class.getName(), "Message received from echo server : " + response);
                Toast.makeText(Activity3.this, "XML Message received from echo server !", Toast.LENGTH_SHORT).show();
                xmlReceived.setText(response);
                return true;
            }
        });
        try {
            User user = new User(1, "antoine", "1234", "antoine.drabble@heig-vd.ch");

            Document doc = new Document();
            Element root = new Element("user");
            doc.setRootElement(root);
            doc.setDocType(new DocType("Directory", "SYSTEM", "http://sym.dutoit.email/directory.dtd"));
            root.setAttribute(new Attribute("id", String.valueOf(user.getId())));
            root.addContent(new Element("username").setText(user.getUsername()));
            root.addContent(new Element("email").setText(user.getEmail()));
            root.addContent(new Element("password").setText(user.getPassword()));

            XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
            outputter.output(doc, System.out);

            communicationManagerXML.sendRequest("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<!DOCTYPE directory SYSTEM \"http://sym.dutoit.email/directory.dtd\">\n" +
                    "<directory />\n", "http://sym.dutoit.email/rest/xml", "CSD", "application/xml", false);
            xmlSent.setText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<!DOCTYPE directory SYSTEM \"http://sym.dutoit.email/directory.dtd\">\n" +
                    "<directory />\n");

            communicationManagerXML.sendRequest(outputter.outputString(doc), "http://sym.dutoit.email/rest/xml", "CSD", "application/xml", false);
            xmlSent.setText(doc.toString());
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
