package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.api.RealTimeDBApi;
import com.noveogroup.evgeny.awersomeproject.ui.recycler.TagListRecyclerViewAdapter;
import com.noveogroup.evgeny.awersomeproject.util.ClarifaiHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import clarifai2.dto.prediction.Concept;

public class TaskExecutionActivity extends AppCompatActivity implements ClarifaiHelper.PostExecuteListener {

    public static final String PHOTO_PATH = "photo_path";
    public static final String TASK_NAME = "task_name";
    public static final String TAGS = "tags";

    static final private String LOG_TAG = "TaskExecutionActivity";
    @BindView(R.id.photo_view)
    public ImageView imageView;
    @BindView(R.id.task_name)
    TextView taskNameView;
    @BindView(R.id.task_tag_recycler_view)
    RecyclerView taskRecyclerView;
    @BindView(R.id.miss_tag_progress_bar)
    ProgressBar progressBar2;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private PhotoTagStatus photoTagStatus;

    private List<String> predictionResults;
    private ArrayList<String> taskTags;
    private TagListRecyclerViewAdapter taskTagsAdapter;
    private RealTimeDBApi dbApi;
    private FirebaseUser currentUser;


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
        dbApi = RealTimeDBApi.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        taskNameView.setText(getIntent().getStringExtra(TASK_NAME));
        taskTags = getIntent().getStringArrayListExtra(TAGS);
        taskTags = getIntent().getStringArrayListExtra(TAGS);
        String photoPath = getIntent().getStringExtra(PHOTO_PATH);
        ClarifaiHelper clarifaiHelper = new ClarifaiHelper(photoPath, this);
        clarifaiHelper.startAsyncTask();
        Glide.with(this).load(new File(photoPath)).into(imageView);
        photoTagStatus = PhotoTagStatus.NOT_STARTED;
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
                if (photoTagStatus == PhotoTagStatus.SUCCESS) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Важное сообщение!")
                            .setMessage("Поздравляем вы выполнили задание")
                            .setCancelable(false)
                            .setNegativeButton("ОК",
                                    (dialog, id) -> {
                                        dialog.cancel();
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    dbApi.addRatingToUser(currentUser.getUid(), taskTags.size());
                    startActivity(new Intent(this, TaskListActivity.class));
                    finish();
                } else if (photoTagStatus == PhotoTagStatus.FAILURE) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Важное сообщение!")
                            .setMessage("Вы не справиль с заданием. Теперь вы умрете")
                            .setCancelable(false)
                            .setNegativeButton("ОК",
                                    (dialog, id) -> {
                                        dialog.cancel();
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void recyclerViewSetup() {
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        taskTagsAdapter = new TagListRecyclerViewAdapter(taskTags, null, this);

        taskTagsAdapter.setChooseColor(ContextCompat.getColor(this, R.color.not_identical_tag_color));
        taskTagsAdapter.setNotChooseColor(ContextCompat.getColor(this, R.color.identical_tag_color));
        taskRecyclerView.setAdapter(taskTagsAdapter);
    }

    private void sortTags() {
        photoTagStatus = PhotoTagStatus.SUCCESS;
        for (String tag : taskTags) {
            if (!predictionResults.contains(tag)) {
                taskTagsAdapter.setTagChosenState(tag, true);
                photoTagStatus = PhotoTagStatus.FAILURE;
            }
        }
    }

    @Override
    public void onAnswerGet(List<Concept> clarifaiOutputs) {
        predictionResults = new ArrayList<>();
        for (Concept concept : clarifaiOutputs) {
            predictionResults.add(concept.name());
        }
        recyclerViewSetup();
        sortTags();
        // progressBar1.setVisibility(View.GONE);
        progressBar2.setVisibility(View.GONE);
    }

    private enum PhotoTagStatus {
        NOT_STARTED, SUCCESS, FAILURE
    }
}
