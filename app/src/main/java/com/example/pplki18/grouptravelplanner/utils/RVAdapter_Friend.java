package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter_Friend extends RecyclerView.Adapter<RVAdapter_Friend.FriendViewHolder>{
    List<Friend> friends;
    DatabaseHelper myDb;
    Context context;
    Drawable drawable;

    public RVAdapter_Friend(List<Friend> friend, Context context){
        this.friends = friend;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public RVAdapter_Friend.FriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_friend, viewGroup, false);
        FriendViewHolder fvh = new FriendViewHolder(v);
        return fvh;
    }

    /*    public byte[] getFriendPic(String friendName) {
            SQLiteDatabase db = myDb.getReadableDatabase();
            byte[] pic = null;
            String command = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE " +
                    UserContract.UserEntry.COL_USERNAME + " = " + friendName;
            Cursor cursor = db.rawQuery(command, null);
            Log.d("DATA", "fetch user pictures");
            if (cursor != null) {
                cursor.moveToFirst();
                pic = cursor.getBlob(cursor.getColumnIndex(UserContract.UserEntry.COL_PICTURE));
            }
            return pic;
        }

        public String getFriendUsernames() {
            SQLiteDatabase db = myDb.getReadableDatabase();
            String name = null;
            String command = "SELECT * FROM " + UserContract.UserEntry.TABLE_NAME + " WHERE " +
                    UserContract.UserEntry._ID + " = " + FriendsContract.FriendsEntry.COL_FRIEND_ID;
            Cursor cursor = db.rawQuery(command, null);
            Log.d("DATA", "fetch user pictures");
            if (cursor != null) {
                cursor.moveToFirst();
                name = cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COL_USERNAME));
            }
            return name;
        }*/
    @Override
    public void onBindViewHolder(RVAdapter_Friend.FriendViewHolder friendViewHolder, int i) {
//        Log.d("INSIDE FRIEND", friends.get(i).getFriend_username());
//        List<Friend> friendDummyList = new ArrayList<Friend>();
//        Friend dummy1 = new Friend();
//        dummy1.setFriend_username("dummy1");
//        Drawable drawable = drawable.user_pic;
//        Bitmap dummypic = ((BitmapDrawable) drawable).getBitmap();
//        dummy1.setUser_friend_image(R.drawable.user_pic);
//        friendDummyList.add(dummy1);
//        friendViewHolder.friendName.setText(friends.get(i).getFriend_username());
        String res = friends.get(i).getId() + "";
        friendViewHolder.friendName.setText(res);

        //TODO set image resource depending on user's profile image

//        byte[] pic = friends.get(i).getFriend_image();
//        Bitmap bmp = BitmapFactory.decodeByteArray(pic, 0, pic.length);
//        friendViewHolder.friendImage.setImageResource(R.drawable.user_pic);
//        friendViewHolder.friendImage.setImageBitmap(bmp);

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
            cardView = itemView.findViewById(R.id.cv);
            friendName = itemView.findViewById(R.id.friend_name);
            friendImage = itemView.findViewById(R.id.friend_image);

        }
    }

//    public List<Friend> getAllFriend() {
//        List<Friend> friends = new ArrayList<Friend>();
//        String selectQuery = "SELECT * FROM " + FriendsContract.FriendsEntry.TABLE_NAME;
//
//        Log.e("GROUPS", selectQuery);
//
//        SQLiteDatabase db = myDb.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (c.moveToFirst()) {
//            do {
//                Friend friend = new Friend();
//                friend.setFriend_id((c.getInt(c.getColumnIndex(FriendsContract.FriendsEntry.COL_FRIEND_ID))));
//                friend.setFriend_name(c.getString(c.getColumnIndex(FriendsContract.FriendsEntry.COL_FRIEND_USERNAME)));
//                friend.setFriend_id((c.getInt(c.getColumnIndex(FriendsContract.FriendsEntry.COL_USER_ID))));
//
////                List<String> members = getAllGroupMember(c.getString(c.getColumnIndex(GroupContract.GroupEntry._ID)));
////                friend.setGroup_members(members);
//
//                // adding to group list
////                groups.add(group);
//            } while (c.moveToNext());
//        }
//
//        return friends;
//    }
}