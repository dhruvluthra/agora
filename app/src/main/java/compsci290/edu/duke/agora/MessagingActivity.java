package compsci290.edu.duke.agora;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.UserMessage;


/**
 * Created by dhruvluthra1 on 4/28/17.
 */

public class MessagingActivity extends AppCompatActivity {

    private String mUser;
    private EditText mMessage;
    private OpenChannel mChannel;

    private String APP_ID = "C6BE868A-87E2-4806-B02B-9E023DA0178E";
    private String CHANNEL_URL = "forum";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        mUser = getIntent().getExtras().getString("username");  // metadata used for chat feature-> needs to be passed through

        SendBird.init(APP_ID, this.getApplicationContext());
        SendBird.connect(mUser, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(getApplicationContext(), "Message failed", Toast.LENGTH_SHORT).show();
                    // error
                    return;
                }
            }
        });


//        OpenChannel.getChannel(CHANNEL_URL, new OpenChannel.OpenChannelGetHandler(){
//            @Override
//            public void onResult(OpenChannel openChannel, SendBirdException e) {
//                if (e!= null){
//                    return;
//                }
//
//                openChannel.enter(new OpenChannel.OpenChannelEnterHandler(){
//                    @Override
//                    public void onResult(SendBirdException e) {
//                        if (e!= null) {
//                            return;
//                        }
//                    }
//                });
//
//                openChannel.sendUserMessage("test", null, null, new BaseChannel.SendUserMessageHandler() {
//                    @Override
//                    public void onSent(UserMessage userMessage, SendBirdException e) {
//                        if (e != null) {
//                            // Error.
//                            return;
//                        }
//                    }
//                });
//            }
//        });

        mMessage = (EditText) findViewById(R.id.message_entry);

        Button toSend = (Button) findViewById(R.id.send_message);

        toSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
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
        OpenChannel.getChannel(CHANNEL_URL, new OpenChannel.OpenChannelGetHandler(){
            @Override
            public void onResult(OpenChannel openChannel, SendBirdException e) {
                if (e!= null){
                    Toast.makeText(getApplicationContext(), "Message failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                openChannel.enter(new OpenChannel.OpenChannelEnterHandler(){
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e!= null) {
                            Toast.makeText(getApplicationContext(), "Message failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });

                openChannel.sendUserMessage(mMessage.getText().toString(), null, null, new BaseChannel.SendUserMessageHandler() {
                    @Override
                    public void onSent(UserMessage userMessage, SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "Message failed", Toast.LENGTH_SHORT).show();
                            // Error.
                            return;
                        }
                    }
                });
            }
        });
    }

}


