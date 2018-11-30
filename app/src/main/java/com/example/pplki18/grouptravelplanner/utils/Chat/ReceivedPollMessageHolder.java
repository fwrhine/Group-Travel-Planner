package com.example.pplki18.grouptravelplanner.utils.Chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pplki18.grouptravelplanner.ActivityPollChoiceTest;
import com.example.pplki18.grouptravelplanner.InGroupActivity;
import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.Message;
import com.example.pplki18.grouptravelplanner.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.TimeZone;

public class ReceivedPollMessageHolder extends ChatViewHolder {
    String groupId;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef, readStampsRef;
    TextView messageText, messageTimeText, photoTimeText, nameText, pollID;
    ImageView photoImageView, profileImage;
    ConstraintLayout cL;
    Context context;



    public ReceivedPollMessageHolder(final View itemView, String groupId, final Context context) {
        super(itemView);
        this.groupId = groupId;
        this.context = context;
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference().child("users");
        readStampsRef = firebaseDatabase.getReference().child("groups").child(groupId).child("read_stamps");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        messageText = (TextView) itemView.findViewById(R.id.text_poll_body);
        messageTimeText = (TextView) itemView.findViewById(R.id.text_message_time);
        photoImageView = itemView.findViewById(R.id.photoImageView);
        messageTimeText = itemView.findViewById(R.id.text_message_time);
        photoTimeText = itemView.findViewById(R.id.photo_message_time);
        nameText = (TextView) itemView.findViewById(R.id.text_message_name);
        profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        pollID = (TextView) itemView.findViewById(R.id.text_message_pollID);
        cL = (ConstraintLayout) itemView.findViewById(R.id.text_poll_redirect_button);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pollID = (TextView) itemView.findViewById(R.id.text_message_pollID);
                String pollIDstring = pollID.getText().toString();
                Intent pollChoiceIntent = new Intent(context, ActivityPollChoiceTest.class);
                        pollChoiceIntent.putExtra("pollID", pollIDstring);
                context.startActivity(pollChoiceIntent);
                Log.v("REDIRECT", "PRESSED ButtoN");
            }
        });
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

                            pollID.setText(message.getPollID());
                            pollID.setVisibility(View.INVISIBLE);

                            String time = String.format("%tT", message.getTime() - TimeZone.getDefault().getRawOffset());
                            messageTimeText.setText(time.substring(0,5));
                        }
                        updateLastRead(message.getTime());
                        addRead(message.getMessageId());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void updateLastRead(final Long timestamp) {
        readStampsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                readStampsRef.child(firebaseUser.getUid()).setValue(timestamp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addRead(final String messageId) {
//        final Query msgRef = firebaseDatabase.getReference().child("message").child(groupId).orderByChild("time").equalTo(message.getTime());
//        final DatabaseReference ref = msgRef.getRef();

        final DatabaseReference msgRef = firebaseDatabase.getReference().child("messages").child(groupId).child(messageId);

        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                msgRef.child("read_by").child(firebaseUser.getUid()).setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
