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
    TextView messageText, messageTimeText, messageReadText;
    ConstraintLayout cL;
    Context context;
    String groupID, pollID;

    public SentPollMessageHolder(final View itemView, String groupID, final Context context) {
        super(itemView);

        messageText = itemView.findViewById(R.id.question);
        messageTimeText = itemView.findViewById(R.id.poll_message_time);
        messageReadText = itemView.findViewById(R.id.poll_read);

        cL = (ConstraintLayout) itemView.findViewById(R.id.poll_body);
        this.context = context;
        this.groupID = groupID;

//        cL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String pollIDstring = pollID.getText().toString();
//                Intent pollChoiceIntent = new Intent(context, ActivityPollChoiceTest.class);
//                pollChoiceIntent.putExtra("pollID", pollIDstring);
//                context.startActivity(pollChoiceIntent);
//                Log.v("REDIRECT", "PRESSED ButtoN");
//            }
//        });
    }

    @Override
    public void bind(Message message) {
        Long hours = TimeUnit.MILLISECONDS.toHours(message.getTime());
        Long minutes = TimeUnit.MILLISECONDS.toMinutes(message.getTime());

        messageText.setVisibility(View.VISIBLE);
        messageTimeText.setVisibility(View.VISIBLE);

        // Set text
        messageText.setText(message.getText());
        pollID = message.getPollID();

        // Set time
        String time = String.format("%tT", message.getTime() - TimeZone.getDefault().getRawOffset());
        messageTimeText.setText(time.substring(0, 5));

        // Set redirect
        cL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pollChoiceIntent = new Intent(context, ActivityPollChoiceTest.class);
                pollChoiceIntent.putExtra("pollID", pollID);
                context.startActivity(pollChoiceIntent);
                Log.v("REDIRECT", "PRESSED BUTTON");
            }
        });

        // Set read by
        HashMap<String, Boolean> reads = message.getRead_by();
        if (reads == null || reads.size() == 0) {
            messageReadText.setVisibility(View.GONE);
        } else {
            messageReadText.setText("Read " + reads.size());
        }

    }
}