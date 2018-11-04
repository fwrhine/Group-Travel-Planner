package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    public List<Message> mMessageList;
    private SessionManager sessionManager;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;

    public MessageAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
        sessionManager = new SessionManager(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference().child("users");
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = (Message) mMessageList.get(position);

        if (message.getSenderId().equals(sessionManager.getUserDetails().get("id"))) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, messageTimeText, photoTimeText;
        ImageView photoImageView;

        public SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            messageTimeText = itemView.findViewById(R.id.text_message_time);
            photoTimeText = itemView.findViewById(R.id.photo_message_time);
        }

        void bind(Message message) {
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

            // Format the stored timestamp into a readable String using method.
//            timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
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

        void bind(final Message message) {
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
}
