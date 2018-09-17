package com.example.vasun.grouptravelplanner;

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

public class LoginActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText edit_username, edit_password;
    Button buttonLogin;
    TextView toSignUpPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_password = (EditText) findViewById(R.id.edit_password);
        toSignUpPage = (TextView) findViewById(R.id.toSignUpPage);
        buttonLogin = (Button) findViewById(R.id.buttonSignup);

        renderSignUpPage();

        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                if(validateLogin()) {
                    Toast.makeText(LoginActivity.this, "Sign-in success!",
                            Toast.LENGTH_SHORT).show();
                    Log.v("SIGN-IN", "SUCCESS");
                }
                else {
                    Toast.makeText(LoginActivity.this, "Invalid username or password.",
                            Toast.LENGTH_SHORT).show();
                    Log.v("ERROR","WRONG INPUT");
                }
                //Create the intent to start another activity
                //Intent intent = new Intent(view.getContext(), AnotherActivity.class);
                //startActivity(intent);
            }
        });

    }

    public void renderSignUpPage() {
        toSignUpPage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }


    public boolean validateLogin() {
        String username = edit_username.getText().toString();
        String password = edit_password.getText().toString();

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity
        myDb = new DatabaseHelper(this);

        //Create and/or open a database to read from it
        SQLiteDatabase db = myDb.getReadableDatabase();

        String query = "SELECT * FROM " + myDb.TABLE_NAME + " WHERE "
                + myDb.COL_2 + "=?" + " AND "
                + myDb.COL_3 + "=?";
        String[] selectionArgs = new String[]{username,password};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if(cursor.getCount() == 1){
            return true;
        }
        return false;
    }
}
