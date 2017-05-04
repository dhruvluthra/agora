package compsci290.edu.duke.agora;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by lisasapozhnikov on 4/27/17.
 */

public class QueueAdapter extends ArrayAdapter<String> {
    Context mContext;
    final DatabaseReference mDatabase;
    ArrayList<String> list;

    public QueueAdapter(Context context, ArrayList<String> studentsList) {
        super(context, 0, studentsList);
        mContext = context;
        list = studentsList;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position.
        String user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the queue entry view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.queue_entry, parent, false);
        }

        // Display student name.
        final TextView name = (TextView) convertView.findViewById(R.id.rowTextView);
        name.setText(user);

        Button delete = (Button)convertView.findViewById(R.id.delete);
        ImageView iv = (ImageView)convertView.findViewById(R.id.headshot);
        final SharedPreferences pref = mContext.getSharedPreferences("MyPref", 0);
        boolean instructor = pref.getBoolean("Instructor", true);
        String path = pref.getString("Headshot", "");

        if (!path.equals("")){
            // If user has chosen to include a photo with their queue entry.
            try{
                // Display their photo next to their name in the queue.
                Uri imageUri = Uri.parse(path);
                File file = new File(imageUri.getPath());
                InputStream ims = new FileInputStream(file);
                iv.setImageBitmap(BitmapFactory.decodeStream(ims));
                iv.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Inflate image.
                        Intent i = new Intent (getContext(), DisplayActivity.class);
                        getContext().startActivity(i);
                    }
                });
            }  catch (FileNotFoundException e){

            }
        }
        if (!instructor){
            // If student, remove deleting capabilities.
            delete.setVisibility(View.INVISIBLE);
        }
        else{
            delete.setOnClickListener(new View.OnClickListener() {
                // Allow instructor to delete names from the queue.
                boolean del = true;
                @Override
                public void onClick(View view) {
                    mDatabase.child("students").addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (del){
                                String sName = name.getText().toString();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String student = snapshot.getValue(String.class);
                                    // Remove deleted entry from Firebase database and local display.
                                    if (student.equals(sName)){
                                        snapshot.getRef().removeValue();
                                        list.remove(sName);
                                        notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            return;
                        }
                    });
                }
            });

        }

        // Return the completed view to render on screen.
        return convertView;
    }
}
