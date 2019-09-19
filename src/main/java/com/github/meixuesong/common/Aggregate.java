package com.github.meixuesong.common;

import com.google.gson.Gson;

public class Aggregate<R extends Versionable> {
    private R root;
    private R snapshot;

    public Aggregate(R root) {
        this.root = root;
        this.snapshot = createSnapshot();
    }

    private R createSnapshot() {
        Gson gson = new Gson();
        String json = gson.toJson(root);

        return gson.fromJson(json, (Class<R>) (root.getClass()));
    }


    public R getRoot() {
        return root;
    }

    public boolean isChanged() {
        Gson gson = new Gson();
        String currentJson = gson.toJson(root);
        String snapshotJson = gson.toJson(snapshot);

        return !snapshotJson.equals(currentJson);
    }

    public boolean isNew() {
        return root.getVersion() == Versionable.NEW_VERSION;
    }

}
