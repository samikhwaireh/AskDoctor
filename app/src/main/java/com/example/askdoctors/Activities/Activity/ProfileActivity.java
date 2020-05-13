package com.example.askdoctors.Activities.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.askdoctors.Activities.Model.Doctors;
import com.example.askdoctors.Activities.Model.Questions;
import com.example.askdoctors.Activities.Model.User;
import com.example.askdoctors.Activities.Adapter.QuestionsAdapter;
import com.example.askdoctors.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity implements QuestionsAdapter.onQuestionClicked {

    private TextView birthdayTv,genderTv,userNameTv ;
    private ImageView profileImageView;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    Doctors doctor;
    User user;

    QuestionsAdapter adapter;
    ArrayList<Questions> questions;

    String firstName, lastName, birthday, gender, profileImage, accType, userId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        birthdayTv = findViewById(R.id.profile_birthdayTv);
        genderTv = findViewById(R.id.profile_genderTv);
        userNameTv = findViewById(R.id.profile_userNameTv);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView messages = findViewById(R.id.message);
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("accType", accType);
                startActivity(intent);
            }
        });
        profileImageView = findViewById(R.id.profile_ImageView);
        RecyclerView profileRv = findViewById(R.id.profile_Rv);
        TextView doctorOrUserTv = findViewById(R.id.profile_doctorOrUserTv);

        questions = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        accType = intent.getStringExtra("accType");
        userId  = intent.getStringExtra("userId");

        if (accType.equals("Doctors")){
            doctorOrUserTv.setText("Doctor");
        }else {
            doctorOrUserTv.setText("User");
        }

        adapter = new QuestionsAdapter(questions,  this, getApplication());
        profileRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        profileRv.setNestedScrollingEnabled(false);
        profileRv.setAdapter(adapter);

        getProfileInfo();

        if (accType.equals("Users"))
            getAskedQuestions();
    }

    public void answer(int position) {
        Intent intent = new Intent(getApplicationContext(), AnswerActivity.class);
        intent.putExtra("Disease", questions.get(position).getDisease());
        intent.putExtra("Question", questions.get(position).getQuestion());
        intent.putExtra("Image", questions.get(position).getImage());
        intent.putExtra("ProfileImage", questions.get(position).getProfileImage());
        intent.putExtra("UserName", questions.get(position).getUserName());
        intent.putExtra("accype", accType);
        startActivity(intent);
    }

    private void getAskedQuestions(){
        DatabaseReference reference = firebaseDatabase.getReference("Questions");
        final Query query = reference.orderByChild("date").limitToLast(5);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    questions.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        if (ds.child("id").getValue(String.class).equals(userId)){

                            Questions question = new Questions();

                            String Disease = ds.child("disease").getValue(String.class);
                            String Image = ds.child("image").getValue(String.class);
                            String Question = ds.child("question").getValue(String.class);
                            String UserName = ds.child("userName").getValue(String.class);
                            String profileImage = ds.child("profileImage").getValue(String.class);

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
                Toasty.error(getApplicationContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }


    private  void getProfileInfo(){
        DatabaseReference reference = firebaseDatabase.getReference(accType).child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (accType.equals("Doctors")){

                        doctor = dataSnapshot.getValue(Doctors.class);

                        firstName = doctor.getFirstName();
                        lastName = doctor.getLastName();
                        birthday = doctor.getBirthday();
                        gender = doctor.getGender();
                        profileImage = doctor.getProfileImage();

                        userNameTv.setText(firstName + " " + lastName);
                        birthdayTv.setText(birthday);
                        genderTv.setText(gender);
                        if (!TextUtils.isEmpty(profileImage)){
                            Picasso.get().load(profileImage).into(profileImageView);
                        }

                    }else {

                        user = dataSnapshot.getValue(User.class);
                        userNameTv.setText(user.getFirstName() + " " + user.getLastName());
                        birthdayTv.setText(user.getBirthday());
                        genderTv.setText(user.getGender());
                        if (!TextUtils.isEmpty(user.getProfileImage())){
                            Picasso.get().load(user.getProfileImage()).into(profileImageView);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(getApplicationContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void deleteQuestion(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
        alert.setTitle("Delete Question");
        alert.setMessage("Do you want to delete this question ?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatabaseReference reference = firebaseDatabase.getReference("Questions");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            for (DataSnapshot ds : dataSnapshot.getChildren()){

                                if (ds.child("disease").getValue(String.class)
                                        .equals(questions.get(position).getDisease())
                                        && ds.child("question").getValue(String.class)
                                        .equals(questions.get(position).getQuestion()) &&
                                        ds.child("image").getValue(String.class)
                                                .equals(questions.get(position).getImage())
                                        && ds.child("id").getValue(String.class)
                                        .equals(firebaseAuth.getUid())){
                                    String key = ds.getKey();
                                    DatabaseReference databaseReference = firebaseDatabase.getReference("Questions").child(key);
                                    databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            Toasty.success(getApplicationContext(), "Question deleted successfully"
                                                    , Toasty.LENGTH_LONG).show();
                                            adapter.notifyDataSetChanged();

                                        }
                                    });

                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toasty.error(getApplicationContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
                    }
                });

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    @Override
    public void openProfile(int position) {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra("accType", "Users");
        intent.putExtra("userId", questions.get(position).getId());
        getApplication().startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
