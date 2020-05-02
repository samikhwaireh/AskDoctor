package com.example.askdoctors.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.askdoctors.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference customerReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView login = findViewById(R.id.log_in);
        final TextView birthday_screen = findViewById(R.id.birthday);
        final EditText firstName = findViewById(R.id.first_name);
        final EditText lastName = findViewById(R.id.last_name);


        Button pick_birthday = findViewById(R.id.pick_btn);
        Button signUP_btn = findViewById(R.id.sign_up_btn);

        final RadioGroup genderGroup = findViewById(R.id.gender_radio);

        final EditText Email = findViewById(R.id.email_input);
        final EditText Password = findViewById(R.id.password_input);

        //on click on sign up button
        signUP_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(firstName, lastName, birthday_screen, Email, Password, genderGroup);
            }
        });

        //on click on login statement
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-> back to login activity
                startLogInActivity();
            }
        });

        //when user pick birthday date
        pick_birthday.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                pickBirthday(birthday_screen);
            }
        });
    }

    //functions start////////////

    private void startLogInActivity(){
        Intent intent;
        intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void closeKeyboard(){
        View view = SignUpActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void pickBirthday(final TextView screen){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        closeKeyboard(); // close picker dialog

        // -> create dialog with date picker
        DatePickerDialog dpd = new DatePickerDialog(this, new OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int dayOfMonth) {
                screen.setText(dayOfMonth + "/" + (mMonth+1) + "/" + mYear); // print picked date to EditText
            }
        }, year, month, day);
        dpd.show();
    }

    private void EmailAndPasswordAuthentication(final String Email, final String Password, final String firstName, final String lastName,
                                                final String gender, final String birthday){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                try {
                    if (task.isSuccessful()){

                        firebaseUser = firebaseAuth.getCurrentUser();
                        firebaseDatabase = FirebaseDatabase.getInstance();
                        customerReference = firebaseDatabase.getReference("Users").child(firebaseUser.getUid());

                        final HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", firebaseUser.getUid());
                        hashMap.put("firstName", firstName);
                        hashMap.put("lastName", lastName);
                        hashMap.put("gender", gender);
                        hashMap.put("birthday", birthday);
                        hashMap.put("email", Email);
                        hashMap.put("password", Password);

                        AlertDialog.Builder alert = new AlertDialog.Builder(SignUpActivity.this);
                        alert.setTitle("Doctor or Patient");
                        alert.setMessage("Are you a DOCTOR ?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference doctorReference = firebaseDatabase.getReference("Doctors")
                                        .child(firebaseUser.getUid());

                                hashMap.put("accType", "doctor");
                                //hashMap.put("Diploma", null);
                                //hashMap.put("University", null);
                                //hashMap.put("Graduate", null);
                                hashMap.put("Status", "Waiting");

                                Intent intent = new Intent(SignUpActivity.this, DoctorsProof.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("map", hashMap);
                                startActivity(intent);

                                /*doctorReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toasty.info(SignUpActivity.this ,
                                                "One step remained...",
                                                Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(SignUpActivity.this, DoctorsProof.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toasty.error(SignUpActivity.this,
                                                "ERROR: "+ Objects.requireNonNull(e.getMessage()).toString()
                                                , Toast.LENGTH_LONG).show();
                                    }
                                });*/
                            }
                        });
                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                hashMap.put("accType", "customer");

                                customerReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toasty.success(SignUpActivity.this ,
                                                "Your account has been created successfully",
                                                Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toasty.error(SignUpActivity.this,
                                                "ERROR: "+ Objects.requireNonNull(e.getMessage()).toString()
                                                , Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });

                        alert.show();

                    }
                }
                catch (Exception e){
                    Toasty.error(SignUpActivity.this ,
                            "ERROR: "+ e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.error(SignUpActivity.this,
                        "ERROR: "+ e.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signUp(EditText firstName, EditText lastName, TextView birthday,
                        EditText email, EditText password, RadioGroup genderGroup){

        String first_name = firstName.getText().toString().trim();
        String last_name = lastName.getText().toString().trim();
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        String birthday_date = birthday.getText().toString().trim();
        String Gender = "";

        int gender_id = genderGroup.getCheckedRadioButtonId();
        switch(gender_id){
            case R.id.male:
                Gender = "male";
                break;
            case R.id.female:
                Gender = "female";
                break;
            case R.id.other:
                Gender = "other";
                break;
        }


        if (TextUtils.isEmpty(first_name)){
            firstName.setError("please enter your first name");
            firstName.requestFocus();
        } else if (TextUtils.isEmpty(last_name)){
            lastName.setError("please enter your last name");
            lastName.requestFocus();
        } else if (TextUtils.isEmpty(Email)){
            email.setError("please enter your email name");
            email.requestFocus();
        } else if (TextUtils.isEmpty(Password) || Password.length() < 6){
            password.setError("please make sure that your password length is more than 6 characters");
            password.requestFocus();
        } else if (TextUtils.isEmpty(birthday_date)){
            birthday.setError("please select your birthday");
            birthday.requestFocus();
        } else if (Gender.equals("")){
            Toast.makeText(this, "please select your gender", Toast.LENGTH_LONG).show();
        } else {
            EmailAndPasswordAuthentication(Email, Password, first_name, last_name, Gender, birthday_date);
        }
    }
}
