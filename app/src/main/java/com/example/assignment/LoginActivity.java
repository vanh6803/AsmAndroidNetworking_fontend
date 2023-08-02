package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.example.assignment.databinding.ActivityLoginBinding;
import com.example.assignment.util.MyTextWatch;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

@SuppressLint("SetTextI18n")
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    String CODE_SIGN = "login";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        initViews();

    }

    private void initViews() {
        //delay animation
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateViews();
            }
        }, 3000);

        // watch text input
        binding.clearText.setVisibility(View.GONE);
        MyTextWatch myTextWatch = new MyTextWatch(binding.edtEmail, binding.clearText);
        binding.edtEmail.addTextChangedListener(myTextWatch);

        binding.errorConfirmPassword.setVisibility(View.GONE);
        binding.errorPassword.setVisibility(View.GONE);
        binding.errorEmail.setVisibility(View.GONE);

        binding.clearText.setOnClickListener(v -> {
            binding.edtEmail.setText("");
        });

        //Change the text and layout of your choice
        switchText();

        //login or sign up
        binding.btnLogin.setOnClickListener(v -> {
            switch (CODE_SIGN) {
                case "login":
                    login();
                    break;
                case "signup":
                    signup();
                    break;
            }
        });


        //Login with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnLoginWithGoogle.setOnClickListener(v -> {
            loginInWithGoogle();
        });
    }




    private void signup() {
        String email = binding.edtEmail.getText().toString();
        String password = binding.edtPassword.getText().toString();
        String confirmPassword = binding.edtConfirmPassword.getText().toString();

        if (validate(email, password) && confirmPassword.equals(password)) {
            binding.errorConfirmPassword.setVisibility(View.GONE);
            binding.errorConfirmPassword.setText("");
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "create account success", Toast.LENGTH_SHORT).show();
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    binding.tvCreateAccount.setText("Create new account?");
                    binding.btnLogin.setText("Login");
                    binding.edtConfirmPasswordLayout.setVisibility(View.GONE);
                    binding.btnLoginWithGoogle.setVisibility(View.VISIBLE);
                    binding.btnLoginWithFacebook.setVisibility(View.VISIBLE);
                    CODE_SIGN = "login";
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(LoginActivity.this, "create account success failed.", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (!confirmPassword.equals(password)) {
            binding.errorConfirmPassword.setVisibility(View.VISIBLE);
            binding.errorConfirmPassword.setText("Enter the correct password");
        }
    }

    private void login() {
        String email = binding.edtEmail.getText().toString();
        String password = binding.edtPassword.getText().toString();
        if (validate(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "login success", Toast.LENGTH_SHORT).show();
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //Change the text and layout of your choice
    private void switchText() {
        binding.tvCreateAccount.setOnClickListener(v -> {
            String text = binding.tvCreateAccount.getText().toString();
            switch (text) {
                // create new account
                case "Create new account?":
                    binding.tvCreateAccount.setText("Already have an account?");
                    binding.btnLogin.setText("Sign up");
                    binding.edtConfirmPasswordLayout.setVisibility(View.VISIBLE);
                    binding.btnLoginWithGoogle.setVisibility(View.GONE);
                    binding.btnLoginWithFacebook.setVisibility(View.GONE);
                    CODE_SIGN = "signup";
                    break;
                // have a account
                case "Already have an account?":
                    binding.tvCreateAccount.setText("Create new account?");
                    binding.btnLogin.setText("Login");
                    binding.edtConfirmPasswordLayout.setVisibility(View.GONE);
                    binding.btnLoginWithGoogle.setVisibility(View.VISIBLE);
                    binding.btnLoginWithFacebook.setVisibility(View.VISIBLE);
                    CODE_SIGN = "login";
                    break;
            }
        });
    }

    private boolean validate(String email, String password) {
        boolean valid = true;

        if (email.isEmpty()) {
            binding.errorEmail.setVisibility(View.VISIBLE);
            binding.errorEmail.setText("Please enter email");
            valid = false;
        } else {
            binding.errorEmail.setVisibility(View.GONE);
            binding.errorEmail.setText("");
        }

        if (password.isEmpty()) {
            binding.errorPassword.setVisibility(View.VISIBLE);
            binding.errorPassword.setText("enter password");
            valid = false;
        } else {
            binding.errorPassword.setVisibility(View.GONE);
            binding.errorPassword.setText("");
        }

        return valid;
    }

    //Create miniature animations
    private void animateViews() {
        // Scale down the lottieAnimationView
        final int originalWidth = binding.lottieAnimationView.getWidth();
        final int originalHeight = binding.lottieAnimationView.getHeight();
        final int targetWidth = 900;
        final int targetHeight = 900;

        ValueAnimator widthAnimator = ValueAnimator.ofInt(originalWidth, targetWidth);
        ValueAnimator heightAnimator = ValueAnimator.ofInt(originalHeight, targetHeight);

        widthAnimator.addUpdateListener(animation -> {
            int newWidth = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = binding.lottieAnimationView.getLayoutParams();
            layoutParams.width = newWidth;
            binding.lottieAnimationView.setLayoutParams(layoutParams);
        });

        heightAnimator.addUpdateListener(animation -> {
            int newHeight = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = binding.lottieAnimationView.getLayoutParams();
            layoutParams.height = newHeight;
            binding.lottieAnimationView.setLayoutParams(layoutParams);
        });
        // Combine all animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000); // Animation duration in milliseconds
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(widthAnimator, heightAnimator);

        // Start the animation
        animatorSet.start();
    }

    int RC_SIGN_IN = 40;

    //login with google
    private void loginInWithGoogle() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                    FirebaseUser user = mAuth.getCurrentUser();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    Toast.makeText(LoginActivity.this, "login success", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}