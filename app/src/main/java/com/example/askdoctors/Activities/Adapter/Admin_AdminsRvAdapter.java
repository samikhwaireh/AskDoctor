package com.example.askdoctors.Activities.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askdoctors.Activities.Model.Admins;
import com.example.askdoctors.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Admin_AdminsRvAdapter extends RecyclerView.Adapter<Admin_AdminsRvAdapter.adminsHolder> {

    ArrayList<Admins> admins;

    public Admin_AdminsRvAdapter(ArrayList<Admins> admins) {
        this.admins = admins;
    }

    @NonNull
    @Override
    public adminsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.admin_doctors_adapter, parent, false);
        return new adminsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adminsHolder holder, int position) {
        Picasso.get().load(admins.get(position).getProfileImage()).into(holder.userImageView);
        holder.userNameTv.setText(admins.get(position).getFirstName() + " " + admins.get(position).getLastName());
        holder.confirmBtn.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return admins.size();
    }

    public class adminsHolder extends RecyclerView.ViewHolder{

        TextView userNameTv;
        Button confirmBtn;
        CircleImageView userImageView;

        public adminsHolder(@NonNull View itemView) {
            super(itemView);

            userImageView = itemView.findViewById(R.id.admin_docRv_Imageview);
            userNameTv = itemView.findViewById(R.id.admin_docRv_userNameTv);
            confirmBtn = itemView.findViewById(R.id.admin_docRv_ConfirmBtn);
        }
    }
}
