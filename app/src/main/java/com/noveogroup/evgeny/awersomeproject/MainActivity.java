package com.noveogroup.evgeny.awersomeproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";
    List<ClarifaiOutput<Concept>> predictionResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        final ClarifaiClient client = new ClarifaiBuilder("f7bcefcc6cbf45219549bc97714c8604").buildSync();
//
//        client.getDefaultModels().generalModel() // You can also do Clarifai.getModelByID("id") to get custom models
//                .predict()
//                .withInputs(
//                        //ClarifaiInput.forImage(array)
//                        ClarifaiInput.forImage(ClarifaiImage.of("http://cdn.fishki.net/upload/post/2017/06/29/2324753/smeshnye-kartinki-s-nadpisjami-0.jpg"))
//                )
//                .executeAsync(new ClarifaiRequest.Callback<List<ClarifaiOutput<Concept>>>() {
//                    @Override
//                    public void onClarifaiResponseSuccess(List<ClarifaiOutput<Concept>> clarifaiOutputs) {
//                        predictionResults = clarifaiOutputs;
//                        Log.d(TAG,"ready");
//                    }
//
//                    @Override
//                    public void onClarifaiResponseUnsuccessful(int errorCode) {
//
//                    }
//
//                    @Override
//                    public void onClarifaiResponseNetworkError(IOException e) {
//
//                    }
//                });

        System.out.println("asd");
    }

    //    public void onClick(View view) {
//        this.getFilesDir().toString();
//        TextView textView = (TextView) findViewById(R.id.text_view);
//        textView.setText(predictionResults.get(0).data().get(0).toString());
//    }
    @OnClick(R.id.on_screen3_button)
    public void onScreen3() {
        Intent intent = new Intent(MainActivity.this, screen3.class);
        startActivity(intent);
    }


}
