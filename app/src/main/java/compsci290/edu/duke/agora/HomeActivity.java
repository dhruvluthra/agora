package compsci290.edu.duke.agora;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
/**
 * Created by dhruvluthra1 on 4/17/17.
 */

public class HomeActivity extends AppCompatActivity {

    private String mUser;

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
                Toast.makeText(getApplicationContext(), "Logout Successful", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
            }
        });

        toJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sessionIntent = new Intent(getApplicationContext(), SessionActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("identity", "student");
                mBundle.putString("username", mUser);
                sessionIntent.putExtras(mBundle);
                startActivity(sessionIntent);
            }
        });

        toCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sessionIntent = new Intent(getApplicationContext(), SessionActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("identity", "teacher");
                mBundle.putString("username", mUser);
                sessionIntent.putExtras(mBundle);
                startActivity(sessionIntent);
            }
        });
    }
}
