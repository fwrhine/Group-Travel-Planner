package com.example.pplki18.grouptravelplanner.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.EditPlanActivity;
import com.example.pplki18.grouptravelplanner.R;
import com.example.pplki18.grouptravelplanner.data.Group;
import com.example.pplki18.grouptravelplanner.old_stuff.DatabaseHelper;
import com.example.pplki18.grouptravelplanner.data.Plan;
import com.example.pplki18.grouptravelplanner.old_stuff.PlanContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RVAdapter_Plan extends RecyclerView.Adapter<RVAdapter_Plan.PlanViewHolder>{

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference planRef;
    DatabaseReference eventRef;
    StorageReference storageReference;

    List<String> eventIDs = new ArrayList<>();

    Bundle bundle;
    List<Plan> plans;
    Context context;
    SimpleDateFormat dateFormatter1, dateFormatter2;

    public RVAdapter_Plan(List<Plan> plans, Context context) {
        this.plans = plans;
        this.context = context;
        dateFormatter1 = new SimpleDateFormat("EEE, MMM d", Locale.US);
        dateFormatter2 = new SimpleDateFormat("d MMMM yyyy", Locale.US);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        eventRef = firebaseDatabase.getReference().child("events");
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public RVAdapter_Plan(List<Plan> plans, Context context, Bundle bundle) {
        this.plans = plans;
        this.context = context;
        this.bundle = bundle;
        dateFormatter1 = new SimpleDateFormat("EEE, MMM d", Locale.US);
        dateFormatter2 = new SimpleDateFormat("d MMMM yyyy", Locale.US);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        eventRef = firebaseDatabase.getReference().child("events");
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    @Override
    public RVAdapter_Plan.PlanViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_plan, viewGroup, false);
        return new PlanViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RVAdapter_Plan.PlanViewHolder planViewHolder, int i) {
        final Plan plan = plans.get(i);
        String total_day_str;
        int total_day = plan.getPlan_total_days();
        if (total_day == 1 || total_day == 0) {
            total_day_str = " (" + total_day + " day trip)";
        } else {
            total_day_str = " (" + total_day + " days trip)";
        }

        String start_date = plan.getPlan_start_date();
        String end_date = plan.getPlan_end_date();

        String dateString = start_date + " - " + end_date
                + total_day_str;
        String createdString = "Created: " + plan.getPlan_created() + " / "
                + "Modified: " + plan.getPlan_modified();

        planViewHolder.planName.setText(plan.getPlan_name());
        planViewHolder.planDate.setText(dateString);
        planViewHolder.planOverview.setText(plan.getPlan_overview());
        planViewHolder.planCreated.setText(createdString);

        final int position = i;
        final String name = plan.getPlan_name();

        if (bundle != null) {
            Group group = bundle.getParcelable("group");
            if (!group.getCreator_id().equals(firebaseUser.getUid())) {
                planViewHolder.planMenuButton.setVisibility(View.GONE);
            }
        }

        planViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditPlanActivity.class);
                intent.putParcelableArrayListExtra("plans", (ArrayList<? extends Parcelable>) plans);
                intent.putExtra("plan_id", plan.getPlan_id());
                intent.putExtra("plan_name", planViewHolder.planName.getText().toString());
                intent.putExtra("plan_date_start", plan.getPlan_start_date());
                intent.putExtra("plan_date_end", plan.getPlan_end_date());
                intent.putExtra("plan_total_days", plan.getPlan_total_days());

                if (bundle != null) {
                    intent.putExtra("bundle", bundle);
                }
                context.startActivity(intent);
            }
        });

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
        return new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete \"" + name + "\"?")

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
    }

    private void deletePlan(Plan plan) {

        if (plan.getPlan_id() != null) {
            deleteHelper(plan, new DeleteEventCallback() {
                @Override
                public void onCallback() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void deleteHelper(Plan plan, final DeleteEventCallback callback) {

        final String plan_id = plan.getPlan_id();
        planRef = firebaseDatabase.getReference().child("plans").child(plan_id);

        final DatabaseReference ref = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid()).child("plans");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (Objects.equals(postSnapshot.getValue(String.class), plan_id)) {
                        ref.child(Objects.requireNonNull(postSnapshot.getKey())).removeValue();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getAllEventIDs(new EventIdCallback() {
            @Override
            public void onCallback(List<String> list) {
                eventIDs = list;

                planRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (String event_id : eventIDs) {
                            final DatabaseReference eventRef = firebaseDatabase.getReference().child("events").child(event_id);
                            eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    eventRef.removeValue();
                                    callback.onCallback();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        planRef.removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private interface DeleteEventCallback {
        void onCallback();
    }

    private void getAllEventIDs(final EventIdCallback userIdCallback){
        planRef.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventIDs.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String eventId = postSnapshot.getValue(String.class); // String of eventID
                    eventIDs.add(eventId);
                }
                userIdCallback.onCallback(eventIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface EventIdCallback{
        void onCallback(List<String> list);
    }

    private AlertDialog renameDialog(final int position, String name) {
        final EditText edtText = new EditText(context);
        return new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Rename \"" + name + "\"?")
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
    }

    private void renamePlan(Plan plan, String new_name) {
        planRef = firebaseDatabase.getReference().child("plans").child(plan.getPlan_id());
        planRef.child("plan_name").setValue(new_name);
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
