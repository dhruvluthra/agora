package compsci290.edu.duke.agora;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



/**
 * Created by dhruvluthra1 on 4/17/17.
 */

public class HomeActivity extends AppCompatActivity {
    private String mUser;
    private boolean activeSession;
    private boolean instructor;

    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    protected void onCreate(Bundle savedInstanceState) {

        mUser = getIntent().getExtras().getString("username");  // metadata used for chat feature-> needs to be passed through

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button toJoin = (Button) findViewById(R.id.joinsession);
        Button toCreate = (Button) findViewById(R.id.createsession);
        Button toQuit = (Button) findViewById(R.id.quit);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        instructor = pref.getBoolean("Instructor", false);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        toQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Quit session and return to home screen.
                if(instructor){
                    // If an instructor deletes a session, update ActiveSession boolean and session coordinates.
                    mDatabase.child("ActiveSession").setValue("false");
                    mDatabase.child("Longitude").setValue(null);
                    mDatabase.child("Latitude").setValue(null);
                }
                FirebaseAuth.getInstance().signOut();
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
            }
        });

        toJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Join an office hours session as a student, if one exists.
                if(!activeSession){
                    Toast.makeText(getApplicationContext(), "No Active Session", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent sessionIntent = new Intent(getApplicationContext(), SessionActivity.class);
                    Bundle mBundle = new Bundle();
                    // Save user identity as 'student'.
                    mBundle.putString("identity", "student");
                    mBundle.putString("username", mUser);
                    sessionIntent.putExtras(mBundle);
                    startActivity(sessionIntent);
                }
            }
        });

        toCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an office hours session as an instructor.
                Intent sessionIntent = new Intent(getApplicationContext(), SessionActivity.class);
                //Initialize queue in Firebase.
                mDatabase.child("students").setValue("studentsList");
                Bundle mBundle = new Bundle();
                // Save user identity as 'instructor'.
                mBundle.putString("identity", "teacher");
                mBundle.putString("username", mUser);
                sessionIntent.putExtras(mBundle);
                mDatabase.child("ActiveSession").setValue("true");
                startActivity(sessionIntent);
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
    }


}

