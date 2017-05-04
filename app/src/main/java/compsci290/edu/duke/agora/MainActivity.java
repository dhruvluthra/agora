package compsci290.edu.duke.agora;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmail;
    private EditText mPassword;
    private boolean goHomeActivity = false;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button toLogin = (Button) findViewById(R.id.login);
        Button toSignUp = (Button) findViewById(R.id.signup);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("ActiveSession").setValue("false");

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Attempt to log in with user entered username and password.
                mEmail = (EditText) findViewById(R.id.username);
                mPassword = (EditText) findViewById(R.id.password);
                signIn(mEmail.getText().toString(), mPassword.getText().toString());
                Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);

                //save username
                Bundle mBundle = new Bundle();
                mBundle.putString("username", mEmail.getText().toString());
                homeIntent.putExtras(mBundle);

                if (true) {
                    //If Firebase authorizes login, can proceed to next screen.
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(homeIntent);
                }
            }
        });

        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Attempt to create account with user entered username and password.
                mEmail = (EditText) findViewById(R.id.username);
                mPassword = (EditText) findViewById(R.id.password);
                createAccount(mEmail.getText().toString(), mPassword.getText().toString());
                Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                if (true) {
                    //If Firebase authorizes account creation, can proceed to next screen.
                    Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT).show();
                    startActivity(homeIntent);
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signIn(String email, String password) {
        //If email or password not entered
        if (!validateForm()) {
            Toast.makeText(getApplicationContext(), "Not a valid entry!", Toast.LENGTH_SHORT).show();
            goHomeActivity = false;
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // If sign in fails, display a message to the user. If sign in succeeds
                // the user will be able to proceed to the next screen.
                if (!task.isSuccessful()) {
                    goHomeActivity = false;
                    Toast.makeText(getApplicationContext(), "Sign In Failed", Toast.LENGTH_SHORT).show();
                }
                else{
                    goHomeActivity = true;
                }
            }
        });
    }

    private void createAccount(String email, String password) {
        //If email or password not entered
        if (!validateForm()) {
            Toast.makeText(getApplicationContext(), "Not a valid entry!", Toast.LENGTH_SHORT).show();
            goHomeActivity = false;
            return;
        }

        // Attempt to store email and password as valid account in Firebase.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If create account fails, display a message to the user. If create account succeeds
                        // the user will be able to proceed to the next screen, and their user info will be stored
                        // in Firebase for future use.
                        if (!task.isSuccessful()) {
                            goHomeActivity = false;
                            Toast.makeText(getApplicationContext(), "Sign Up Failed", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            goHomeActivity = true;
                        }

                    }
                });
    }

    private boolean validateForm() {
        // Ensure that a username and password have been entered.
        boolean valid = true;
        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

}
