package com.example.askdoctors.Activities.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.askdoctors.Activities.Model.Doctors;
import com.example.askdoctors.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

/*
kısa anlatım, girilen bilgilerin geçerli veya geçersiz olduğundan emin olmak için kullanılan fonksiyon: checkInfo()
kullanıcı save button'una bastığında ilk önce bu fonksiyon çağırılıyor.
eğer gerekli bilgiler doğru şekilde girildiyse updloadDocProof(View view) fonksiyonu çalışacak ve ilk önce seçilen
resmi firebase storage'de belirlenen bir yere reference vererek kaydedilecek. kaydetme işlemi başarılı olduktan sonra
kaydedilen dosyaya (firebase'teki dosya) yeni bir reference veriliyor bunun da sebebi bu dosyanın indirme linkini almak için.
indirme linki alındıktan sonra kullanıcının firebase realtime'da kaydedilen bilgileri güncelleniyor bir database referece'i
vererek.

resim seçmek için kullaılan fonksiyon : openGalleryForDiploma(View view)
bu fonksiyon çağırıldığında önce uygulamaya fotoğraflara erişim izni verilip verilmediğini kontrol eder
eğer verildiyse direkt fotoğraflara geçer, eğer verilmediyse önce kullanıcıdan bir izin ister sonra eğer kullanıcı
kabul ederse fotoğraflara geçer.
kullanıcıdan izin isteme komutu : ActivityCompat.requestPermissions
kullanıcının izne kabul etmesini çeken fonksiyon : onRequestedPermissionsResult()
seçilen fotoğrafı alan fonksiyon : onActivityResult()
*/
public class DoctorsProof extends AppCompatActivity {

    EditText universityEt,graduateEt;
    Bitmap selectedImage;
    Uri uri;
    ImageView imageView;
    Button saveBtn;
    String university, graduate;
    ProgressBar progressBar;

    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docktors_proof);

        universityEt = findViewById(R.id.docProof_universityEt);
        graduateEt = findViewById(R.id.docProof_graduateEt);
        imageView = findViewById(R.id.docProof_ImageView);
        saveBtn = findViewById(R.id.docProof_saveBtn);
        progressBar = findViewById(R.id.docProof_Progressbar);
        progressBar.setVisibility(View.GONE);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }



    public void openGalleryForDiploma(View view){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
            , 10);

        }else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 20);
        }
    }

    //kullanıcının izne cevabını öğrenmek için kullanılan fonksiyon
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 10 && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 20);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //seçilen resmi alabilmek için kullanılan fonksiyon
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 20)
            if (resultCode == RESULT_OK && data != null){

            uri = data.getData();
            try {


                if (Build.VERSION.SDK_INT>=28){
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), uri);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedImage);
                }else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    imageView.setImageBitmap(selectedImage);
                }
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void updloadDocProof(View view) {

        saveBtn.setEnabled(false);


        closeKeyboard();


        university = universityEt.getText().toString().trim();
        graduate = graduateEt.getText().toString().trim();

        boolean result = checkInfo();

        if (result == false){

            progressBar.setVisibility(View.VISIBLE);

            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            Intent intent = getIntent();
            final HashMap<String, String> userInfo;
            userInfo = (HashMap<String, String>)intent.getSerializableExtra("map");
            assert userInfo != null;
            userInfo.put("diploma", uri.toString());
            userInfo.put("university", university);
            userInfo.put("graduate", graduate);
            userInfo.put("profileImage", null);

            String email = userInfo.get("email");
            String password = userInfo.get("password");

            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    final String id = firebaseAuth.getUid();
                    StorageReference storageReference = firebaseStorage.getReference();

                    UUID uuid = UUID.randomUUID();


                    final String path = "DoctorsProof/" + id + "/" + uuid + ".jpg";

                    storageReference.child(path).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            StorageReference getDownloadUrl = firebaseStorage.getReference(path);

                            getDownloadUrl.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //Map<String, Object> userInfo = new HashMap<>();


                                    DatabaseReference databaseReference = firebaseDatabase.getReference("Doctors")
                                            .child(id);

                                    databaseReference.setValue(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            progressBar.setVisibility(View.GONE);
                                            Intent intent = new Intent(DoctorsProof.this, SetProfileInfo_Activity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("accType", "Doctors");
                                            intent.putExtra("firstName", userInfo.get("firstName"));
                                            intent.putExtra("lastName", userInfo.get("lastName"));
                                            startActivity(intent);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            progressBar.setVisibility(View.GONE);
                                            Toasty.error(DoctorsProof.this, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_LONG).show();

                                            //kaydetme button'u tekrar etkinleştiriliyor
                                            saveBtn.setEnabled(true);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    progressBar.setVisibility(View.GONE);
                                    Toasty.error(DoctorsProof.this, Objects.requireNonNull(e.getMessage()),Toast.LENGTH_LONG).show();


                                    saveBtn.setEnabled(true);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressBar.setVisibility(View.GONE);
                            Toasty.error(DoctorsProof.this, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_LONG).show();

                            saveBtn.setEnabled(true);
                        }
                    });
                }
            });



        }else {

            saveBtn.setEnabled(true);
        }
    }


    public void pickGraduateDate(View view){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        closeKeyboard();


        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker datePicker, int myear, int mmonth, int dayOfMonth) {

                graduateEt.setText(dayOfMonth + "/" + (mmonth+1) + "/" + myear);
            }
        }, year, month, day);
        dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpd.show();
    }

    //klavyeyi kapatan fonksiyon
    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    //girilen bililerin geçerliliğini çeken fonksiyon
    private boolean checkInfo(){
        boolean con = false;
        if (selectedImage == null){
            con = true;
        }
        if (TextUtils.isEmpty(university)){
            con = true;
            universityEt.setError("Empty!");
            universityEt.requestFocus();
        }
        if (TextUtils.isEmpty(graduate)){
            con = true;
            graduateEt.setError("Empty!");
            graduateEt.requestFocus();
        }
        return con;
    }
}
