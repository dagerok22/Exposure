package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.api.RealTimeDBApi;
import com.noveogroup.evgeny.awersomeproject.db.model.Task;
import com.noveogroup.evgeny.awersomeproject.helper.ItemClickSupport;
import com.noveogroup.evgeny.awersomeproject.ui.recycler.TaskListRecyclerViewAdapter;
import com.noveogroup.evgeny.awersomeproject.util.LocationUtil;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private Location currentLocation;
    private LocationUtil locationUtil;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        ButterKnife.bind(this);
        //dataSet = new ArrayList<>();


//        firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        Toast.makeText(getApplicationContext(), currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        RealTimeDBApi dbApi = RealTimeDBApi.getInstance();
        adapter = new TaskListRecyclerViewAdapter();
        initializeRecyclerView();
        initializeOnRecyclerItemClickListener();
        synchronizeDataAndLocationFetching(dbApi);
    }

    private void synchronizeDataAndLocationFetching(RealTimeDBApi dbApi) {
        dbApi.getAllTasks(data -> {
            dataSet = data;
            adapter.setDataSet(dataSet);
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
        });
        locationUtil = LocationUtil.getInstance(this, location -> {
            this.currentLocation = location;
            adapter.setCurrentLocation(currentLocation);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationUtil.apiConnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationUtil.apiDisconnect();
    }

    private void initializeOnRecyclerItemClickListener() {
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerViewIncome, position, v) -> {
            TaskListRecyclerViewAdapter adapter = (TaskListRecyclerViewAdapter) recyclerView.getAdapter();
            Intent intent = TaskDetailsActivity.getIntent(this, adapter.getItems().get(position), currentLocation);
            boolean isAnimationEnabled = getSharedPreferences(getString(R.string.settings_file), MODE_PRIVATE).getBoolean(getString(R.string.task_detail_transition_animation), true);
            ActivityOptionsCompat options = isAnimationEnabled ? initializeTransitionAnimation(position) : null;

            startActivity(intent, options != null ? options.toBundle() : null);
        });
    }

    @NonNull
    private ActivityOptionsCompat initializeTransitionAnimation(int position) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
        Pair<View, String> titlePair =
                Pair.create(viewHolder.itemView.findViewById(R.id.title), "title");
        Pair<View, String> tagsPair =
                Pair.create(viewHolder.itemView.findViewById(R.id.tags), "tags");
        Pair<View, String> authorPair =
                Pair.create(viewHolder.itemView.findViewById(R.id.author), "author");
        Pair<View, String> ratingPair =
                Pair.create(viewHolder.itemView.findViewById(R.id.rating), "rating");
        Pair<View, String> agePair =
                Pair.create(viewHolder.itemView.findViewById(R.id.age), "age");
        Pair<View, String> distancePair =
                Pair.create(viewHolder.itemView.findViewById(R.id.distance), "distance");
        return ActivityOptionsCompat
                .makeSceneTransitionAnimation(this,
                        titlePair,
                        tagsPair,
                        authorPair,
                        ratingPair,
                        agePair,
                        distancePair);
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

    private void setUpAdapter() {
        adapter.setCurrentLocation(currentLocation);
        adapter.setDataSet(dataSet);
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.fab)
    void onAddTaskFabClicked() {
        Intent intent = new Intent(this, AddNewTaskActivity.class);
        startActivity(intent);
    }

}
