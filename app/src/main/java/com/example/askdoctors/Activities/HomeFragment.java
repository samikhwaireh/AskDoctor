package com.example.askdoctors.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment implements QuestionsAdapter.onQuestionClicked {
    RecyclerView homeRv;
    Button askBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    ArrayList<Questions> questions;
    ArrayList<String> keys;

    QuestionsAdapter adapter;
    String accType;

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

        accType = getActivity().getIntent().getStringExtra("user");

        String accType = getActivity().getIntent().getStringExtra("user").trim();
        if (!TextUtils.isEmpty(accType) &&accType.equals("Doctors"))
            askBtn.setVisibility(View.GONE);

        askBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AskQuestionActivity.class);
                startActivity(intent);
            }
        });


        getQuestions();
        adapter = new QuestionsAdapter(questions,this);
        homeRv.setLayoutManager(new LinearLayoutManager(getContext()));
        homeRv.setAdapter(adapter);


        return view;
    }



    private void getQuestions(){
        DatabaseReference reference = firebaseDatabase.getReference("Questions");
        final Query query = reference.orderByChild("date").limitToLast(5);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    questions.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()){

                            Questions question = new Questions();
                            String Disease = ds.child("disease").getValue(String.class);
                            String Image = ds.child("image").getValue(String.class);
                            String Question = ds.child("question").getValue(String.class);
                            String UserName = ds.child("userName").getValue(String.class);
                            String profileImage = ds.child("profileImage").getValue(String.class);

                            if (!TextUtils.isEmpty(UserName)){
                                question.setDisease(Disease);
                                question.setImage(Image);
                                question.setProfileImage(profileImage);
                                question.setUserName(UserName);
                                question.setQuestion(Question);

                                questions.add(question);
                                adapter.notifyDataSetChanged();

                            }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(getContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void answer(int position) {
        Intent intent = new Intent(getContext(), AnswerActivity.class);
        intent.putExtra("Disease", questions.get(position).getDisease());
        intent.putExtra("Question", questions.get(position).getQuestion());
        intent.putExtra("Image", questions.get(position).getImage());
        intent.putExtra("ProfileImage", questions.get(position).getProfileImage());
        intent.putExtra("UserName", questions.get(position).getUserName());
        startActivity(intent);
    }

    @Override
    public void deleteQuestion(int position) {
        return;
    }
}
