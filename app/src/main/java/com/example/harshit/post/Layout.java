package com.example.harshit.post;

/*
<header>
Module Name          : Layout
Date of Creation     : 10-04-2018
Author               : Group 2

Modification History : 11-04-2018 :- added exception for negative no. of seats

Synopsis             : This module is responsible for generating classroom layout

Global Variables     : GridView GridView;       gridview to represent seats
                      private int previousSelectedPosition = -1;    represents previousSelectedPosition
                      EditText EditText;        User input for no. of seats
                      Button GenerateLayout     button to generate classroom layout

Functions            : void onClick    clicking the button will generate the layout based on no. of seats
</header>
*/

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Layout extends AppCompatActivity {

    GridView GridView;
    private int previousSelectedPosition = -1;
    EditText EditText;
    Button GenerateLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Calling the constructor of parent class.
        setContentView(R.layout.activity_layout); // Setting the activity content to the classroom layout view.
        // Get the widgets reference from XML layout
        GridView = findViewById(R.id.gridview);
        EditText = findViewById(R.id.seat_count);
        GenerateLayout = findViewById(R.id.generate);

    }

    public void onClick(View view){

        String seats = EditText.getText().toString();  // extracting the string from the user input
        // Initializing a new String Array
        if(seats.matches(""))
        {
            Toast.makeText(getApplicationContext(),"Number of seats entered is empty",Toast.LENGTH_LONG).show();
            return ;
        }
        int number_of_seats = Integer.parseInt(seats); // converting string to integer
        if(number_of_seats<0)
        {
            Toast.makeText(getApplicationContext(),"Number of seats entered is negative",Toast.LENGTH_LONG).show();
            return ;
        }
        String[] seatInfo = new String[number_of_seats];

        for (int i=0;i<number_of_seats;i++)
        {
            seatInfo[i]=Integer.toString(i+1);
        }

        // Populate a List from Array elements
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, seatInfo);

        GridView.setAdapter(adapter);
        GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text
                String selectedItem = parent.getItemAtPosition(position).toString();

                // Get the current selected view as a TextView
                TextView tv = (TextView) view;

                // Set the current selected item background color
                tv.setBackgroundColor(Color.parseColor("#FF9AD082"));

                // Set the current selected item text color
                tv.setTextColor(Color.BLUE);

                // Get the last selected View from GridView
                TextView previousSelectedView = (TextView) GridView.getChildAt(previousSelectedPosition);

                // If there is a previous selected view exists
                if (previousSelectedPosition != -1)
                {
                    // Set the last selected View to deselect
                    previousSelectedView.setSelected(false);

                    // Set the last selected View background color as deselected item
                    previousSelectedView.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    // Set the last selected View text color as deselected item
                    previousSelectedView.setTextColor(Color.DKGRAY);
                }

                // Set the current selected view position as previousSelectedPosition
                previousSelectedPosition = position;
            }
        });

        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(seats);
    }

    class BackgroundTask extends AsyncTask<String, Void, String> {

        String ADD_SEATS_URL;
        @Override
        protected void onPreExecute(){

            ADD_SEATS_URL = "https://22harshit.000webhostapp.com/add_seat.php";
        }


        @Override
        protected String doInBackground(String... args) {
            String seats;
            seats = args[0];

            try {

                //creating a URL
                URL url  = new URL(ADD_SEATS_URL);
                //Opening the URL using HttpURLConnection
                HttpURLConnection httpURLConnection  = (HttpURLConnection) url.openConnection();
                // specifying the request method
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                // creating a output stream
                OutputStream outputStream = httpURLConnection.getOutputStream();
                //We will use a buffered reader to read the string from service
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                // encoding the data string to be sent
                String data_string = URLEncoder.encode("seats","UTF-8")+"="+URLEncoder.encode(seats,"UTF-8");
                // writing the data to the buffer
                bufferedWriter.write(data_string);

                // closing and flushing the connections
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();
                httpURLConnection.disconnect();

                //finally returning the status string
                return "Layout generated";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void ... values){
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result)
        {
            // printing the success message
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
        }


    }

}
