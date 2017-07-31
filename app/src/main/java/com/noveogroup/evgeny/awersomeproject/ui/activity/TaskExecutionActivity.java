package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.ui.recycler.TagListRecyclerViewAdapter;
import com.noveogroup.evgeny.awersomeproject.util.ClarifaiHelper;
import com.noveogroup.evgeny.awersomeproject.util.PhotoHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import clarifai2.dto.prediction.Concept;

public class TaskExecutionActivity extends AppCompatActivity {

    public static final String PHOTO_PATH = "photo_path";
    public static final String TASK_NAME = "task_name";
    public static final String TAGS = "tags";
    static final String LOG_TAG = "TaskExecutionActivity";
    @BindView(R.id.photo_view)
    public ImageView imageView;
    @BindView(R.id.task_name)
    TextView taskNameView;
    @BindView(R.id.photo_tag_recycler_view)
    RecyclerView photoRecyclerView;
    @BindView(R.id.task_tag_recycler_view)
    RecyclerView taskRecyclerView;
    @BindView(R.id.tag_progress_bar)
    ProgressBar progressBar1;
    @BindView(R.id.miss_tag_progress_bar)
    ProgressBar progressBar2;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    List<String> predictionResults;
    ArrayList<String> taskTags;
    TagListRecyclerViewAdapter photoTagsAdapter;
    TagListRecyclerViewAdapter taskTagsAdapter;

    public static Intent newIntent(Context context, String photoPath, String taskName, ArrayList<String> tags) {
        Intent intent = new Intent(context, TaskExecutionActivity.class);
        intent.putExtra(PHOTO_PATH, photoPath);
        intent.putExtra(TASK_NAME, taskName);
        intent.putStringArrayListExtra(TAGS, tags);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_execution);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        taskNameView.setText(getIntent().getStringExtra(TASK_NAME));
        taskTags = getIntent().getStringArrayListExtra(TAGS);
        startAsyncTask();
        Glide.with(this).load(new File(getIntent().getStringExtra(PHOTO_PATH))).into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_icons, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                Toast.makeText(this, "galka", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void recyclerViewSetup() {
        photoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        photoTagsAdapter = new TagListRecyclerViewAdapter(predictionResults, null, this);
        photoTagsAdapter.setChooseColor(Color.GREEN);
        photoRecyclerView.setAdapter(photoTagsAdapter);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskTagsAdapter = new TagListRecyclerViewAdapter(taskTags, null, this);
        taskTagsAdapter.setChooseColor(Color.RED);
        taskTagsAdapter.setNotChooseColor(Color.GREEN);
        taskRecyclerView.setAdapter(taskTagsAdapter);
    }

    void startAsyncTask() {
        AsyncTask<Void, Void, List<Concept>> asyncTask = new AsyncTask<Void, Void, List<Concept>>() {
            @Override
            protected List<Concept> doInBackground(Void... params) {
                String currentPhotoPath = getIntent().getStringExtra(PHOTO_PATH);
                PhotoHelper.compressPhotoFile(currentPhotoPath, 25);
                return ClarifaiHelper.getTagList(currentPhotoPath);
            }

            @Override
            protected void onPostExecute(List<Concept> clarifaiOutputs) {
                predictionResults = new ArrayList<>();
                for (Concept concept : clarifaiOutputs) {
                    predictionResults.add(concept.name());
                }
                recyclerViewSetup();
                sortTags();
                progressBar1.setVisibility(View.GONE);
                progressBar2.setVisibility(View.GONE);
                Log.d(LOG_TAG, "onPost");
            }
        };
        asyncTask.execute((Void[]) null);
    }

    private void sortTags() {
        for (String tag : taskTags) {
            if (predictionResults.contains(tag)) {
                photoTagsAdapter.setTagChosenState(tag, true);
            } else {
                taskTagsAdapter.setTagChosenState(tag, true);
            }
        }
    }
}
