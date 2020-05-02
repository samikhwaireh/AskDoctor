package com.example.askdoctors.Activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.askdoctors.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        SharedPreferences sharedPreferences = getSharedPreferences("login", 0);
        String isChecked = sharedPreferences.getString("isChecked", "0");
        if (isChecked.equals("1")){
            if(firebaseUser != null){
                startActivity(new Intent(this , HomeActivity.class));
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText Email = findViewById(R.id.email_input);
        final EditText Password = findViewById(R.id.password_input);
        TextView signUp = findViewById(R.id.sign_up);
        final Switch remember = findViewById(R.id.switcher);

        Button login = findViewById(R.id.login_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_txt = Email.getText().toString().trim();
                String password_txt = Password.getText().toString().trim();

                if(TextUtils.isEmpty(email_txt)){
                    Toast.makeText(LoginActivity.this , "The email is not valid" , Toast.LENGTH_SHORT).show();
                    Email.setError("Empty!");
                    Email.requestFocus();
                }
                else if(TextUtils.isEmpty(password_txt)){
                    Toast.makeText(LoginActivity.this , "Enter your password!" , Toast.LENGTH_SHORT).show();
                    Password.setError("Empty!");
                    Password.requestFocus();
                }
                else if (remember.isChecked()){
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE)
                            .edit();
                    editor.putString("isChecked", "1");
                    editor.apply();
                    logInUser(email_txt, password_txt);
                }
                else{
                    logInUser(email_txt,password_txt);
                }
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpActivity();
            }
        });

    }


    private void logInUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginActivity.this , "login successfully" , Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this , HomeActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,
                        e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openSignUpActivity(){
        Intent intent;
        intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}