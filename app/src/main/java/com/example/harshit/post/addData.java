package com.example.harshit.post;

/*
<header>
Module Name          : addData
Date of Creation     : 10-04-2018
Author               : Group 2

Modification History : 11-04-2018 :- added exception for negative student id.

Synopsis             : This module is responsible for adding a student in the record

Global Variables     : EditText ID         Input from the user for student id
                      EditText Name       Input from the user for student name
                      String Student_ID,  Variable to send student id to database
                      String StudentName  Variable to send student name to database

Functions            : void saveInfo       save the student id and the student name to the database
</header>
*/

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
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

public class addData extends AppCompatActivity {

    EditText ID,Name; // declaration of text input on the add data screen .
    String Student_ID,StudentName;   // declaration of Student ID , Student Name .

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);         // Calling the constructor of parent class.
        setContentView(R.layout.activity_add_data); // Setting the activity content to the add data view.

        //***
        // Get the widgets reference from XML layout
        // assigning the display button and text resource to the declared variable
        ID = findViewById(R.id.ID);
        Name = findViewById(R.id.Name);
        //***

    }


    public void saveInfo(View view){
        // fetching the id and the name from the user input
        Student_ID = ID.getText().toString();
        StudentName = Name.getText().toString();
        // Starting the add request to the online database server
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(Student_ID,StudentName);
        finish(); // exit the current screen when data is added .
    }

    public void deleteInfo(View view){
        // fetching the id and the name from the user input
        Toast.makeText(getApplicationContext(),"Delete in progress.. wait for some time ",Toast.LENGTH_LONG).show();
        finish();
    }


    class BackgroundTask extends AsyncTask<String, Void, String> {

        String ADD_DATA_URL;  // url of the database request handling script
        @Override
        protected void onPreExecute(){  // task to be preformed before execution of the request to the server

            ADD_DATA_URL = "https://22harshit.000webhostapp.com/add_info.php";
        }


        @Override
        protected String doInBackground(String... args) {
            String id, name;  // declaring the id and the name from the arguments given to the function
            // fetching the id and the name from the arguments given to the function
            id = args[0];
            name = args[1];
            if (Integer.valueOf(id) >= 0){
                try {
                    //creating a URL
                    URL url = new URL(ADD_DATA_URL);
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
                    String data_string = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") + "&" +
                            URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                    // writing the data to the buffer
                    bufferedWriter.write(data_string);

                    // closing and flushing the connections
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    //finally returning the status string
                    return "One row data inserted";

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            else
            {
                return "Error : negative Student ID";
            }
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


