package ume.streams;

import java.util.LinkedList;

import ume.loaders.LifeCycleLoader.DataStream;
import ume.loaders.LifeCycleLoader.LoaderResultCallback;

public class QueueDataStream<D> implements DataStream<D> {
    private LinkedList<Result> data;

    public QueueDataStream() {
        data = new LinkedList<>();
    }

    @Override
    public boolean hasData() {
        return !data.isEmpty();
    }

    @Override
    public void onData(D d, int id) {
        data.add(new Result(d, id));
    }

    @Override
    public void onError(Exception e, int id) {
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
