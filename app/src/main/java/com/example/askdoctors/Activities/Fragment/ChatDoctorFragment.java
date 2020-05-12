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

import com.example.askdoctors.Activities.Activity.ProfileActivity;
import com.example.askdoctors.Activities.Adapter.DoctorChatListAdapter;
import com.example.askdoctors.Activities.Adapter.ProfileAdapter;
import com.example.askdoctors.Activities.Model.ChatList;
import com.example.askdoctors.Activities.Model.Doctors;
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

public class ChatDoctorFragment extends Fragment {

    private RecyclerView recyclerView;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference, databaseReference;

    private DoctorChatListAdapter doctorChatListAdapter;
    private List<Doctors> mDoctors;

    private List<ChatList> chatListList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_chat_doctor, container, false);

        recyclerView = viewGroup.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();


        mDoctors = new ArrayList<>();
        chatListList = new ArrayList<>();

        getDoctorChatList();

        return viewGroup;
    }

    private void getDoctorChatList(){
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
                    getDoctorInfo();
                } else if (!dataSnapshot.exists()){
                    Toast.makeText(getContext(), "You don't have any chat room", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getDoctorInfo(){
        reference = firebaseDatabase.getReference("Doctors");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDoctors.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Doctors doctors = snapshot.getValue(Doctors.class);
                    for (ChatList chatList : chatListList){
                        assert doctors != null;
                        if (doctors.getId().equals(chatList.getReceiver())){
                            mDoctors.add(doctors);
                        }
                    }
                }
                doctorChatListAdapter = new DoctorChatListAdapter(getContext(), mDoctors, false);
                recyclerView.setAdapter(doctorChatListAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
