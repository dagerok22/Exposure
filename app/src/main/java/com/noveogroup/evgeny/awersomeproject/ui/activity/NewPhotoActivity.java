package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.api.RealTimeDBApi;
import com.noveogroup.evgeny.awersomeproject.ui.recycler.TagListRecyclerViewAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    boolean wasRequestSend;
    Bitmap scaled;
    List<ClarifaiOutput<Concept>> predictionResults;

    public static Intent newIntent(Context context, String photoPath) {
        Intent intent = new Intent(context, NewPhotoActivity.class);
        intent.putExtra(PHOTO_PATH, photoPath);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_photo);
        ButterKnife.bind(this);
        startAsyncTask();
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
                scaled = scalePhotoFile(currentPhotoPath);
                imageView.post(() -> imageView.setImageBitmap(scaled));
                //return null;
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

    private Bitmap scalePhotoFile(String currentPhotoPath) {
        if (currentPhotoPath == null) {
            throw new NullPointerException("Can't get photo path from intent");
        }
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        if (bitmap == null) {
            throw new NullPointerException("Can't get bitmap from photo path");
        }
        int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(new File(currentPhotoPath));
            scaled.compress(Bitmap.CompressFormat.JPEG, 10, fOut);
            fOut.flush();
            fOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return scaled;
    }


    @NonNull
    private List<ClarifaiOutput<Concept>> getClarifaiOutputs(String currentPhotoPath) {
        Log.d(TAG, "doInbackground");
        final ClarifaiClient client = new ClarifaiBuilder("f7bcefcc6cbf45219549bc97714c8604").buildSync();

        return client.getDefaultModels().generalModel() // You can also do Clarifai.getModelByID("id") to get custom models
                .predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(new File(currentPhotoPath))))
                .executeSync()
                .get();
    }

    @OnClick(R.id.upload)
    void uploadTask(){
        String name = "John";
        List<String> tags = new ArrayList<>(Arrays.asList(new String[]{"cat, mammal, pet"}));
        double lat = 0.5;
        double lng = 0.6;
        float rating = 5.6f;
        String authorName = "Simon";
        int authorId = 4;
        RealTimeDBApi dbApi = RealTimeDBApi.getInstance();
        dbApi.writeImageAndGetUrl(new File(getIntent().getStringExtra(PHOTO_PATH)), new RealTimeDBApi.HandleImageFileCallback() {
            @Override
            public void onSuccess(Uri imageRef) {
                dbApi.writeTask("Name", tags, imageRef.toString(), new LatLng(0.5, 0.5), rating, authorId, authorName, new Date());
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
