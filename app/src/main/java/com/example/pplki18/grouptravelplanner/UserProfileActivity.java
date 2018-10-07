package com.example.pplki18.grouptravelplanner;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.pplki18.grouptravelplanner.utils.GlideApp;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    SessionManager session;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser firebaseUser;
    DatabaseReference userRef;
    StorageReference storageReference;

    String photoUrl;
    Uri mImageUri;

    TextView fullname_label, username_label, email_label, phone_label, birthday_label;
//    Button buttonLogout;
    ImageButton buttonCheckFullname, buttonEditFullname;
    CircleImageView profile_image;

    RelativeLayout phone_layout, birthday_layout;

    Toolbar edit_profile_toolbar;

    Spinner genderSpinner;

    FloatingActionButton fab_pic_user;

    String fullname_str, username_str, email_str, phone_no, birthday;

    HashMap<String, String> user;

    private int GALLERY = 1, CAMERA = 2;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference();

        init();

        setSupportActionBar(edit_profile_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Edit Profile");

        editUsername();
        setSelectGender();
        editPhoneNo();
        editBirthday();
        choosePicture();

        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){

                } else {
                    Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
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

        fullname_label = (TextView) findViewById(R.id.fullname_label);
        username_label = (TextView) findViewById(R.id.username_label);
        email_label = (TextView) findViewById(R.id.email_label);
        phone_label = (TextView) findViewById(R.id.phone_label);
        birthday_label = (TextView) findViewById(R.id.birthday_label);

        edit_profile_toolbar = (Toolbar) findViewById(R.id.edit_profile_toolbar);

        genderSpinner = (Spinner) findViewById(R.id.gender_label);

        fab_pic_user = (FloatingActionButton) findViewById(R.id.fab_pic_user);

//        buttonLogout = (Button) findViewById(R.id.buttonLogout);
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

        fullname_str = session.getUserDetails().get(session.KEY_FULLNAME);

        fullname_label.setText(fullname_str);
        username_label.setText(username_str);
        email_label.setText(email_str);

        photoUrl = session.getUserDetails().get(session.KEY_PHOTO_URL);
        // Load user profile and put it to the imageView
        Log.d("IMAGE", photoUrl + "");
        Uri uri = Uri.parse(photoUrl);

        GlideApp.with(this)
                .load(uri)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .centerCrop()
                .apply(new RequestOptions().override(300, 300))
                .apply(RequestOptions.circleCropTransform())
                .into(profile_image);

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

//        buttonLogout.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        session.logoutUser();
//                    }
//                }
//        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

                        userRef.child("fullName").setValue(new_fullname);

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

                        userRef.child("gender").setValue(String.valueOf(selected_item));
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

//    public void setUpToolbar() {
//        edit_profile_toolbar.setNavigationOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(UserProfileActivity.this, InHomeActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                    }
//                }
//        );
//    }

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA);
        }
        startActivityForResult(intent, CAMERA);
    }

    // TODO: Benerin biar ga crash waktu ngambil foto dari kamera.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if(requestCode == GALLERY) {
            if (data != null) {
                mImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                    bitmap = getResizedBitmap(bitmap, 300);
                    uploadImage(mImageUri, bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(UserProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if(requestCode == CAMERA) {
            mImageUri = data.getData();
            Log.d("FILEPATH_CAM", mImageUri.toString());
        }
    }

    private void uploadImage(Uri filePath, Bitmap bitmap){
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/user/"+ firebaseUser.getUid());
            final Bitmap finalBitmap = bitmap;
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    session.updateSession(session.KEY_PHOTO_URL, uri.toString());
                                    userRef.child("photoUrl").setValue(uri.toString());
                                }
                            });
                            profile_image.setImageBitmap(finalBitmap);
                            Toast.makeText(UserProfileActivity.this, "Profile Image Updated",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UserProfileActivity.this, "Failed!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        } else {
            Toast.makeText(UserProfileActivity.this, "Profile Image Not Updated",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mImageUri != null) {
            outState.putString("cameraImageUri", mImageUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri")) {
            mImageUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
        }
    }

    ///////////////////////////////////////////// convert image view to byte array /////////////////////////////////////////////


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

}
