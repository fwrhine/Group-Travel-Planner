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

        email = getEmailExtra(savedInstanceState);
        editNewPassword = (EditText) findViewById(R.id.edit_newPassword);
        editConfirmPassword = (EditText) findViewById(R.id.edit_repeatPassword);
        savePassword = (Button) findViewById(R.id.button_send_verification);

        savePassword.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                changePassword();
            }
        });
    }

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
        if(email == null){
            Toast.makeText(RecoverPasswordActivity.this, "Unknown Error.",
                    Toast.LENGTH_SHORT).show();
        }

        String newPassword = editNewPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

        if(newPassword.equals(confirmPassword)) {
            myDb = new DatabaseHelper(this);

            //Create and/or open a database to read from it
            SQLiteDatabase db = myDb.getReadableDatabase();

            String query = "UPDATE " + UserEntry.TABLE_NAME
                    + " SET " + UserEntry.COL_PASSWORD + " =\"" + newPassword + "\""
                    + " WHERE " + UserEntry.COL_EMAIL + " =\"" + email + "\"";

            db.execSQL(query);

            Toast.makeText(RecoverPasswordActivity.this, "Password changed!.",
                    Toast.LENGTH_SHORT).show();

            Intent i = new Intent(RecoverPasswordActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        else {
            Toast.makeText(RecoverPasswordActivity.this, "Password doesn't match!",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
