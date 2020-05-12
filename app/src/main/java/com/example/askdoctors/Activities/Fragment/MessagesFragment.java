package com.example.askdoctors.Activities.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.askdoctors.Activities.Adapter.ProfileAdapter;
import com.example.askdoctors.Activities.Model.ChatList;
import com.example.askdoctors.Activities.Model.Doctors;
import com.example.askdoctors.Activities.Model.User;
import com.example.askdoctors.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MessagesFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.messages_fragment, container, false);

        TabLayout tabLayout = viewGroup.findViewById(R.id.tab_layout);
        ViewPager viewPager = viewGroup.findViewById(R.id.pager);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);



//        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                usersList.clear();
//                if (dataSnapshot.exists()){
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        ChatList chatList = snapshot.getValue(ChatList.class);
//                        usersList.add(chatList);
//                    }
//                    chatList();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toasty.error(getContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
//            }
//
//        });

        return viewGroup;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ChatDoctorFragment(), "Doctors");
        adapter.addFragment(new ChatUserFragment(), "Users");
        viewPager.setAdapter(adapter);
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

//    private void getChatList(){
//        firebaseDatabase.getReference("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()){
//                        final ChatList chatList = snapshot.getValue(ChatList.class);
//                        assert chatList != null;
//                        firebaseDatabase.getReference("Users").child(chatList.getReceiver()).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                if (!dataSnapshot.exists()){
//                                    firebaseDatabase.getReference("Doctors").child(chatList.getReceiver()).addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//                                    });
//                                } else if (dataSnapshot.exists()){
//                                    User user = dataSnapshot.getValue();
//                                }
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

//    private void getChatList(){
//        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                        ChatList chatList = snapshot.getValue(ChatList.class);
//                        assert chatList != null;
//                        firebaseDatabase.getReference("ChatList").child(chatList.getReceiver()).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()){
//
//                                }
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                                Toasty.error(getContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toasty.error(getContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
//            }
//        });
//    }

//    private void chatList() {
//        mDoctors = new ArrayList<>();
//        firebaseUser = auth.getCurrentUser();
//        reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mDoctors.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Doctors doctors = snapshot.getValue(Doctors.class);
//                    for (ChatList chatList : usersList){
//                        assert doctors != null;
//                        if(doctors.getId().equals(chatList.getReceiver()) ){
//                            mDoctors.add(doctors);
//                        }
//                    }
//                }
//                profileAdapter = new ProfileAdapter(getContext(), mDoctors, false);
//                recyclerView.setAdapter(profileAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toasty.error(getContext(), databaseError.getMessage(), Toasty.LENGTH_LONG).show();
//            }
//        });
//    }
}
