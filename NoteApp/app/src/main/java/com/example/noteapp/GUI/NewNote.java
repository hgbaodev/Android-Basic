package com.example.noteapp.GUI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.noteapp.DTO.Note;
import com.example.noteapp.BUS.NoteBUS;
import com.example.noteapp.NotificationReceiver;
import com.example.noteapp.R;
import com.example.noteapp.Util.ConvertTime;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class NewNote extends AppCompatActivity {

    private ImageButton btnSave,btnImage,btnNoti;
    private EditText txtTitle, txtContent;
    private TextView txtTimeNoti;
    private ImageView imgNote,imgBack;
    private Note note;
    private String action;
    private String imgPath = "";
    private int REQUEST_CODE_SELECT_IMAGE = 101;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);
        initView();
        createNotificationChanel();
        blindSaveButton();

        Intent intent=getIntent();
        action = intent.getStringExtra("action");
        if(!action.equalsIgnoreCase("create")) addControls();
        addEvents();

        btnImage.setOnClickListener(v -> {
            selectImage();
        });

        btnNoti.setOnClickListener(v -> {
            showDateTimePicker();
        });

        imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }


    private void initView() {
        txtTitle = findViewById(R.id.txtTitle);
        txtContent = findViewById(R.id.txtContent);
        btnSave = findViewById(R.id.btnSave);
        btnImage = findViewById(R.id.btnImage);
        btnNoti = findViewById(R.id.btnNoti);
        imgNote = findViewById(R.id.imgNote);
        txtTimeNoti = findViewById(R.id.txtTimeNoti);
        imgBack = findViewById(R.id.back);
    }


    private void addControls() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        note = NoteBUS.getNoteById(id);
        txtTitle.setText(note.getTitle());
        txtContent.setText(note.getContent());
        if(!note.getPathImage().equals("")) {
            imgPath = note.getPathImage();
            imgNote.setImageBitmap(BitmapFactory.decodeFile(imgPath));
            imgNote.setVisibility(View.VISIBLE);
        }

        if(!note.getReminderTime().equals("")) {
            txtTimeNoti.setText("Nhắc nhở tôi vào " + note.getReminderTime());
            calendar = ConvertTime.parseCalendar(note.getReminderTime());
            txtTimeNoti.setVisibility(View.VISIBLE);
        }
    }

    private void addEvents() {

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(action.equalsIgnoreCase("create"))
                {
                    processSaveNote(v);
                }
                else
                {
                    processUpdateNote(v);
                }
            }
        });

        txtTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showSaveButton();
                } else {
                    blindSaveButton();
                }
            }
        });

        txtContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showSaveButton();
                } else {
                    blindSaveButton();
                }
            }
        });
    }

    private void blindSaveButton() {
        btnSave.setVisibility(View.INVISIBLE);
    }

    private void showSaveButton() {
        btnSave.setVisibility(View.VISIBLE);
    }

    private void processUpdateNote(View v) {
        blindSaveButton();
        hideKeyboard(v);

        String title = txtTitle.getText().toString();
        String content = txtContent.getText().toString();
        String currentDateTime = getCurrentDateTime();
        String reminderTime = (calendar != null) ? ConvertTime.parseString(calendar) : "";
        Note noteUpdate = new Note(note.getId(),title,content,note.getUpdatedAt(),currentDateTime, imgPath, reminderTime);

        boolean updated = NoteBUS.update(noteUpdate);

        if (updated) {
            if (!reminderTime.equals(note.getReminderTime())) {
                cancelNotification(note.getId());
                scheduleNotification(note.getId(), calendar.getTimeInMillis(), content);
            }
            Toast.makeText(getBaseContext(), "Cập nhật ghi chú thành công!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Không thể cập nhật ghi chú", Toast.LENGTH_LONG).show();
        }
    }

    private void processSaveNote(View v) {
        blindSaveButton();
        hideKeyboard(v);

        String title = txtTitle.getText().toString();
        String content = txtContent.getText().toString();
        String currentDateTime = getCurrentDateTime();
        String reminderTime = (calendar != null) ? ConvertTime.parseString(calendar) : "";
        Note note = new Note(title,content,currentDateTime,currentDateTime, imgPath, reminderTime);
        Note noteCreate = NoteBUS.insert(note);
        // Đặt lịch
        if (calendar != null) {
            scheduleNotification(noteCreate.getId(), calendar.getTimeInMillis(), content);
        }
        Toast.makeText(getBaseContext(), "Thêm ghi chú thành công !", Toast.LENGTH_LONG).show();
        finish();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            if(data !=null) {
                Uri selectImageUri = data.getData();
                if(selectImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imgNote.setImageBitmap(bitmap);
                        imgNote.setVisibility(View.VISIBLE);
                        imgPath = getPath(selectImageUri);
                    } catch (Exception exception) {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    private String getPath(Uri contenturi) {
        String filepath;
        Cursor cursor = getContentResolver()
                .query(contenturi, null, null, null, null);
        if (cursor == null) {
            filepath = contenturi.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filepath = cursor.getString(index);
            cursor.close();
        }
        return filepath;
    }

    private void showDateTimePicker() {
        if(action.equalsIgnoreCase("create") || calendar == null) calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        showTimePicker();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        if(action.equalsIgnoreCase("create") || note.getReminderTime().equals("")) {
            datePickerDialog.setOnCancelListener(dialog -> {
                calendar = null;
            });
        }

        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        txtTimeNoti.setText("Nhắc tôi vào lúc " + ConvertTime.parseString(calendar));
                        txtTimeNoti.setVisibility(View.VISIBLE);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        if(action.equalsIgnoreCase("create") || note.getReminderTime().equals("")) {
            timePickerDialog.setOnCancelListener(dialog -> {
                calendar = null;
            });
        }


        timePickerDialog.show();
    }

    private void scheduleNotification(int noteId, long timeInMillis, String notificationContent) {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("content", notificationContent);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                noteId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }
    }

    private void cancelNotification(int noteId) {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                noteId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public void createNotificationChanel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "NOTE_APP";
            String description = "NOTE_APP";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("NOTE_APP", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}