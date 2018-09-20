package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.UserContract.UserEntry;

public class RecoverPasswordActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    String email;
    EditText editNewPassword;
    EditText editConfirmPassword;
    Button savePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        // Retrieve the extra that is put into the intent from ForgotPasswordActivity
        email = getEmailExtra(savedInstanceState);

        // Initialize the instance variables and add listeners to the buttons
        init();
    }

    /**
     * Receive the email put into the intent as an extra.
     * @param savedInstanceState
     * @return String - email of the user who forgot the password
     */
    public String getEmailExtra(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                return null;
            } else {
                return extras.getString("email");
            }
        } else {
            return (String) savedInstanceState.getSerializable("email");
        }
    }

    public void changePassword(){
        // Handles when the received intent is null
        if(email == null){
            Toast.makeText(RecoverPasswordActivity.this, "Unknown Error.",
                    Toast.LENGTH_SHORT).show();
        }

        String newPassword = editNewPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

        if(newPassword.equals(confirmPassword)) {
            // To access our database, we instantiate our subclass of SQLiteOpenHelper
            // and pass the context, which is the current activity
            myDb = new DatabaseHelper(this);

            // Create and/or open a database to read from it
            SQLiteDatabase db = myDb.getReadableDatabase();

            // Query string to update the user's password
            String query = "UPDATE " + UserEntry.TABLE_NAME
                    + " SET " + UserEntry.COL_PASSWORD + " =\"" + newPassword + "\""
                    + " WHERE " + UserEntry.COL_EMAIL + " =\"" + email + "\"";
            // Execute the query
            db.execSQL(query);

            // Show success message to the user
            Toast.makeText(RecoverPasswordActivity.this, "Password changed!.",
                    Toast.LENGTH_SHORT).show();

            // Redirect to LoginActivity and forget the previous activities
            Intent i = new Intent(RecoverPasswordActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        else {
            // Show error message to the user
            Toast.makeText(RecoverPasswordActivity.this, "Password doesn't match!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void init(){
        // Initialize views
        editNewPassword = (EditText) findViewById(R.id.edit_newPassword);
        editConfirmPassword = (EditText) findViewById(R.id.edit_repeatPassword);
        savePassword = (Button) findViewById(R.id.button_send_verification);

        // Add listener to the save password button.
        savePassword.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                changePassword();
            }
        });
    }

}
