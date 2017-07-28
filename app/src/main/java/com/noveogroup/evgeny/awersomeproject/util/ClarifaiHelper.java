package com.noveogroup.evgeny.awersomeproject.util;

import android.util.Log;

import java.io.File;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.prediction.Concept;

/**
 * Created by Evgeny on 28.07.2017.
 */

public class ClarifaiHelper {

    private static ClarifaiClient client;

    public static List<Concept> getTagList(String currentPhotoPath) {
        Log.d("ClarifaiHelper", "request start");
        if (client == null) {
            client = new ClarifaiBuilder("f7bcefcc6cbf45219549bc97714c8604").buildSync();
        }
        List<Concept> tags = client
                .getDefaultModels()
                .generalModel()
                .predict()
                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(new File(currentPhotoPath))))
                .executeSync()
                .get()
                .get(0)
                .data();
        Log.d("ClarifaiHelper", "request success");
        return tags;
    }
}
