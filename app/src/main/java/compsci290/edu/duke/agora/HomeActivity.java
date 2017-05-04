package compsci290.edu.duke.agora;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.location.Location;

import java.util.ArrayList;

/**
 * Created by dhruvluthra1 on 4/17/17.
 */

public class HomeActivity extends AppCompatActivity {
    private String mUser;
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    protected void onCreate(Bundle savedInstanceState) {

        mUser = getIntent().getExtras().getString("username");  // metadata used for chat feature-> needs to be passed through

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button toJoin = (Button) findViewById(R.id.joinsession);
        Button toCreate = (Button) findViewById(R.id.createsession);
        Button toQuit = (Button) findViewById(R.id.quit);

        toQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Quit session and return to home screen.
                Toast.makeText(getApplicationContext(), "Logout Successful", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
            }
        });

        toJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Join an existing office hours session as a student.
                Intent sessionIntent = new Intent(getApplicationContext(), SessionActivity.class);
                Bundle mBundle = new Bundle();
                // Save user identity as 'student'.
                mBundle.putString("identity", "student");
                mBundle.putString("username", mUser);
                sessionIntent.putExtras(mBundle);
                startActivity(sessionIntent);
            }
        });
        toCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an office hours session as an instructor.
                Intent sessionIntent = new Intent(getApplicationContext(), SessionActivity.class);
                Bundle mBundle = new Bundle();
                // Save user identity as 'instructor'.
                mBundle.putString("identity", "teacher");
                mBundle.putString("username", mUser);
                sessionIntent.putExtras(mBundle);
                startActivity(sessionIntent);
            }
        });
    }


}

