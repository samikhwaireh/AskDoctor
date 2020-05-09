package com.example.askdoctors.Activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askdoctors.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Admin_DoctorsRvAdapter extends RecyclerView.Adapter<Admin_DoctorsRvAdapter.doctorsHolder> {

    ArrayList<Doctors> doctors;

    public Admin_DoctorsRvAdapter(ArrayList<Doctors> doctors) {
        this.doctors = doctors;
    }

    @NonNull
    @Override
    public doctorsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.admin_doctors_adapter, parent, false);
        return new doctorsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull doctorsHolder holder, int position) {
        Picasso.get().load(doctors.get(position).getProfileImage()).into(holder.userImageView);
        holder.userNameTv.setText(doctors.get(position).getFirstName() + " " + doctors.get(position).getLastName());
        if (doctors.get(position).getStatus().equals("Confirmed"))
            holder.confirmBtn.setText("Unconfirm");
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public class doctorsHolder extends RecyclerView.ViewHolder{
        TextView userNameTv;
        Button confirmBtn;
        CircleImageView userImageView;
        public doctorsHolder(@NonNull View itemView) {
            super(itemView);

            userImageView = itemView.findViewById(R.id.admin_docRv_Imageview);
            userNameTv = itemView.findViewById(R.id.admin_docRv_userNameTv);
            confirmBtn = itemView.findViewById(R.id.admin_docRv_ConfirmBtn);
        }
    }
}
