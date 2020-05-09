package com.example.askdoctors.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askdoctors.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class Admin_DoctorsFragment extends Fragment implements Admin_DoctorsRvAdapter.confirmDoctors{
    RecyclerView doctorsRv;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    ArrayList<Doctors> doctors;
    Admin_DoctorsRvAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_doctorsfragment, container, false);
        doctorsRv = view.findViewById(R.id.Admin_DoctorsRv);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        doctors = new ArrayList<>();

        getDoctors();
        adapter = new Admin_DoctorsRvAdapter(doctors,this);
        doctorsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        doctorsRv.setAdapter(adapter);
        return view;
    }

    private void getDoctors(){

        DatabaseReference reference = firebaseDatabase.getReference("Doctors");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctors.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    DatabaseReference databaseReference = firebaseDatabase.getReference("Doctors").child(key);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Doctors doctor = dataSnapshot.getValue(Doctors.class);
                            doctors.add(doctor);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toasty.error(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConfirmClicked(final int positon) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference questionsRef = rootRef.child("Doctors");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final boolean[] con = {false};

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot questionsSnapshot : dataSnapshot.child(ds.getKey()).getChildren()){

                        if (dataSnapshot.child(ds.getKey()).child("firstName").getValue(String.class)
                                .equals(doctors.get(positon).getFirstName()) &&
                                dataSnapshot.child(ds.getKey()).child("lastName").getValue(String.class)
                                        .equals(doctors.get(positon).getLastName())){


                            final String id = dataSnapshot.child(ds.getKey()).child("id").getValue(String.class);
                            DatabaseReference reference = firebaseDatabase.getReference("Doctors").child(id);


                            Map<String, Object> confirmitionMsg = new HashMap<>();
                            final String msg;
                            if (doctors.get(positon).getStatus().equals("Waiting")){
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
                                    Toasty.success(getContext(), msg, Toasty.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toasty.error(getContext(), e.getLocalizedMessage(), Toasty.LENGTH_LONG).show();
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
                Toasty.error(getContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
            }
        };
        questionsRef.addListenerForSingleValueEvent(eventListener);

    }

    @Override
    public void onDoctorClicked(int position) {
        Intent intent = new Intent(getContext(), ShowDoctorsProof.class);
        intent.putExtra("image", doctors.get(position).getDiploma());
        intent.putExtra("university", doctors.get(position).getUniversity());
        intent.putExtra("graduate", doctors.get(position).getGraduate());
        intent.putExtra("firstName", doctors.get(position).getFirstName());
        intent.putExtra("lastName", doctors.get(position).getLastName());
        intent.putExtra("status", doctors.get(position).getStatus());
        intent.putExtra("profileImage", doctors.get(position).getProfileImage());

        startActivity(intent);
    }
}
