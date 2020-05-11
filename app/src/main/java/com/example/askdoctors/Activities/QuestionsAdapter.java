package com.example.askdoctors.Activities;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askdoctors.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.questionsHolder> {

    ArrayList<Questions> questions;
    onQuestionClicked questionClicked;
    public QuestionsAdapter(ArrayList<Questions> questions, onQuestionClicked questionClicked) {
        this.questions = questions;
        this.questionClicked = questionClicked;
    }

    @NonNull
    @Override
    public questionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.qusetion_row_adapter, parent, false);

        return new questionsHolder(view,questionClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull questionsHolder holder, int position) {
       if (questions.isEmpty()){
           holder.userNameTv.setVisibility(View.GONE);
           holder.diseaseTv.setText("No asked questions :(");
           holder.questionTv.setVisibility(View.GONE);
           holder.questionImageView.setVisibility(View.GONE);
           holder.userImageView.setVisibility(View.GONE);
           holder.answerBtn.setVisibility(View.GONE);
       }else {
           if (questions.get(position).getImage().equals("noImage")){
               holder.questionImageView.setVisibility(View.GONE);
           }else {
               Picasso.get().load(questions.get(position).getImage()).into(holder.questionImageView);
           }

           Picasso.get().load(questions.get(position).getProfileImage()).into(holder.userImageView);
           holder.questionTv.setText(questions.get(position).getQuestion());
           holder.diseaseTv.setText(questions.get(position).getDisease());
           holder.userNameTv.setText(questions.get(position).getUserName());
       }

    }

    @Override
    public int getItemCount() {
        if (questions.isEmpty()){
            return 1;
        }else {
            return questions.size();
        }
    }

    public class questionsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImageView,questionImageView;
        TextView userNameTv,diseaseTv,questionTv;
        Button answerBtn;
        onQuestionClicked questionClicked;

        public questionsHolder(@NonNull View itemView, final onQuestionClicked questionClicked) {
            super(itemView);
            this.questionClicked = questionClicked;
            userImageView = itemView.findViewById(R.id.questionAdapter_userImage);
            questionImageView = itemView.findViewById(R.id.questionAdapter_questionImage);
            userNameTv = itemView.findViewById(R.id.questionAdapter_userNameTv);
            diseaseTv = itemView.findViewById(R.id.questionAdapter_diseaseTv);
            questionTv = itemView.findViewById(R.id.questionAdapter_QuestionTv);
            answerBtn = itemView.findViewById(R.id.questionAdapter_answerBtn);

            answerBtn.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    questionClicked.deleteQuestion(getLayoutPosition());
                    return true;
                }
            });
        }

        @Override
        public void onClick(View v) {
            questionClicked.answer(getLayoutPosition());
        }
    }

    public interface onQuestionClicked{
        void answer(int position);
        void deleteQuestion(int position);
    }
}
