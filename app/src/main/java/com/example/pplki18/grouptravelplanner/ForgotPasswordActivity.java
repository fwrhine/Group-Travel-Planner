package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.UserContract.UserEntry;

public class ForgotPasswordActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    Button buttonVerify;
    EditText edit_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edit_email = (EditText) findViewById(R.id.edit_email);
        buttonVerify = (Button) findViewById(R.id.button_send_verification);

        buttonVerify.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                String email = edit_email.getText().toString();
                if(validate(email)) {
                    Intent i = new Intent(ForgotPasswordActivity.this, RecoverPasswordActivity.class);
                    i.putExtra("email", email);
                    startActivity(i);
                }
                else {
                    Toast.makeText(ForgotPasswordActivity.this, "E-mail does not exist!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validate(String email){
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity
        myDb = new DatabaseHelper(this);

        //Create and/or open a database to read from it
        SQLiteDatabase db = myDb.getReadableDatabase();

        String query = "SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE "
                + UserEntry.COL_EMAIL + "=?";

        String[] selectionArgs = new String[]{email};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if(cursor.getCount() == 1){
            return true;
        }
        return false;
    }


}
