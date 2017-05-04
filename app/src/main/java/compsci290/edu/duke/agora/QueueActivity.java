package compsci290.edu.duke.agora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.view.View;
import android.widget.Toast;

/**
 * Created by lisasapozhnikov on 4/25/17.
 */

public class QueueActivity extends AppCompatActivity {

    ArrayList<String> studentsList = new ArrayList<String>();
    QueueAdapter qAdapter;
    private boolean instructor;
    private int queueLength = 2;
    private static final int CAMERA_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE=1;
    private EditText add;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        instructor = pref.getBoolean("Instructor", false);

        setContentView(R.layout.activity_queue);

        // Ask user permission to access camera.
        ActivityCompat.requestPermissions(QueueActivity.this,
                new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        //Create listener for students added to queue in Firebase database.
        mDatabase.child("students").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                // Update display.
                String value = snapshot.getValue(String.class);
                studentsList.add(value);
                qAdapter.notifyDataSetChanged();
            }

            //Create listener for students updated in Firebase database.
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                // Update display;
                String value = snapshot.getValue(String.class);
                studentsList.add(value);
                qAdapter.notifyDataSetChanged();
            }

            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                return;
            }

            @Override
            //Create listener for students removed from Firebase database.
            public void onChildRemoved(DataSnapshot snapshot){
                String value = snapshot.getValue(String.class);
                studentsList.remove(value);
                qAdapter.notifyDataSetChanged();
            }

            public void onCancelled(DatabaseError error) {
                return;
            }
        });

        ListView listView = (ListView) findViewById(R.id.list_view);
        qAdapter = new QueueAdapter(this, studentsList);
        listView.setAdapter(qAdapter);
        add = (EditText) findViewById(R.id.add);
        // Hide keyboard.
        add.setInputType(InputType.TYPE_NULL);

        Button enter = (Button) findViewById(R.id.enter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Display keyboard.
                add.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queueLength++;
                // Update Firebase database with user entered name.
                mDatabase.child("students").child("Student " + queueLength).setValue(add.getText().toString());
                add.getText().clear();
                // Hide keyboard.
                InputMethodManager im = (InputMethodManager) getSystemService(QueueActivity.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Camera permission granted. User can take photo of themselves.
                    ImageButton camera = (ImageButton)findViewById(R.id.camera);
                    camera.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            // Invoke camera intent.
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // On Image Capture, retrieve photo
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataBytes = baos.toByteArray();

            // Upload photo to Firebase storage.
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference userRef = storageRef.child("images").child(add.getText().toString());
            UploadTask uploadTask = userRef.putBytes(dataBytes);

            // Handle upload failure.
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
    }

}
