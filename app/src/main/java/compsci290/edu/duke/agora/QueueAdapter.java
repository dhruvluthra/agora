package compsci290.edu.duke.agora;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        final String user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the queue entry view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.queue_entry, parent, false);
        }

        // Display student name.
        final TextView name = (TextView) convertView.findViewById(R.id.rowTextView);
        name.setText(user);

        Button delete = (Button)convertView.findViewById(R.id.delete);
        final ImageView iv = (ImageView)convertView.findViewById(R.id.headshot);
        final SharedPreferences pref = mContext.getSharedPreferences("MyPref", 0);
        boolean instructor = pref.getBoolean("Instructor", false);

        // Download user photo from Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference userRef = storageRef.child("images").child(user);

        final long ONE_MEGABYTE = 1024 * 1024;
        userRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Display user photo in queue entry
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv.setImageBitmap(bmp);
            }
        });
        userRef.getBytes(ONE_MEGABYTE).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(mContext, "Image Download Failed", Toast.LENGTH_SHORT).show();

            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Inflate image.
                Intent i = new Intent (getContext(), DisplayActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("PhotoID", user);
                i.putExtras(mBundle);
                getContext().startActivity(i);
            }
        });
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
                                    // Remove deleted entry from Firebase database.
                                    if (student.equals(sName)){
                                        snapshot.getRef().removeValue();
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
