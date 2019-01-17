package ume.streams;

import java.util.LinkedList;

import ume.loaders.LifeCycleLoader.DataStore;
import ume.loaders.LifeCycleLoader.LoaderResultCallback;

public class StreamDataStore<D> implements DataStore<D> {
    private LinkedList<Result> data;

    public StreamDataStore() {
        data = new LinkedList<>();
    }

    @Override
    public boolean hasData() {
        return !data.isEmpty();
    }

    @Override
    public void store(D d, int id) {
        data.add(new Result(d, id));
    }

    @Override
    public void store(Exception e, int id) {
        data.add(new Result(e, id));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void dispatch(LoaderResultCallback<D> callback) {
        Result result = data.poll();
        if (result.error != null)
            callback.OnError(result.error, result.type);
        else
            callback.onResultReady(result.result, result.type);
    }

    private class Result {
        D result;
        int type;
        Exception error;

        Result(D result, int type) {
            this.result = result;
            this.type = type;
        }

        Result(Exception error, int type) {
            this.error = error;
            this.type = type;
        }
    }
}
