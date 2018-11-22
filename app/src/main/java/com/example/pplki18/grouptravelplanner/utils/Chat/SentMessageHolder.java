package com.example.pplki18.grouptravelplanner.utils.Chat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.Message;

import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SentMessageHolder extends ChatViewHolder {
    TextView messageText, messageTimeText, photoTimeText, messageReadText, photoReadText;
    ImageView photoImageView;

    public SentMessageHolder(View itemView) {
        super(itemView);

        messageText = itemView.findViewById(R.id.text_message_body);
        photoImageView = itemView.findViewById(R.id.photoImageView);
        messageTimeText = itemView.findViewById(R.id.text_message_time);
        photoTimeText = itemView.findViewById(R.id.photo_message_time);
        messageReadText = itemView.findViewById(R.id.text_message_read);
        photoReadText = itemView.findViewById(R.id.photo_message_read);
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
    // Format the stored timestamp into a readable String using method.
//            timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
}