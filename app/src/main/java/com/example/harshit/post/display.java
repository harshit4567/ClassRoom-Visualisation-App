package com.example.harshit.post;

/*
<header>
Module Name          : display
Date of Creation     : 10-04-2018
Author               : Group 2

Modification History : 11-04-2018 :- added try catch statements to handle crashing of the app

Synopsis             : This module is responsible for displaying the classroom

Global Variables     : GridView GridView   gridview to represent seats

Functions            : void getJSON          get seat information in json format from database
                      void loadIntoListView load the json format array to list view to display
</header>
*/


import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class display extends AppCompatActivity {

    GridView GridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Calling the constructor of parent class.
        setContentView(R.layout.activity_display); // Setting the activity content to the display view

        // Get the widgets reference from XML layout
        // assigning the display button and text resource to the declared variable
        GridView = (GridView)findViewById(R.id.display);

        getJSON("https://22harshit.000webhostapp.com/display.php");  // Fetching database entries of the seats information  database
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
                if(s!=null) {

                    try {
                        loadIntoListView(s);
                    } catch (JSONException e) { // try catch exception for JSON
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Internet not working", Toast.LENGTH_LONG).show();
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
        final JSONArray jsonArray = new JSONArray(json);

        //creating a string array for listview
        String[] SeatsInfo = new String[jsonArray.length()];

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject seat = jsonArray.getJSONObject(i);
            //getting the name from the json object and putting it inside string array
            SeatsInfo[i] = seat.getString("seat_number") +" : "+ seat.getString("student_ID");
            Log.i("data",SeatsInfo[i]);
        }

        //the array adapter to load data into list
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SeatsInfo);

        //attaching adapter to listview
        GridView.setAdapter(arrayAdapter);

        GridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final int size = GridView.getChildCount();
                        for(int i = 0; i < size; i++) {

                            TextView gridChild = (TextView) GridView.getChildAt(i);
                            try {
                                if(Integer.parseInt(jsonArray.getJSONObject(i).getString("status")) == 0) {
                                    gridChild.setBackgroundColor(Color.GRAY);
                                }
                                else
                                {
                                    // fetching  the state of the student of the selected seat
                                    int state = Integer.parseInt(jsonArray.getJSONObject(i).getString("state"));
                                    // assigning color codes to the seats based on the state value
                                    if(state<=4)
                                    {
                                        gridChild.setBackgroundColor(Color.RED);
                                    }
                                    else if(state<=7) // for state value in between 5 and 7
                                    {
                                        gridChild.setBackgroundColor(Color.BLUE);
                                    }
                                    else  // i.e for state values >7
                                    {
                                        gridChild.setBackgroundColor(Color.GREEN);
                                    }

                                }
                            } catch (JSONException e) { // avoid error if Json array is NULL
                                e.printStackTrace();
                            }

                        }


                    }
                }
        );

    }

}

