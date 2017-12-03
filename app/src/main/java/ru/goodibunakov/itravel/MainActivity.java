package ru.goodibunakov.itravel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //очищение SharedPreferences в момент создания новой поездки
                getSharedPreferences("preferencePersons", Context.MODE_PRIVATE).edit().clear().apply();
                Intent intent = new Intent(MainActivity.this, CreateNewTripActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onBackPressed() {
    }
}
