package com.example.pplki18.grouptravelplanner;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.old_stuff.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.old_stuff.UserContract.UserEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPasswordActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    Button buttonVerify;
    EditText edit_email;
    ProgressBar progressBar;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Initialize the instance variables and add listeners to the buttons
        init();
    }

    /**
     * Validate the inputted email
     * @param email - The email inputted by the user
     * @return true if the email exists in the database.
     */
    public boolean validate(String email){
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity
        myDb = new DatabaseHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = myDb.getReadableDatabase();

        // Query string to get a row_user of user based on the email.
        String query = "SELECT * FROM " + UserEntry.TABLE_NAME + " WHERE "
                + UserEntry.COL_EMAIL + "=?";
        // Arguments for the query
        String[] selectionArgs = new String[]{email};

        // Execute the query and store to a cursor
        Cursor cursor = db.rawQuery(query, selectionArgs);

        // Checks whether the user exists
        if(cursor.getCount() == 1){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public void init(){
        // Initialize views
        edit_email = (EditText) findViewById(R.id.edit_email);
        buttonVerify = (Button) findViewById(R.id.button_send_verification);
        progressBar = (ProgressBar) findViewById(R.id.progress_loader);

        // Initialize the verify button
        buttonVerify.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email = edit_email.getText().toString();
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(ForgotPasswordActivity.this, "Email sent.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
