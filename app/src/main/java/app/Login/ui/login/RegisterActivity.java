package app.Login.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import app.Login.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private Button Btn;
    private ProgressBar progressBar;
    private Spinner spinner;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.passwd);
        Btn = findViewById(R.id.btnregister);
        progressBar = findViewById(R.id.progressbar);
        spinner = (Spinner) findViewById(R.id.Category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



        // Set on Click Listener on Registration button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                registerNewUser();
            }
        });
    }

    private void registerNewUser()
    {

        // show the visibility of progress bar to show loading
        progressBar.setVisibility(View.VISIBLE);

        // Take the value of three strings texts in registration
        String email, password, category;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();
        category = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();


        // create new user or register new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            /*Toast.makeText(getApplicationContext(),
                                            "Registration successful!",
                                            Toast.LENGTH_LONG)
                                    .show();*/

                            // hide the progress bar
                            progressBar.setVisibility(View.GONE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(category).build();
                            user.updateProfile(profileUpdates);

                            mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                @Override
                                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user != null) {
                                        Toast.makeText(getApplicationContext(),
                                                user.getDisplayName(),
                                                Toast.LENGTH_LONG)
                                                .show();

                                        // if the user created intent to login activity
                                        Intent intent
                                                = new Intent(RegisterActivity.this,
                                                FirstFragment.class);
                                        startActivity(intent);
                                    } else {
                                        return;
                                    }
                                }
                            });
                        }
                        else{
                                Exception exception = task.getException();

                                // Registration failed
                                Toast.makeText(
                                        getApplicationContext(),
                                        exception.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();

                                // hide the progress bar
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
    }
}
