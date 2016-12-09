package com.cs4518.halfway.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cs4518.halfway.R;

public class MemberViewHolder extends RecyclerView.ViewHolder {
    public TextView memberUsername;

    public MemberViewHolder(View itemView) {
        super(itemView);

        memberUsername = (TextView) itemView.findViewById(R.id.member_username);
    }

    public void bindMember(String username) {
        memberUsername.setText(username);
    }
}
