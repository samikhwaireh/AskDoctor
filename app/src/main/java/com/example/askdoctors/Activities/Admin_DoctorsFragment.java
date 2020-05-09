package com.example.askdoctors.Activities;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class Admin_DoctorsFragment extends Fragment {
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
        adapter = new Admin_DoctorsRvAdapter(doctors);
        doctorsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        doctorsRv.setAdapter(adapter);
        return view;
    }

    private void getDoctors(){
        DatabaseReference reference = firebaseDatabase.getReference("Doctors");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
}
