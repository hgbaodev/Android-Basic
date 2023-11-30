package com.example.musicapp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;
public class BreadcrumbView extends LinearLayout {
    public BreadcrumbView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
    }

    public void setPath(List<String> path) {
        removeAllViews();

        int pathSize = path.size();

        for (int i = 4; i < pathSize; i++) {
            String segment = path.get(i);

            TextView textView = new TextView(getContext());
            textView.setText(segment);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            if (i == pathSize - 1) {
                textView.setTypeface(null, Typeface.NORMAL);
            } else {
                textView.setTypeface(null, Typeface.BOLD);
            }

            addView(textView);

            if (i < pathSize - 1) {
                addSeparator();
            }
        }
    }


    private void addSeparator() {
        TextView separator = new TextView(getContext());
        separator.setText(" > ");
        addView(separator);
    }
}

