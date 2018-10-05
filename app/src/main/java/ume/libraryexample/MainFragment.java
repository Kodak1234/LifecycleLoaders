package ume.libraryexample;

import android.os.Bundle;
import android.util.Log;

import ume.loaders.LifeCycleLoader;
import ume.loaders.LifeCycleLoaderManager;
import ume.loaders.LifeCycleLoaderManager.LoaderInstallInfo;
import ume.loaders.LoaderFragment;

public class MainFragment extends LoaderFragment<Integer> {
    private static final String TAG = "MainFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //returns a loader with id == 0 if it exist, else one is created
        CustomLoader loader = getLoader(0, null);
    }


    @Override
    public LifeCycleLoader<Integer> createLifeCycleLoader(int id, Bundle arg) {
        //return your loader instance here base on the loader id
        return new CustomLoader();
    }
    //loader result will be delivered here
    @Override
    public void onResultReady(Integer integer, int i) {
        super.onResultReady(integer, i);
        Log.d(TAG, "onResultReady() called with: integer = [" + integer + "], i = [" + i + "]");
    }

    //loader errors will be delivered here
    @Override
    public void OnError(Exception e,int id) {
        super.OnError(e,id);
        Log.d(TAG, "OnError() called with: e = [" + e + "]");
    }
}
