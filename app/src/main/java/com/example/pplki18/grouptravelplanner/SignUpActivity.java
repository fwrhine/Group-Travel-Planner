package com.example.pplki18.grouptravelplanner;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText edit_fullname, edit_username, edit_email, edit_password;
    TextInputLayout signup_fullname, signup_username;
    TextView toLoginPage;
    Button buttonSignup;
    RelativeLayout signUpLayout;
    ProgressBar progressBar;

    FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mUsersDatabaseReference;

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
        progressBar = (ProgressBar) findViewById(R.id.progress_loader);

        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        toLoginPage = (TextView) findViewById(R.id.toLoginPage);

        signUpLayout = (RelativeLayout) findViewById(R.id.signup_layout);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        createNewUser();
        renderLoginPage();
        hideKeyboard(signUpLayout);
    }

    public void createNewUser() {
        buttonSignup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        final String fullname = edit_fullname.getText().toString();
                        final String username = edit_username.getText().toString();
                        final String email = edit_email.getText().toString();
                        final String password = edit_password.getText().toString();

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                            FirebaseUser fUser = mAuth.getCurrentUser();
                                            User user = new User(fUser.getUid(), fullname, username, email);
                                            mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");
                                            mUsersDatabaseReference.child(fUser.getUid()).setValue(user);
                                            mAuth.signOut();
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });
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
