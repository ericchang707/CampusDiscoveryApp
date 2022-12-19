package app.Login.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.metrics.Event;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.Login.R;

public class JoinEvent extends AppCompatActivity {
    private Button join;
    private Button maybe;
    private Button no;
    private Button remove;
    private int eventId;
    private int currentEventStatus=-1;
    private String user;
    private TextView textView;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_event);
        join = findViewById(R.id.will_attend);
        maybe = findViewById(R.id.might_attend);
        no = findViewById(R.id.will_not_attend);
        back = findViewById(R.id.back);
        remove = findViewById(R.id.removeRSVP);
        textView = (TextView)findViewById(R.id.textView2);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.e("Update RSVP", extras.getString("eventId"));
            eventId = Integer.valueOf(extras.getString("eventId"));
            user = extras.getString("username");
        } else {
            eventId = 18;
        }

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus(2);
            }
        });

        maybe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus(1);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus(0);
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeStatus();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JoinEvent.this, EventActivity.class);
                intent.putExtra("username", user);
                startActivity(intent);
            }
        });

        try{
            updateView();
        } catch(Exception e) {

        }
    }

    private void updateView() throws JSONException{
            URL url;
            HttpURLConnection con = null;
            String eventChosen = "https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/rsvp/search?eventid="+eventId;
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
                    Log.e("FETCH RSVP", e.getClass().getSimpleName());
                    Log.e("FETCH RSVP", e.getMessage());
                } else {
                    Log.e("FETCH RSVP", "Null exception");
                }
            } finally {
                if(con != null) {
                    con.disconnect();
                }
            }

            Log.e("FETCH RSVP", eventInfo);

            currentEventStatus=-1;
            try {
                JSONArray reader = new JSONArray(eventInfo);
                Log.e("FETCH RSVP", ""+reader.length());

                for(int i = 0; i < reader.length(); i++) {
                    JSONObject event = reader.getJSONObject(i);
                    Log.e("FETCH RSVP", event.getString("username") + " || " + user);
                    if(event.getString("username").equals(user)) {
                        currentEventStatus = event.getInt("status");
                        Log.e("FETCH RSVP", "The found status is: "+currentEventStatus);
                        break;
                    }
                }

                if(currentEventStatus==2) {textView.setText("Will attend"); }
                else if(currentEventStatus==1) {textView.setText("Might attend"); }
                else if(currentEventStatus==0) {textView.setText("Will not attend"); }
                else {textView.setText("No RSVP Sent."); }

            } catch (Exception e) {
                Log.e("Update RSVP", e.toString());
            }
        }

    private void removeStatus() {
        URL url;
        HttpURLConnection con = null;
        String eventChosen = "https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/rsvp/delete";
        // Get request for that particular event.
        try {
            url = new URL(eventChosen);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            String jsonInputString = "{\"username\": \"" + user;
            jsonInputString += "\",\n \"event_id\": \"" + eventId + "\"}";

            Log.e("Update RSVP", jsonInputString);

            OutputStream out = new BufferedOutputStream(con.getOutputStream());
            out.write(jsonInputString.getBytes());
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String eventInfo = "";
            String tmp = "";
            while((tmp = in.readLine()) != null) {
                eventInfo += tmp;
            }
            in.close();
            Log.e("Update RSVP", eventInfo);

        }catch(Exception e) {
            if(e != null) {
                Log.e("Update RSVP", e.getClass().getSimpleName());
                Log.e("Update RSVP", e.getMessage());
            } else {
                Log.e("Update RSVP", "Null exception");
            }
        } finally {
            if(con != null) {
                con.disconnect();
            }
        }

        currentEventStatus=-1;

        try {
            updateView();
        } catch (Exception e) {

        }

    }

    private void updateStatus(int status) {
        // Post Request
        URL url;
        HttpURLConnection con = null;

        try {
            if(currentEventStatus==-1) {
                url = new URL("https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/rsvp/create");
            }
            else {
                url = new URL("https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/rsvp/update");
            }
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            String jsonInputString = "{\"username\": \"" + user;
            jsonInputString += "\",\n \"event_id\": \"" + eventId;
            jsonInputString += "\",\n \"status\": \"" + status + "\"}";

            Log.e("Update RSVP", jsonInputString);

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

            if(!response.contains("Success")) {
                Toast.makeText(getApplicationContext(), "No Invitation", Toast.LENGTH_LONG).show();
            }

            if(timeConflict()) {
                removeStatus();
                Toast.makeText(getApplicationContext(), "Time Conflict", Toast.LENGTH_LONG).show();
            }

            Log.e("Update RSVP", response);
        }
        catch(Exception e) {
            if(e != null) {
                Log.e("Update RSVP", e.getClass().getSimpleName());
                Log.e("Update RSVP", e.getMessage());
            } else {
                Log.e("Update RSVP", "Null exception");
            }
        } finally {
            if(con != null) {
                con.disconnect();
            }
        }
        currentEventStatus=status;

        try {
            updateView();
        } catch (Exception e) {

        }
    }

    public boolean timeConflict() throws MalformedURLException, IOException {
        URL url;
        HttpURLConnection con = null;
        url = new URL("https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/rsvp/conflict");
        con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        String jsonInputString = "{\"username\": \"" + user;
        jsonInputString += "\"}";

        Log.e("Update RSVP", jsonInputString);

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

        if(!response.contains("no conflicts")) {
            return true;
        }
        return false;

    }
}
