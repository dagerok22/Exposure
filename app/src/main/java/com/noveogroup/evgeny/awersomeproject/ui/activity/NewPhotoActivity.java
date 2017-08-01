package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.ui.recycler.TagListRecyclerViewAdapter;
import com.noveogroup.evgeny.awersomeproject.util.ClarifaiHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import clarifai2.dto.prediction.Concept;

public class NewPhotoActivity extends AppCompatActivity implements TagListRecyclerViewAdapter.ItemTapListener,
        ClarifaiHelper.PostExecuteListener {

    public static final String PHOTO_PATH = "photo_path";
    static private final int MAX_CHOSEN_TAGS = 7;

    @BindView(R.id.task_name)
    public EditText taskNameEditText;
    @BindView(R.id.photo_view)
    public ImageView imageView;
    @BindView(R.id.photo_tag_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tag_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    ArrayList<String> chosenTags;
    List<String> predictionResults;
    TagListRecyclerViewAdapter adapter;

    public static Intent newIntent(Context context, String photoPath) {
        Intent intent = new Intent(context, NewPhotoActivity.class);
        intent.putExtra(PHOTO_PATH, photoPath);
        return intent;
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
                returnTags();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void returnTags() {
        if (!chosenTags.isEmpty()) {
            if (!TextUtils.isEmpty(taskNameEditText.getText())) {
                Intent answerIntent = new Intent();
                answerIntent.putStringArrayListExtra(AddNewTaskActivity.TAGS_ARRAY, chosenTags);
                answerIntent.putExtra(AddNewTaskActivity.NEW_TASK_NAME, taskNameEditText.getText().toString());
                setResult(RESULT_OK, answerIntent);
                finish();
            } else {
                Toast.makeText(this, "Enter task name", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Chose an least one tag first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_photo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        chosenTags = new ArrayList<>();
        String photoPath = getIntent().getStringExtra(PHOTO_PATH);
        ClarifaiHelper clarifaiHelper = new ClarifaiHelper(photoPath, this);
        clarifaiHelper.startAsyncTask();
        Glide.with(this).load(new File(photoPath)).into(imageView);
    }

    private void recyclerViewSetup() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TagListRecyclerViewAdapter(predictionResults, this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAnswerGet(List<Concept> clarifaiOutputs) {
        predictionResults = new ArrayList<>();
        for (Concept concept : clarifaiOutputs) {
            predictionResults.add(concept.name());
        }
        recyclerViewSetup();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void tagChosen(String tag, boolean checked) {
        if (checked) {
            chosenTags.remove(tag);
            adapter.setTagChosenState(tag, false);
        } else {
            if (chosenTags.size() < MAX_CHOSEN_TAGS) {
                chosenTags.add(tag);
                adapter.setTagChosenState(tag, true);
            }
        }
    }
}
