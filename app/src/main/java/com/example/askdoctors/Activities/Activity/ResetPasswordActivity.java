package com.example.askdoctors.Activities.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.askdoctors.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText email_forget;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ImageView back = findViewById(R.id.backto);
        email_forget = findViewById(R.id.email_forget);
        Button submit = findViewById(R.id.submit_forget);

        auth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_forget.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ResetPasswordActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this, "The email has been sent", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                            } else {
                                String msg = Objects.requireNonNull(task.getException()).getMessage();
                                Toast.makeText(ResetPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}
