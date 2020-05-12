package com.example.askdoctors.Activities.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askdoctors.Activities.Activity.ChatActivity;
import com.example.askdoctors.Activities.Model.User;
import com.example.askdoctors.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserChatListAdapter extends RecyclerView.Adapter<UserChatListAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;

    public UserChatListAdapter(Context mContext, List<User> mUsers, boolean isChat){
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public UserChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserChatListAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserChatListAdapter.ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.username.setText(user.getFirstName()+user.getLastName());

        if (!TextUtils.isEmpty(user.getProfileImage())){
            Picasso.get().load(user.getProfileImage()).into(holder.profile_image);
        } else {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }


        if (isChat){
            if (user.getOnline().equals("online")){
                holder.on_img.setVisibility(View.VISIBLE);
                holder.off_img.setVisibility(View.GONE);
            }
            else {
                holder.on_img.setVisibility(View.GONE);
                holder.off_img.setVisibility(View.VISIBLE);
            }
        } else {
            holder.on_img.setVisibility(View.GONE);
            holder.off_img.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext , ChatActivity.class);
                intent.putExtra("accType", "Users");
                intent.putExtra("userId" , user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mUsers.isEmpty()){
            return 0;
        }else {
            return mUsers.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView profile_image;
        public TextView username;
        private ImageView on_img, off_img;


        ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username_browse);
            profile_image = itemView.findViewById(R.id.profile_img);
            on_img = itemView.findViewById(R.id.on_img);
            off_img = itemView.findViewById(R.id.off_img);
        }
    }
}
