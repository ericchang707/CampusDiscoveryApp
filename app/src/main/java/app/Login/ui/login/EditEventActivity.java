package app.Login.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import app.Login.R;

public class EditEventActivity extends AppCompatActivity {

    private Button Btn;
    private EditText titleView, descriptionView;
    private Spinner locationSpinner;
    private TimePicker timeChosen;
    private int eventId, inviteStatus, attendeeCount, attendeeLimit;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Btn = findViewById(R.id.btnupdate);
        titleView = findViewById(R.id.title);
        descriptionView = findViewById(R.id.description);
        locationSpinner = findViewById(R.id.location);
        timeChosen = findViewById(R.id.timechosen);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.e("Create Event", extras.getString("eventId"));
            eventId = Integer.valueOf(extras.getString("eventId"));
            user = extras.getString("username");
        } else {
            eventId = 18;
        }


        //Populate Existing Event Data
        try {
            populateEvent();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set on Click Listener on Create button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                updateEvent();
            }
        });
    }

    private void populateLocations(String defaultValue) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(defaultValue);
        locationSpinner.setSelection(spinnerPosition);
    }

    private void populateEvent() throws JSONException {
        URL url;
        HttpURLConnection con = null;
        String eventChosen = "https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/event/search?id="+eventId;
        String eventInfo = "";
        // Get request for that particular event.
        try {
            url = new URL(eventChosen);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String tmp = "";
            while((tmp = in.readLine()) != null) {
                eventInfo += tmp;
            }
            in.close();

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

        try {
            JSONObject reader = new JSONObject(eventInfo);
            JSONArray second = (JSONArray)reader.get("Events");
            JSONObject event = second.getJSONObject(0);

            Log.d("Edit Event", event.toString());

            //locationView.setText();
            populateLocations(event.getString("location"));
            descriptionView.setText(event.getString("description"));
            titleView.setText(event.getString("name"));

            inviteStatus = Integer.valueOf(event.getString("invite_only"));
            attendeeCount = Integer.valueOf(event.getString("number_of_attendees"));
            attendeeLimit = Integer.valueOf(event.getString("attendee_limit"));
        } catch (Exception e) {
            Log.e("Create Event", e.toString());
        }
    }

    private void updateEvent() {

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
            url = new URL("https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/event/update");
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            String jsonInputString = "{\"username\": \"" + user;
            jsonInputString += "\",\n \"id\": \"" + eventId;
            jsonInputString += "\",\n \"name\": \"" + title;
            jsonInputString += "\",\n \"description\": \"" + description;
            jsonInputString += "\",\n \"start_date\": \"2022-11-1T" + time;
            jsonInputString += "\",\n \"location\": \"" + location;
            jsonInputString += "\",\n \"invite_only\": \"" + inviteStatus;
            jsonInputString += "\",\n \"number_of_attendees\": \"" + attendeeCount;
            jsonInputString += "\",\n \"attendee_limit\": \"" + attendeeLimit;
            jsonInputString += "\"}";

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

        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(EditEventActivity.this, EventActivity.class);
        intent.putExtra("username", user);
        startActivity(intent);

        return;
    }
}