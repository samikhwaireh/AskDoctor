package com.example.askdoctors.Activities.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.askdoctors.R;
import com.squareup.picasso.Picasso;

public class AnswerActivity extends AppCompatActivity {
    TextView userNameTv,diseaseTv,questionTv;
    ImageView userImageView,questionImageView;
    EditText answerEt;
    RecyclerView answersRv;
    Button answerBtn;

    String profileImage,userName,disease,question,questionImage,accType;

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

        Intent intent = getIntent();
        profileImage = intent.getStringExtra("ProfileImage");
        userName = intent.getStringExtra("UserName");
        disease = intent.getStringExtra("Disease");
        question = intent.getStringExtra("Question");
        questionImage = intent.getStringExtra("Image");
        accType = intent.getStringExtra("accType");

        if (!TextUtils.isEmpty(profileImage)){
            Picasso.get().load(profileImage).into(userImageView);
        }
        userNameTv.setText(userName);
        diseaseTv.setText(disease);
        questionTv.setText(question);
        if (!TextUtils.isEmpty(profileImage)){
            Picasso.get().load(questionImage).into(questionImageView);
        }


    }
}
