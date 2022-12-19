package app.Login.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class ViewEventActivity extends AppCompatActivity {


    private Button Btn, Yes, Maybe, No;

    private TextView titleView, descriptionView, locationView, timeView, hostView, rsvpView;
    private int eventId;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Btn = findViewById(R.id.btnGoBack);
        Yes = findViewById(R.id.viewYes);
        Maybe = findViewById(R.id.viewMaybe);
        No = findViewById(R.id.viewNo);
        titleView = findViewById(R.id.title);
        descriptionView = findViewById(R.id.description);
        locationView = findViewById(R.id.location);
        timeView = findViewById(R.id.timechosen);
        hostView = findViewById(R.id.host);
        rsvpView = findViewById(R.id.rsvp);

        Yes = findViewById(R.id.viewYes);
        No = findViewById(R.id.viewNo);
        Maybe = findViewById(R.id.viewMaybe);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //Log.e("Create Event", extras.getString("eventId"));
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

        //Log.d("View Event", ""+eventId);

        // Set on Click Listener on Create button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ViewEventActivity.this, EventActivity.class);
                intent.putExtra("username", user);
                startActivity(intent);
            }
        });

        Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewEventActivity.this, ViewYes.class);
                intent.putExtra("username", user);
                intent.putExtra("eventId", ""+eventId);
                startActivity(intent);
            }
        });

        Maybe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewEventActivity.this, ViewMaybe.class);
                intent.putExtra("username", user);
                intent.putExtra("eventId", ""+eventId);
                startActivity(intent);
            }
        });

        No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewEventActivity.this, ViewNo.class);
                intent.putExtra("username", user);
                intent.putExtra("eventId", ""+eventId);
                startActivity(intent);
            }
        });
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

            Log.e("Create Event", event.toString());

            locationView.setText("Event Location: " + event.getString("location"));
            descriptionView.setText("Event Description:\n" + event.getString("description"));
            titleView.setText("Event Title: " + event.getString("name"));
            hostView.setText("Host: " + event.getString("created_by_user"));
            rsvpView.setText("Capacity: " + event.getString("attendee_limit") +
                    ", Attending: " + event.getString("number_of_attendees"));

            String time = event.getString("start_date");
            time = time.substring(time.indexOf("T")+1, time.indexOf(":00.000Z"));

            timeView.setText("Time: " + time);
            //Log.e("Create Event", time);

        } catch (Exception e) {
            Log.e("Create Event", e.toString());
        }

        //Set text items to match that.

    }

}