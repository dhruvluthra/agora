package compsci290.edu.duke.agora;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by lisasapozhnikov on 4/24/17.
 */

public class SessionActivity extends AppCompatActivity {

    private boolean instructor;
    private boolean activeSession;
    private String mUser;
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    Double longitude;
    Double latitude;
    ImageView iv;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 3;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        String id = getIntent().getExtras().getString("identity");
        mUser = getIntent().getExtras().getString("username");

        if (id.equals("teacher")){
            instructor = true;
        }
        else{
            instructor = false;
        }

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("Instructor", instructor);
        editor.putString("UserName", mUser);
        editor.commit();

        if (instructor){
            Toast.makeText(getApplicationContext(), "Instructor Permissions", Toast.LENGTH_SHORT).show();
            // Ask user permission to access current location. Only want to cache current location when
            // an instructor creates a session.
            ActivityCompat.requestPermissions(SessionActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
        }
        else{
            Toast.makeText(getApplicationContext(), "Student Permissions", Toast.LENGTH_SHORT).show();
        }


        Button toQueue = (Button) findViewById(R.id.queue);
        Button toMessage = (Button) findViewById(R.id.forum);

        toQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to queue sign up.
                Intent queueIntent = new Intent(getApplicationContext(), QueueActivity.class);
                startActivity(queueIntent);
            }
        });

        toMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to open discussion forum.
                Intent messageIntent = new Intent(getApplicationContext(), MessagingActivity.class);
                startActivity(messageIntent);
            }
        });

        Button toLocate = (Button) findViewById(R.id.getLocation);

        // Create listeners for recorded coordinates in Firebase.
        mDatabase.child("Longitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                longitude= dataSnapshot.getValue(Double.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                return;
            }
        });
        mDatabase.child("Latitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                latitude= dataSnapshot.getValue(Double.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                return;
            }
        });

        // Add listener for the existance of an active session.
        mDatabase.child("ActiveSession").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class).equals("true")){
                    activeSession = true;
                }
                else{
                    activeSession = false;
                }
                if (instructor && !activeSession){
                    Toast.makeText(getApplicationContext(), "No Active Session", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                return;
            }
        });

        toLocate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Pull up google maps to office hours location, if a session exists.
                if (!activeSession){
                    Toast.makeText(getApplicationContext(), "No Active Session", Toast.LENGTH_SHORT).show();
                }
                else{
                    String geoUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + "Office Hours" + ")";
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }

            });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION:
                // Response to requesting location access permission.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission is granted.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        }
                    }
                    //Access current location
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location == null){
                    }
                    else{
                        //Save current location to Firebase.
                        mDatabase.child("Longitude").setValue(location.getLongitude());
                        mDatabase.child("Latitude").setValue(location.getLatitude());
                    }
                } else {
                    //Permission is denied.
                    Toast.makeText(getApplicationContext(), "Location Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;

        }
    }

}
