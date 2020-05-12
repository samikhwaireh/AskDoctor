package com.example.askdoctors.Activities.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.askdoctors.Activities.Adapter.MessagesAdapter;
import com.example.askdoctors.Activities.Model.Doctors;
import com.example.askdoctors.Activities.Model.Message;
import com.example.askdoctors.Activities.Model.User;
import com.example.askdoctors.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView profile_image_;
    private EditText message_text;
    private TextView name;
    private RecyclerView recyclerView;

    MessagesAdapter messagesAdapter;
    List<Message> mMessage;
    String userId, accType, imageUrl;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;

    private ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        profile_image_ = findViewById(R.id.profile_image_chat);
        name = findViewById(R.id.name_user_chat);
        ImageView back_arrow = findViewById(R.id.back_message);
        ImageView send = findViewById(R.id.send_message_btn);
        message_text = findViewById(R.id.message_Text);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycle_chat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        accType = intent.getStringExtra("accType");

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        getUserInfo(name);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = message_text.getText().toString().trim();
                if(!msg.equals("")){
                    sendMessage(msg, firebaseUser.getUid(), userId);
                }
                else{
                    Toast.makeText(ChatActivity.this ,
                            "you can't send empty message" ,
                            Toast.LENGTH_LONG)
                            .show();
                }
                message_text.setText("");
            }
        });

        seenMessage();
    }

    private void getUserInfo(final TextView Name){
        firebaseDatabase.getReference(accType).child(userId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (accType.equals("Doctors")){
                    Doctors doctors = dataSnapshot.getValue(Doctors.class);
                    assert doctors != null;
                    Name.setText(doctors.getFirstName() + " " + doctors.getLastName());
                    if (!TextUtils.isEmpty(doctors.getProfileImage())){
                        Picasso.get().load(doctors.getProfileImage()).into(profile_image_);
                        imageUrl = doctors.getProfileImage().toString();
                    } else {
                        profile_image_.setImageResource(R.mipmap.ic_launcher);
                    }
                } else if (accType.equals("Users")){
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    name.setText(user.getFirstName() + " " + user.getLastName());
                    if (!TextUtils.isEmpty(user.getProfileImage())){
                        Picasso.get().load(user.getProfileImage()).into(profile_image_);
                        imageUrl = user.getProfileImage();
                    } else {
                        profile_image_.setImageResource(R.mipmap.ic_launcher);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(getApplicationContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
        readMessages();
    }

    private void readMessages(){
        mMessage = new ArrayList<>();
        reference = firebaseDatabase.getReference().child("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessage.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    assert message != null;
                    if(message.getReceiver().equals(firebaseUser.getUid()) && message.getSender().equals(userId)
                            || message.getReceiver().equals(userId) && message.getSender().equals(firebaseUser.getUid())){
                        mMessage.add(message);
                    }
                    messagesAdapter = new MessagesAdapter(ChatActivity.this, mMessage, imageUrl);
                    recyclerView.setAdapter(messagesAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(getApplicationContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }

    private void sendMessage(String message, String sender, final String receiver){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("sender", sender);
        map.put("receiver", receiver);
        map.put("isSeen", "false");

        reference.child("Messages").push().setValue(map);

        final DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userId);
        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatref.child("receiver").setValue(userId);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(getApplicationContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });

        final DatabaseReference chatref1 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userId)
                .child(firebaseUser.getUid());
        chatref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatref1.child("receiver").setValue(firebaseUser.getUid());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(getApplicationContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }

    private void seenMessage(){
        reference = firebaseDatabase.getReference("Messages");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);

                    assert message != null;
                    if (message.getReceiver().equals(firebaseUser.getUid())
                            && message.getSender().equals(userId)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", "true");
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
