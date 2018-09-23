package com.example.pplki18.grouptravelplanner.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class RVAdapter_User extends RecyclerView.Adapter<RVAdapter_User.UserViewHolder>{

    private List<User> users;
    private final ClickListener listener;

    public RVAdapter_User(List<User> users, ClickListener listener){
        this.users = users;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_user, viewGroup, false);
        UserViewHolder pvh = new UserViewHolder(v, listener);
        return pvh;
    }

    @Override
    public void onBindViewHolder(UserViewHolder userViewHolder, int i) {
        userViewHolder.userName.setText(users.get(i).getUser_name());

//        byte[] byteArray = users.get(i).getUser_image();
//        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//        userViewHolder.userImage.setImageBitmap(bmp);

        userViewHolder.userImage.setImageResource(R.drawable.ic_face);
        userViewHolder.button.setImageResource(R.drawable.ic_radio_button_unchecked);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardView;
        private TextView userName;
        private ImageView userImage;
        private ImageView button;
        private WeakReference<ClickListener> listenerRef;

        UserViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cv);
            userName = (TextView)itemView.findViewById(R.id.user_name);
            userImage = (ImageView)itemView.findViewById(R.id.user_image);
            button = (ImageView)itemView.findViewById(R.id.button);
            listenerRef = new WeakReference<>(listener);

            cardView.setOnClickListener(this);
        }

        // onClick Listener for view
        @Override
        public void onClick(View v) {
//            Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            listenerRef.get().onClick(v, getAdapterPosition());
        }

    }

    public interface ClickListener {
        void onClick(View v, int position);
    }
}
