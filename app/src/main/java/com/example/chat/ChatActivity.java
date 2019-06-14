package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private  String MessageReceivedID, messageReceiverName, messageReceiverImage, MessageSenderID;
    private TextView userName,userLastSeen;
    private CircleImageView userImage;
    private Toolbar ChatToolBar;
    private ImageButton SendMessageButton;
    private EditText MessageInputText;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private final List<Messages> messagesList =  new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        MessageReceivedID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
      //  messageReceiverImage = getIntent().getExtras().get("visit_image").toString();
        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        MessageSenderID = mAuth.getCurrentUser().getUid();

        Toast.makeText(this, ""+messageReceiverName, Toast.LENGTH_SHORT).show();

    IntializeControllers();

    userName.setText(messageReceiverName);
    // Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);


        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });

    }

    private void IntializeControllers()
    {


        ChatToolBar = (Toolbar)findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);

        userImage = (CircleImageView)findViewById(R.id.custom_profile_image);
        userName = (TextView)findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView)findViewById(R.id.custom_user_last_seen);
        SendMessageButton = (ImageButton)findViewById(R.id.send_message_btn);
        MessageInputText = (EditText)findViewById(R.id.input_message);


        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView)findViewById(R.id.private_messages_list_of_user);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        RootRef.child("Message").child(MessageSenderID).child(MessageReceivedID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendMessage()
    {
        String messageText = MessageInputText.getText().toString();
        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "write message", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Message/" + MessageSenderID +"/" + MessageReceivedID;
            String messageReceiverRef = "Message/" + MessageReceivedID +"/" + MessageSenderID;

            DatabaseReference  userMessageKey = RootRef.child("Message").child(MessageSenderID)
                    .child(MessageReceivedID).push();

            String messagePushID = userMessageKey.getKey();

            Map MessageTextBody =  new HashMap();
            MessageTextBody.put("message", messageText);
            MessageTextBody.put("type", "text");
            MessageTextBody.put("from", MessageSenderID);

            Map MessageBodyDetail = new HashMap();
            MessageBodyDetail.put(messageSenderRef + "/" + messagePushID, MessageTextBody);
            MessageBodyDetail.put(messageReceiverRef + "/" + messagePushID, MessageTextBody);

            RootRef.updateChildren(MessageBodyDetail).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "sent", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });






        }
    }
}
