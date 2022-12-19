package app.Login.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import app.Login.R;

public class FilterActivity extends AppCompatActivity {
    private EditText firstDate, host, location;
    private Button confirm;
    private String filter = "";
    private String user;
    private String firstDatePart = "", hostPart = "", locationPart = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        firstDate = findViewById(R.id.firstDate);
        host = findViewById(R.id.host);
        location = findViewById(R.id.location);
        confirm = findViewById(R.id.confirm);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("username");
        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, FilteredEventActivity.class);
                updateFilter();
                intent.putExtra("filter", filter);
                intent.putExtra("username", user);
                startActivity(intent);
            }
        });


    }

    private void updateFilter() {
        if (firstDate.getText().length() != 0) {
            firstDatePart = "firstdate=" + firstDate.getText().toString();
            filter += ("?" + firstDatePart);
        }
        if (host.getText().length() != 0) {
            hostPart = "host=" + host.getText().toString();
            if (filter.length() != 0) {
                filter += ("&" + hostPart);
            } else {
                filter += ("?" + hostPart);
            }
        }
        if (location.getText().length() != 0) {
            locationPart = "location=" + location.getText().toString();
            if (filter.length() != 0) {
                filter += ("&" + locationPart);
            } else {
                filter += ("?" + locationPart);
            }
        }

    }
}
