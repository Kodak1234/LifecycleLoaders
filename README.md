# LifecycleLoaders
Loaders that behaves like android support view model store. However unlike android support view model, LifecycleLoaders holds an activity context. The context is released in activity or fragment onDestroy. LifecycleLoaders is preserved across configuration changes and re-attachies to an activity or fragment in onCreate.

## Usage

Add the following to your app.gradle file <br/>
<pre>
android {
    ...
    compileOptions {
        targetCompatibility 1.8
        sourceCompatibility 1.8
    }
}<br/>

dependencies {
    ...
    implementation 'com.github.Kodak1234:LifecycleLoaders:1.0.2'
}

</pre>
### Extend from LoaderActivity
<pre>
package ume.libraryexample;

import android.os.Bundle;
import android.widget.TextView;

import ume.loaders.LifeCycleLoader;
import ume.loaders.LoaderActivity;

public class MainActivity extends LoaderActivity&lt;Integer&gt; {
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

</pre>

### Extend from LoaderFragment
<pre>
package ume.libraryexample;

import android.os.Bundle;
import android.util.Log;

import ume.loaders.LifeCycleLoader;
import ume.loaders.LoaderFragment;

public class MainFragment extends LoaderFragment&lt;Integer&gt; {
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
        Log.d(TAG, "onResultReady() called with: integer = [" + integer + "], i = [" + i + "]");
    }

    //loader errors will be delivered here
    @Override
    public void OnError(Exception e) {
        Log.d(TAG, "OnError() called with: e = [" + e + "]");
    }
}

</pre>

### Implementing your loader. Extend from LifecycleLoader
<pre>
package ume.libraryexample;

import android.content.Context;

import ume.loaders.LifeCycleLoader;

/**
 * Count's from zero to 100
 */
public class CustomLoader extends LifeCycleLoader&lt;Integer&gt implements Runnable {
    private Thread thread;

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
            deliverError(e);
        }
    }
}

</pre>

### Passing extra data from host to loader
Implement ValueRequest in your host. The same ValueRequest can be used for multiple loaders
<pre>
public MyHost implements ValueRequest {
        Object getValue(int loaderId, int valueId){
            return the correct value base on the loaderId and valueId;
        }
        
        //override this in your host and call setValueRequest on LoaderInstallInfo object
        @Override
        public LoaderInstallInfo getLoaderInfo(int key) {
            LoaderInstallInfo info = super.getLoaderInfo(key);
            info.setValueRequest(this)
            return info;
        }
    }
   
</pre>
Inside your loader call   requestValue(valueId). Calling requestValue(valueId) before attachedToHost() or after onReset() will return null.    
### LoaderHelper
If you don't want to extend from LoaderActivity or LoaderFragment use LoaderHelper to implement your own custom activity or fragment.
Be sure to to pass lifecycle call backs from the host to the LoaderHelper. Also pass state save events to the LoaderHelper.

