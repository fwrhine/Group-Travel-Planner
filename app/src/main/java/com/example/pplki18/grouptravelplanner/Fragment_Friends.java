package com.example.pplki18.grouptravelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class Fragment_Friends extends Fragment {

    private Button to_search_friend;    // TEMP - nopal

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Chat");
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        to_search_friend = (Button) getView().findViewById(R.id.to_search_friend);
        setAddFriendButton();
    }

    // TEMP - nopal
    public void setAddFriendButton() {
        to_search_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("TEMP","To Search Friend");
                Intent myIntent = new Intent(getActivity(), SearchBarActivity.class);
                Fragment_Friends.this.startActivity(myIntent);
            }
        });
    }
}
