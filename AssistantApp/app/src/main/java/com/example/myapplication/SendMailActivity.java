package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SendMailActivity extends AppCompatActivity {
    private Button btnSend;
    private EditText edtTo, edtSubject, edtContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);
        initViews();
        initEvents();
    }
    public void initViews(){
        this.btnSend = (Button) findViewById(R.id.btn);
        this.edtTo = (EditText) findViewById(R.id.edtTo);
        this.edtSubject = (EditText) findViewById(R.id.edtSubject);
        this.edtContent = (EditText) findViewById(R.id.edtContent);
    }
    public void initEvents(){
        this.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mailTo = edtTo.getText().toString().trim();
                String subject = edtSubject.getText().toString().trim();
                String content = edtContent.getText().toString().trim();
                if(isEmailValid(mailTo)){
                    sendMail(mailTo, subject, content);
                }else{
                    edtTo.setError("Vui lòng nhập đúng email!");
                }
            }
        });
    }
    public void sendMail(String mailTo, String subject, String content){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailTo});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }
    public static boolean isEmailValid(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return (email.matches(emailRegex));
    }

}