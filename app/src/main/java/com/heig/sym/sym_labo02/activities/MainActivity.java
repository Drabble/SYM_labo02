package com.heig.sym.sym_labo02.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.heig.sym.sym_labo02.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startActivity1(View v){
        Intent i = new Intent(this, Activity1.class);
        startActivity(i);
    }

    public void startActivity2(View v){
        Intent i = new Intent(this, Activity1.class);
        startActivity(i);
    }

    public void startActivity3(View v){
        Intent i = new Intent(this, Activity3.class);
        startActivity(i);
    }

    public void startActivity4(View v){
        Intent i = new Intent(this, Activity4.class);
        startActivity(i);
    }
}
