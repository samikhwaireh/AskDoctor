package com.example.askdoctors.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.askdoctors.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ShowDoctorsProof extends AppCompatActivity {
    TextView universityTv,graduateTv,userNameTv;
    ImageView imageView,userImageView;
    String firstName,lastName,status,previousStatus,tmp;
    Button confirmBtn;

    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_doctors_proof);

        universityTv = findViewById(R.id.showProofs_universityTv);
        graduateTv = findViewById(R.id.showProofs_graduateTv);
        imageView = findViewById(R.id.showProofs_ImageView);
        confirmBtn = findViewById(R.id.showProofs_confirmBtn);
        userNameTv = findViewById(R.id.showProofs_userNameTv);
        userImageView = findViewById(R.id.showProofs_ImageView);

        firebaseDatabase = FirebaseDatabase.getInstance();


        Intent intent = getIntent();
        String university = intent.getStringExtra("university");
        String graduate = intent.getStringExtra("graduate");
        String image = intent.getStringExtra("image");
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        status = intent.getStringExtra("status");
        String profileImage = intent.getStringExtra("profileImage");

        if (status.equals("confirmed")){
            confirmBtn.setText("Unconfirm");
            previousStatus = "Waiting";
        }else {
            confirmBtn.setText("confirm");
            previousStatus = "confirmed";
        }

        universityTv.setText("University : "+ university);
        graduateTv.setText("Graduate : " + graduate);
        userNameTv.setText(firstName + " " + lastName);
        Picasso.get().load(profileImage).into(userImageView);
        Picasso.get().load(image).into(imageView);

    }

    public void back(View view){
        Intent intent = new Intent(this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void confirm(View view){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference questionsRef = rootRef.child("Doctors");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final boolean[] con = {false};

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot questionsSnapshot : dataSnapshot.child(ds.getKey()).getChildren()){

                        if (dataSnapshot.child(ds.getKey()).child("firstName").getValue(String.class)
                                .equals(firstName) &&
                                dataSnapshot.child(ds.getKey()).child("lastName").getValue(String.class)
                                        .equals(lastName)){


                            final String id = dataSnapshot.child(ds.getKey()).child("id").getValue(String.class);
                            DatabaseReference reference = firebaseDatabase.getReference("Doctors").child(id);


                            Map<String, Object> confirmitionMsg = new HashMap<>();
                            final String msg;
                            if (status.equals("Waiting")){
                                confirmitionMsg.put("Status", "confirmed");
                                msg = "Confirmed";
                            }else {
                                confirmitionMsg.put("Status", "Waiting");
                                msg = "Unconfirmed";
                            }

                            reference.updateChildren(confirmitionMsg).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    con[0] = true;

                                    tmp = status;
                                    status = previousStatus;
                                    previousStatus=tmp;

                                    if (status.equals("confirmed")){
                                        confirmBtn.setText("Unconfirm");
                                    }else {
                                        confirmBtn.setText("confirm");
                                    }

                                    Toasty.success(ShowDoctorsProof.this, msg, Toasty.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toasty.error(ShowDoctorsProof.this, e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
                                }
                            });
                        }
                        break;
                    }
                    if (con[0] == true)
                        break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toasty.error(ShowDoctorsProof.this, databaseError.getMessage(), Toasty.LENGTH_LONG).show();
            }
        };
        questionsRef.addListenerForSingleValueEvent(eventListener);
    }
}
