package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.ui.recycler.TagListRecyclerViewAdapter;
import com.noveogroup.evgeny.awersomeproject.util.PhotoHelper;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class NewPhotoActivity extends AppCompatActivity {

    public static final String PHOTO_PATH = "photo_path";
    static final String TAG = "NewPhotoActivity";

    @BindView(R.id.photo_view)
    public ImageView imageView;
    @BindView(R.id.tag_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tag_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    boolean wasRequestSend;
    List<ClarifaiOutput<Concept>> predictionResults;

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
                sendTaskToStorage();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendTaskToStorage() {
        Toast.makeText(this,"galka",Toast.LENGTH_SHORT).show();
//        String name = "FirstTask";
//        List<String> tags =predictionResults.get(0).data();
//        double lat = 0.5;
//        double lng = 0.6;
//        float rating = 5.6f;
//        String authorName = "Simon";
//        int authorId = 4;
//        RealTimeDBApi dbApi = RealTimeDBApi.getInstance();
//        dbApi.writeImageAndGetUrl(new File(getIntent().getStringExtra(PHOTO_PATH)), new RealTimeDBApi.HandleImageFileCallback() {
//            @Override
//            public void onSuccess(Uri imageRef) {
//                dbApi.writeTask("Name", tags, imageRef.toString(), new LatLng(0.5, 0.5), rating, authorId, authorName, new Date());
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                e.printStackTrace();
//            }
//        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_photo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        startAsyncTask();
        Glide.with(this).load(new File(getIntent().getStringExtra(PHOTO_PATH))).into(imageView);
    }

    private void recyclerViewSetup() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TagListRecyclerViewAdapter adapter = new TagListRecyclerViewAdapter(predictionResults.get(0).data());
        recyclerView.setAdapter(adapter);
    }

    void startAsyncTask() {
        AsyncTask<Void, Void, List<ClarifaiOutput<Concept>>> asyncTask = new AsyncTask<Void, Void, List<ClarifaiOutput<Concept>>>() {
            @Override
            protected List<ClarifaiOutput<Concept>> doInBackground(Void... params) {
                String currentPhotoPath = getIntent().getStringExtra(PHOTO_PATH);
                PhotoHelper.compressPhotoFile(currentPhotoPath, 25);
                return getClarifaiOutputs(currentPhotoPath);
            }

            @Override
            protected void onPostExecute(List<ClarifaiOutput<Concept>> clarifaiOutputs) {
                predictionResults = clarifaiOutputs;
                recyclerViewSetup();
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onPost");
            }
        };
        asyncTask.execute((Void[]) null);
    }

    @NonNull
    private List<ClarifaiOutput<Concept>> getClarifaiOutputs(String currentPhotoPath) {
        Log.d(TAG, "doInbackground");
        final ClarifaiClient client = new ClarifaiBuilder("f7bcefcc6cbf45219549bc97714c8604").buildSync();

        return client.getDefaultModels().generalModel()
                .predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(new File(currentPhotoPath))))
                .executeSync()
                .get();
    }
}
