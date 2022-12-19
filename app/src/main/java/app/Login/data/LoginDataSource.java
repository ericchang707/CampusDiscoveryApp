package app.Login.data;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import app.Login.data.model.LoggedInUser;
import app.Login.ui.login.LoginActivity;

import java.io.IOException;
import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private FirebaseAuth mAuth;

    public Result<LoggedInUser> login(String username, String password) {
        mAuth = FirebaseAuth.getInstance();

        // TODO: handle loggedInUser authentication
        try {
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                            }
                        }
                    });
        } catch(Exception e) {

        }
        if (mAuth.getCurrentUser() != null) {
            LoggedInUser suuccessfulLogin =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            username);
            return new Result.Success<>(suuccessfulLogin);
        } else {
            return new Result.Error(new IOException("Error logging in"));
        }

    }

    public void logout() {
        // TODO: revoke authentication
        mAuth.signOut();
    }
}
