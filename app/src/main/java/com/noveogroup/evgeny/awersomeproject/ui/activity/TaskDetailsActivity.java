package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.model.Task;
import com.noveogroup.evgeny.awersomeproject.util.DateTransformerUtil;
import com.noveogroup.evgeny.awersomeproject.util.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskDetailsActivity extends AppCompatActivity {

    private static final String KEY_TASK_ITEM = "TASK_ITEM";

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.tags)
    TextView tags;
    @BindView(R.id.author)
    TextView author;
    @BindView(R.id.rating)
    TextView rating;
    @BindView(R.id.age)
    TextView age;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);


        Task currentTask = (Task) getIntent().getSerializableExtra(KEY_TASK_ITEM);
        title.setText(currentTask.getName());
        tags.setText(StringUtil.getTagsString(currentTask.getTags()));
        author.setText(currentTask.getAuthorName());
        rating.setText(String.valueOf(currentTask.getRating()));
        age.setText(DateTransformerUtil.getAgeOfTask(currentTask.getDate(), getApplicationContext()));

//        initializeMap();

    }

    public static Intent getIntent(Context context, Task task) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TASK_ITEM, task);
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

//    private void initializeMap() {
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(googleMap -> {
//            LatLngBounds lngBounds = LatLngBounds.builder()
//                    .include(new LatLng(currentTask.getLat(), currentTask.getLng()))
//                    .build();
//            googleMap.setLatLngBoundsForCameraTarget(lngBounds);
//        });
//    }

}
