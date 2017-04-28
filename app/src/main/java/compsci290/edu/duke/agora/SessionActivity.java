package compsci290.edu.duke.agora;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/**
 * Created by lisasapozhnikov on 4/24/17.
 */

public class SessionActivity extends AppCompatActivity {

    private boolean instructor;
    private String mUser;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        String id = getIntent().getExtras().getString("identity");

        mUser = getIntent().getExtras().getString("username");  // metadata used for chat feature-> needs to be passed through

        if (id.equals("teacher")){
            instructor = true;
        }
        else{
            instructor = false;
        }

        if (instructor){
            Toast.makeText(getApplicationContext(), "Instructor Permissions", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Student Permissions", Toast.LENGTH_SHORT).show();
        }

        Button toQueue = (Button) findViewById(R.id.queue);
        Button toMessage = (Button) findViewById(R.id.forum);

        toQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent queueIntent = new Intent(getApplicationContext(), QueueActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putBoolean("instructor", instructor);
                queueIntent.putExtras(mBundle);
                startActivity(queueIntent);
            }
        });

        toMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageIntent = new Intent(getApplicationContext(), MessagingActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("username", mUser);
                messageIntent.putExtras(mBundle);
                startActivity(messageIntent);
            }
        });

    }

}
