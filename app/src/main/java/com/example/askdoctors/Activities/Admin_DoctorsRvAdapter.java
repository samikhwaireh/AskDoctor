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
    confirmDoctors confirmDoctors;

    public Admin_DoctorsRvAdapter(ArrayList<Doctors> doctors,confirmDoctors confirmDoctors) {
        this.doctors = doctors;
        this.confirmDoctors = confirmDoctors;
    }

    @NonNull
    @Override
    public doctorsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.admin_doctors_adapter, parent, false);
        return new doctorsHolder(view, confirmDoctors);
    }

    @Override
    public void onBindViewHolder(@NonNull doctorsHolder holder, int position) {

        Picasso.get().load(doctors.get(position).getProfileImage()).into(holder.userImageView);
        holder.userNameTv.setText(doctors.get(position).getFirstName() + " " + doctors.get(position).getLastName());
        if (doctors.get(position).getStatus().equals("confirmed")){
            holder.confirmBtn.setText("Unconfirm");
        }else {
            holder.confirmBtn.setText("Confirm");
        }


    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public class doctorsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView userNameTv;
        Button confirmBtn;
        CircleImageView userImageView;
        confirmDoctors confirmDoctors;
        public doctorsHolder(@NonNull View itemView, final confirmDoctors confirmDoctors) {
            super(itemView);
            this.confirmDoctors = confirmDoctors;
            userImageView = itemView.findViewById(R.id.admin_docRv_Imageview);
            userNameTv = itemView.findViewById(R.id.admin_docRv_userNameTv);
            confirmBtn = itemView.findViewById(R.id.admin_docRv_ConfirmBtn);

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDoctors.onConfirmClicked(getLayoutPosition());
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            confirmDoctors.onDoctorClicked(getLayoutPosition());
        }
    }

    public interface confirmDoctors{
        void onConfirmClicked(int positon);
        void onDoctorClicked(int position);
    }
}
