package com.example.askdoctors.Activities.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.askdoctors.Activities.Adapter.DoctorChatListAdapter;
import com.example.askdoctors.Activities.Adapter.UserChatListAdapter;
import com.example.askdoctors.Activities.Model.ChatList;
import com.example.askdoctors.Activities.Model.Doctors;
import com.example.askdoctors.Activities.Model.User;
import com.example.askdoctors.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatUserFragment extends Fragment {

    private RecyclerView recyclerView;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference, databaseReference;

    private UserChatListAdapter userChatListAdapter;
    private List<User> mUsers;

    private List<ChatList> chatListList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_chat_user, container, false);

        recyclerView = viewGroup.findViewById(R.id.recycle_view1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        mUsers = new ArrayList<>();
        chatListList = new ArrayList<>();

        getUsersChatList();

        return viewGroup;
    }

    private void getUsersChatList(){
        databaseReference = firebaseDatabase.getReference("ChatList").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    chatListList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        ChatList chatList = snapshot.getValue(ChatList.class);
                        chatListList.add(chatList);
                    }
                    getUserInfo();
                } else if (!dataSnapshot.exists()){
                    //Toast.makeText(getContext(), "You don't have any chat room", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getUserInfo(){
        reference = firebaseDatabase.getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (ChatList chatList : chatListList){
                        assert user != null;
                        if (user.getId().equals(chatList.getReceiver())){
                            mUsers.add(user);
                        }
                    }
                }
                userChatListAdapter = new UserChatListAdapter(getContext(), mUsers, false);
                recyclerView.setAdapter(userChatListAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
