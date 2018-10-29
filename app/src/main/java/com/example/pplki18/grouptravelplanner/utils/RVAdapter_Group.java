package com.example.pplki18.grouptravelplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.utils.SessionManager;

import org.w3c.dom.Text;

import java.util.List;

public class RVAdapter_Group extends RecyclerView.Adapter<RVAdapter_Group.GroupViewHolder>{

    private List<Group> groups;
    DatabaseHelper myDb;
    SessionManager sessionManager;
    private Context context;

    public RVAdapter_Group(List<Group> groups, Context context){
        this.groups = groups;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_group, viewGroup, false);
        GroupViewHolder pvh = new GroupViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(GroupViewHolder groupViewHolder, int i) {
        groupViewHolder.groupName.setText(groups.get(i).getGroup_name());

        byte[] byteArray = groups.get(i).getGroup_image();
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        groupViewHolder.groupImage.setImageBitmap(bmp);

//        groupViewHolder.groupMembers.setText(groups.get(i).getGroup_members().toString());
        //TODO set image resource depending on user's profile image
/*        SQLiteDatabase db = myDb.getReadableDatabase();

        sessionManager = new SessionManager(context.getApplicationContext());

        String getFirstUserImg = "SELECT user.picture FROM users as u, groups as g, userGroup as ug WHERE " +
            "u._ID = ug.user_id AND ";
        db.execSQL(getFirstUserImg);
        groupViewHolder.memberImg1.setImageBitmap();*/

        groupViewHolder.memberImg1.setImageResource(R.drawable.user_pic);
        groupViewHolder.memberImg2.setImageResource(R.drawable.user_pic);
        groupViewHolder.memberImg3.setImageResource(R.drawable.user_pic);
        groupViewHolder.memberImg4.setImageResource(R.drawable.user_pic);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView groupName;
        ImageView groupImage;
        ImageView memberImg1;
        ImageView memberImg2;
        ImageView memberImg3;
        ImageView memberImg4;

        GroupViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cv);
            groupName = (TextView)itemView.findViewById(R.id.group_name);
            groupImage = (ImageView)itemView.findViewById(R.id.group_image);
            memberImg1 = (ImageView) itemView.findViewById(R.id.user_image1);
            memberImg2 = (ImageView) itemView.findViewById(R.id.user_image2);
            memberImg3 = (ImageView) itemView.findViewById(R.id.user_image3);
            memberImg4 = (ImageView) itemView.findViewById(R.id.user_image4);
        }

    }
}

