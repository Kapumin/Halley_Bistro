package com.abjt.halleybistro.get_started;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;

import com.abjt.halleybistro.MainActivity;
import com.abjt.halleybistro.R;
import com.abjt.halleybistro.authentication.SignInActivity;
import com.abjt.halleybistro.utils.Constants;
import com.abjt.halleybistro.utils.SharedPreferenceManager;

public class GetStartedActivity extends AppCompatActivity {


    @Override
    protected void onStart() {
        super.onStart();
        if (new SharedPreferenceManager(getApplicationContext()).
                getBoolean(Constants.KEY_IS_LOGGED_IN)) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        findViewById(R.id.getStartedButton).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignInActivity.class)));
    }

}