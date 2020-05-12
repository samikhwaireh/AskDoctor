package com.example.askdoctors.Activities.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.askdoctors.Activities.Adapter.MessagesAdapter;
import com.example.askdoctors.Activities.Model.Message;
import com.example.askdoctors.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ImageView profile_image_, send;
    private EditText message_text;
    private TextView name;
    private RecyclerView recyclerView;

    MessagesAdapter messagesAdapter;
    List<Message> mMessage;
    String userId;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;

    private ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }
}
