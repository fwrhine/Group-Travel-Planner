package com.example.pplki18.grouptravelplanner.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pplki18.grouptravelplanner.InHomeActivity;
import com.example.pplki18.grouptravelplanner.Poll;
import com.example.pplki18.grouptravelplanner.R;


import java.util.List;

public class RVAdapter_PollChoices extends RecyclerView.Adapter<RVAdapter_PollChoices.PollChoiceViewHolder>{

        List<Poll> pollChoiceList;
        Context context;
        SessionManager sessionManager;

    public RVAdapter_PollChoices(List<Poll> pollChoiceList, Context context){
            this.pollChoiceList = pollChoiceList;
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return pollChoiceList.size();
        }

        @NonNull
        @Override
        public RVAdapter_PollChoices.PollChoiceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_reminder, viewGroup, false);
            RVAdapter_PollChoices.PollChoiceViewHolder rvh = new RVAdapter_PollChoices.PollChoiceViewHolder(v);
            return rvh;
        }

        @Override
        public void onBindViewHolder(final RVAdapter_PollChoices.PollChoiceViewHolder pollChoiceViewHolder, int i) {
            //TRYING TO USE CALENDAR EVENT TABLE
            String choice = pollChoiceList.get(i).getChoiceList().get(i);
            Integer choiceVal = pollChoiceList.get(i).getChoiceMap().get(choice);

            pollChoiceViewHolder.choiceText.setText(choice);
            pollChoiceViewHolder.choiceVal.setText(choiceVal);

            pollChoiceViewHolder.choicePlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vote(pollChoiceViewHolder);
                    //go back to chat??
                }
            });
        }

        public void goBackToList(){
            Intent myIntent = new Intent(this.context , InHomeActivity.class);
            myIntent.putExtra("fragment", "reminder");
            context.startActivity(myIntent);
        }

        public void vote(PollChoiceViewHolder viewHolder) {
            Integer newVal = Integer.parseInt(viewHolder.choiceVal.getText().toString());
            viewHolder.choiceVal.setText(newVal++);
            //TODO save votes locally or in firebase??
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public static class PollChoiceViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;
            TextView choiceText;
            TextView choiceVal;
            Button choicePlus;
//        Context context;

            PollChoiceViewHolder(View itemView) {
                super(itemView);
//            context = itemView.getContext();
                cardView = (CardView)itemView.findViewById(R.id.cv_row_poll_choice);
                choiceText = (TextView)itemView.findViewById(R.id.poll_choiceText);
                choiceVal = (TextView)itemView.findViewById(R.id.poll_choice_num);
                choicePlus = (Button)itemView.findViewById(R.id.poll_choice_increment);

            }

        }
    }
