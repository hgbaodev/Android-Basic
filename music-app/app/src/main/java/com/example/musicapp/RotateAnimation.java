package com.example.musicapp;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import de.hdodenhof.circleimageview.CircleImageView;

public class RotateAnimation {

    public static void start(CircleImageView img){
        android.view.animation.RotateAnimation rotateAnimation = new android.view.animation.RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, // Xoay xung quanh trục X ở giữa view
                Animation.RELATIVE_TO_SELF, 0.5f); // Xoay xung quanh trục Y ở giữa view
        rotateAnimation.setDuration(20000); // Thời gian một vòng xoay (milliseconds)
        rotateAnimation.setRepeatCount(Animation.INFINITE); // Lặp vô hạn
        rotateAnimation.setInterpolator(new LinearInterpolator()); // Chuyển động đều
        img.startAnimation(rotateAnimation);
    }
    public static void stop(CircleImageView img){
        img.clearAnimation();
    }
}
