package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.UserContract.UserEntry;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {
    SessionManager sessionManager;
    DatabaseHelper myDb;
    EditText edit_username, edit_password;
    Button buttonLogin;
    TextView toSignUpPage;
    TextView toForgotPasswordPage;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_password = (EditText) findViewById(R.id.edit_password);
        toSignUpPage = (TextView) findViewById(R.id.toSignUpPage);
        toForgotPasswordPage = (TextView) findViewById(R.id.toForgotPasswordPage);
        buttonLogin = (Button) findViewById(R.id.buttonSignup);

        // Initialize session manager, because we're gonna change the login status
        sessionManager = new SessionManager(getApplicationContext());

        renderSignUpPage();
        renderForgotPasswordPage();

        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                if(validateLogin()) {
                    sessionManager.createLoginSession(getUsernameFromEditText());
                    Log.d("SIGN-IN", sessionManager.isLoggedIn()+".");
                    Toast.makeText(LoginActivity.this, "Sign-in success!",
                            Toast.LENGTH_SHORT).show();
                    Log.d("SIGN-IN", "SUCCESS");
                }
                else {
                    Toast.makeText(LoginActivity.this, "Invalid username or password.",
                            Toast.LENGTH_SHORT).show();
                    Log.e("SIGN-IN","WRONG INPUT");
                }
            }
        });

    }

    public void renderSignUpPage() {
        toSignUpPage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public void renderForgotPasswordPage() {
        toForgotPasswordPage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }


    public boolean validateLogin() {
        String username = getUsernameFromEditText();
        String password = getPasswordFromEditText();

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity
        myDb = new DatabaseHelper(this);

        //Create and/or open a database to read from it
        SQLiteDatabase db = myDb.getReadableDatabase();

        String query = "SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE "
                + UserEntry.COL_USERNAME + "=?" + " AND "
                + UserEntry.COL_PASSWORD + "=?";
        String[] selectionArgs = new String[]{username,password};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if(cursor.getCount() == 1){
            return true;
        }
        return false;
    }

    public String getUsernameFromEditText(){
        return edit_username.getText().toString();
    }

    public String getPasswordFromEditText(){
        return edit_password.getText().toString();
    }
}
