package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.data.Message;
import com.example.pplki18.grouptravelplanner.utils.MessageAdapter;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;
import com.example.pplki18.grouptravelplanner.utils.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import java.util.ArrayList;
import java.util.List;

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
        messageDatabaseReference = firebaseDatabase.getReference().child("message").child(group.getGroup_id());
        chatPhotoStorageReference = firebaseStorage.getReference().child("chat_photos").child(group.getGroup_id());

        addSendButtonClickListener();

        options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(messageDatabaseReference, Message.class)
                        .build();

        adapter =
                new FirebaseRecyclerAdapter<Message, ChatViewHolder>(options) {


                    @Override
                    protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull Message model) {
                        switch (holder.getItemViewType()) {
                            case VIEW_TYPE_MESSAGE_SENT:
                                holder.bind(model);
                                break;
                            case VIEW_TYPE_MESSAGE_RECEIVED:
                                holder.bind(model);
                            case VIEW_TYPE_POLL_SENT:
                                holder.bind(model);
                                break;
                            case VIEW_TYPE_POLL_RECEIVED:
                                holder.bind(model);
                        }
                    }

                    @NonNull
                    @Override
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        switch (viewType) {
                            case VIEW_TYPE_MESSAGE_SENT:
                                return new SentMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false));

                            case VIEW_TYPE_MESSAGE_RECEIVED:
                                return new ReceivedMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false));
                                //TODO case for poll send and receive and do viewholder
                            case VIEW_TYPE_POLL_SENT:
                                return new SentMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poll_sent, parent, false));

                            case VIEW_TYPE_POLL_RECEIVED:
                                return new ReceivedMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poll_received, parent, false));
                        }
                        return null;
                    }

                    @Override
                    public int getItemViewType(int position) {
                        Message message = getItem(position);
                        if (message.getSenderId().equals(sessionManager.getUserDetails().get("id"))) {
                            // If the current user is the sender of the message
                            return VIEW_TYPE_MESSAGE_SENT;
                        } else {
                            // If some other user sent the message
                            return VIEW_TYPE_MESSAGE_RECEIVED;
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
        pollButton = getView().findViewById(R.id.pollButton);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Message> messages = new ArrayList<>();

        progressBar.setVisibility(View.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        photoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        pollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), Activity_CreatePoll.class);
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
                Message message = new Message(firebaseUser.getUid(), messageEditText.getText().toString(), null, System.currentTimeMillis());
                messageDatabaseReference.push().setValue(message);

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

    public abstract static class ChatViewHolder extends RecyclerView.ViewHolder {
        public ChatViewHolder(View itemView) {
            super(itemView);
        }
        public abstract void bind(Message message);
    }

    private static class SentMessageHolder extends ChatViewHolder {
        TextView messageText, messageTimeText, photoTimeText;
        ImageView photoImageView;

        public SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            messageTimeText = itemView.findViewById(R.id.text_message_time);
            photoTimeText = itemView.findViewById(R.id.photo_message_time);
        }

        @Override
        public void bind(Message message) {
            boolean isPhoto = message.getPhotoUrl() != null;

            if (isPhoto) {
                messageText.setVisibility(View.GONE);
                messageTimeText.setVisibility(View.GONE);
                photoImageView.setVisibility(View.VISIBLE);
                photoTimeText.setVisibility(View.VISIBLE);
                Glide.with(photoImageView.getContext())
                        .load(message.getPhotoUrl())
                        .into(photoImageView);
            } else {
                messageText.setVisibility(View.VISIBLE);
                messageTimeText.setVisibility(View.VISIBLE);
                photoImageView.setVisibility(View.GONE);
                photoTimeText.setVisibility(View.GONE);
                messageText.setText(message.getText());
            }
        }
            // Format the stored timestamp into a readable String using method.
//            timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
    }

    private class ReceivedMessageHolder extends ChatViewHolder {
        TextView messageText, messageTimeText, photoTimeText, nameText;
        ImageView photoImageView, profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            messageTimeText = (TextView) itemView.findViewById(R.id.text_message_time);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            messageTimeText = itemView.findViewById(R.id.text_message_time);
            photoTimeText = itemView.findViewById(R.id.photo_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        @Override
        public void bind(final Message message) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User user = postSnapshot.getValue(User.class);
                        if (message.getSenderId().equals(user.getUser_id())) {
                            nameText.setText(user.getUser_name());

                            Glide.with(photoImageView.getContext())
                                    .load(user.getUser_image())
                                    .into(photoImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            boolean isPhoto = message.getPhotoUrl() != null;
            if (isPhoto) {
                messageText.setVisibility(View.GONE);
                messageTimeText.setVisibility(View.GONE);
                photoImageView.setVisibility(View.VISIBLE);
                photoTimeText.setVisibility(View.VISIBLE);
                Glide.with(photoImageView.getContext())
                        .load(message.getPhotoUrl())
                        .into(photoImageView);
            } else {
                messageText.setVisibility(View.VISIBLE);
                messageTimeText.setVisibility(View.VISIBLE);
                photoImageView.setVisibility(View.GONE);
                photoTimeText.setVisibility(View.GONE);
                messageText.setText(message.getText());
            }
        }
    }

    private void attachDatabaseReadListener() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Message message = dataSnapshot.getValue(Message.class);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
        }
    }
}
