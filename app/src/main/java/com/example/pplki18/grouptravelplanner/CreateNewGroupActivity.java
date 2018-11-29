package com.example.pplki18.grouptravelplanner;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.example.pplki18.grouptravelplanner.old_stuff.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.User;
import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.utils.RVAdapter_User;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateNewGroupActivity extends AppCompatActivity {
    private static final String TAG = "CreateNewGroupActivity";

    private Button btnCreate;
    private FloatingActionButton fab_pic;
    private EditText editText;
    private CircleImageView circleImageView;
    private Toolbar toolbar;
    private ArrayList<String> user_ids;
    private RecyclerView recyclerViewUser;
    private LinearLayoutManager linearLayoutManager;
    private SearchView searchView;
    private final int GALLERY = 1;
    private final int CAMERA = 2;

    private FirebaseUser firebaseUser;
    private DatabaseReference userRef;
    private DatabaseReference groupRef;
    private StorageReference storageReference;

    private byte[] groupImageByte;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = firebaseDatabase.getReference().child("users");
        groupRef = firebaseDatabase.getReference().child("groups");
        storageReference = FirebaseStorage.getInstance().getReference();
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

                if (editText.length() == 0) {
                    toastMessage("Please enter group name.");
                } else if (user_ids.size() == 1) {
                    toastMessage("Please select group members.");
                } else {
                    createGroup(group_name);
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
                List<User> result = null;

                if(!newText.equals(null)){
                    result = searchUser(newText);
                }

                Log.d("QUERY", "START_SEARCH");
                populateUserRecyclerView(result);
                return true;
            }
        });

        recyclerViewUser.setHasFixedSize(true);
        recyclerViewUser.setLayoutManager(linearLayoutManager);

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

        RVAdapter_User adapter = new RVAdapter_User(users, user_ids, new RVAdapter_User.ClickListener() {
            @Override public void onClick(View v, int position) {
                ImageView button = (ImageView) v.findViewById(R.id.button);

                Log.d("POSITION", String.valueOf(position));
                Log.d("ID", users.get(position).getId());
                Log.d("NAME", users.get(position).getId());

                if (user_ids.contains(users.get(position).getId())) {
                    user_ids.remove(users.get(position).getId());
//                    v.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    button.setImageResource(R.drawable.ic_radio_button_unchecked);
                } else {
                    user_ids.add(users.get(position).getId());
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

    private void choosePhotoFromGallery() {
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
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    bitmap =  getResizedBitmap(bitmap, 300);
                    circleImageView.setImageBitmap(bitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    groupImageByte = stream.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                    toastMessage("Failed!");
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            circleImageView.setImageBitmap(thumbnail);
        }
    }

    //get resized bitmap
    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
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

    /*
     * Search user by full name
     * */
    // TODO: Benerin biar cuma cari temen. Sekarang masih cari semua user.
    private List<User> searchUser(final String query) {
        final List<User> users = new ArrayList<>();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    if(user.getFullName().toLowerCase().contains(query.toLowerCase()) && !user.getId().equals(firebaseUser.getUid())){
                        users.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        recyclerViewUser = (RecyclerView)findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        user_ids = new ArrayList<>();
        user_ids.add(firebaseUser.getUid());
        searchView = (SearchView) findViewById(R.id.search_user);
        HashMap<String, String> user = sessionManager.getUserDetails();
        String currId = user.get(SessionManager.KEY_ID);
    }

    private void createGroup(final String group_name){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating group...");
        progressDialog.show();

        final String groupId = groupRef.push().getKey();

        if (groupImageByte != null) {
            // Case kalo user ngupload gambar
            final StorageReference ref = storageReference.child("images/group/"+ groupId);
            ref.putBytes(groupImageByte)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Group group = new Group(groupId, group_name, uri.toString(), firebaseUser.getUid());
                                    groupRef.child(groupId).setValue(group);
                                    groupRef.child(groupId).child("members").setValue(user_ids);
                                    updateUsers(user_ids, groupId);
                                }
                            });
                            toastMessage("Group created!");
                            Intent intent = new Intent(CreateNewGroupActivity.this, InHomeActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            toastMessage("Failed to upload image.");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Creating "+(int)progress+"%");
                        }
                    });
        } else {
            Group group = new Group(groupId, group_name, "none", firebaseUser.getUid());
            groupRef.child(groupId).setValue(group);
            groupRef.child(groupId).child("members").setValue(user_ids);
            updateUsers(user_ids, groupId);
            progressDialog.dismiss();
            toastMessage("Group created!");
            Intent intent = new Intent(this, InHomeActivity.class);
            startActivity(intent);
        }
    }

    /**
     * This method updates the user's information.
     * Assign the users to the created group.
     */
    private void updateUsers(List<String> user_ids, final String groupId){
        for(String userId : user_ids){
            final DatabaseReference groupsRef = userRef.child(userId).child("groups");
            final List<String> groups = new ArrayList<>();

            groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    groups.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                        groups.add(postSnapshot.getValue(String.class));
                    }
                    groups.add(groupId);
                    groupsRef.setValue(groups);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
