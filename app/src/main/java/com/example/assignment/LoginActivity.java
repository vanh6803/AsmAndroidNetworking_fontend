package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.assignment.databinding.ActivityLoginBinding;
import com.example.assignment.util.MyTextWatch;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateViews();
            }
        }, 3000);

        binding.clearText.setVisibility(View.GONE);
        MyTextWatch myTextWatch = new MyTextWatch(binding.edtEmail, binding.clearText);
        binding.edtEmail.addTextChangedListener(myTextWatch);

        binding.clearText.setOnClickListener(v->{
            binding.edtEmail.setText("");
        });


    }

    private void animateViews() {
        // Scale down the lottieAnimationView
        final int originalWidth = binding.lottieAnimationView.getWidth();
        final int originalHeight = binding.lottieAnimationView.getHeight();
        final int targetWidth = 900;
        final int targetHeight = 900;

        ValueAnimator widthAnimator = ValueAnimator.ofInt(originalWidth, targetWidth);
        ValueAnimator heightAnimator = ValueAnimator.ofInt(originalHeight, targetHeight);

        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int newWidth = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = binding.lottieAnimationView.getLayoutParams();
                layoutParams.width = newWidth;
                binding.lottieAnimationView.setLayoutParams(layoutParams);
            }
        });

        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int newHeight = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = binding.lottieAnimationView.getLayoutParams();
                layoutParams.height = newHeight;
                binding.lottieAnimationView.setLayoutParams(layoutParams);
            }
        });

//        // Move other views up
//        int translationDistance = 50;
//        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(binding.layoutEd, "translationY", -translationDistance);
//        ObjectAnimator translationYAnimator2 = ObjectAnimator.ofFloat(binding.clearText, "translationY", -translationDistance);

        // Combine all animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000); // Animation duration in milliseconds
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(widthAnimator, heightAnimator);

        // Start the animation
        animatorSet.start();
    }


}