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
<prev>
