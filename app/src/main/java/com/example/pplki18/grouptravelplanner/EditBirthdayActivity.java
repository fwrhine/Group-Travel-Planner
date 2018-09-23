package com.example.pplki18.grouptravelplanner;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.UserContract;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class EditBirthdayActivity extends FragmentActivity {
    SessionManager session;
    DatabaseHelper myDb;

    Toolbar edit_birthday_toolbar;
    ImageButton save_birthday_button;
    static EditText edit_birthday;

    String username_str, birthday;

    SimpleDateFormat dateFormatter;
    DatePickerDialog datePickerDialog;

    HashMap<String, String> user;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_birthday);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

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
        birthday = user.get(SessionManager.KEY_BIRTHDAY);

        edit_birthday_toolbar = (Toolbar) findViewById(R.id.edit_birthday_toolbar);

        save_birthday_button = (ImageButton) findViewById(R.id.save_birthday_button);

        edit_birthday = (EditText) findViewById(R.id.edit_birthday);
//        edit_birthday.addTextChangedListener(textWatcher);

        edit_birthday.setText(birthday);
        edit_birthday.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        edit_birthday_toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(EditBirthdayActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
        );

        save_birthday_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String new_birthday = edit_birthday.getText().toString();

                        // Query string to update the user's gender
                        String query = "UPDATE " + UserContract.UserEntry.TABLE_NAME
                                + " SET " + UserContract.UserEntry.COL_BIRTHDAY + "="
                                + "\"" + new_birthday + "\"" + " WHERE "
                                + UserContract.UserEntry.COL_USERNAME + "="
                                + "\"" + username_str + "\"";

                        // Execute the query
                        db.execSQL(query);
                        session.updateSession(SessionManager.KEY_BIRTHDAY, new_birthday + "");

                        Log.d("CHECK", "SUCCESS");

                        Intent intent = new Intent(EditBirthdayActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
        );

        edit_birthday.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        showTruitonDatePickerDialog(v);
                    }
                }
        );
    }

    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            edit_birthday.setText(day + "/" + (month + 1) + "/" + year);
        }
    }
}
