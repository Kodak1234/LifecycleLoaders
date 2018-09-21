# LifecycleLoaders
Loaders that behaves like android support view model store. However unlike android support view model, LifecycleLoaders holds an activity context. The context is released in activity or fragment onDestroy. LifecycleLoaders is preserved across configuration changes and re-attachies to an activity or fragment in onCreate.

## Usage
### Extend from LoaderActivity
<pre>
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
</pre>

### Extend from LoaderFragment
<pre>
public class MainFragment extends LoaderFragment<Void> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //returns a loader with id == 0 if it exist, else one is created
        LifeCycleLoader loader = getLoader(0, null);
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
}

</pre>

### Implementing your loader. Extend from LifecycleLoader
<pre>
public class MainLoader extends LifeCycleLoader<Void> {

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
        //stop all thread. Loader is dieing and won't be retained.
    }


}
</pre>

### See LoaderFragment or LoaderActivity to see how to implement your own fragment or activity without extending from the ones preovided by the library
