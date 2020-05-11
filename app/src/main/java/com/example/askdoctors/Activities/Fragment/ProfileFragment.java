package com.example.askdoctors.Activities.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askdoctors.Activities.Adapter.QuestionsAdapter;
import com.example.askdoctors.Activities.Activity.AnswerActivity;
import com.example.askdoctors.Activities.Model.Doctors;
import com.example.askdoctors.Activities.Activity.LoginActivity;
import com.example.askdoctors.Activities.Model.Questions;
import com.example.askdoctors.Activities.Model.User;
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

public class ProfileFragment extends Fragment implements QuestionsAdapter.onQuestionClicked {

    TextView birthdayTv,genderTv,userNameTv ,doctorOrUserTv;
    Button updateBtn;
    ImageView profileImageView, logouBtn;
    RecyclerView profileRv;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    String accType;

    Doctors doctor;
    User user;

    QuestionsAdapter adapter;
    ArrayList<Questions> questions;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        birthdayTv = view.findViewById(R.id.profile_birthdayTv);
        genderTv = view.findViewById(R.id.profile_genderTv);
        userNameTv = view.findViewById(R.id.profile_userNameTv);
        logouBtn = view.findViewById(R.id.profile_logoutBtn);
        updateBtn = view.findViewById(R.id.profile_updateBtn);
        profileImageView = view.findViewById(R.id.profile_ImageView);
        profileRv = view.findViewById(R.id.profile_Rv);
        doctorOrUserTv = view.findViewById(R.id.profile_doctorOrUserTv);
        questions = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        accType = getActivity().getIntent().getStringExtra("user");

        if (accType.equals("Doctors")){
            doctorOrUserTv.setText("Doctor");
        }else {
            doctorOrUserTv.setText("User");
        }

        getprofileInfo();

        logouBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });


        if (accType.equals("Users"))
            getAskedQuestions();

        adapter = new QuestionsAdapter(questions,this);
        profileRv.setLayoutManager(new LinearLayoutManager(getContext()));
        profileRv.setAdapter(adapter);

        return  view;
    }

    public  void getprofileInfo(){
        DatabaseReference reference = firebaseDatabase.getReference(accType).child(firebaseAuth.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (accType.equals("Doctors")){

                        doctor = dataSnapshot.getValue(Doctors.class);
                        userNameTv.setText(doctor.getFirstName() + " " + doctor.getLastName());
                        birthdayTv.setText(doctor.getBirthday());
                        genderTv.setText(doctor.getGender());
                        if (!TextUtils.isEmpty(doctor.getProfileImage())){
                            Picasso.get().load(doctor.getProfileImage()).into(profileImageView);
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
                Toasty.error(getContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }

    public void getAskedQuestions(){
        DatabaseReference reference = firebaseDatabase.getReference("Questions");
        final Query query = reference.orderByChild("Date").limitToLast(5);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    questions.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()){

                        if (ds.child("ID").getValue(String.class).equals(firebaseAuth.getUid())){
                            Questions question = new Questions();
                            String Disease = ds.child("Disease").getValue(String.class);
                            String Image = ds.child("Image").getValue(String.class);
                            String Question = ds.child("Question").getValue(String.class);
                            String UserName = ds.child("UserName").getValue(String.class);
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
        intent.putExtra("accype", accType);
        startActivity(intent);
    }

    @Override
    public void deleteQuestion(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
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

                                if (ds.child("Disease").getValue(String.class)
                                .equals(questions.get(position).getDisease())
                                && ds.child("Question").getValue(String.class)
                                .equals(questions.get(position).getQuestion()) &&
                                ds.child("Image").getValue(String.class)
                                .equals(questions.get(position).getImage())
                                && ds.child("ID").getValue(String.class)
                                        .equals(firebaseAuth.getUid())){
                                    String key = ds.getKey();
                                    DatabaseReference databaseReference = firebaseDatabase.getReference("Questions").child(key);
                                    databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            Toasty.success(getContext(), "Question deleted successfully"
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
                        Toasty.error(getContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
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
}
