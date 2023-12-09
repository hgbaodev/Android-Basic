package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class ScanerCodeActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;
    private TextView tempTv;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaner_code);
        initViews();
        scanCodeEvent();

    }
    public void initViews(){
        scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        tempTv = (TextView) findViewById(R.id.temp);
    }
    public void scanCodeEvent(){
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        coreEvents(result.getText().trim());
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }
    public void coreEvents(String text){
        tempTv.setText(text);
        if(text.startsWith("http")) {
            // url or image
            openUrl(text);
        }else if(text.startsWith("MAILTO")){
            String text_arr[] = text.split(":");
            sendMail(text_arr[1], "","");
        }else if(text.startsWith("MATMSG:")){
            // mail MATMSG:TO:yourmail;SUB:sub;BODY:content;
            String text_arr[] = text.split(";");
            String mailTo = text_arr[0].split(":")[2];
            String subject = text_arr[1].split(":")[1];
            String content = text_arr[2].split(":")[1];
            sendMail(mailTo, subject,content);
        }else if(text.startsWith("SMSTO:")) {
            // sms SMSTO:phonenumber:content
            String text_arr[] = text.split(":");
            sendSMS(text_arr[1], text_arr[2]);
        }else{
            // exception
            Toast.makeText(this, text,Toast.LENGTH_LONG).show();
        }

    }
    public void sendMail(String mailTo, String subject, String content){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailTo});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }
    public void sendSMS(String smsTo, String content ){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + smsTo));
        intent.putExtra("sms_body", content);
        startActivity(intent);
    }
    public void openUrl(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}