package com.example.askdoctors.Activities.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askdoctors.Activities.Adapter.ProfileAdapter;
import com.example.askdoctors.Activities.Model.Doctors;
import com.example.askdoctors.Activities.Model.User;
import com.example.askdoctors.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProfileAdapter profileAdapter;
    private EditText search;

    private ImageView toChat;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    private List<Doctors> mDoctors;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.search_fragment, container, false);

        recyclerView = viewGroup.findViewById(R.id.recycle_user);
        toChat = viewGroup.findViewById(R.id.to_chats);
        search = viewGroup.findViewById(R.id.search_users);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        mDoctors = new ArrayList<>();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        readDoctors();

        return viewGroup;
    }

    private void searchUsers(String s) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        Query query = firebaseDatabase
                .getReference("Doctors")
                .orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDoctors.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Doctors doctors = snapshot.getValue(Doctors.class);

                    assert doctors != null;
                    assert firebaseUser != null;
                    if (!doctors.getId().equals(firebaseUser.getUid())){
                        mDoctors.add(doctors);
                    }
                }
                profileAdapter = new ProfileAdapter(getContext(), mDoctors, false);
                recyclerView.setAdapter(profileAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    //get all doctors profiles when the fragment create
    private void readDoctors(){

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Doctors");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDoctors.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Doctors doctors = snapshot.getValue(Doctors.class);
                    assert doctors != null;
                    if (!doctors.getId().equals(firebaseUser.getUid())){
                        mDoctors.add(doctors);
                    }
                }
                profileAdapter = new ProfileAdapter(getContext(), mDoctors, false);
                recyclerView.setAdapter(profileAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
