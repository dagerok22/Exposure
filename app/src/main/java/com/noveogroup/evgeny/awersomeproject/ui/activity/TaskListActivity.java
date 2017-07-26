package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.api.RealTimeDBApi;
import com.noveogroup.evgeny.awersomeproject.db.model.Task;
import com.noveogroup.evgeny.awersomeproject.ui.recycler.TaskListRecyclerViewAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListActivity extends AppCompatActivity {

    public static final String DATASET_KEY = "dataset";

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.task_list_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    TaskListRecyclerViewAdapter adapter;
    List<Task> dataSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            dataSet = (List<Task>) savedInstanceState.getSerializable(DATASET_KEY);
        } else {
            dataSet = new ArrayList<>();
        }

        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        RealTimeDBApi dbApi = RealTimeDBApi.getInstance();
        initializeRecyclerView();
        dbApi.getAllTasks(data -> {
            dataSet = data;
            initializeAndSetUpAdapter();
            progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DATASET_KEY, (Serializable) dataSet);
    }

    private void initializeRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void initializeAndSetUpAdapter() {
        adapter = new TaskListRecyclerViewAdapter(dataSet);
        recyclerView.setAdapter(adapter);
    }

}
