package com.example.pplki18.grouptravelplanner.utils.Chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.pplki18.grouptravelplanner.data.Message;

public abstract class ChatViewHolder extends RecyclerView.ViewHolder {
    public ChatViewHolder(View itemView) {
        super(itemView);
    }
    public abstract void bind(Message message);
}
