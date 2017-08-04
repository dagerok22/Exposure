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
import com.noveogroup.evgeny.awersomeproject.util.NewTaskHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskListActivity extends AppCompatActivity implements LocationUtil.UpdatedLocationHandler {

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_CHOOSE_TAGS = 2;

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.task_list_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    TaskListRecyclerViewAdapter adapter;
    List<Task> dataSet;
    private Location currentLocation;
    private FirebaseAuth firebaseAuth;
    private RealTimeDBApi dbApi;
    private FirebaseUser currentUser;
    private NewTaskHelper newTaskHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        ButterKnife.bind(this);
        newTaskHelper = new NewTaskHelper(this);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        dbApi = RealTimeDBApi.getInstance();
        adapter = new TaskListRecyclerViewAdapter(this, currentUser);
        initializeRecyclerView();
        initializeOnRecyclerItemClickListener();
        synchronizeData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocationUtil.getInstance(this).addLocationUpdatesListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationUtil.getInstance(this).removeLocationUpdatesListener(this);
    }

    @Override
    public boolean handleUpdatedLocation(Location location) {
        currentLocation = location;
        if (dataSet != null) {
            adapter.setCurrentLocation(currentLocation);
            adapter.notifyDataSetChanged();
        }
        return false;
    }

    @OnClick(R.id.fab)
    void onAddTaskFabClicked() {
        Intent intent = newTaskHelper.dispatchTakePictureIntent();
        if (intent != null) {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            startActivityForResult(NewPhotoActivity.newIntent(this, newTaskHelper.getCurrentPhotoPath()),
                    REQUEST_CHOOSE_TAGS);
        }
        if (requestCode == REQUEST_CHOOSE_TAGS && resultCode == RESULT_OK) {
            newTaskHelper.onActivityResult(data);
        }
    }

    private void initializeRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }


    private void initializeOnRecyclerItemClickListener() {
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerViewIncome, position, v) -> {
            TaskListRecyclerViewAdapter adapter = (TaskListRecyclerViewAdapter) recyclerView.getAdapter();
            Intent intent = TaskDetailsActivity.getIntent(this, adapter.getItems().get(position));
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
        Pair<View, String> agePair =
                Pair.create(viewHolder.itemView.findViewById(R.id.age), "age");
        Pair<View, String> distancePair =
                Pair.create(viewHolder.itemView.findViewById(R.id.distance), "distance");
        return ActivityOptionsCompat
                .makeSceneTransitionAnimation(this,
                        titlePair,
                        tagsPair,
                        authorPair,
                        agePair,
                        distancePair);
    }

    private void synchronizeData() {
        dbApi.getAllTasks(data -> {
            dataSet = data;
            adapter.setDataSet(dataSet);
            if (currentLocation != null) {
                adapter.setCurrentLocation(currentLocation);
            }
            progressBar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        });
    }


}