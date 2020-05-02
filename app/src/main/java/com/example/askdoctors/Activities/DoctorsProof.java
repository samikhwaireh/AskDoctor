package com.example.askdoctors.Activities;

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
import android.widget.Toast;

import com.example.askdoctors.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    //kullanıcıya gösterilen kutular metin almak için
    EditText universityEt,graduateEt;

    //seçilen resim
    Bitmap selectedImage;

    //resim bilgisi
    Uri uri;

    //resmin gösterildiği kutu
    ImageView imageView;

    //kaydetme button'u
    Button saveBtn;

    //kullanıcıdan alınan metinlerin değişkenleri
    String university, graduate;

    //alınan bilgileri firebase'te kaydetmeye gereken değişkenler (FirebaseStorage sınıfı ....)
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

        //firebaseStorage ve FirebaseDatabase sınıfındaki constructor'u oluşturma
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    //kullanıcı hiçbir şey girmeden kaydetmeden geri tuşuna basarsa
    //kullanıcıya uyarı gösterilir ve çıkmak istediğini soran bir uyarı olur
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //uyarı başlığı
        alert.setTitle("EXIT");
        //uyarı metni
        alert.setMessage("If you exit your email will be deleted, are you sure ?");
        //kullanıcı cevabı negatif olursa aynı sayfada kalacak
        //kullanıcı hayır seçeneği basınca
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //kullanıcı çıkmayı seçerse ne olacak
        //kullanıcı evet seçeneğini seçerse bir önceki sayfada girdiği bilgiler silinecek (sign up sayfasında)
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //önceki sayfada girdiği bilgilere reference alınıyor ve silme işlemi yapılıyor
                DatabaseReference databaseReference = firebaseDatabase.getReference("Doctors").child(firebaseUser.getUid());
                databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(DoctorsProof.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(DoctorsProof.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        alert.show();
    }

    //fotoğraflara erişmek için kullanılan foksiyon
    public void openGalleryForDiploma(View view){
        //daha önceden izin verilip verilmediği kontrol ediliyor
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
        //eğer izin kabul edildiyse
        if (requestCode == 10 && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 20);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //seçilen resmi alabilmek için kullanılan fonksiyon
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 20 && resultCode == RESULT_OK && data != null){
            //resmin ilgileri alınıyor
            uri = data.getData();
            try {

                //kullanıcının telefonu hangi android sürümü kullandığı kontrol ediliyor
                //yeni sürümlerde sdk'si 28 ve daha güncel sürümlerde resi alabilmek için imageDecoder komutu kullanılıyor
                //fakat daha eski sürümlerde getbitmap komutu kullanılıyor
                if (Build.VERSION.SDK_INT>=28){
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedImage);
                }else {
                    selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imageView.setImageBitmap(selectedImage);
                }
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //firebase'te bilgileri kaydetmek için kullanılan fonksiyon
    public void updloadDocProof(View view) {
        //save button'u etkinsizleştiriliyor (bir kezden fazla basılırsa aynı işlemi aynı resmi tekrar tekrar kaydetmemek için)
        saveBtn.setEnabled(false);

        //klavye açıksa kapatılıyor
        closeKeyboard();

        //girilen bilgiler alınıyor
        university = universityEt.getText().toString().trim();
        graduate = graduateEt.getText().toString().trim();
        //girilen bilgilerin geçerli olup olmadığı kontrol ediliyor
        boolean result = checkInfo();
        //eğer geçerliyse
        if (result == false){
            //önce resmi firebase storage'de tam nerede kaydedileceği belirleniyor
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            StorageReference storageReference = firebaseStorage.getReference();
            //rastgele isim oluşturma
            UUID uuid = UUID.randomUUID();

            //DoctorsProofs kalsörün içinde kullanıcıların id'leri ile isimli klasörler bulunacak
            //resim kullanıcının id'sini taşıyan klasörün içinde resme rastgele bir isim vererek bir reference belirlerniyor

            //Doctor/kullanıcıID/rastgeleIsimim.jpg
            final String path = "DoctorsProof/" + firebaseUser.getUid() + "/" + uuid + ".jpg";
            //resim kaydediliyor
            storageReference.child(path).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //kaydedilen resme reference alınıyor
                         StorageReference getDownloadUrl = firebaseStorage.getReference(path);

                         //resmin indirme linki alınıyor
                         getDownloadUrl.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                             @Override
                             public void onSuccess(Uri uri) {
                                 //firebase realtime'da güncellemek istenilen kullanıcının bilgileri giriliyor
                                 //Map<String, Object> userInfo = new HashMap<>();
                                 Intent intent = getIntent();
                                 HashMap<String, String> userInfo;
                                 userInfo = (HashMap<String, String>)intent.getSerializableExtra("map");
                                 assert userInfo != null;
                                 userInfo.put("Diploma", uri.toString());
                                 userInfo.put("University", university);
                                 userInfo.put("Graduate", graduate);
                                 //kullanıcının bilgiler'e reference veriliyor (firebase realtime'dekine)
                                 DatabaseReference databaseReference = firebaseDatabase.getReference("Doctors")
                                         .child(firebaseUser.getUid());
                                 //bilgiler güncelleniyor
                                 databaseReference.setValue(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void aVoid) {

                                         //kullanıcıya hesabın oluşturulduğu söyleniyor ve admin hesabın onaylaması bekleniyor
                                        Toasty.info(DoctorsProof.this, "Account created successfully, " +
                                                "waiting for admin confirmation", Toast.LENGTH_LONG).show();

                                        //kullanıcı giriş sayfasına yönlendiriliyor
                                        Intent intent = new Intent(DoctorsProof.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                     }
                                 }).addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull Exception e) {
                                         //güncelleme işlemi başarısız olursa hata ayrıntıları gösteriliyor
                                         Toasty.error(DoctorsProof.this, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_LONG).show();

                                         //kaydetme button'u tekrar etkinleştiriliyor
                                         saveBtn.setEnabled(true);
                                     }
                                 });
                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 //indirme linki alma işlemi başarısız olursa hata ayrıntıları gösteriliyor
                                 Toasty.error(DoctorsProof.this, Objects.requireNonNull(e.getMessage()),Toast.LENGTH_LONG).show();

                                 //kaydetme button'u tekrar etkinleştiriliyor
                                 saveBtn.setEnabled(true);
                             }
                         });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //resmi kaydetme işlemi başarısız olursa hata ayrıntıları gösteriliyor
                    Toasty.error(DoctorsProof.this, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_LONG).show();

                    //kaydetme button'u tekrar etkinleştiriliyor
                    saveBtn.setEnabled(true);
                }
            });
        }else {
            //kaydetme button'u tekrar etkinleştiriliyor
            saveBtn.setEnabled(true);
        }
    }

    //mezun tarihi seçmek için takvimi penceresini açan fonksiyon
    public void pickGraduateDate(View view){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        //takvim penceresi açıldığında klavyenin açılması engelleyen fonksiyon
        closeKeyboard();

        //tarih seçildiğinde ne olacak
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker datePicker, int myear, int mmonth, int dayOfMonth) {
                //seçilen tarihi mezun kutusunda gösteriliyor
                graduateEt.setText(dayOfMonth + "/" + (mmonth+1) + "/" + myear);
            }
        }, day, month, year);

        //takvin penceresi gösteriliyor
        dpd.show();
    }

    //klavyeyi kapatan fonksiyon
    public void closeKeyboard(){
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
