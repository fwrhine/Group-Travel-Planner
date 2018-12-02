package com.example.pplki18.grouptravelplanner.utils.Chat;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.User;

public class MemberHolder extends RecyclerView.ViewHolder {

    String groupId;
    TextView user_fullName, user_position;
    ImageView user_photo;

    public MemberHolder(View itemView, String groupId) {
        super(itemView);
        this.groupId = groupId;
        user_fullName = itemView.findViewById(R.id.user_fullname);
        user_photo = itemView.findViewById(R.id.user_photo);
        user_position = itemView.findViewById(R.id.user_position);
    }

    public void bind(User user) {
        user_fullName.setText(user.getFullName());

        if(user.getId().equals(groupId)) {
            user_position.setText("Leader");
        } else {
            user_position.setText("Member");
        }

    }
}
