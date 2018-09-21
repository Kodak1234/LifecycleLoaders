package ume.libraryexample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import ume.loaders.LifeCycleLoader;
import ume.loaders.LoaderActivity;

//Extend from LoaderActivity
public class MainActivity extends LoaderActivity<Void> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //returns a loader with id == 0 if it exist, else one is created
        LifeCycleLoader loader = getLoader(0,null);
    }


    @Override
    public LifeCycleLoader<Void> createLifeCycleLoader(int id, Bundle arg) {
        //return your loader instance here base on the loader id
        return null;
    }

    //Loader results will be received here
    @Override
    public void onResultReady(Void aVoid, int type) {
        super.onResultReady(aVoid, type);
    }

    //Errors that occurs in the loader will be delivered here
    @Override
    public void OnError(Exception e) {
        super.OnError(e);
    }
}
