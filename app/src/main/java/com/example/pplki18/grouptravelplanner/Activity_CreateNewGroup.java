package com.example.pplki18.grouptravelplanner;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.GroupContract;
import com.example.pplki18.grouptravelplanner.data.UserContract;
import com.example.pplki18.grouptravelplanner.data.UserGroupContract;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Activity_CreateNewGroup extends AppCompatActivity {
    private static final String TAG = "Activity_CreateNewGroup";

    DatabaseHelper databaseHelper;
    private Button btnCreate, btnChoose;
    private EditText editText;
    private ImageView imageView;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private ArrayList<Integer> user_ids;
    private RecyclerView recyclerViewUser;
    private LinearLayoutManager linearLayoutManager;
    private int GALLERY = 1, CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        setSupportActionBar(toolbar);
        init();

        //Todo: choose image button: on click, ask permission to read gallery
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        //Todo: create new group button: on click, insert data to database
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group_name = editText.getText().toString();
                byte[] group_image = imageViewToByte(imageView);
                Group newGroup = new Group(group_name, group_image);

                if (editText.length() != 0) {
                    CreateGroup(newGroup, user_ids);
                    Intent myIntent = new Intent(Activity_CreateNewGroup.this, Activity_InGroup.class);
                    Activity_CreateNewGroup.this.startActivity(myIntent);

                    //empty name and image input
                    editText.setText("");
                    imageView.setImageResource(R.mipmap.ic_launcher_round);
                } else {
                    toastMessage("Name cannot be empty.");
                }
            }
        });

        recyclerViewUser.setHasFixedSize(true);
        recyclerViewUser.setLayoutManager(linearLayoutManager);

        populateGroupRecyclerView();


    }

    ////////////////////////////////////////////////// populate user list //////////////////////////////////////////////////////

    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populateGroupRecyclerView() {
        Log.d(TAG, "populateGroupRecyclerView: Displaying list of groups in the ListView.");

        //get data and append to list
        final List<User> users = getAllUsers();

        RVAdapter_User adapter = new RVAdapter_User(users, new RVAdapter_User.ClickListener() {
            @Override public void onClick(View v, int position) {
                Toast.makeText(Activity_CreateNewGroup.this, "SOME OTHER = " + String.valueOf(position), Toast.LENGTH_SHORT).show();

                if (user_ids.contains(position)) {
                    user_ids.remove(Integer.valueOf(position));
                    v.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                } else {
                    user_ids.add(position);
                    v.setBackgroundColor(getResources().getColor(R.color.user_pressed));
                }

                Log.d("IDS", user_ids.toString());
            }
        });

        recyclerViewUser.setAdapter(adapter);
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
                    imageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Activity_CreateNewGroup.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(thumbnail);
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

    ////////////////////////////////////////////// insert new group to database ////////////////////////////////////////////////

    //Todo: insert data to database, if success, open group chat
    public void CreateGroup(Group group, ArrayList<Integer> user_ids) {
        long createGroup = insertGroup(group, user_ids);

        if (createGroup != -1) {
            toastMessage("New group!");
        } else {
            toastMessage("Fail.");
        }

        user_ids.clear();
    }

    //////////////////////////////////////////////////// database functions ////////////////////////////////////////////////////

    public long insertGroup(Group group, ArrayList<Integer> user_ids) {
        sessionManager = new SessionManager(getApplicationContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GroupContract.GroupEntry.COL_GROUP_NAME, group.getGroup_name());
        values.put(GroupContract.GroupEntry.COL_GROUP_IMAGE, group.getGroup_image());
        values.put(GroupContract.GroupEntry.COL_GROUP_CREATOR, sessionManager.KEY_USERNAME);

        // insert row_user
        long group_id = db.insert(GroupContract.GroupEntry.TABLE_NAME, null, values);

        // assigning users to groups
        for (int user_id : user_ids) {
            insertUserGroup(group_id, user_id);
        }

        return group_id;
    }

    public long insertUserGroup(long group_id, int user_id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserGroupContract.UserGroupEntry.COL_GROUP_ID, group_id);
        contentValues.put(UserGroupContract.UserGroupEntry.COL_USER_ID, user_id);

        long id = db.insert(UserGroupContract.UserGroupEntry.TABLE_NAME, null, contentValues);

        return id;
    }

    /*
     * Get all users
     * */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<User>();
        String selectQuery = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME;

        Log.e("USERS", selectQuery);


        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                User user = new User();
                user.setUser_name((c.getString(c.getColumnIndex(UserContract.UserEntry.COL_FULLNAME))));

                // adding to group list
                users.add(user);
            } while (c.moveToNext());
        }

        return users;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        editText = findViewById(R.id.editText);
        imageView = findViewById(R.id.imageView);
        btnCreate = findViewById(R.id.btnCreate);
        btnChoose = findViewById(R.id.btnChoose);
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(getApplicationContext());
        recyclerViewUser = (RecyclerView)findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        user_ids = new ArrayList<>();
    }

}
