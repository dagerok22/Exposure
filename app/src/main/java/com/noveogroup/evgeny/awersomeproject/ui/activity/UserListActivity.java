package com.noveogroup.evgeny.awersomeproject.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.api.RealTimeDBApi;
import com.noveogroup.evgeny.awersomeproject.db.model.User;
import com.noveogroup.evgeny.awersomeproject.ui.recycler.UserListRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListActivity extends AppCompatActivity implements RealTimeDBApi.OnUserResultCallBack {
    @BindView(R.id.users_recycler_view)
    RecyclerView usersRecyclerView;

    @BindView(R.id.user_list_progress_bar)
    ProgressBar progressBar;


    public static Intent newIntent(Context context) {
        return new Intent(context, UserListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);
        RealTimeDBApi.getInstance().getAllUsers(this);

    }

    @Override
    public void onDataReceived(List<User> data) {
        ArrayList<User> users = new ArrayList<>(data);

        Collections.sort(users,(user1, user2) -> {
            if (user2.getRating() - user1.getRating() != 0) {
                return user2.getRating() - user1.getRating();
            } else{
                return user1.getName().compareTo(user2.getName());
            }
        });
        progressBar.setVisibility(View.GONE);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setNestedScrollingEnabled(false);
        usersRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        usersRecyclerView.setAdapter(new UserListRecyclerViewAdapter(users));
    }
}
