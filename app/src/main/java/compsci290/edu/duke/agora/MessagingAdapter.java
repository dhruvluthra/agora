package compsci290.edu.duke.agora;

import android.widget.ArrayAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by dhruvluthra1 on 4/28/17.
 */


public class MessagingAdapter extends ArrayAdapter<Entry> {
    Context mContext;
    ArrayList<Entry> mEntries;

    public MessagingAdapter(Context context, ArrayList<Entry> entries) {
        super(context, 0, entries);
        mContext = context;
        mEntries = entries;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Entry message = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_entry, parent, false);
        }
        // Display username.
        TextView userName = (TextView) convertView.findViewById(R.id.userTextView);
        userName.setText(message.mUser);

        // Display user message.
        TextView messageText = (TextView) convertView.findViewById(R.id.messageTextView);
        messageText.setText(message.mMessage);

        return convertView;
    }


}
