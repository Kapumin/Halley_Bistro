package com.abjt.halleybistro.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abjt.halleybistro.R;
import com.abjt.halleybistro.models.User;
import com.abjt.halleybistro.utils.Constants;
import com.abjt.halleybistro.utils.Toaster;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    //Views
    private EditText inputMobileNumber, inputUsername, inputPassword, inputConfirmPassword;
    private TextView textSignIn;
    private MaterialButton signUpButton;
    private ProgressBar loading;
    private TextView warningText;

    //User Details
    private User user;

    //Toaster
    private Toaster toaster;

    //Firebase Authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
        setListeners();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        inputMobileNumber = findViewById(R.id.inputMobileNumber);
        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        loading = findViewById(R.id.loading);
        signUpButton = findViewById(R.id.signUpButton);
        textSignIn = findViewById(R.id.textSignIn);
        warningText = findViewById(R.id.warningText);

        //Custom
        if (getApplicationContext() != null) {
            toaster = new Toaster(getApplicationContext());
        }
    }

    private void setListeners() {

        //SignUp
        signUpButton.setOnClickListener(v -> validate());

        //Sign In
        textSignIn.setOnClickListener(v -> onBackPressed());
    }

    private void validate() {
        if (inputMobileNumber.getText().toString().trim().isEmpty()) {
            toaster.showToast("Enter Mobile Number");
        } else if (inputUsername.getText().toString().trim().isEmpty()) {
            toaster.showToast("Enter a Username");
        } else if (inputPassword.getText().toString().trim().isEmpty()) {
            toaster.showToast("Enter a Password");
        } else if (inputConfirmPassword.getText().toString().trim().isEmpty()) {
            toaster.showToast("Confirm your Password");
        } else if (!inputPassword.getText().toString().equals(inputConfirmPassword.getText().toString())) {
            toaster.showToast("Password and Confirm Password must be same");
        } else {
            sendOTP();
        }
    }

    private void sendOTP() {
        toggleLoading(true);
        user = new User();
        user.username = inputUsername.getText().toString();
        user.mobileNumber = inputMobileNumber.getText().toString();
        user.password = inputPassword.getText().toString();

        Log.d("PAuth", "mobile number : " + user.mobileNumber);


        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + inputMobileNumber.getText().toString())
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.d("PAuth", "onComplete");
                        toggleLoading(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.d("PAuth", "onFailed");
                        toggleLoading(false);
                        toaster.showToast("OTP Verification Failed");
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        Log.d("PAuth", "onCode Sent");
                        toaster.showToast("OTP Sent");
                        toggleLoading(false);
                        Intent intent = new Intent(getApplicationContext(), OTPVerificationActivity.class);
                        intent.putExtra(Constants.KEY_USER, user);
                        intent.putExtra(Constants.KEY_VERIFICATION_ID, verificationId);
                        startActivity(intent);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }


    private void toggleLoading(Boolean isLoading) {
        if (isLoading) {
            warningText.setVisibility(View.VISIBLE);
            warningText.requestFocus();
            loading.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.GONE);
        } else {
            warningText.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
            signUpButton.setVisibility(View.VISIBLE);
        }
    }
}
