package com.example.askdoctors.Activities.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askdoctors.Activities.Activity.AskQuestionActivity;
import com.example.askdoctors.Activities.Model.answers;
import com.example.askdoctors.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.answersHolder> {

    ArrayList<answers> answers;

    public AnswersAdapter(ArrayList<com.example.askdoctors.Activities.Model.answers> answers) {
        this.answers = answers;
    }

    @NonNull
    @Override
    public answersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.answers_row_adapter, parent, false);

        return new answersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull answersHolder holder, int position) {

        Picasso.get().load(answers.get(position).getProfileImage()).into(holder.imageView);
        holder.commentTv.setText(answers.get(position).getComment());
        holder.userNameTv.setText(answers.get(position).getUserName());

    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public class answersHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView commentTv,userNameTv;

        public answersHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.answeradapter_userImageView);
            commentTv = itemView.findViewById(R.id.answeradapter_commentTv);
            userNameTv = itemView.findViewById(R.id.answeradapter_userNameTv);
        }
    }
}
