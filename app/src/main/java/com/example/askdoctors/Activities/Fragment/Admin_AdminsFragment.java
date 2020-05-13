package com.example.askdoctors.Activities.Fragment;

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

import com.example.askdoctors.Activities.Adapter.Admin_AdminsRvAdapter;
import com.example.askdoctors.Activities.Model.Admins;
import com.example.askdoctors.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class Admin_AdminsFragment extends Fragment {
    RecyclerView AdminsRv;


    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    ArrayList<com.example.askdoctors.Activities.Model.Admins> Admins;
    Admin_AdminsRvAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_adminsfragment, container, false);

        AdminsRv = view.findViewById(R.id.Admin_AdminsRv);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        Admins = new ArrayList<>();

        getAdmins();
        adapter = new Admin_AdminsRvAdapter(Admins);
        AdminsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        AdminsRv.setAdapter(adapter);

        return view;
    }

    private void getAdmins(){
        DatabaseReference reference = firebaseDatabase.getReference("Admins");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    DatabaseReference databaseReference = firebaseDatabase.getReference("Admins").child(key);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Admins admin = new Admins();
                            String firstName = dataSnapshot.child("firstName").getValue(String.class);
                            String lastName = dataSnapshot.child("lastName").getValue(String.class);
                            String gender = dataSnapshot.child("gender").getValue(String.class);

                            admin.setFirstName(firstName);
                            admin.setLastName(lastName);
                            admin.setGender(gender);


                            Admins.add(admin);
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
