package app.Login.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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

public class CampusMap extends AppCompatActivity {

    private ArrayList<Button> eventButtons;
    private Button back;
    private String user, filter;

    private URL eventSearch;
    private HttpURLConnection connection;
    private List<HashMap<String, String>> eventList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("username");
            filter = "" + extras.getString("filter");
        }
        eventButtons = new ArrayList<>();
        eventList = new ArrayList<>();

        try {
            initEvents();
        } catch (Exception e) {
            e.printStackTrace();
        }

        createLayoutAndDraw();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CampusMap.this, EventActivity.class);
                intent.putExtra("username", user);
                startActivity(intent);
            }
        });
    }

    private void createLayoutAndDraw() {
        float dp = this.getResources().getDisplayMetrics().density;

        ConstraintLayout layout = new ConstraintLayout(this);

        backButton(layout, dp);
        mapDraw(layout, dp);
        drawEventButtons(layout, dp);

        setContentView(layout);

    }

    private void backButton(ConstraintLayout layout, float dp) {
        back = new Button(this);
        back.setText("Back");
        back.setBackgroundColor(getResources().getColor(R.color.purple_200));
        ConstraintLayout.LayoutParams layoutParams = new
                ConstraintLayout.LayoutParams((int) (384*dp), (int) (48*dp));
        layoutParams.setMargins(50, 12, 0, 683);
        back.setLayoutParams((layoutParams));
        layout.addView(back);
    }

    private void mapDraw(ConstraintLayout layout, float dp) {
        ImageView map = new ImageView(this);
        map.setImageResource(R.drawable.map);
        map.setRotation(270);

        ConstraintLayout.LayoutParams layoutParams = new
                ConstraintLayout.LayoutParams((int) (409*dp), (int) (700*dp));
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;

        map.setLayoutParams(layoutParams);
        layout.addView(map);
    }

    private void drawEventButtons(ConstraintLayout layout, float dp) {
        Iterator<HashMap<String, String>> iterator = eventList.iterator();
        while (iterator.hasNext()) {
            Map<String, String> m = iterator.next();
            String eventLocation = m.get("location");
            int id = Integer.parseInt(m.get("id"));

            if(eventLocation.equals("Bobby Dodd Stadium")) {
                eventButtonCreate(layout, dp, 300, 375, "BD",
                        id);
            } else if (eventLocation.equals("Campus Recreation Center")) {
                eventButtonCreate(layout, dp, 50, 285, "CRC",
                        id);
            } else if (eventLocation.equals("STAMPS Fields")) {
                eventButtonCreate(layout, dp, 50, 255, "SF",
                        id);
            } else if (eventLocation.equals("Burger Bowl")) {
                eventButtonCreate(layout, dp, 40, 215, "BB",
                        id);
            } else if (eventLocation.equals("John Lewis Student Center")) {
                eventButtonCreate(layout, dp, 156, 348, "SC",
                        id);
            } else if (eventLocation.equals("Clough Undergraduate Learning Commons")) {
                eventButtonCreate(layout, dp, 230, 310, "CULC",
                        id);
            } else if (eventLocation.equals("West Village")) {
                eventButtonCreate(layout, dp, 85, 170, "WIL",
                        id);
            } else if (eventLocation.equals("Russ Chandler Stadium")) {
                eventButtonCreate(layout, dp, 240, 236, "BALL",
                        id);
            }
        }
    }

    private void eventButtonCreate(ConstraintLayout layout, float dp,
                                   int leftMargin, int topMargin,String text,
                                   int eventId) {
        Button current = new Button(this);

        current.setText(text);
        current.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        current.setBackgroundColor(getResources().getColor(R.color.purple_500));
        ConstraintLayout.LayoutParams layoutParams = new
                ConstraintLayout.LayoutParams((int) (60*dp), (int) (34*dp));
        layoutParams.setMargins((int) (leftMargin*dp), (int) (topMargin*dp), 0, 0);
        //layoutParams.setMarginStart((int) (leftMargin*dp));
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;

        current.setLayoutParams((layoutParams));
        layout.addView(current);

        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CampusMap.this, ViewEventActivity.class);
                intent.putExtra("username", user);
                intent.putExtra("eventId", ""+eventId);
                startActivity(intent);
            }
        });

        eventButtons.add(current);
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
        String url ="https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/event/search";
        if(!filter.equals("null")) {
            url += filter;
        }

        Log.d("Draw Map", url);

        eventSearch = new URL(url);
        connection = (HttpURLConnection) eventSearch.openConnection();

        JSONObject json = readJsonFromUrl();
        JSONArray jsonarray = null;
        try {
            jsonarray = (JSONArray)json.get("Events");
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonObject = jsonarray.getJSONObject(i);
                //Log.e("Draw Map", jsonObject.toString());
                Iterator<?> iterator = jsonObject.keys();
                HashMap<String, String> map = new HashMap<>();
                while (iterator.hasNext()) {
                    Object key = iterator.next();
                    Object value = jsonObject.get(key.toString());
                    map.put(key.toString(), value.toString());
                }
                Log.e("Draw Map", map.toString());
                eventList.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    };
}
