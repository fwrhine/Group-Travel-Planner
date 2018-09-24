package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.UserContract;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import java.util.HashMap;
import java.util.Locale;

public class EditPhoneNoActivity extends AppCompatActivity {
    SessionManager session;
    DatabaseHelper myDb;

    Toolbar edit_phone_toolbar;
    ImageButton save_phone_button;
    EditText edit_phone;

    String username_str, phone_number;

    boolean numberChanged;

    HashMap<String, String> user;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone_no);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init();
    }

    public void init() {
        session = new SessionManager(getApplicationContext());

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity
        myDb = new DatabaseHelper(this);

        // Create and/or open a database to read from it
        db = myDb.getReadableDatabase();

        // get user data from session
        user = session.getUserDetails();

        username_str = user.get(SessionManager.KEY_USERNAME);
        phone_number = user.get(SessionManager.KEY_PHONE);

        edit_phone_toolbar = (Toolbar) findViewById(R.id.edit_phone_toolbar);

        save_phone_button = (ImageButton) findViewById(R.id.save_phone_button);

        edit_phone = (EditText) findViewById(R.id.edit_phone);
//        edit_phone.addTextChangedListener(textWatcher);

        numberChanged = false;

        edit_phone.setText(phone_number);
        edit_phone.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        edit_phone_toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(EditPhoneNoActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
        );

        save_phone_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String new_phone_no = edit_phone.getText().toString();
                        new_phone_no = PhoneNumberUtils.formatNumber(new_phone_no, Locale.getDefault().getCountry());

                        // Query string to update the user's gender
                        String query = "UPDATE " + UserContract.UserEntry.TABLE_NAME
                                + " SET " + UserContract.UserEntry.COL_PHONE + "="
                                + "\"" + new_phone_no + "\"" + " WHERE "
                                + UserContract.UserEntry.COL_USERNAME + "="
                                + "\"" + username_str + "\"";

                        // Execute the query
                        db.execSQL(query);
                        session.updateSession(SessionManager.KEY_PHONE, new_phone_no+"");

                        Log.d("CHECK", "SUCCESS");

                        Intent intent = new Intent(EditPhoneNoActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
        );

    }
//
//    TextWatcher textWatcher = new TextWatcher() {
//
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            numberChanged = true;
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//
//        }
//    };
}
