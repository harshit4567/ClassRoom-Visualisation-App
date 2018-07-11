package com.example.harshit.post;

/*
<header>
Module Name          : selectSeat
Date of Creation     : 10-04-2018
Author               : Group 2

Modification History : 11-04-2018 :- added the exception for existing id allocated seat

Synopsis             : This module is responsible for checking the student id and proceed for seat selection

Global Variables     : EditText ID         declaration of text input on the select seat screen .
                      String StudentID    declaration of Student ID .
                      Button selectSeat   button to go for seat selection

Functions            : void checkStudent   check if the student is enrolled in the course or not
                      void seatStatus     fetches seat status and proceed for seat selection
</header>
*/

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class selectSeat extends AppCompatActivity {

    EditText ID; // declaration of text input on the select seat screen .
    String StudentID; // declaration of Student ID .
    Button selectSeat; // declaration of select seat button on the select seat screen . .

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);// Calling the constructor of parent class.
        setContentView(R.layout.activity_select_seat);// Setting the activity content to theselect seat view

        // Get the widgets reference from XML layout
        // assigning the display button and text resource to the declared variable
        ID = (EditText) findViewById(R.id.StudentID);
        selectSeat = (Button) findViewById(R.id.selectSeat);
    }

    public void checkStudent(View view) {
        // fetching the id from the user input
        StudentID = ID.getText().toString();
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(StudentID);
    }

    class BackgroundTask extends AsyncTask<String, Void, String> {

        String CHECK_INFO_URL;

        @Override
        protected void onPreExecute() {

            CHECK_INFO_URL = "https://22harshit.000webhostapp.com/check.php";
        }


        @Override
        protected String doInBackground(String... args) {
            String id;
            id = args[0];
            try {
                //creating a URL
                URL url = new URL(CHECK_INFO_URL);
                //Opening the URL using HttpURLConnection
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                // specifying the request method
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                // creating a output stream
                OutputStream outputStream = httpURLConnection.getOutputStream();
                //We will use a buffered reader to read the string from service
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                // encoding the data string to be sent
                String data_string = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                // writing the data to the buffer
                bufferedWriter.write(data_string);

                // closing and flushing the connections
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedWriter.close();
                outputStream.close();

                // reading the response from server
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder statusMessage = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    statusMessage.append(line).append('\n');
                }
                inputStream.close();
                httpURLConnection.disconnect();
                //finally returning the status string
                return statusMessage.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            // trimming of the data to convert to integer
            if(result!=null) {
                result = result.replaceAll("\\s", "");
                int status = Integer.parseInt(result.replaceAll("[\\D]", ""));
                // checking whether the student id is in the database or not
                if (status == 1) {
                    selectSeat.setEnabled(TRUE);
                } else {
                    // printing the status message
                    Toast.makeText(getApplicationContext(), "Enter a valid Student ID", Toast.LENGTH_LONG).show();
                    selectSeat.setEnabled(FALSE);
                }
            }
            else
                Toast.makeText(getApplicationContext(), "Internet not working", Toast.LENGTH_LONG).show();
        }


    }

    public void seatStatus(View view){ // function displaying available seats for the students
        Bundle bundle = new Bundle(); // creating a information bundle to pass data to the next view
        bundle.putString("ID",ID.getText().toString()); // adding information to the bundle
        Intent intent = new Intent(this,seatStatus.class); // creating a new intent
        intent.putExtras(bundle); // attaching the extra information with the intent
        startActivity(intent); // starting a new intent ( screen )
    }

}


