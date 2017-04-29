package compsci290.edu.duke.agora;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sendbird.android.BaseMessage;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.UserMessage;

import java.util.ArrayList;


/**
 * Created by dhruvluthra1 on 4/28/17.
 */

public class MessagingActivity extends AppCompatActivity {

    private final String APP_ID = "C6BE868A-87E2-4806-B02B-9E023DA0178E";
    // private  final String APP_ID = "DEFC88FB-F5FE-4EDC-9606-3B35C1EAB123";
    private final String CHANNEL_URL = "forum";
    private final String CONNECTION_HANDLER_ID = "connection_handler_id";
    private final String CHANNEL_HANDLER_ID = "channel_handler_id";

    private String mUser;
    private EditText mMessage;
    private OpenChannel mChannel;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> messageList = new ArrayList<String>();

    private boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        mUser = getIntent().getExtras().getString("username");  // metadata used for chat feature-> needs to be passed through

        SendBird.init(APP_ID, this.getApplicationContext());
        SendBird.connect(mUser, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                Log.d("Message", "connected");
                if (e != null) {
                    Toast.makeText(getApplicationContext(), "Message failed4", Toast.LENGTH_SHORT).show();
                    Log.d("Message", "error: " + e.toString());
                    // error
                    return;
                }
            }
        });


        mMessage = (EditText) findViewById(R.id.message_entry);
        mMessage.setInputType(InputType.TYPE_NULL);

        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMessage.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });

        Button toSend = (Button) findViewById(R.id.send_message);

        toSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendMessage();
            }
        });


        // enterChannel(CHANNEL_URL);

        ListView listView = (ListView) findViewById(R.id.m_list_view);


        adapter = new ArrayAdapter<String>(this, R.layout.activity_messaging, messageList);
        listView.setAdapter(adapter);
        Log.d("Message", "got here");


    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set this as true to restart auto-background detection.
        // This means that you will be automatically disconnected from SendBird when your
        // app enters the background.
        SendBird.setAutoBackgroundDetection(true);

        SendBird.addConnectionHandler(CONNECTION_HANDLER_ID, new SendBird.ConnectionHandler() {
            @Override
            public void onReconnectStarted() {
                Log.d("CONNECTION", "OpenChatFragment onReconnectStarted()");
            }

            @Override
            public void onReconnectSucceeded() {
                Log.d("CONNECTION", "OpenChatFragment onReconnectSucceeded()");
            }

            @Override
            public void onReconnectFailed() {
                Log.d("CONNECTION", "OpenChatFragment onReconnectFailed()");
            }
        });

        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                Toast.makeText(getApplicationContext(), "Message receieved", Toast.LENGTH_SHORT).show();
                // Add new message to view
                if (baseChannel.getUrl().equals(CHANNEL_URL)) {
                    // mChatAdapter.addFirst(baseMessage);

                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

        SendBird.disconnect(new SendBird.DisconnectHandler() {
            @Override
            public void onDisconnected() {

            }
        });
    }

    private void sendMessage() {
        InputMethodManager im = (InputMethodManager) getSystemService(QueueActivity.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        OpenChannel.getChannel(CHANNEL_URL, new OpenChannel.OpenChannelGetHandler(){
            @Override
            public void onResult(OpenChannel openChannel, SendBirdException e) {
                if (e!= null){
                    Toast.makeText(getApplicationContext(), "Message failed1", Toast.LENGTH_SHORT).show();
                    return;
                }

                openChannel.enter(new OpenChannel.OpenChannelEnterHandler(){
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e!= null) {
                            Toast.makeText(getApplicationContext(), "Message failed2", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });

                openChannel.sendUserMessage(mMessage.getText().toString() , null, null, new BaseChannel.SendUserMessageHandler() {
                    @Override
                    public void onSent(UserMessage userMessage, SendBirdException e) {
                        mMessage.getText().clear();
                        Log.d("Message", "message sent: " + userMessage.getMessage());
                        if (e != null) {
                            Log.d("Message", "send error");
                            Toast.makeText(getApplicationContext(), "Message failed3", Toast.LENGTH_SHORT).show();
                            // Error.
                            return;
                        }

                        SendBird.addChannelHandler("read", new SendBird.ChannelHandler() {
                            @Override
                            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                                Log.d("Message", "got received");
                                Log.d("Message", "message: " );
                                if (baseMessage instanceof UserMessage) {
                                    // message is a UserMessage
                                    String message = ((UserMessage) baseMessage).getMessage();
                                    Log.d("Message", message);
                                    messageList.add(message);
                                    adapter.notifyDataSetChanged();
                                }
                                return;
                            }
                        });
                    }
                });
            }
        });
    }

//    private void enterChannel(String channelUrl) {
//        OpenChannel.getChannel(channelUrl, new OpenChannel.OpenChannelGetHandler() {
//            @Override
//            public void onResult(final OpenChannel openChannel, SendBirdException e) {
//                if (e != null) {
//                    // Error!
//                   //  e.printStackTrace();
//                    return;
//                }
//
//                // Enter the channel
//                openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
//                    @Override
//                    public void onResult(SendBirdException e) {
//                        if (e != null) {
//                            // Error!
//                            e.printStackTrace();
//                            return;
//                        }
//
//                        mChannel = openChannel;
//
//                        // loadInitialMessageList(30);
//
//                        // Set action bar title to name of channel
//                        //((OpenChannelActivity) getActivity()).setActionBarTitle(mChannel.getName());
//                    }
//                });
//            }
//        });
//    }
//
//    private void sendUserMessage(String text) {
//        mChannel.sendUserMessage(text, new BaseChannel.SendUserMessageHandler() {
//            @Override
//            public void onSent(UserMessage userMessage, SendBirdException e) {
//                if (e != null) {
//                   return;
//                }
//
//                // Display sent message to RecyclerView
//                // mChatAdapter.addFirst(userMessage);
//            }
//        });
//    }

}






