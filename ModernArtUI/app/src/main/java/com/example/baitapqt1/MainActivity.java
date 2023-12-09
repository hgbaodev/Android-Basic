package com.example.baitapqt1;

import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private FrameLayout container;
    private SeekBar seekBar;
    private String currentLayout = "linear";

    private String layoutOption = "linear";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);
        seekBar = findViewById(R.id.seekBar);

        setLayout("linear");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Cập nhật màu sắc của các hình chữ nhật khi giá trị của SeekBar thay đổi
                updateRectanglesColor(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Không cần xử lý khi bắt đầu kéo SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Không cần xử lý khi kết thúc kéo SeekBar
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_more_info) {
            showMoreInformationDialog();
            return true;
        } else if (id == R.id.action_change_layout) {
            // Chọn một layout mới khi người dùng nhấp vào Options Menu
            changeLayout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeLayout() {
        // Hiển thị một Dialog để cho người dùng chọn layout mới
        final CharSequence[] items = {"Linear", "Relative", "Table"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Layout");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Lưu layout hiện tại
                String selectedLayout = items[item].toString().toLowerCase();
                setLayout(selectedLayout);
            }
        });
        builder.show();
    }

    private void setLayout(String layoutType) {
        // Xóa tất cả các child view trong container
        container.removeAllViews();

        // Thiết lập layout mới dựa trên loại layout
        if (layoutType.equals("linear")) {
            layoutOption = "linear";
            getLayoutInflater().inflate(R.layout.linear_layout, container, true);
        } else if (layoutType.equals("relative")) {
            layoutOption = "relative";
            getLayoutInflater().inflate(R.layout.relative_layout, container, true);
        } else if (layoutType.equals("table")) {
            layoutOption = "table";
            getLayoutInflater().inflate(R.layout.table_layout, container, true);
        } else {
            Toast.makeText(this, "Unknown layout type", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lưu layout hiện tại
        currentLayout = layoutType;
    }

    private void showMoreInformationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("More Information");
        builder.setMessage("Hello, we are group 16.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    private void updateRectanglesColor(int progress) {
        Random random = new Random();
        int randomValue;
        int alpha;
        switch (layoutOption) {
            case "linear":
                LinearLayout linear = findViewById(R.id.linear);
                for (int i = 0; i < linear.getChildCount(); i++) {
                    View childView = linear.getChildAt(i);

                    if (childView instanceof LinearLayout) {
                        LinearLayout linearLayoutChild = (LinearLayout) childView;

                        for(int j = 0; j < linearLayoutChild.getChildCount(); j++) {
                            randomValue = random.nextInt(256);
                            alpha = random.nextInt(256);
                            int color = Color.argb(alpha, progress - randomValue + alpha, progress, randomValue);
                            linearLayoutChild.getChildAt(j).setBackgroundColor(color);
                        }
                    }
                }
                break;
            case "relative":
                RelativeLayout relative = findViewById(R.id.relative);
                for (int i = 0; i < relative.getChildCount(); i++) {
                    View childView = relative.getChildAt(i);

                    randomValue = random.nextInt(256);
                    alpha = random.nextInt(256);
                    int color = Color.argb(alpha, progress - randomValue + alpha, progress, randomValue);
                    childView.setBackgroundColor(color);
                }
                break;
            case "table":
                TableRow row = findViewById(R.id.tableRow);
                for (int i = 0; i < row.getChildCount(); i++) {
                    View childView = row.getChildAt(i);
                    if (childView instanceof TableLayout) {
                        TableLayout childTable = (TableLayout) childView;
                        for(int j = 0; j < childTable.getChildCount(); j++) {
                            randomValue = random.nextInt(256);
                            alpha = random.nextInt(256);
                            int color = Color.argb(alpha,progress - randomValue + alpha, progress, randomValue);
                            childTable.getChildAt(j).setBackgroundColor(color);
                        }
                    }
                }
                break;
        }

    }
}