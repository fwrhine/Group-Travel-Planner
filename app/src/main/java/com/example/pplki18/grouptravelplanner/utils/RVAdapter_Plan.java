package com.example.pplki18.grouptravelplanner.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.PlanContract;

import java.util.List;

public class RVAdapter_Plan extends RecyclerView.Adapter<RVAdapter_Plan.PlanViewHolder>{

    List<Plan> plans;
    Context context;

    public RVAdapter_Plan(List<Plan> plans, Context context) {
        this.plans = plans;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    @Override
    public RVAdapter_Plan.PlanViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_plan, viewGroup, false);
        RVAdapter_Plan.PlanViewHolder pvh = new RVAdapter_Plan.PlanViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final RVAdapter_Plan.PlanViewHolder planViewHolder, int i) {
        Plan plan = plans.get(i);
        String total_day_str;
        int total_day = plan.getPlan_total_days();
        if (total_day == 1 || total_day == 0) {
            total_day_str = " (" + total_day + " day trip)";
        } else {
            total_day_str = " (" + total_day + " days trip)";
        }
        String dateString = plan.getPlan_start_date() + " - " +plan.getPlan_end_date()
                + total_day_str;
        String createdString = "Modified: " + plan.getPlan_modified() + " / "
                + "Created: " + plan.getPlan_created();

        planViewHolder.planName.setText(plan.getPlan_name());
        planViewHolder.planDate.setText(dateString);
        planViewHolder.planOverview.setText(plan.plan_overview);
        planViewHolder.planCreated.setText(createdString);

        final int position = i;
        final String name = plan.getPlan_name();

        planViewHolder.planMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, planViewHolder.planMenuButton);
                //inflating menu from xml resource
                popup.inflate(R.menu.plan_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        AlertDialog box;
                        switch (item.getItemId()) {
                            case R.id.rename_plan:
                                box = renameDialog(position, name);
                                box.show();
                                break;
                            case R.id.delete_plan:
                                box = deleteConfirmation(position, name);
                                box.show();
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private AlertDialog deleteConfirmation(final int position, String name) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete " + name + "?")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        deletePlan(plans.get(position));
                        plans.remove(position);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    private void deletePlan(Plan plan) {
        DatabaseHelper myDb = new DatabaseHelper(context);
        SQLiteDatabase db = myDb.getWritableDatabase();

        String deleteQuery = "DELETE FROM " + PlanContract.PlanEntry.TABLE_NAME + " WHERE " +
                PlanContract.PlanEntry._ID + " = " + plan.getPlan_id();

        db.execSQL(deleteQuery);
        db.close();
        notifyDataSetChanged();
    }

    private AlertDialog renameDialog(final int position, String name) {
        final EditText edtText = new EditText(context);
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Rename " + name)
                .setMessage("Insert new name below!")
                .setView(edtText)
                .setPositiveButton("Rename", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your renaming code
                        String new_name = edtText.getText().toString();
                        renamePlan(plans.get(position), new_name);
                        plans.get(position).setPlan_name(new_name);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    public void renamePlan(Plan plan, String new_name) {
        DatabaseHelper myDb = new DatabaseHelper(context);
        SQLiteDatabase db = myDb.getWritableDatabase();

        String updateQuery = "UPDATE " + PlanContract.PlanEntry.TABLE_NAME + " SET " +
                PlanContract.PlanEntry.COL_PLAN_NAME + " = " + "\"" + new_name + "\"" +
                " WHERE " + PlanContract.PlanEntry._ID + " = " + plan.getPlan_id();

        db.execSQL(updateQuery);
        db.close();
        notifyDataSetChanged();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView planName;
        TextView planDate;
        TextView planOverview;
        TextView planCreated;
        ImageButton planMenuButton;

        PlanViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cv_plan);
            planName = (TextView) itemView.findViewById(R.id.plan_name);
            planDate = (TextView) itemView.findViewById(R.id.plan_date);
            planOverview = (TextView) itemView.findViewById(R.id.plan_overview);
            planCreated = (TextView) itemView.findViewById(R.id.plan_created);
            planMenuButton = (ImageButton) itemView.findViewById(R.id.plan_menu_button);
        }

    }
}
