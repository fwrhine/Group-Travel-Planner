package com.example.pplki18.grouptravelplanner;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.GroupContract;
import com.example.pplki18.grouptravelplanner.data.UserContract;
import com.example.pplki18.grouptravelplanner.data.UserGroupContract;
import com.example.pplki18.grouptravelplanner.utils.Group;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_User;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.example.pplki18.grouptravelplanner.utils.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateNewGroupActivity extends AppCompatActivity {
    private static final String TAG = "CreateNewGroupActivity";

    DatabaseHelper databaseHelper;
    private Button btnCreate;
    private FloatingActionButton fab_pic;
    private EditText editText;
    private CircleImageView circleImageView;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private ArrayList<Integer> user_ids;
    private RecyclerView recyclerViewUser;
    private LinearLayoutManager linearLayoutManager;
    private SearchView searchView;
    private HashMap<String, String> user;
    private String currId;
    private int GALLERY = 1, CAMERA = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        init();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Create Group");

        //Todo: choose image button: on click, ask permission to read gallery
        fab_pic.setOnClickListener(new View.OnClickListener() {
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
                byte[] group_image = imageViewToByte(circleImageView);
                Group newGroup = new Group(group_name, group_image);

                if (editText.length() == 0) {
                    toastMessage("Please enter group name.");
                } else if (user_ids.size() == 0) {
                    toastMessage("Please select group members.");
                } else {
//                    Log.d("CURRENT ID", currId + Integer.parseInt(currId));
                    user_ids.add(Integer.parseInt(currId));
                    CreateGroup(newGroup, user_ids);
                    Intent myIntent = new Intent(CreateNewGroupActivity.this, InGroupActivity.class);
                    CreateNewGroupActivity.this.startActivity(myIntent);

//                    //empty name and image input
//                    editText.setText("");
//                    circleImageView.setImageResource(R.mipmap.ic_launcher_round);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<User> result = searchUser(newText.toString());

                Log.d("QUERY", "START_SEARCH");
                populateUserRecyclerView(result);
                return true;
            }
        });

        recyclerViewUser.setHasFixedSize(true);
        recyclerViewUser.setLayoutManager(linearLayoutManager);

        populateUserRecyclerView(getAllUsers());

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    ////////////////////////////////////////////////// populate user list //////////////////////////////////////////////////////

    //Todo: refactor? exactly the same code as the one in CreateNewGroup
    private void populateUserRecyclerView(final List<User> users) {
        Log.d(TAG, "populateUserRecyclerView: Displaying list of groups in the ListView.");
    // TODO user_adapter????????
        RVAdapter_User adapter = new RVAdapter_User(users, user_ids, new RVAdapter_User.ClickListener() {
            @Override public void onClick(View v, int position) {
                ImageView button = (ImageView) v.findViewById(R.id.button);

                Log.d("POSITION", String.valueOf(position));
                Log.d("ID", String.valueOf(users.get(position).getUser_id()));
                Log.d("NAME", String.valueOf(users.get(position).getUser_name()));

                if (user_ids.contains(users.get(position).getUser_id())) {
                    user_ids.remove(Integer.valueOf(users.get(position).getUser_id()));
//                    v.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    button.setImageResource(R.drawable.ic_radio_button_unchecked);
                } else {
                    user_ids.add(users.get(position).getUser_id());
//                    v.setBackgroundColor(getResources().getColor(R.color.user_pressed));
                    button.setImageResource(R.drawable.ic_check_circle);
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
                    bitmap =  getResizedBitmap(bitmap, 70);
                    circleImageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateNewGroupActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            circleImageView.setImageBitmap(thumbnail);
        }
    }

    ///////////////////////////////////////////// convert image view to byte array /////////////////////////////////////////////

    //convert image view to byte array
    public byte[] imageViewToByte(ImageView image) {
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

        HashMap<String, String> user = sessionManager.getUserDetails();
        Log.d("USER DETAIL", user.toString());

        ContentValues values = new ContentValues();
        values.put(GroupContract.GroupEntry.COL_GROUP_NAME, group.getGroup_name());
        values.put(GroupContract.GroupEntry.COL_GROUP_IMAGE, group.getGroup_image());
        values.put(GroupContract.GroupEntry.COL_GROUP_CREATOR, currId);
        Log.d("CURRENT ID", currId);

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
        String selectQuery = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE "
                + UserContract.UserEntry._ID + " != ?";
        String[] selectionArgs = new String[]{currId};

        Log.e("USERS", selectQuery);


        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, selectionArgs);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                User user = new User();
                user.setUser_name((c.getString(c.getColumnIndex(UserContract.UserEntry.COL_FULLNAME))));
                user.setUser_id((c.getInt(c.getColumnIndex(UserContract.UserEntry._ID))));

                Log.d("NAME", c.getString(c.getColumnIndex(UserContract.UserEntry.COL_FULLNAME)));
                Log.d("ID", String.valueOf(c.getInt(c.getColumnIndex(UserContract.UserEntry._ID))));

                // adding to group list
                users.add(user);
            } while (c.moveToNext());
        }

        return users;
    }

    /*
     * Search user by full name
     * */
    public List<User> searchUser(String query) {
        List<User> users = new ArrayList<User>();

        String selectQuery = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE "
                + UserContract.UserEntry.COL_FULLNAME + " LIKE ? AND "
                + UserContract.UserEntry._ID + " != ?";
        String[] selectionArgs = new String[]{"%" + query + "%", currId};

        Log.e("USERS", selectQuery);

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, selectionArgs);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                User user = new User();
                user.setUser_name((c.getString(c.getColumnIndex(UserContract.UserEntry.COL_FULLNAME))));
                user.setUser_id((c.getInt(c.getColumnIndex(UserContract.UserEntry._ID))));

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        user_ids.clear();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        editText = findViewById(R.id.editText);
        circleImageView = findViewById(R.id.group_image);
        fab_pic = findViewById(R.id.fab_pic);
        btnCreate = findViewById(R.id.btnCreate);
        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(getApplicationContext());
        recyclerViewUser = (RecyclerView)findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        user_ids = new ArrayList<>();
        searchView = (SearchView) findViewById(R.id.search_user);
        user = sessionManager.getUserDetails();
        currId = user.get(SessionManager.KEY_ID);
    }

}
