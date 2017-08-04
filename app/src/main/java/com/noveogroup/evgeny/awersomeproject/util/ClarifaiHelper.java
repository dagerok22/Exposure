package com.noveogroup.evgeny.awersomeproject.util;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.prediction.Concept;
import clarifai2.exception.ClarifaiException;


/**
 * Created by Evgeny on 28.07.2017.
 */

public class ClarifaiHelper {
    private static ClarifaiClient client;
    PostExecuteListener postExecuteListener;
    String photoPath;
    public ClarifaiHelper(String photoPath, PostExecuteListener postExecuteListener) {
        this.photoPath = photoPath;
        this.postExecuteListener = postExecuteListener;
    }

    private List<Concept> getTagList(String currentPhotoPath) throws Exception {
        Log.d("ClarifaiHelper", "request start");
        if (client == null) {
            client = new ClarifaiBuilder("f7bcefcc6cbf45219549bc97714c8604").buildSync();
        }
        List<Concept> tags;
        try {
            tags = client
                    .getDefaultModels()
                    .generalModel()
                    .predict()
                    .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(new File(currentPhotoPath))))
                    .executeSync()
                    .get()
                    .get(0)
                    .data();
            Log.d("ClarifaiHelper", "request success");
        } catch (ClarifaiException e) {
            throw new Exception(e);
        }
        return tags;
    }

    public void startAsyncTask() {
        AsyncTask<Void, Void, List<Concept>> asyncTask = new AsyncTask<Void, Void, List<Concept>>() {
            @Override
            protected List<Concept> doInBackground(Void... params) {
                PhotoHelper.compressPhotoFile(photoPath, 25);
                try {
                    return getTagList(photoPath);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Concept> clarifaiOutputs) {
                postExecuteListener.onAnswerGet(clarifaiOutputs);
            }
        };
        asyncTask.execute((Void[]) null);
    }


    public interface PostExecuteListener {
        void onAnswerGet(List<Concept> clarifaiOutputs);
    }
}

