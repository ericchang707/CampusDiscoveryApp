package app.Login.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import app.Login.R;

public class CreateEventActivity extends AppCompatActivity {

    private Button Btn;
    private EditText titleView, descriptionView, dateView;
    private TimePicker timeChosen;
    private String user;
    private int inviteStatus;
    private Spinner locationSpinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Btn = findViewById(R.id.btncreate);
        titleView = findViewById(R.id.title);
        descriptionView = findViewById(R.id.description);
        locationSpinner = findViewById(R.id.location);
        timeChosen = findViewById(R.id.timechosen);
        dateView = findViewById(R.id.date);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("username");
        }

        // Set on Click Listener on Create button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                createEvent();
            }
        });

        populateLocations();
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_invite:
                if(checked){
                    inviteStatus = 1;
                }
                else {
                    inviteStatus = 0;
                }
                break;
        }
    }

    private void populateLocations() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

    }

    private void createEvent() {
        String date = dateView.getText().toString();
        // Date doesn't match format
        if(!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            Toast.makeText(getApplicationContext(), "Date Incorrect", Toast.LENGTH_LONG).show();
            return;
        }

        int hour = timeChosen.getCurrentHour();
        int min = timeChosen.getCurrentMinute();
        String title, description, location, time = "";
        URL url;
        HttpURLConnection con = null;

        if(hour < 10) {
            time = "0";
        }
        time += hour + ":";

        if(min < 10) {
            time += "0";
        }
        time += min + ":00.000Z";


        title = titleView.getText().toString();
        description = descriptionView.getText().toString();
        location = locationSpinner.getItemAtPosition(locationSpinner.getSelectedItemPosition()).toString();

        // Post Request
        try {
            url = new URL("https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/event/create");
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            String jsonInputString = "{\"name\": \"" + title;
            jsonInputString += "\",\n \"description\": \"" + description;
            jsonInputString += "\",\n \"startDate\": \"" + date + "T" + time;
            jsonInputString += "\",\n \"user\": \"" + user;
            jsonInputString += "\",\n \"location\": \"" + location;
            jsonInputString += "\",\n \"invite_only\": \"" + inviteStatus;
            jsonInputString += "\",\n \"number_of_attendees\": 1";
            jsonInputString += ",\n \"attendee_limit\": 50";
            jsonInputString += "}";

            Log.e("Create Event", jsonInputString);

            OutputStream out = new BufferedOutputStream(con.getOutputStream());
            out.write(jsonInputString.getBytes());
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String response = "";
            String tmp = "";
            while((tmp = in.readLine()) != null) {
                response += tmp;
            }
            in.close();

            Log.e("Create Event", response);
        }
        catch(Exception e) {
            if(e != null) {
                Log.e("Create Event", e.getClass().getSimpleName());
                Log.e("Create Event", e.getMessage());
            } else {
                Log.e("Create Event", "Null exception");
            }
        } finally {
            if(con != null) {
                con.disconnect();
            }
        }

        Toast.makeText(getApplicationContext(), "Event Created", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(CreateEventActivity.this, EventActivity.class);
        intent.putExtra("username", user);
        startActivity(intent);

        return;
    }
}