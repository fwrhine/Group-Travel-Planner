package com.example.pplki18.grouptravelplanner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.example.pplki18.grouptravelplanner.data.UserContract.UserEntry;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    SessionManager sessionManager;
    DatabaseHelper myDb;
    EditText edit_username, edit_password;
    Button buttonLogin;
    TextView toSignUpPage;
    TextView toForgotPasswordPage;
    ProgressBar progressBar;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    RelativeLayout loginLayout;

    InputMethodManager imm;

    Intent intent;
    String id;
    String fullname;
    String username;
    String gender;
    String phone_no;
    String birthday;
    Integer READ_CALENDAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        READ_CALENDAR = 5;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, READ_CALENDAR);
        }
        // Initialize the instance variables and add listeners to the buttons
        init();
        addLoginListener();
    }

    public void init(){
        // Initialize views
        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_password = (EditText) findViewById(R.id.edit_password);
        toSignUpPage = (TextView) findViewById(R.id.toSignUpPage);
        toForgotPasswordPage = (TextView) findViewById(R.id.toForgotPasswordPage);
        progressBar = (ProgressBar) findViewById(R.id.progress_loader);

        buttonLogin = (Button) findViewById(R.id.buttonSignup);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        loginLayout = (RelativeLayout) findViewById(R.id.login_layout);

        // Initialize session manager, because we're gonna change the login status
        sessionManager = new SessionManager(getApplicationContext());

        renderSignUpPage();
        renderForgotPasswordPage();
        hideKeyboard(loginLayout);

    }

    public void addLoginListener(){
        // Add listener to the login button
        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                String email = getEmailFromEditText();
                final String password = getPasswordFromEditText();
                progressBar.setVisibility(View.VISIBLE);
                //authenticate user
                if(email.length() == 0 || password.length() == 0){
                    Toast.makeText(LoginActivity.this, "Please insert email and password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        // there was an error
                                        if (password.length() < 6) {
                                            edit_password.setError("Minimum password length is 6");
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        firebaseUser = mAuth.getCurrentUser();
                                        Log.d("UID", firebaseUser.getUid());
                                        DatabaseReference userRef = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());
                                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);

                                                // Saves the login information to the session
                                                sessionManager.createLoginSession(firebaseUser.getUid(), user.fullName, user.username, getEmailFromEditText(), user.gender, user.phone, user.birthday, user.photoUrl);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Log.d("PHOTO_URL", user.photoUrl);
                                                // Send success message to the user
                                                Toast.makeText(LoginActivity.this, "Sign-in success!",
                                                        Toast.LENGTH_SHORT).show();

                                                // Redirect to ProfileActivity and forget the previous activities
                                                intent = new Intent(LoginActivity.this, InHomeActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                Log.d("SIGN-IN", "SUCCESS");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });
    }

    public void renderSignUpPage() {
        // Add listener to the SignUp TextView
        toSignUpPage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public void renderForgotPasswordPage() {
        // Add listener to the ForgotPassword TextView
        toForgotPasswordPage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public String getEmailFromEditText(){
        return edit_username.getText().toString();
    }

    public String getPasswordFromEditText(){
        return edit_password.getText().toString();
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
}
