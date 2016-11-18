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

import com.heig.sym.sym_labo02.R;
import com.heig.sym.sym_labo02.communications.CommunicationEventListener;
import com.heig.sym.sym_labo02.communications.CommunicationManager;

import java.net.HttpURLConnection;

/**
 * Makes a POST request on the echo server with text/plain and outputs the server response in
 * the corresponding field.
 *
 * Comme vu avec l'assistant, l'activité 1 et 2 sont les même êtant donné que l'envoi
 * différé a été simplement rajouté à la classe CommunicationManager qui était utilisée
 * dans l'activité 1. Le CommunicationManager utilisait une AsyncTask pour les communications
 * non différée comme on peut le voir dans la version du CommunicationManager à l'adresse suivante :
 * https://github.com/servietsky777/SYM_labo02/blob/b744166c28422bba3b697232db42163d2c1e189e/app/src/main/java/com/heig/sym/sym_labo02/communications/CommunicationManager.java
 * Comme vu avec le professeur l'envoi différé a été fait avec un Singleton (donc les
 * requêtes seront détruite quand l'application est terminée. Il faudrait utiliser un
 * service pour pallier à ce problème mais ce n'est pas nécessaire pour ce laboratoire).
 * Le Singleton (CommunicationManager) utilise un ScheduledExecutorService afin de
 * créer la requête. Si la requête fait un timeout elle se réinscrira elle même dans
 * le ScheduledExecutorService pour être réexécutée quelques secondes plus tard.
 * L'interface CommunicationEventListener a été quelque peu modifiée afin de rajouter une méthode
 * pour gérer le cas d'une erreur de réponse du serveur.
 */
public class Activity1 extends AppCompatActivity {

    /**
     * Called when creating a new instance of the activity 1.
     * Creates a new communication manager to run a POST request on the echo server containing
     * the text/plain value "ok" and writes the response in the corresponding fields.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        // Retrieve the text view to write the server response
        final TextView message = (TextView)findViewById(R.id.message);

        try {
            // Get the singleton instance of the communication manager and send the request
            CommunicationManager.getInstance().sendRequest(this, "ok", "http://sym.dutoit.email/rest/txt", "CSD", "text/plain", false, HttpURLConnection.HTTP_OK, new CommunicationEventListener() {

                /**
                 * Make a toast containing the request success message and update the text field
                 * with the server response.
                 *
                 * @param response
                 * @return
                 */
                @Override
                public boolean handleServerResponse(String response) {
                    Log.i(Activity1.class.getName(), "Message received from echo server : " + response);

                    // If the response starts with the same value as the request show the toast and update the field
                    if(response.startsWith("ok")){
                        Toast.makeText(Activity1.this, R.string.message_received_success, Toast.LENGTH_SHORT).show();
                        message.setText(response);
                    }
                    // Otherwise show an error output
                    else{
                        Toast.makeText(Activity1.this, R.string.message_received_data_fail, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }

                /**
                 * Handles the server error, makes a toast to indicate that there has been an error
                 * and update the text field with the response.
                 *
                 * @param response
                 */
                @Override
                public void handleServerError(String response) {
                    Log.i(Activity1.class.getName(), "Error or wrong status code received from echo server : " + response);
                    Toast.makeText(Activity1.this, R.string.message_received_status_fail, Toast.LENGTH_SHORT).show();
                    message.setText(response);
                }
            });
        } catch (Exception e) {
            // In the case of an exception show a toast with the exception message
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
