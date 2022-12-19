package app.Login.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import app.Login.R;

public class ViewNo extends AppCompatActivity {

    private Button back;
    private URL rsvpSearch;
    private HttpURLConnection connection;
    private List<HashMap<String, String>> userList;
    private int eventId;
    private String user;
    private RecyclerView users;
    StatusAdapter statusAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);
        back = findViewById(R.id.go_back);
        users = findViewById(R.id.attending);

        statusAdapter = new StatusAdapter();
        users.setAdapter(statusAdapter);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("username");
            eventId = Integer.valueOf(extras.getString("eventId"));
        } else {
            eventId = 18;
        }

        try {
            String eventChosen = "https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/rsvp/search?eventid=" + eventId + "&status=0";
            Log.d("View Event", eventChosen);
            rsvpSearch = new URL(eventChosen);
            connection = (HttpURLConnection) rsvpSearch.openConnection();
        } catch(Exception e) {

        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewNo.this, ViewEventActivity.class);
                intent.putExtra("username", user);
                intent.putExtra("eventId", ""+eventId);
                startActivity(intent);
            }
        });

        try {
            initUsers();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initNames();
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
            //Log.d("View Event", "" + ((char)cp));
        }
        return sb.toString();
    }

    public JSONArray readJsonFromUrl() throws IOException, JSONException {
        InputStream is = connection.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            //Log.d("View Event", jsonText.toString());
            JSONArray reader = new JSONArray(jsonText);
            return reader;
        } finally {
            is.close();
        }
    }


    private void initUsers() throws JSONException, IOException {
        JSONArray reader = readJsonFromUrl();
        userList = new ArrayList<>();
        try {
            for (int i = 0; i < reader.length(); i++) {
                JSONObject o = reader.getJSONObject(i);
                Iterator<?> iterator = o.keys();
                HashMap<String, String> map = new HashMap<>();
                while(iterator.hasNext()) {
                    Object key = iterator.next();
                    Object value = o.get(key.toString());
                    map.put(key.toString(), value.toString());
                }
                userList.add(map);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initNames() {
        List<String> nameList = new ArrayList<>();
        Iterator<HashMap<String, String>> iterator = userList.iterator();
        while (iterator.hasNext()) {
            Map<String, String> m = iterator.next();
            nameList.add(m.get("username"));
        }

        Log.d("View Event", nameList.toString());
        Log.d("View Event", userList.toString());
        Log.d("View Event", user.toString());

        statusAdapter.updateItems(nameList, userList, user);
    }
}
