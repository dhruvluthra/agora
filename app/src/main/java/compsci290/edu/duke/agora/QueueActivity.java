package compsci290.edu.duke.agora;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import com.google.firebase.database.*;

import java.util.ArrayList;
import android.view.View;

/**
 * Created by lisasapozhnikov on 4/25/17.
 */

public class QueueActivity extends AppCompatActivity {

    ArrayList<String> studentsList = new ArrayList<String>();
    QueueAdapter qAdapter;
    private boolean instructor;
    private int queueLength = 2;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        instructor = getIntent().getExtras().getBoolean("instructor");

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("Instructor", instructor);
        editor.commit();

        setContentView(R.layout.activity_queue);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if (instructor){
            mDatabase.setValue("students");
        }


        mDatabase.child("students").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = snapshot.getValue(String.class);
                studentsList.add(value);
                    qAdapter.notifyDataSetChanged();

            }

            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = snapshot.getValue(String.class);
                studentsList.add(value);
                    qAdapter.notifyDataSetChanged();
            }

            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                return;
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot){
                return;
            }

            public void onCancelled(DatabaseError error) {
                return;
            }
        });

        ListView listView = (ListView) findViewById(R.id.list_view);


        qAdapter = new QueueAdapter(this, studentsList);
        listView.setAdapter(qAdapter);
        

        final EditText add = (EditText) findViewById(R.id.add);
        add.setInputType(InputType.TYPE_NULL);
        Button enter = (Button) findViewById(R.id.enter);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queueLength++;
                mDatabase.child("students").child("Student " + queueLength).setValue(add.getText().toString());
                add.getText().clear();
                InputMethodManager im = (InputMethodManager) getSystemService(QueueActivity.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

    }

//    public void myClickHandler(View v) {
//
//        Log.d("QueueActivity", "button press");
//    }

}
