/**
 * Project: Labo 02 SYM
 * Authors: Antoine Drabble & Patrick Djomo
 * Date: 28.11.2016
 */
package com.heig.sym.sym_labo02.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.heig.sym.sym_labo02.R;

/**
 * Show the 4 buttons to access the activities
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Handles the creation of the activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Handle the click of the button activity 1 and start the corresponding activity
     *
     * @param v
     */
    public void startActivity1(View v){
        Intent i = new Intent(this, Activity1.class);
        startActivity(i);
    }

    /**
     * Handle the click of the button activity 2 and start the corresponding activity
     *
     * @param v
     */
    public void startActivity2(View v){
        Intent i = new Intent(this, Activity1.class);
        startActivity(i);
    }

    /**
     * Handle the click of the button activity 3 and start the corresponding activity
     *
     * @param v
     */
    public void startActivity3(View v){
        Intent i = new Intent(this, Activity3.class);
        startActivity(i);
    }

    /**
     * Handle the click of the button activity 4 and start the corresponding activity
     *
     * @param v
     */
    public void startActivity4(View v){
        Intent i = new Intent(this, Activity4.class);
        startActivity(i);
    }
}
