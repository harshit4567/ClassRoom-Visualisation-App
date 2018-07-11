package com.example.harshit.post;

/*
<header>
Module Name          : Activity
Date of Creation     : 10-04-2018
Author               : Group 2

Modification History : 11-04-2018 :- added the activity of display classroom

Synopsis             : This is the main activity of the app

Global Variables     : Button showData         button to show students record
                       Button addData          button to add a student
                       Button selectSeat       button to select a seat
                       Button layoutGenerator  button to generate classroom layout
                       Button displayClassroom button to display classroom
                       TextView networkStatus  Text view to show the internet connectivity

Functions            : void addData            function to add a student
                       void showData           function to show students record
                       void selectSeat         function to select a seat
                       void layoutGenerator    function to generate classroom layout
                       void displayClassroom   function to display classroom
</header>
*/

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Activity extends AppCompatActivity {

    Button showData, addData,selectSeat,layoutGenerator,displayClassroom; // declaration of display buttons on the main screen .
    TextView networkStatus;   // declaration of text-view of network status


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);   // Calling the constructor of parent class.
        setContentView(R.layout.activity_);   // Setting the activity content to the main view.

        // ****
        // assigning the display button and text resource to the declared variable
        showData  = findViewById(R.id.showData);
        addData = findViewById(R.id.addData);
        selectSeat = findViewById(R.id.selectSeat);
        layoutGenerator = findViewById(R.id.layoutGenerator);
        displayClassroom = findViewById(R.id.displayClassroom);
        networkStatus = findViewById(R.id.Network_status);
        // ****


        // checking the internet connectivity of the device
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); // fetching network information

        if(networkInfo!=null &&networkInfo.isConnected())
        {
            networkStatus.setVisibility(View.INVISIBLE);
        }
        else
        {
            // disabled buttons if internet is not connected
            showData.setEnabled(false);
            addData.setEnabled(false);
            selectSeat.setEnabled(false);
            displayClassroom.setEnabled(false);
            layoutGenerator.setEnabled(false);

        }
    }

    //****
    // Declaring the on-click screens(activity) of the display buttons
    public void addData(View view)
    {
        startActivity(new Intent(this,addData.class));
    }
    public void showData(View view)
    {
        startActivity(new Intent(this,showData.class));
    }
    public void selectSeat(View view) {startActivity(new Intent(this,selectSeat.class));}
    public void layoutGenerator(View view) {startActivity(new Intent(this,Layout.class));}
    public void displayClassroom(View view) {startActivity(new Intent(this,display.class));}
    //****
}