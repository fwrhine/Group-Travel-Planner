package com.example.pplki18.grouptravelplanner;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;

public class SignUpActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText edit_fullname, edit_username, edit_email, edit_password;
    TextInputLayout signup_fullname, signup_username;
    TextView toLoginPage;
    Button buttonSignup;
    RelativeLayout signUpLayout;

    InputMethodManager imm;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        myDb = new DatabaseHelper(this);

        signup_fullname = (TextInputLayout) findViewById(R.id.signup_fullname);
        signup_username = (TextInputLayout) findViewById(R.id.signup_username);

        edit_fullname = (EditText) findViewById(R.id.edit_fullname);
        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_password = (EditText) findViewById(R.id.edit_password);

        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        toLoginPage = (TextView) findViewById(R.id.toLoginPage);

        signUpLayout = (RelativeLayout) findViewById(R.id.signup_layout);

        createNewUser();
        renderLoginPage();
        hideKeyboard(signUpLayout);
    }

    public void createNewUser() {
        buttonSignup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!validate()) {
                            return;
                        }

                        int isCreated = myDb.insertUser(edit_fullname.getText().toString(),
                                edit_username.getText().toString(),
                                edit_email.getText().toString(),
                                edit_password.getText().toString());

                        if (isCreated == 2) {
                            Toast.makeText(SignUpActivity.this, "Username or email is already taken",
                                    Toast.LENGTH_LONG).show();
                        } else if (isCreated == 1) {
                            Toast.makeText(SignUpActivity.this, "Sign-up success!",
                                    Toast.LENGTH_LONG).show();
                            intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else if (isCreated == 0) {
                            Toast.makeText(SignUpActivity.this, "Sign-up failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    public void renderLoginPage() {
        // Add listener to the Login TextView
        toLoginPage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                SignUpActivity.this.finish();
            }
        });
    }

    public void hideKeyboard(View view) {
        view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
        );
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
            valid = false;edit_password.setError(null);
        }

        return valid;
    }
}
