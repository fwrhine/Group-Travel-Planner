package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.data.Message;
import com.example.pplki18.grouptravelplanner.utils.Chat.ChatViewHolder;
import com.example.pplki18.grouptravelplanner.utils.Chat.ReceivedMessageHolder;
import com.example.pplki18.grouptravelplanner.utils.Chat.ReceivedPollMessageHolder;
import com.example.pplki18.grouptravelplanner.utils.Chat.SentMessageHolder;
import com.example.pplki18.grouptravelplanner.utils.Chat.SentPollMessageHolder;
import com.example.pplki18.grouptravelplanner.utils.MessageAdapter;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.example.pplki18.grouptravelplanner.data.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Fragment_GroupChat extends Fragment {

    private static final String TAG = "ChatGroupActivity";

    private final int VIEW_TYPE_MESSAGE_SENT = 1;
    private final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private final int VIEW_TYPE_POLL_SENT = 3;
    private final int VIEW_TYPE_POLL_RECEIVED = 4;
    private static final int RC_PHOTO_PICKER = 2;
    private Group group;
    private SessionManager sessionManager;

    private RecyclerView messageRecyclerView;
    private ProgressBar progressBar;
    private ImageButton photoPickerButton;
    private EditText messageEditText;
    private Button sendButton;
    private Button pollButton;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference messageDatabaseReference;
    private DatabaseReference userRef;
    private DatabaseReference readStampsRef;
    private ChildEventListener childEventListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;
    private StorageReference chatPhotoStorageReference;

    FirebaseRecyclerAdapter<Message, ChatViewHolder> adapter;
    FirebaseRecyclerOptions<Message> options;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Chat");
        return inflater.inflate(R.layout.fragment_groupchat, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent i = getActivity().getIntent();
        group = i.getParcelableExtra("group");
        getActivity().setTitle(group.getGroup_name());

        init();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = firebaseDatabase.getReference().child("users");

        messageDatabaseReference = firebaseDatabase.getReference().child("messages").child(group.getGroup_id());
        chatPhotoStorageReference = firebaseStorage.getReference().child("chat_photos").child(group.getGroup_id());

        addSendButtonClickListener();

        options = new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(messageDatabaseReference, Message.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Message, ChatViewHolder>(options) {


                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Message model) {
                        switch (holder.getItemViewType()) {
                            case VIEW_TYPE_MESSAGE_SENT:
                                holder.bind(model);
                                break;
                            case VIEW_TYPE_MESSAGE_RECEIVED:
                                holder.bind(model);
                            case VIEW_TYPE_POLL_SENT:
                                holder.bind(model);
//                                holder.itemView.findViewById(R.id.text_poll_redirect).setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        TextView pollIDextra = holder.itemView.findViewById(R.id.text_message_pollID);
//                                        String pollID = pollIDextra.getText().toString();
//                                        Intent pollChoiceIntent = new Intent(getActivity() , ActivityPollChoiceTest.class);
//                                        pollChoiceIntent.putExtra("pollID", pollID);
//                                        startActivity(pollChoiceIntent);
//                                    }
//                                });
                                break;
                            case VIEW_TYPE_POLL_RECEIVED:
                                holder.bind(model);
                                holder.itemView.findViewById(R.id.text_poll_redirect).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                        }
                    }

                    @NonNull
                    @Override
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        switch (viewType) {
                            case VIEW_TYPE_MESSAGE_SENT:
                                return new SentMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false));

                            case VIEW_TYPE_MESSAGE_RECEIVED:
                                return new ReceivedMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false), group.getGroup_id());
                              //TODO case for poll send and receive and do viewholder
                            case VIEW_TYPE_POLL_SENT:
                                return new SentPollMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poll_sent, parent, false), getContext());
//
                            case VIEW_TYPE_POLL_RECEIVED:
                                return new ReceivedPollMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poll_received, parent, false), group.getGroup_id(), getContext());
                        }
                        return null;
                    }

                    @Override
                    public int getItemViewType(int position) {
                        Message message = getItem(position);
                        if (message.getSenderId().equals(sessionManager.getUserDetails().get("id"))) {
                            // If the current user is the sender of the message
                            // if pollID exist
                            if (!message.getPollID().isEmpty()) {
                                return VIEW_TYPE_POLL_SENT;
                            }
                            else {
                                return VIEW_TYPE_MESSAGE_SENT;
                            }
                        } else {
//                            if (!message.getPollID().isEmpty()) {
//                                return VIEW_TYPE_POLL_RECEIVED;
//                            }
//                            else {
                                // If some other user sent the message
                                return VIEW_TYPE_MESSAGE_RECEIVED;
//                            }
                        }
                    }
                };
        messageRecyclerView.setAdapter(adapter);
    }

    private void init() {
        sessionManager = new SessionManager(getContext());
        progressBar = getView().findViewById(R.id.progress_loader);
        messageRecyclerView = getView().findViewById(R.id.messageRecyclerView);
        photoPickerButton = getView().findViewById(R.id.photoPickerButton);
        messageEditText = getView().findViewById(R.id.messageEditText);
        sendButton = getView().findViewById(R.id.sendButton);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);

        messageRecyclerView.setLayoutManager(linearLayoutManager);
        pollButton = getView().findViewById(R.id.pollButton);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Message> messages = new ArrayList<>();

        progressBar.setVisibility(View.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        photoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        pollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), Activity_CreatePoll.class);
                String[] voters = new String[group.getMembers().size()];
                Log.v("Group members", group.getMembers().toString());
                for (int ii = 0; ii < group.getMembers().size(); ii++) {
                    voters[ii] = group.getMembers().get(ii);
                }
                ArrayList<String> votersList = new ArrayList<String>(Arrays.asList(voters));
                myIntent.putStringArrayListExtra("voters", votersList);
                String groupID = group.getGroup_id();
                myIntent.putExtra("groupID", groupID);
                // Need to put group id or chat??
                Fragment_GroupChat.this.startActivity(myIntent);
            }
        });

        // Enable Send button when there's text to send
         messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void addSendButtonClickListener() {
        // Send button sends a message and clears the EditText
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
                String msgKey = messageDatabaseReference.push().getKey();
                Message message = new Message(msgKey, firebaseUser.getUid(), messageEditText.getText().toString(), null, System.currentTimeMillis());
                messageDatabaseReference.child(msgKey).setValue(message);
                messageRecyclerView.smoothScrollToPosition(messageRecyclerView.getAdapter().getItemCount());

                // Clear input box
                messageEditText.setText("");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        messageRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Log.d("SENDING PHOTO", true + "");
            Uri selectedImageUri = data.getData();
            final StorageReference photoRef = chatPhotoStorageReference.child(selectedImageUri.getLastPathSegment());

            // Upload photo and push message
            photoRef.putFile(selectedImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String msgKey = messageDatabaseReference.push().getKey();
                        Message message = new Message(msgKey, firebaseUser.getUid(), null, downloadUri.toString(), System.currentTimeMillis());
                        messageDatabaseReference.child(msgKey).setValue(message);
                        messageRecyclerView.smoothScrollToPosition(messageRecyclerView.getAdapter().getItemCount());
                    } else {
                        Toast.makeText(getContext(), "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
