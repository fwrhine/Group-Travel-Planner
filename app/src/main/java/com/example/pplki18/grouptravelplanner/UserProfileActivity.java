package com.example.pplki18.grouptravelplanner;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.UserContract;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    SessionManager session;
    DatabaseHelper myDb;

    TextView fullname_label, username_label, email_label, phone_label, birthday_label;
    Button buttonLogout;
    ImageButton buttonCheckFullname, buttonEditFullname;
    CircleImageView profile_image;

    RelativeLayout phone_layout, birthday_layout;

    Toolbar edit_profile_toolbar;

    Spinner genderSpinner;

    FloatingActionButton fab_pic_user;

    String fullname_str, username_str, email_str, phone_no, birthday;
    byte[] byte_array;

    HashMap<String, String> user;

    private int GALLERY = 1, CAMERA = 2;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
        editUsername();
        setSelectGender();
        editPhoneNo();
        editBirthday();
        setUpToolbar();
        choosePicture();
    }

    public void init() {
        session = new SessionManager(getApplicationContext());

        fullname_label = (TextView) findViewById(R.id.fullname_label);
        username_label = (TextView) findViewById(R.id.username_label);
        email_label = (TextView) findViewById(R.id.email_label);
        phone_label = (TextView) findViewById(R.id.phone_label);
        birthday_label = (TextView) findViewById(R.id.birthday_label);

        edit_profile_toolbar = (Toolbar) findViewById(R.id.edit_profile_toolbar);

        genderSpinner = (Spinner) findViewById(R.id.gender_label);

        fab_pic_user = (FloatingActionButton) findViewById(R.id.fab_pic_user);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonEditFullname = (ImageButton) findViewById(R.id.buttonEditFullname);
        buttonCheckFullname = (ImageButton) findViewById(R.id.buttonCheckFullname);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);

        phone_layout = (RelativeLayout) findViewById(R.id.phone_layout);
        birthday_layout = (RelativeLayout) findViewById(R.id.birthday_layout);

        fullname_label.setEnabled(false);
        fullname_label.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);
        buttonCheckFullname.setVisibility(View.GONE);

        // get user data from session
        user = session.getUserDetails();

        // get username from the session
        username_str = user.get(SessionManager.KEY_USERNAME);

        // get email from the session
        email_str = user.get(SessionManager.KEY_EMAIL);

        // get phone number from the session
        phone_no = user.get(SessionManager.KEY_PHONE);

        // get birthday from the session
        birthday = user.get(SessionManager.KEY_BIRTHDAY);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity
        myDb = new DatabaseHelper(this);

        // Create and/or open a database to read from it
        db = myDb.getReadableDatabase();

        // Find the user from the database
        String query = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE "
                + UserContract.UserEntry.COL_USERNAME + "=?";
        String[] selectionArgs = new String[]{username_str};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        // Get the data (fullname only) from the database
        if(cursor != null && cursor.moveToFirst()) {
            do {
                fullname_str = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COL_FULLNAME));
                byte_array = cursor.getBlob(cursor.getColumnIndex(UserContract.UserEntry.COL_PICTURE));
            } while (cursor.moveToNext());
        }

        fullname_label.setText(fullname_str);
        username_label.setText(username_str);
        email_label.setText(email_str);

        Log.d("IMAGE", byte_array + "");
        if (byte_array != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(byte_array, 0, byte_array.length);
            if(bitmap != null) {
                profile_image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));
            } else {
                Log.d("IMAGE", "ERROR");
            }
        }

        // set up user phone number
        if (phone_no.isEmpty()) {
            phone_label.setText("set my phone number");
            phone_label.setTypeface(phone_label.getTypeface(), Typeface.ITALIC);
        } else {
            phone_no = PhoneNumberUtils.formatNumber(phone_no, Locale.getDefault().getCountry());
            phone_label.setText(phone_no);
            phone_label.setTypeface(phone_label.getTypeface(), Typeface.NORMAL);
        }

        // set up user birthday
        if (birthday.isEmpty()) {
            birthday_label.setText("set my birthday");
            birthday_label.setTypeface(birthday_label.getTypeface(), Typeface.ITALIC);
        } else {
            birthday_label.setText(birthday);
            birthday_label.setTypeface(birthday_label.getTypeface(), Typeface.NORMAL);
        }

        buttonLogout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        session.logoutUser();
                    }
                }
        );
    }

    public void editUsername() {
        buttonEditFullname.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fullname_label.setEnabled(true);
                        fullname_label.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                        buttonCheckFullname.setVisibility(View.VISIBLE);
                        buttonEditFullname.setVisibility(View.GONE);

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    }
                }
        );

        buttonCheckFullname.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fullname_label.setEnabled(false);
                        fullname_label.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);
                        buttonCheckFullname.setVisibility(View.GONE);
                        buttonEditFullname.setVisibility(View.VISIBLE);
                        String new_fullname = fullname_label.getText().toString();

                        session.updateSession(SessionManager.KEY_FULLNAME, new_fullname);
                        Log.d("FULLNAME", new_fullname);

                        // Create and/or open a database to read from it
                        db = myDb.getReadableDatabase();

                        // Query string to update the user full name
                        String query = "UPDATE " + UserContract.UserEntry.TABLE_NAME
                                + " SET " + UserContract.UserEntry.COL_FULLNAME + "=?"
                                + " WHERE " + UserContract.UserEntry.COL_USERNAME + "=?";
                        // Arguments for the query
                        String[] selectionArgs = new String[]{new_fullname, username_str};

                        // Execute the query
                        db.execSQL(query, selectionArgs);

                    }
                }
        );
    }

    public void setSelectGender() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(UserProfileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.genders));
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setSelection(Integer.parseInt(user.get(SessionManager.KEY_GENDER)));

        genderSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        int selected_item = genderSpinner.getSelectedItemPosition();

                        // Query string to update the user's gender
                        String query = "UPDATE " + UserContract.UserEntry.TABLE_NAME
                                + " SET " + UserContract.UserEntry.COL_GENDER + "=" + selected_item + " WHERE "
                                + UserContract.UserEntry.COL_USERNAME + "=" + "\"" + username_str + "\"";

                        // Execute the query
                        db.execSQL(query);
                        session.updateSession(SessionManager.KEY_GENDER, selected_item+"");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }
        );
    }

    public void editPhoneNo() {
        phone_layout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserProfileActivity.this, EditPhoneNoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
        );
    }

    public void editBirthday() {
        birthday_layout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserProfileActivity.this, EditBirthdayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
        );
    }

    public void setUpToolbar() {
        edit_profile_toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserProfileActivity.this, Activity_InHome.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
        );
    }

    public void choosePicture() {
        fab_pic_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();

            }
        });



    }

    ////////////////////////////////////////////////////// choose image ////////////////////////////////////////////////////////

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    bitmap =  getResizedBitmap(bitmap, 70);
                    profile_image.setImageBitmap(bitmap);
                    boolean flag = saveImageToDb();
                    if (flag) {
                        Toast.makeText(UserProfileActivity.this, "Profile Image Updated",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserProfileActivity.this, "Profile Image Not Updated",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(UserProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            profile_image.setImageBitmap(thumbnail);
            boolean flag = saveImageToDb();
            if (flag) {
                Toast.makeText(UserProfileActivity.this, "Profile Image Updated",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UserProfileActivity.this, "Profile Image Not Updated",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    ///////////////////////////////////////////// convert image view to byte array /////////////////////////////////////////////

    //convert image view to byte array
    public static byte[] imageViewToByte(ImageView image) {
        try {
            Bitmap bitmap = getBitmapFromDrawable(image.getDrawable());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    //convert drawable to bitmap
    @NonNull
    private static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    //get resized bitmap
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private boolean saveImageToDb() {
        byte[] profile_image_byte = imageViewToByte(profile_image);

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserContract.UserEntry.COL_PICTURE, profile_image_byte);

        String whereClause = UserContract.UserEntry.COL_USERNAME + " = ? ";
        final String whereArgs[] = {username_str};

        long result = db.update(UserContract.UserEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
        if(result == -1)
            return false;
        else
            return true;
    }

}
