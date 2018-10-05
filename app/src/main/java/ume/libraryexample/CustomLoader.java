package ume.libraryexample;

import android.content.Context;
import android.util.Log;

import ume.loaders.LifeCycleLoader;

/**
 * Count's from zero to Integer.MAX
 */
public class CustomLoader extends LifeCycleLoader<Integer> implements Runnable {
    private Thread thread;
    private static final String TAG = "CustomLoader";

    public CustomLoader() {
        //loader is only created once per host
        thread = new Thread(this, "LoaderThread");
        thread.start();
    }

    //loader has been attached to its host.
    //this is called before onStart
    @Override
    protected void attachedToHost() {
        super.attachedToHost();
        //it is safe to call getContext here
        Context context = getContext();
        Log.i(TAG, "attachedToHost: "+requestValue(0));
    }

    //host has started
    @Override
    protected void onStart() {
        super.onStart();
    }

    //host has stopped
    @Override
    protected void onStop() {
        super.onStop();
    }

    //host has been destroy
    //called before onDestroy
    @Override
    protected void onReset() {
        super.onReset();
        //release any context reference here.
        //calling getContext after this method returns will return null
    }

    //This is only called if the host is being destroyed and won't be recreated again.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.interrupt();
        //stop all thread. Loader is dieing and won't be retained.
    }

    @Override
    public void run() {
        int i = 0;
        try {
            while (!thread.isInterrupted()) {

                //result are queued if host onStart has not been called.
                //the results will be delivered when the host onStart is called
                deliverResult(i++, 0);
                Thread.sleep(1000);
                if (i == 100)
                    throw new InterruptedException("Thread terminated");
            }
        } catch (InterruptedException e) {
            //pass error to host. host will receive it if it i started
            //else the error will be queued and delivered when the host starts
            deliverError(e,0);
        }
    }
}
