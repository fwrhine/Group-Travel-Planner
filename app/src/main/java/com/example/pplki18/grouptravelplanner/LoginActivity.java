package com.example.pplki18.grouptravelplanner;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

    RelativeLayout loginLayout;

    InputMethodManager imm;

    Intent intent;
    String email;
    String gender;
    String phone_no;
    String birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize the instance variables and add listeners to the buttons
        init();
    }

    /**
     * Validate the inputted username and password.
     * @return true if the inputs are valid.
     */
    public boolean validateLogin() {
        String username = getUsernameFromEditText();
        String password = getPasswordFromEditText();

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity
        myDb = new DatabaseHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = myDb.getReadableDatabase();

        // Query string to get a row of user based on the username and password
        String query = "SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE "
                + UserEntry.COL_USERNAME + "=?" + " AND "
                + UserEntry.COL_PASSWORD + "=?";
        // Arguments for the query
        String[] selectionArgs = new String[]{username,password};

        // Execute the query and store to a cursor
        Cursor cursor = db.rawQuery(query, selectionArgs);

        // Checks whether the user exists
        if(cursor.getCount() == 1){
            if(cursor !=null && cursor.moveToFirst()) {
                do {
                    email = cursor.getString(cursor.getColumnIndex("email"));
                    gender = cursor.getInt(cursor.getColumnIndex("gender")) + "";
                    phone_no = cursor.getString(cursor.getColumnIndex("phone_no"));
                    birthday = cursor.getString(cursor.getColumnIndex("birthday"));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public void init(){
        // Initialize views
        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_password = (EditText) findViewById(R.id.edit_password);
        toSignUpPage = (TextView) findViewById(R.id.toSignUpPage);
        toForgotPasswordPage = (TextView) findViewById(R.id.toForgotPasswordPage);
        buttonLogin = (Button) findViewById(R.id.buttonSignup);

        loginLayout = (RelativeLayout) findViewById(R.id.login_layout);

        // Initialize session manager, because we're gonna change the login status
        sessionManager = new SessionManager(getApplicationContext());

        renderSignUpPage();
        renderForgotPasswordPage();
        hideKeyboard(loginLayout);

        // Add listener to the login button
        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                if(validateLogin()) {

                    // Saves the login information to the session
                    sessionManager.createLoginSession(getUsernameFromEditText(), email, gender, phone_no, birthday);
                    Log.d("SIGN-IN", sessionManager.isLoggedIn()+".");

                    // Send success message to the user
                    Toast.makeText(LoginActivity.this, "Sign-in success!",
                            Toast.LENGTH_SHORT).show();

                    // Redirect to ProfileActivity and forget the previous activities
                    intent = new Intent(LoginActivity.this, Activity_GroupList.class); //TEMP
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Log.d("SIGN-IN", "SUCCESS");
                }
                else {
                    // Send error message to the user
                    Toast.makeText(LoginActivity.this, "Invalid username or password.",
                            Toast.LENGTH_SHORT).show();
                    Log.e("SIGN-IN","WRONG INPUT");
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

    public String getUsernameFromEditText(){
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
