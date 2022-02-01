package com.abjt.halleybistro.authentication;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.abjt.halleybistro.MainActivity;
import com.abjt.halleybistro.R;
import com.abjt.halleybistro.utils.Constants;
import com.abjt.halleybistro.utils.SharedPreferenceManager;
import com.abjt.halleybistro.utils.Toaster;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignInActivity extends AppCompatActivity {


    private EditText inputMobileNumber, inputPassword;
    private LinearLayout signUpLayout;
    private MaterialButton signInButton;
    private ProgressBar loading;

    private SharedPreferenceManager sharedPreferenceManager;
    private Toaster toaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
        setListeners();

    }

    private void init() {
        if (getApplicationContext() != null) {
            sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
            toaster = new Toaster(getApplicationContext());
        }

        inputMobileNumber = findViewById(R.id.inputMobileNumber);
        inputPassword = findViewById(R.id.inputPassword);

        signInButton = findViewById(R.id.signInButton);
        loading = findViewById(R.id.loading);
        signUpLayout = findViewById(R.id.signUpLayout);
    }

    private void setListeners() {
        signUpLayout.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        signInButton.setOnClickListener(v -> validate());
    }

    private void validate() {
        if (inputMobileNumber.getText().toString().trim().isEmpty()) {
            toaster.showToast("Enter Mobile Number");
        } else if (inputMobileNumber.getText().toString().trim().length() < 10) {
            toaster.showToast("Enter Valid Mobile Number");
        } else if (inputPassword.getText().toString().trim().isEmpty()) {
            toaster.showToast("Enter Password");
        } else {
            signIn();
        }
    }

    private void signIn() {
        toggleLoading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_CUSTOMERS)
                .whereEqualTo(Constants.KEY_MOBILE_NUMBER, inputMobileNumber.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        sharedPreferenceManager.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
                        sharedPreferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        sharedPreferenceManager.putString(Constants.KEY_USERNAME, documentSnapshot.getString(Constants.KEY_USERNAME));
                        sharedPreferenceManager.putString(Constants.KEY_MOBILE_NUMBER, documentSnapshot.getString(Constants.KEY_MOBILE_NUMBER));
                        sharedPreferenceManager.putString(Constants.KEY_PASSWORD, documentSnapshot.getString(Constants.KEY_PASSWORD));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        toaster.showToast("Logged In");
                        toggleLoading(false);
                        startActivity(intent);
                        finish();
                    } else {
                        toggleLoading(false);
                        toaster.showToast("Unable to login!");
                    }
                });
    }

    private void toggleLoading(Boolean isLoading) {
        if (isLoading) {
            loading.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
        } else {
            loading.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
        }
    }
}