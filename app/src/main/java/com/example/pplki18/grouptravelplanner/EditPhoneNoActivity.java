package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Locale;

public class EditPhoneNoActivity extends AppCompatActivity {
    SessionManager session;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser firebaseUser;
    DatabaseReference userRef;

    Toolbar edit_phone_toolbar;
    ImageButton save_phone_button;
    EditText edit_phone;

    String username_str, phone_number;

    boolean numberChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone_no);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());
        init();
        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){

                } else {
                    Intent intent = new Intent(EditPhoneNoActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onPause(){
        super.onPause();
        firebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume(){
        super.onResume();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public void init() {
        session = new SessionManager(getApplicationContext());

        username_str = session.getUserDetails().get(session.KEY_USERNAME);
        phone_number = session.getUserDetails().get(session.KEY_PHONE);

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
                        userRef.child("phone").setValue(new_phone_no);
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
