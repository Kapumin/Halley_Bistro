package com.abjt.halleybistro.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.abjt.halleybistro.MainActivity;
import com.abjt.halleybistro.R;
import com.abjt.halleybistro.models.User;
import com.abjt.halleybistro.utils.Constants;
import com.abjt.halleybistro.utils.SharedPreferenceManager;
import com.abjt.halleybistro.utils.Toaster;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OTPVerificationActivity extends AppCompatActivity {

    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private ProgressBar loading;
    private MaterialButton verifyButton;
    private LinearLayout resendOTP;

    private User user;
    private String verificationId;

    private Toaster toaster;
    private SharedPreferenceManager sharedPreferenceManager;

    //Firebase Authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_send);

        init();

    }

    private void init() {
        //getUserData
        mAuth = FirebaseAuth.getInstance();
        user = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        verificationId = getIntent().getStringExtra(Constants.KEY_VERIFICATION_ID);
        if (getApplicationContext() != null) {
            sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
            toaster = new Toaster(getApplicationContext());
        }

        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        verifyButton = findViewById(R.id.verifyButton);
        resendOTP = findViewById(R.id.resendOTPLayout);
        loading = findViewById(R.id.loading);

        setUpOTPInputs();

        //verification
        setVerificationListener();
    }

    private void setUpOTPInputs() {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void setVerificationListener() {

        verifyButton.setOnClickListener(v -> {
            if (inputCode1.getText().toString().trim().isEmpty()
                    || inputCode2.getText().toString().trim().isEmpty()
                    || inputCode3.getText().toString().trim().isEmpty()
                    || inputCode4.getText().toString().trim().isEmpty()
                    || inputCode5.getText().toString().trim().isEmpty()
                    || inputCode6.getText().toString().trim().isEmpty()
            ) {
                toaster.showToast("Enter Valid OTP");
            } else {
                String code = inputCode1.getText().toString() +
                        inputCode2.getText().toString() +
                        inputCode3.getText().toString() +
                        inputCode4.getText().toString() +
                        inputCode5.getText().toString() +
                        inputCode6.getText().toString();

                if (verificationId != null) {
                    toggleLoading(true);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationId,
                            code
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    toaster.showToast("Logging In please wait");
                                    saveUserCredentials();
                                } else {
                                    toggleLoading(false);
                                    toaster.showToast("The Verification Code Was Invalid");
                                }
                            }).addOnFailureListener(e -> {
                        toggleLoading(false);
                        toaster.showToast("Verification Failed");
                    });
                }
            }
        });

        resendOTP.setOnClickListener(v -> {
            PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber("+91" + user.mobileNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            toggleLoading(false);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            toggleLoading(false);
                            toaster.showToast("OTP Verification Failed");
                        }

                        @Override
                        public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            toaster.showToast("OTP sent please wait for awhile  \n DO NOT PRESS BACK");
                            toggleLoading(false);
                            verificationId = newVerificationId;
                            clearEditText();
                        }
                    })
                    .build();

            PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
        });
    }

    private void clearEditText() {
        inputCode1.setText("");
        inputCode2.setText("");
        inputCode3.setText("");
        inputCode4.setText("");
        inputCode5.setText("");
        inputCode6.setText("");
    }

    private void saveUserCredentials() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        HashMap<String, Object> customer = new HashMap<>();
        customer.put(Constants.KEY_USERNAME, user.username);
        customer.put(Constants.KEY_MOBILE_NUMBER, user.mobileNumber);
        customer.put(Constants.KEY_PASSWORD, user.password);
        customer.put(Constants.KEY_FCM_TOKEN, user.fcmToken);

        database.collection(Constants.KEY_COLLECTION_CUSTOMERS)
                .add(customer)
                .addOnSuccessListener(documentReference -> {
                    toggleLoading(false);
                    sharedPreferenceManager.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
                    sharedPreferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    sharedPreferenceManager.putString(Constants.KEY_USERNAME, user.username);
                    sharedPreferenceManager.putString(Constants.KEY_MOBILE_NUMBER, user.mobileNumber);
                    sharedPreferenceManager.putString(Constants.KEY_PASSWORD, user.password);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }

    private void toggleLoading(Boolean isLoading) {
        if (isLoading) {
            loading.setVisibility(View.VISIBLE);
            verifyButton.setVisibility(View.GONE);
        } else {
            loading.setVisibility(View.GONE);
            verifyButton.setVisibility(View.VISIBLE);
        }
    }
}