package com.example.harshit.post;

/*
<header>
Module Name          : showData
Date of Creation     : 10-04-2018
Author               : Group 2

Modification History : 11-04-2018 :- added try catch statements for error handling

Synopsis             : This module is responsible for showing student record.

Global Variables     : ListView listView        - View to show the existing students enrolled in the course


Functions            : void getJSON              get student information in json format from database
                      void loadIntoListView     load the json format array to list view to display student record
</header>
*/

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class showData extends AppCompatActivity {

    ListView listView ; // declaration of list view on the show data screen .

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_show_data);    // Setting the activity content to the add data view.
        super.onCreate(savedInstanceState);             // Calling the constructor of parent class
        listView = findViewById(R.id.student_list);     // assigning the list view resource to the declared variable
        getJSON("https://22harshit.000webhostapp.com/out.php"); // Fetching database entries of the students enrolled
    }

    private void getJSON(final String urlWebService) {
       /*
       * As fetching the json string is a network operation
       * And we cannot perform a network operation in main thread
       * so we need an AsyncTask
       * The constrains defined here are
       * Void -> We are not passing anything
       * Void -> Nothing at progress update as well
       * String -> After completion it should return a string and it will be the json string
       * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if(s!=null)
                    {
                        loadIntoListView(s);
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Internet connection is not working ", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {



                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json).append("\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadIntoListView(String json) throws JSONException {
        //creating a json array from the json string
        JSONArray jsonArray = new JSONArray(json);

        //creating a string array for listview
        String[] studentdataList = new String[jsonArray.length()];

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject student = jsonArray.getJSONObject(i);
            //getting the name from the json object and putting it inside string array
            studentdataList[i] = student.getString("ID") + " : " + student.getString("Name");
            Log.i("data",studentdataList[i]);
        }

        //the array adapter to load data into list
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, studentdataList);

        //attaching adapter to listview
        Log.i("data",arrayAdapter.toString());
        listView.setAdapter(arrayAdapter);
    }

}


