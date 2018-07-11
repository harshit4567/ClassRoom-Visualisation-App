package com.example.harshit.post;

/*
<header>
Module Name          : seatStatus
Date of Creation     : 10-04-2018
Author               : Group 2

Modification History : 11-04-2018 :- added colour to the last seat selected

Synopsis             : This module is responsible for selecting unoccupied seats by
                      checking their status.

Global Variables     :     TextView ID          declaration of text view  on the seatStatus screen .
                          GridView GridView    declaration of Seat Grid View on the seatStatus screen .
                          Button ConfirmSeat   declaration of Confirm button on the seatStatus screen .
                          String SeatNumber    variable to send seat no. to the database
                          String StudentID     variable to send student id. to the database
                          String SelectedSeat  variable to send seat no. of the seat selected to the database
                          int previousSelected Position  represents the last seat selected

Functions            : void saveInfo             function that saves the information in the database
                      void onItemSelected       function thatfetches the seat no. of the selected seat
                      void getJSON              get seat information in json format from database
                      void loadIntoListView     load the json format array to list view to display
</header>
*/

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Random;

public class seatStatus extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView ID; // declaration of text view  on the seatStatus screen .
    GridView GridView; // declaration of Seat Grid View on the seatStatus screen .
    Button ConfirmSeat; // declaration of Confirm button on the seatStatus screen .
    String SeatNumber;
    String StudentID="";
    String SelectedSeat;
    int previousSelectedPosition=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Calling the constructor of parent class.
        setContentView(R.layout.activity_seat_status);  // Setting the activity content to the add data view.

        // Get the widgets reference from XML layout
        // assigning the display button and text resource to the declared variable
        GridView = (GridView) findViewById(R.id.gridLayout);
        ID = (TextView)findViewById(R.id.Sudent_ID);
        ConfirmSeat = (Button)findViewById(R.id.confirm_seat);

        // extracting the information passed on by previous activity
        Bundle bundle = getIntent().getExtras();
        ID.setText(bundle.getString("ID"));
        StudentID = bundle.getString("ID");

        // Fetching database entries of the seats available
        getJSON("https://22harshit.000webhostapp.com/out_seats.php");

    }

    public void saveInfo(View view){
        // fetching the id and the seat number from the user input
        StudentID = ID.getText().toString();
        SeatNumber = SelectedSeat;
        BackgroundTask backgroundTask = new BackgroundTask();
        // Starting the add request to the online database server
        backgroundTask.execute(StudentID,SeatNumber);
        finish(); // exit the current screen when data is added .

    }


    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        SelectedSeat = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + SelectedSeat, Toast.LENGTH_LONG).show();

    }
    public void onNothingSelected(AdapterView<?> arg0) {
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
                    loadIntoListView(s);
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
        String[] seatStatus = new String[jsonArray.length()];

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj = jsonArray.getJSONObject(i);
            //getting the name from the json object and putting it inside string array
            seatStatus[i] = obj.getString("seat_number") ;

        }

        //the array adapter to load data into list
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, seatStatus);

        //attaching adapter to list view
        GridView.setAdapter(arrayAdapter);
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
                SelectedSeat=Integer.toString(position+1);
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

    }


    class BackgroundTask extends AsyncTask<String, Void, String> {

        String SELECT_SEAT_URL;
        @Override
        protected void onPreExecute(){

            SELECT_SEAT_URL = "http://22harshit.000webhostapp.com/new_seat.php";
        }


        @Override
        protected String doInBackground(String... args) {
            String id,seat_number;
            Random rnd = new Random();
            String state = Integer.toString(rnd.nextInt(10) + 1);
            id = args[0];
            seat_number = args[1];
            try {
                //creating a URL
                URL url  = new URL(SELECT_SEAT_URL);
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
                String data_string = URLEncoder.encode("ID","UTF-8")+"="+URLEncoder.encode(id,"UTF-8")+"&"+
                        URLEncoder.encode("seat_number","UTF-8")+"="+URLEncoder.encode(seat_number,"UTF-8")+"&"+
                        URLEncoder.encode("state","UTF-8")+"="+URLEncoder.encode(state,"UTF-8");
                // writing the data to the buffer
                bufferedWriter.write(data_string);

                // closing and flushing the connections
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedWriter.close();
                outputStream.close();

                // reading the response from server
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder statusMessage = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
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
        protected void onProgressUpdate(Void ... values){
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result)
        {
            // printing the status message
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
        }


    }


}


