/**
 * Project: Labo 02 SYM
 * Authors: Antoine Drabble & Patrick Djomo
 * Date: 28.11.2016
 */
package com.heig.sym.sym_labo02.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.heig.sym.sym_labo02.R;
import com.heig.sym.sym_labo02.communications.CommunicationEventListener;
import com.heig.sym.sym_labo02.communications.CommunicationManager;

public class Activity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        final TextView message = (TextView)findViewById(R.id.message);

        try {
            CommunicationManager.getInstance().sendRequest(this, "ok", "http://sym.dutoit.email/rest/txt", "CSD", "text/plain", false, 200, new CommunicationEventListener() {
                @Override
                public boolean handleServerResponse(String response) {
                    Log.i(Activity1.class.getName(), "Message received from echo server : " + response);
                    if(response.startsWith("ok")){
                        Toast.makeText(Activity1.this, "Message received from echo server ! ", Toast.LENGTH_SHORT).show();
                        message.setText(response);
                    }
                    else{
                        Toast.makeText(Activity1.this, "Error in the data received from the echo server ! ", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
