package com.example.askdoctors.Activities.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.askdoctors.Activities.Adapter.AnswersAdapter;
import com.example.askdoctors.Activities.Model.answers;
import com.example.askdoctors.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class AnswerActivity extends AppCompatActivity {
    TextView userNameTv,diseaseTv,questionTv;
    ImageView userImageView,questionImageView;
    EditText answerEt;
    RecyclerView answersRv;
    Button answerBtn;

    String profileImage,userName,disease,question,questionImage,accType,key;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    ArrayList<answers> answers;
    AnswersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        userNameTv = findViewById(R.id.answer_userNameTv);
        diseaseTv = findViewById(R.id.answer_diseaseTv);
        questionTv = findViewById(R.id.answer_QuestionTv);
        userImageView = findViewById(R.id.answer_userImage);
        questionImageView = findViewById(R.id.answer_questionImage);
        answerEt = findViewById(R.id.answer_answerEt);
        answersRv = findViewById(R.id.answers_Rv);
        answerBtn = findViewById(R.id.answer_answerBtn);

        firebaseAuth = firebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        answers = new ArrayList<>();

        Intent intent = getIntent();
        profileImage = intent.getStringExtra("ProfileImage");
        userName = intent.getStringExtra("UserName");
        disease = intent.getStringExtra("Disease");
        question = intent.getStringExtra("Question");
        questionImage = intent.getStringExtra("Image");
        accType = intent.getStringExtra("accType");
        key = intent.getStringExtra("key");


        if (!TextUtils.isEmpty(profileImage)){
            Picasso.get().load(profileImage).into(userImageView);
        }

        userNameTv.setText(userName);
        diseaseTv.setText(disease);
        questionTv.setText(question);
        if (questionImage.equals("noImage")){
            questionImageView.setVisibility(View.GONE);
        }else {
            Picasso.get().load(questionImage).into(questionImageView);
        }

        getAnswers();
        adapter = new AnswersAdapter(answers);
        answersRv.setLayoutManager(new LinearLayoutManager(this));
        answersRv.setAdapter(adapter);
        answersRv.setNestedScrollingEnabled(false);

    }

    public void answer(View view){

        final String comment = answerEt.getText().toString().trim();
        if (!TextUtils.isEmpty(comment)){

            final Map<String, Object> commentDetails = new HashMap<>();
            final Map<String, Object> map = new HashMap<>();

            map.put("date", -1* new Date().getTime());
            map.put("comment", comment);


            DatabaseReference reference = firebaseDatabase.getReference(accType).child(firebaseAuth.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final String userImage = dataSnapshot.child("profileImage").getValue(String.class);
                    final String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    final String lastName = dataSnapshot.child("lastName").getValue(String.class);

                    map.put("profileImage", userImage);
                    map.put("userName", firstName + " " + lastName);
                    commentDetails.put(UUID.randomUUID().toString(),map);
                    Map<String, Object> newComment = new HashMap<>();



                    DatabaseReference databaseReference = firebaseDatabase.getReference("Questions").child(key).child("comments");
                    databaseReference.updateChildren(commentDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            answerEt.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(AnswerActivity.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toasty.error(AnswerActivity.this, databaseError.getMessage(), Toasty.LENGTH_LONG).show();
                }
            });

        }

    }

    private void getAnswers(){

        final DatabaseReference databaseReference = firebaseDatabase.getReference("Questions").child(key).child("comments");
        Query query = databaseReference.orderByChild("date").limitToLast(5);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    answers.clear();
                   for (DataSnapshot ds : dataSnapshot.getChildren()){

                        String comment = ds.child("comment").getValue(String.class);
                        String user = ds.child("userName").getValue(String.class);
                        String profileImage = ds.child("profileImage").getValue(String.class);

                        answers answer = new answers();

                        answer.setComment(comment);
                        answer.setProfileImage(profileImage);
                        answer.setUserName(user);

                        answers.add(answer);
                        adapter.notifyDataSetChanged();

                   }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(AnswerActivity.this, databaseError.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });

    }
/*
    private void closeKeyboard(){
        View view = AnswerActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

 */
}
