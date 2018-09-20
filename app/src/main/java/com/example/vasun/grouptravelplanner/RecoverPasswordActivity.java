package com.example.vasun.grouptravelplanner;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vasun.grouptravelplanner.data.DatabaseHelper;

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

            String query = "UPDATE " + myDb.TABLE_NAME
                    + " SET " + myDb.COL_3 + " =\"" + newPassword + "\""
                    + " WHERE " + myDb.COL_4 + " =\"" + email + "\"";

            db.execSQL(query);

            Toast.makeText(RecoverPasswordActivity.this, "Password changed!.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(RecoverPasswordActivity.this, "Password doesn't match!",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
