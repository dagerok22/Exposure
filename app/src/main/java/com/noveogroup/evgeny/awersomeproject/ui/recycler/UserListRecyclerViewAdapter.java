package com.noveogroup.evgeny.awersomeproject.ui.recycler;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.model.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class UserListRecyclerViewAdapter extends RecyclerView.Adapter<UserListRecyclerViewAdapter.ViewHolder> {
    private List<User> users;

    public UserListRecyclerViewAdapter(List<User> users) {
       this.users = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setPosition(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_name)
        TextView userNameView;
        @BindView(R.id.user_rating)
        TextView userRaitingView;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        void setPosition(int position) {
            userNameView.setText(users.get(position).getName());
            userRaitingView.setText(String.valueOf(users.get(position).getRating()));
        }
    }
}