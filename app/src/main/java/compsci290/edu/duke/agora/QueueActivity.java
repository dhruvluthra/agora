package compsci290.edu.duke.agora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import com.google.firebase.database.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    private static final int WRITE_PERMISSION = 2;
    private static final int REQUEST_IMAGE_CAPTURE=1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    File mFile;
    private String mCurrentPhotoPath = "";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        instructor = getIntent().getExtras().getBoolean("instructor");

        setContentView(R.layout.activity_queue);

        // Ask user permission to access camera.
        ActivityCompat.requestPermissions(QueueActivity.this,
                new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION);

        // Ask user permission to write to external storage.
        ActivityCompat.requestPermissions(QueueActivity.this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        if (instructor){
            //Initialize queue in Firebase.
            mDatabase.child("students").setValue("studentsList");
        }

        //Create listener for students added to queue in Firebase.
        mDatabase.child("students").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                // Update display.
                String value = snapshot.getValue(String.class);
                studentsList.add(value);
                qAdapter.notifyDataSetChanged();
            }

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
                            dispatchTakePictureIntent();

                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                }
            case WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // External storage write permission granted.
                } else {
                    Toast.makeText(getApplicationContext(), "External Write Permission Denied", Toast.LENGTH_SHORT).show();

                }
                return;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the photo file.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("Message", ex.getMessage());
            }
            // Continue only if the file was successfully created.
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                // Dispatch camera intent.
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create a unique image file name. Save camera image in the external files directory.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==REQUEST_IMAGE_CAPTURE&& resultCode==RESULT_OK){
            // Get locally stored image file.
            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            File file = new File(imageUri.getPath());
            mFile = file;
            if (file.exists()){
                final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("Headshot", mCurrentPhotoPath);
                editor.commit();
            }
            //Scan image file to Media Store.
            MediaScannerConnection.scanFile(QueueActivity.this, new String[]{imageUri.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener(){
                public void onScanCompleted(String path, Uri uri){
                    Log.d("Message", "scan completed");
                }
            });

        }
    }

}
