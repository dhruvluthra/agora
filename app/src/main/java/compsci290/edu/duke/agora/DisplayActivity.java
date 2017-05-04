package compsci290.edu.duke.agora;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by lisasapozhnikov on 5/4/17.
 */

public class DisplayActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagedisplay);

        ImageView imageView = (ImageView) findViewById(R.id.imageView1);

        // Get user headshot local path.
        final SharedPreferences pref = DisplayActivity.this.getSharedPreferences("MyPref", 0);
        String path = pref.getString("Headshot", "");

        if (!path.equals("")){
            // If user has a locally stored headshot.
            try{
                // Inflate image view to full screen.
                Uri imageUri = Uri.parse(path);
                File file = new File(imageUri.getPath());
                InputStream ims = new FileInputStream(file);
                imageView.setImageBitmap(BitmapFactory.decodeStream(ims));

            }  catch (FileNotFoundException e){

            }
        }
    }

}
