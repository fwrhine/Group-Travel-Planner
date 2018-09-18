package com.example.pplki18.grouptravelplanner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    InputMethodManager imm;

    @BindView(R.id.edit_fullname) EditText edit_fullname;
    @BindView(R.id.edit_username) EditText edit_username;
    @BindView(R.id.edit_email) EditText edit_email;
    @BindView(R.id.edit_password) EditText edit_password;

    @BindView(R.id.toLoginPage) TextView toLoginPage;

    @OnClick(R.id.buttonSignup) void createUser() {
        if (!validate()) {
            return;
        }

        int isCreated = myDb.insertData(edit_fullname.getText().toString(),
                edit_username.getText().toString(),
                edit_email.getText().toString(),
                edit_password.getText().toString());

        if (isCreated == 2) {
            Toast.makeText(SignUpActivity.this, "Username or email is already taken",
                    Toast.LENGTH_LONG).show();
        } else if (isCreated == 1) {
            Toast.makeText(SignUpActivity.this, "Sign-up success!",
                    Toast.LENGTH_LONG).show();
        } else if (isCreated == 0) {
            Toast.makeText(SignUpActivity.this, "Sign-up failed",
                    Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.toLoginPage) void renderLoginPage() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.signup_layout) void hideKeyboard(View view) {
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        myDb = new DatabaseHelper(this);
        ButterKnife.bind(this);
    }

    public boolean validate() {
        boolean valid = true;

        String fullname = edit_fullname.getText().toString();
        String username = edit_username.getText().toString();
        String email = edit_email.getText().toString();
        String password = edit_password.getText().toString();

        if (fullname.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Full name cannot be empty",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }

        else if (username.isEmpty() || username.length() < 5) {
            Toast.makeText(SignUpActivity.this, "Username must have at least 5 " +
                            "characters",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }

        else if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignUpActivity.this, "Enter a valid email address!",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }

        else if (password.isEmpty() || password.length() < 5 || password.length() > 15) {
            Toast.makeText(SignUpActivity.this, "Password must consists of 5 to 15 " +
                            "alphanumeric characters",
                    Toast.LENGTH_LONG).show();
            valid = false;
            edit_password.setError(null);
        }

        return valid;
    }
}
