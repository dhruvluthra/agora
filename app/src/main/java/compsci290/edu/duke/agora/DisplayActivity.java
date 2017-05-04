package compsci290.edu.duke.agora;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by lisasapozhnikov on 5/4/17.
 */

public class DisplayActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagedisplay);

        final ImageView imageView = (ImageView) findViewById(R.id.imageView1);

        Intent intent = getIntent();
        String mUser = intent.getStringExtra("PhotoID");

        if (!mUser.equals("")){
            // If username is not null, download user photo from Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference userRef = storageRef.child("images").child(mUser);
            final long ONE_MEGABYTE = 1024 * 1024;
            userRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bmp);
                }
            });
            userRef.getBytes(ONE_MEGABYTE).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(getApplicationContext(), "Image Download Failed", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

}
