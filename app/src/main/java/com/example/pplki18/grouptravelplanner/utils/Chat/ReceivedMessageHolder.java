package com.example.pplki18.grouptravelplanner.utils.Chat;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.Message;
import com.example.pplki18.grouptravelplanner.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.TimeZone;

public class ReceivedMessageHolder extends ChatViewHolder {
    DatabaseReference userRef;
    TextView messageText, messageTimeText, photoTimeText, nameText;
    ImageView photoImageView, profileImage;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
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
        Log.d("BINDING chat for user", message.getSenderId());
        userRef.child(message.getSenderId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user.getPhotoUrl() != null && !user.getPhotoUrl().equals("none")) {
                            Uri uri = Uri.parse(user.getPhotoUrl());
                            Log.d("PHOTO_URL", user.getPhotoUrl());
                            Glide.with(profileImage.getContext())
                                    .load(uri)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(profileImage);
                        } else {
                            profileImage.setImageResource(R.drawable.user_pic);
                        }

                        nameText.setText(user.getFullName());
                        boolean isPhoto = message.getPhotoUrl() != null;
                        if (isPhoto) {
                            messageText.setVisibility(View.GONE);
                            messageTimeText.setVisibility(View.GONE);
                            photoImageView.setVisibility(View.VISIBLE);
                            photoTimeText.setVisibility(View.VISIBLE);
                            Glide.with(photoImageView.getContext())
                                    .load(message.getPhotoUrl())
                                    .into(photoImageView);

                            String time = String.format("%tT", message.getTime() - TimeZone.getDefault().getRawOffset());
                            photoTimeText.setText(time.substring(0,5));
                        } else {
                            messageText.setVisibility(View.VISIBLE);
                            messageTimeText.setVisibility(View.VISIBLE);
                            photoImageView.setVisibility(View.GONE);
                            photoTimeText.setVisibility(View.GONE);
                            messageText.setText(message.getText());

                            String time = String.format("%tT", message.getTime() - TimeZone.getDefault().getRawOffset());
                            messageTimeText.setText(time.substring(0,5));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
