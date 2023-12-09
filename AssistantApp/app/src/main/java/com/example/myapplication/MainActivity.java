package com.example.myapplication;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private CardView cvCamera, cvQr, cvMail, cvContact;
    private static final int CAMERA_PERMISSION_CODE = 101;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    public void initViews(){
        cvCamera = findViewById(R.id.cvCamera);
        cvCamera.setOnClickListener(this);
        cvQr = findViewById(R.id.cvQr);
        cvQr.setOnClickListener(this);
        cvMail = findViewById(R.id.cvMail);
        cvMail.setOnClickListener(this);
        cvContact = findViewById(R.id.cvContact);
        cvContact.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id==R.id.cvQr){
            scannerEvent();
        }else if(id==R.id.cvCamera){
            cameraEvent();
        }else if(id==R.id.cvMail){
            mailEvent();
        }else if(id==R.id.cvContact){
            contactEvent();
        }
    }
    public void scannerEvent(){
        Intent scannerIntent = new Intent(MainActivity.this, ScanerCodeActivity.class);
        startActivity(scannerIntent);
    }
    public void cameraEvent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntentLauncher.launch(takePictureIntent);
    }
    public void mailEvent(){
        Intent mailIntent = new Intent(MainActivity.this, SendMailActivity.class);
        startActivity(mailIntent);
    }
    public void contactEvent(){
        Intent contactsIntent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
        startActivity(contactsIntent);
    }

    public ActivityResultLauncher<Intent> cameraIntentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data != null) {
                            Intent imageSavedIntent = new Intent(MainActivity.this, SaveImageActivity.class);
                            imageSavedIntent.putExtras(data.getExtras());
                            startActivity(imageSavedIntent);
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "Camera cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền sử dụng camera", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Quyền sử dụng camera bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }
}