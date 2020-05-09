package com.example.askdoctors.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askdoctors.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    RecyclerView homeRv;
    Button askBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    ArrayList<Questions> questions;
    ArrayList<String> keys;

    QuestionsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        homeRv = view.findViewById(R.id.homeFragment_Rv);
        askBtn = view.findViewById(R.id.homeFragment_askBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        questions = new ArrayList<>();
        keys = new ArrayList<>();

        askBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AskQuestionActivity.class);
                startActivity(intent);
            }
        });


        getQuestions();
        adapter = new QuestionsAdapter(questions);
        homeRv.setLayoutManager(new LinearLayoutManager(getContext()));
        homeRv.setAdapter(adapter);


        return view;
    }

    private void getQuestions(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messagesRef = rootRef.child("Questions");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                   keys.add(ds.getKey());

                }

                for (int index = 0; index<keys.size(); index++){
                    for (DataSnapshot questionsSnapshot : dataSnapshot.child(keys.get(index)).getChildren()){
                        Questions question = dataSnapshot.child(keys.get(index)).child(questionsSnapshot.getKey())
                                .getValue(Questions.class);

                        questions.add(question);
                        adapter.notifyDataSetChanged();
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        messagesRef.addListenerForSingleValueEvent(eventListener);
    }
}
