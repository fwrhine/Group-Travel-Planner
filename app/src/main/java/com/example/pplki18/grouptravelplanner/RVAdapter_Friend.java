package com.example.pplki18.grouptravelplanner;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;

import java.util.List;

public class RVAdapter_Friend extends RecyclerView.Adapter<RVAdapter_Friend.FriendViewHolder>{
    List<Friend> friend;
    DatabaseHelper myDb;
    RVAdapter_Friend(List<Friend> friend){
        this.friend = friend;
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public RVAdapter_Friend.FriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_friend, viewGroup, false);
        RVAdapter_Friend.FriendViewHolder pvh = new RVAdapter_Friend.FriendViewHolder(v);
        return pvh;
    }

/*    public Byte getUserPic() {
        SQLiteDatabase db = myDb.getReadableDatabase();
        String command = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME;
        Cursor data = db.rawQuery(command, null);
        Log.d("DATA", null);
        return data.ge;
    }*/

    @Override
    public void onBindViewHolder(RVAdapter_Friend.FriendViewHolder personViewHolder, int i) {
        personViewHolder.friendName.setText("Dummy 1");
        personViewHolder.friendImage.setImageResource(R.drawable.user_pic);

/*        byte[] byteArray = friend.get(i).friend_image;
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        personViewHolder.groupImage.setImageBitmap(bmp);*/

        //TODO set image resource depending on user's profile image
//        personViewHolder.friendImage.setImageResource(R.drawable.user_pic);


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView friendName;
        ImageView friendImage;

        FriendViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cv);
            friendName = (TextView)itemView.findViewById(R.id.friend_name);
            friendImage = (ImageView)itemView.findViewById(R.id.friend_image);


        }
    }
}
