package com.example.askdoctors.Activities.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.askdoctors.Activities.Activity.UpdateProfileActivity;
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
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ProfileFragment extends Fragment implements QuestionsAdapter.onQuestionClicked {

    private TextView birthdayTv,genderTv,userNameTv ,doctorOrUserTv;
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

    String firstName,lastName,birthday,gender,profileImage,key;

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
                SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("login", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("isChecked", "0");
                editor.apply();

                firebaseAuth.signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });


        if (accType.equals("Users"))
            getAskedQuestions();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UpdateProfileActivity.class);
                intent.putExtra("accType",accType);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("gender",gender);
                intent.putExtra("birthday", birthday);
                intent.putExtra("profileImage", profileImage);
                startActivity(intent);
            }
        });

        adapter = new QuestionsAdapter(questions,this,getContext());
        profileRv.setLayoutManager(new LinearLayoutManager(getContext()));
        profileRv.setNestedScrollingEnabled(false);
        profileRv.setAdapter(adapter);
        profileRv.setNestedScrollingEnabled(false);

        return  view;
    }

    private  void getprofileInfo(){
        DatabaseReference reference = firebaseDatabase.getReference(accType).child(firebaseAuth.getUid());
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
                Toasty.error(getContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }

    private void getAskedQuestions(){
        DatabaseReference reference = firebaseDatabase.getReference("Questions");
        final Query query = reference.orderByChild("date").limitToLast(5);

        DatabaseReference databaseReference = firebaseDatabase.getReference(accType).child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final String profileImage = dataSnapshot.child("profileImage").getValue(String.class);
                final String firstName = dataSnapshot.child("firstName").getValue(String.class);
                final String lastName = dataSnapshot.child("lastName").getValue(String.class);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            questions.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()){



                                if (ds.child("id").getValue(String.class).equals(firebaseAuth.getUid())){
                                    Questions question = new Questions();
                                    String Disease = ds.child("disease").getValue(String.class);
                                    String Image = ds.child("image").getValue(String.class);
                                    String Question = ds.child("question").getValue(String.class);


                                    key = ds.getKey();
                                    question.setDisease(Disease);
                                    question.setImage(Image);
                                    question.setProfileImage(profileImage);
                                    question.setUserName(firstName + " " + lastName);
                                    question.setKey(key);
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
        intent.putExtra("key", questions.get(position).getKey());
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
                reference.addValueEventListener(new ValueEventListener() {
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

    @Override
    public void openProfile(int position) {

    }
}
