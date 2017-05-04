package compsci290.edu.duke.agora;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.UserMessage;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by dhruvluthra1 on 4/28/17.
 */

public class MessagingActivity extends AppCompatActivity {

    private final String APP_ID = "C6BE868A-87E2-4806-B02B-9E023DA0178E";
    private final String CHANNEL_URL = "forum";
    private String mUser;
    private EditText mMessage;
    MessagingAdapter mAdapter;
    private ArrayList<Entry> messageList = new ArrayList<Entry>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        // Get username for discussion forum.
        mUser = getIntent().getExtras().getString("username");  // metadata used for chat feature-> needs to be passed through

        // Initialize and connet to SendBird open channel.
        SendBird.init(APP_ID, this.getApplicationContext());
        SendBird.connect(mUser, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Retrieve and display 30 previous messages.
                getMessages();
            }
        });

        mMessage = (EditText) findViewById(R.id.message_entry);
        // Hide keyboard.
        mMessage.setInputType(InputType.TYPE_NULL);

        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Display keyboard.
                mMessage.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });

        // Send user entered message on 'send' button click.
        Button toSend = (Button) findViewById(R.id.send_message);
        toSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        ListView listView = (ListView) findViewById(R.id.m_list_view);

        mAdapter = new MessagingAdapter(this, messageList);
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // User will be automatically disconnected from SendBird when this app enters the background.
        SendBird.setAutoBackgroundDetection(true);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Disconnect from SendBird open channel.
        SendBird.disconnect(new SendBird.DisconnectHandler() {
            @Override
            public void onDisconnected() {
            }
        });
    }



    private void sendMessage() {
        // Hide keyboard.
        InputMethodManager im = (InputMethodManager) getSystemService(QueueActivity.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        // Fetch and enter SendBird open channel.
        OpenChannel.getChannel(CHANNEL_URL, new OpenChannel.OpenChannelGetHandler() {
            @Override
            public void onResult(OpenChannel openChannel, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(getApplicationContext(), "Message failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "Message failed2", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });


                // Send current message through open channel.
                openChannel.sendUserMessage(mMessage.getText().toString(), null, null, new BaseChannel.SendUserMessageHandler() {
                    @Override
                    public void onSent(UserMessage userMessage, SendBirdException e) {
                        mMessage.getText().clear();
                        // Display current message.
                        Entry currentMessage = new Entry(mUser, userMessage.getMessage());
                        messageList.add(currentMessage);
                        mAdapter.notifyDataSetChanged();
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "Message failed3", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });


                // Add handler for received messages in open channel.
                SendBird.addChannelHandler("read", new SendBird.ChannelHandler() {
                    @Override
                    public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                        if (baseMessage instanceof UserMessage) {
                            // Display delivered user message.
                            String message = ((UserMessage) baseMessage).getMessage();
                            Entry currentMessage = new Entry(((UserMessage) baseMessage).getSender().getUserId(), message);
                            messageList.add(currentMessage);
                            mAdapter.notifyDataSetChanged();
                        }
                        return;
                    }
                });
            }
        });
    }

    private void getMessages() {
        // Fetch and display 30 previous messages.
        OpenChannel.getChannel(CHANNEL_URL, new OpenChannel.OpenChannelGetHandler() {
            @Override
            public void onResult(final OpenChannel openChannel, SendBirdException e) {
                if (e != null) {
                    return;
                }

                openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {
                            return;
                        }
                        PreviousMessageListQuery prevMessageListQuery = openChannel.createPreviousMessageListQuery();
                        prevMessageListQuery.load(30, true, new PreviousMessageListQuery.MessageListQueryResult() {
                            @Override
                            public void onResult(List<BaseMessage> messages, SendBirdException e) {
                                if (e != null) {
                                    return;
                                }
                                for(BaseMessage m : messages) {
                                    Entry currentMessage = new Entry(((UserMessage) m).getSender().getUserId(), ((UserMessage) m).getMessage());
                                    messageList.add(currentMessage);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        });
    }

}





