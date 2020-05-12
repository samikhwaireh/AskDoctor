package com.example.askdoctors.Activities.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.askdoctors.Activities.Model.Message;
import com.example.askdoctors.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private Context mContext;
    private List<Message> mMessage;

    private final int MSG_LEFT = 0;
    private final int MSG_RIGHT = 1;

    private String imageurl;

    private FirebaseUser firebaseUser;

    public MessagesAdapter(Context mContext, List<Message> mMessage, String imageurl) {
        this.mContext = mContext;
        this.mMessage = mMessage;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        Message message = mMessage.get(position);
        holder.message_text.setText(message.getMessage());

        if (imageurl.equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        //get last message
        if (position == mMessage.size() - 1) {
            if (message.getIsSeen().equals("true")) {
                holder.isSeen.setText("Seen");
            } else {
                holder.isSeen.setText("Delivered");
            }
        } else {
            holder.isSeen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mMessage.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profile_image;
        TextView message_text, isSeen;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            message_text = itemView.findViewById(R.id.msg_text);
            profile_image = itemView.findViewById(R.id.profile_image);
            isSeen = itemView.findViewById(R.id.seen);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mMessage.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }
    }
}