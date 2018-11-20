package com.example.pplki18.grouptravelplanner.utils.Chat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.Message;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SentMessageHolder extends ChatViewHolder {
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
        Long hours = TimeUnit.MILLISECONDS.toHours(message.getTime());
        Long minutes = TimeUnit.MILLISECONDS.toMinutes(message.getTime());
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
    // Format the stored timestamp into a readable String using method.
//            timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
}