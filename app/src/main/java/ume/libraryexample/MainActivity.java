package ume.libraryexample;

import android.os.Bundle;
import android.widget.TextView;

import ume.loaders.LifeCycleLoader;
import ume.loaders.LoaderActivity;

public class MainActivity extends LoaderActivity<Integer> {
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //returns a loader with id == 0 if it exist, else one is created
        CustomLoader loader = getLoader(0,null);
        text = findViewById(R.id.text);
    }


    @Override
    public LifeCycleLoader<Integer> createLifeCycleLoader(int id, Bundle arg) {
        //return your loader instance here base on the loader id
        return new CustomLoader();
    }

    //Loader results will be received here
    @Override
    public void onResultReady(Integer i, int type) {
        super.onResultReady(i, type);
        text.setText(i.toString());
    }

    //Errors that occurs in the loader will be delivered here
    @Override
    public void OnError(Exception e) {
        super.OnError(e);
        text.setText("Exception message: ");
        text.append(e.getMessage());
    }
}
