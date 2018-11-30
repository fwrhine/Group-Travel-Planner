package com.example.pplki18.grouptravelplanner.utils.Chat;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pplki18.grouptravelplanner.ActivityPollChoiceTest;
import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.Message;

import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SentPollMessageHolder extends ChatViewHolder {
    TextView messageText, messageTimeText, photoTimeText, messageReadText, photoReadText, pollID;
    ImageView photoImageView;
    ConstraintLayout cL;
    Context context;
    Button btn_poll_redirect;

    public SentPollMessageHolder(final View itemView, final Context context) {
        super(itemView);

        messageText = itemView.findViewById(R.id.text_poll_body);
        photoImageView = itemView.findViewById(R.id.photoImageView);
        messageTimeText = itemView.findViewById(R.id.text_message_time);
        photoTimeText = itemView.findViewById(R.id.photo_message_time);
        messageReadText = itemView.findViewById(R.id.text_poll_read);
        photoReadText = itemView.findViewById(R.id.photo_message_read);
        pollID = itemView.findViewById(R.id.text_message_pollID);
        cL = (ConstraintLayout) itemView.findViewById(R.id.text_poll_redirect);
        this.context = context;

        btn_poll_redirect = (Button) itemView.findViewById(R.id.text_poll_redirect_button);

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
    public void bind(Message message) {
        boolean isPhoto = message.getPhotoUrl() != null;
        Long hours = TimeUnit.MILLISECONDS.toHours(message.getTime());
        Long minutes = TimeUnit.MILLISECONDS.toMinutes(message.getTime());
        if (isPhoto) {
            messageText.setVisibility(View.GONE);
            messageTimeText.setVisibility(View.GONE);
            messageReadText.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            photoTimeText.setVisibility(View.VISIBLE);
            photoReadText.setVisibility(View.VISIBLE);

            // Set image
            Glide.with(photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(photoImageView);

            // Set time
            String time = String.format("%tT", message.getTime() - TimeZone.getDefault().getRawOffset());
            photoTimeText.setText(time.substring(0,5));

            // Set read by
            HashMap<String, Boolean> reads = message.getRead_by();
            if (reads == null || reads.size() == 0) {
                photoReadText.setVisibility(View.GONE);
            } else {
                photoReadText.setText("Read " + reads.size());
            }
        } else {
            messageText.setVisibility(View.VISIBLE);
            messageTimeText.setVisibility(View.VISIBLE);
            messageReadText.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            photoTimeText.setVisibility(View.GONE);
            photoReadText.setVisibility(View.GONE);

            // Set text
            messageText.setText(message.getText());
            pollID.setText(message.getPollID());
            pollID.setVisibility(View.INVISIBLE);

            // Set time
            String time = String.format("%tT", message.getTime() - TimeZone.getDefault().getRawOffset());
            messageTimeText.setText(time.substring(0,5));

            // Set read by
            HashMap<String, Boolean> reads = message.getRead_by();
            if (reads == null || reads.size() == 0) {
                messageReadText.setVisibility(View.GONE);
            } else {
                messageReadText.setText("Read " + reads.size());
            }
        }
    }
}