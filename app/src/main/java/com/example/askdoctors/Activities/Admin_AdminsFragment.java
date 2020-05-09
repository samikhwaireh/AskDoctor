package com.example.askdoctors.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askdoctors.R;

public class Admin_AdminsFragment extends Fragment {
    RecyclerView AdminsRv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_adminsfragment, container, false);

        AdminsRv = view.findViewById(R.id.Admin_AdminsRv);

        return view;
    }
}
