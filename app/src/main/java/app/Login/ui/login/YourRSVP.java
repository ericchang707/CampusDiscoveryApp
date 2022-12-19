package app.Login.ui.login;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.Login.R;

public class YourRSVP extends AppCompatActivity{

    //private RecyclerView recyclerView;
    private URL eventSearch;
    private HttpURLConnection connection;
    private List<HashMap<String, String>> eventList;
    private List<String> eventIds;
    private String user;
    private Button toMain;
    RecyclerView events;
    EventsAdapter eventsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_rsvp);
        events = findViewById(R.id.RSVPevents);
        toMain = findViewById(R.id.back_to_main);

        eventsAdapter = new EventsAdapter();
        events.setAdapter(eventsAdapter);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("username");
        }

        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(YourRSVP.this, EventActivity.class);
                intent.putExtra("username", user);
                startActivity(intent);
            }
        });

        try {
            String s = "https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/rsvp/search?username=" + "bob@gmail.com";
            Log.d("Event Search", s);
            eventSearch = new URL(s);
            connection = (HttpURLConnection) eventSearch.openConnection();
            getEventsWithRSVP();
        } catch (Exception e) {

        }

        try {
            String s = "https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/event/search";
            Log.d("Event Search", s);
            eventSearch = new URL(s);
            connection = (HttpURLConnection) eventSearch.openConnection();
            initEvents();
            initTitles();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getEventsWithRSVP() throws IOException, JSONException{
        eventIds = new ArrayList<String>();
        InputStream is = connection.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray json = new JSONArray(jsonText);

            for (int i = 0; i < json.length(); i++) {
                JSONObject o = json.getJSONObject(i);
                Log.d("Event Search", o.toString());
                eventIds.add("" + o.get("event_id"));
            }
            Log.d("Event Search", eventIds.toString());
        } finally {
            is.close();
        }
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONObject readJsonFromUrl() throws IOException, JSONException {
        InputStream is = connection.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private void initEvents() throws JSONException, IOException {
        JSONObject json = readJsonFromUrl();
        Log.d("Event Search", json.toString());
        eventList = new ArrayList<>();
        try {
            JSONArray jsonarray = (JSONArray)json.get("Events");
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject o = jsonarray.getJSONObject(i);
                Iterator<?> iterator = o.keys();
                HashMap<String, String> map = new HashMap<>();
                while(iterator.hasNext()) {
                    Object key = iterator.next();
                    Object value = o.get(key.toString());
                    map.put(key.toString(), value.toString());
                }
                if (eventIds.contains(""+o.get("id"))) {
                    Log.d("Event Search", "Adding " + o.get("id"));
                    eventList.add(map);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    };


    private void initTitles() {
        List<String> titleList = new ArrayList<>();
        Iterator<HashMap<String, String>> iterator = eventList.iterator();
        while (iterator.hasNext()) {
            Map<String, String> m = iterator.next();
            titleList.add(m.get("name"));
        }

        Log.e("Event Search", titleList.toString());
        Log.e("Event Search", eventList.toString());
        Log.e("Event Search", user.toString());

        eventsAdapter.updateItems(titleList, eventList, user);
    }
}
