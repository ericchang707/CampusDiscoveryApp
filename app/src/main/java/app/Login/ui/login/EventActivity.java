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

public class EventActivity extends AppCompatActivity{

    //private RecyclerView recyclerView;
    private URL eventSearch;
    private HttpURLConnection connection;
    private List<HashMap<String, String>> eventList;
    private String user;
    private Button toMap;
    Button createButton;
    RecyclerView events;
    EventsAdapter eventsAdapter;
    private Button viewMap, filter, registered;

    public EventActivity() throws IOException {
        eventSearch = new URL("https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/event/search");
        connection = (HttpURLConnection) eventSearch.openConnection();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        toMap = findViewById(R.id.view_map);
        createButton = findViewById(R.id.button);
        events = findViewById(R.id.events);
        viewMap = findViewById(R.id.view_map);
        filter = findViewById(R.id.filter);
        registered = findViewById(R.id.registered);

        eventsAdapter = new EventsAdapter();
        events.setAdapter(eventsAdapter);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("username");
        }

        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventActivity.this, CampusMap.class);
                intent.putExtra("username", user);
                intent.putExtra("filter", "null");
                startActivity(intent);
            }
        });


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventActivity.this, CreateEventActivity.class);
                intent.putExtra("username", user);
                startActivity(intent);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventActivity.this, FilterActivity.class);
                intent.putExtra("username", user);
                startActivity(intent);
            }
        });

        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventActivity.this, CampusMap.class);
                intent.putExtra("username", user);
                startActivity(intent);
            }
        });

        registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventActivity.this, YourRSVP.class);
                intent.putExtra("username", user);
                startActivity(intent);
            }
        });

        try {
            initEvents();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initTitles();

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
        eventList = new ArrayList<>();
        JSONArray jsonarray = null;
        try {
            jsonarray = (JSONArray)json.get("Events");
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonObject = jsonarray.getJSONObject(i);
                Log.e("Create Event", jsonObject.toString());
                Iterator<?> iterator = jsonObject.keys();
                HashMap<String, String> map = new HashMap<>();
                while (iterator.hasNext()) {
                    Object key = iterator.next();
                    Object value = jsonObject.get(key.toString());
                    map.put(key.toString(), value.toString());
                }
                eventList.add(map);
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

        Log.e("Create Event", titleList.toString());
        Log.e("Create Event", eventList.toString());
        Log.e("Create Event", user.toString());

        eventsAdapter.updateItems(titleList, eventList, user);
    }
}
