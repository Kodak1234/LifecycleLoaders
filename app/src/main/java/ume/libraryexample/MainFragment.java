package ume.libraryexample;

import android.os.Bundle;

import ume.loaders.LifeCycleLoader;
import ume.loaders.LifeCycleLoader.LoaderResultCallback;
import ume.loaders.LifeCycleLoaderManager.LoaderInstallInfo;
import ume.loaders.LoaderFragment;

public class MainFragment extends LoaderFragment implements LoaderResultCallback<Void> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //returns a loader with id == 0 if it exist, else one is created
        //LifeCycleLoader loader = getLoader(0,null);
    }


    @Override
    public LifeCycleLoader<Void> createLifeCycleLoader(int id, Bundle arg) {
        //return your loader instance here base on the loader id
        return null;
    }
    //loader result will be delivered here
    @Override
    public void onResultReady(Void aVoid, int i) {

    }

    //loader errors will be delivered here
    @Override
    public void OnError(Exception e) {

    }

    @Override
    public LoaderInstallInfo getLoaderInfo(int i) {
        return new LoaderInstallInfo(requireContext(), this);
    }
}
