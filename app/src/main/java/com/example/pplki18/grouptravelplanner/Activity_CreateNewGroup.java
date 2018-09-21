package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Activity_CreateNewGroup extends AppCompatActivity {
    private static final String TAG = "Activity_CreateNewGroup";

    DatabaseHelper databaseHelper;
    private Button btnCreate, btnChoose;
    private EditText editText;
    private ImageView imageView;
    private ListView listViewUser;
    private Toolbar toolbar;
    private ArrayList<Long> user_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        setSupportActionBar(toolbar);
        init();

        //Todo: choose image button: on click, ask permission to read gallery
//        btnChoose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showPictureDialog();
//            }
//        });

        //Todo: create new group button: on click, insert data to database
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group_name = editText.getText().toString();
                byte[] group_image = imageViewToByte(imageView);
                Group newGroup = new Group(group_name, group_image);

                if (editText.length() != 0) {
                    CreateGroup(newGroup, user_ids);

                    //empty name and image input
                    editText.setText("");
                    imageView.setImageResource(R.mipmap.ic_launcher_round);
                } else {
                    toastMessage("Name cannot be empty.");
                }
            }
        });

        //Todo: users on list view: on click, move to top of list and change icon to checklist
        listViewUser.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                //Todo: add to user_ids
//                user_ids.add();
            }
        });

        populateUserListView();
    }

    ////////////////////////////////////////////////// populate user list //////////////////////////////////////////////////////

    private void populateUserListView() {
        Log.d(TAG, "populateUserListView: Displaying data in the ListView.");

        //get data and append to list
        List<User> users = databaseHelper.getAllUsers();
        List<String> user_names = new ArrayList<>();

        for (User user: users) {
            user_names.add(user.getUser_name());
        }

        //create the list adapter and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, user_names);
        listViewUser.setAdapter(adapter);
    }

    ////////////////////////////////////////////////////// choose image ////////////////////////////////////////////////////////

//    private void showPictureDialog(){
//        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
//        pictureDialog.setTitle("Select Action");
//        String[] pictureDialogItems = {
//                "Select photo from gallery",
//                "Capture photo from camera" };
//        pictureDialog.setItems(pictureDialogItems,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case 0:
//                                choosePhotoFromGallary();
//                                break;
//                            case 1:
//                                takePhotoFromCamera();
//                                break;
//                        }
//                    }
//                });
//        pictureDialog.show();
//    }

//    public void choosePhotoFromGallary() {
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//        startActivityForResult(galleryIntent, GALLERY);
//    }
//
//    private void takePhotoFromCamera() {
//        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, CAMERA);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == this.RESULT_CANCELED) {
//            return;
//        }
//        if (requestCode == GALLERY) {
//            if (data != null) {
//                Uri contentURI = data.getData();
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                    String path = saveImage(bitmap);
//                    Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
//                    imageview.setImageBitmap(bitmap);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        } else if (requestCode == CAMERA) {
//            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//            imageview.setImageBitmap(thumbnail);
//            saveImage(thumbnail);
//            Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
//        }
//    }

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
    public void CreateGroup(Group group, ArrayList<Long> user_ids) {
        long createGroup = databaseHelper.insertGroup(group, user_ids);

        if (createGroup != -1) {
            toastMessage("New group!");
            Intent myIntent = new Intent(Activity_CreateNewGroup.this, Activity_InGroup.class);
            Activity_CreateNewGroup.this.startActivity(myIntent);
        } else {
            toastMessage("Fail.");
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        editText = findViewById(R.id.editText);
        imageView = findViewById(R.id.imageView);
        listViewUser = findViewById(R.id.listViewUser);
        btnCreate = findViewById(R.id.btnCreate);
        btnChoose = findViewById(R.id.btnChoose);
        databaseHelper = new DatabaseHelper(this);
        user_ids = new ArrayList<>();
    }

}
