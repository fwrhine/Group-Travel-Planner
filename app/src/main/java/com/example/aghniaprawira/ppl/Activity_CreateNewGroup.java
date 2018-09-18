package com.example.aghniaprawira.ppl;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
    final int REQUEST_CODE_GALLERY = 999;

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
                ActivityCompat.requestPermissions(
                        Activity_CreateNewGroup.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
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

    //Todo: insert data to database, if success, open group chat
    public void CreateGroup(Group group, ArrayList<Long> user_ids) {
        long createGroup = databaseHelper.createGroup(group, user_ids);

        if (createGroup != -1) {
            toastMessage("New group!");
            Intent myIntent = new Intent(Activity_CreateNewGroup.this, Activity_InGroup.class);
            Activity_CreateNewGroup.this.startActivity(myIntent);
        } else {
            toastMessage("Fail.");
        }
    }

    //Todo: help
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Todo: help
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


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

    @NonNull
    private static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }
}
