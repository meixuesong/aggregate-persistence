package com.github.meixuesong.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Aggregate<R extends Versionable> {
    private R root;
    private R snapshot;
    private Gson gson;

    public Aggregate(R root) {
        this.root = root;
        this.snapshot = createSnapshot();
    }

    private R createSnapshot() {
        String json = getGson().toJson(root);

        return getGson().fromJson(json, (Class<R>) (root.getClass()));
    }

    private Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                    .create();
        }

        return gson;
    }


    public R getRoot() {
        return root;
    }

    public boolean isChanged() {
        String currentJson = getGson().toJson(root);
        String snapshotJson = getGson().toJson(snapshot);

        return !snapshotJson.equals(currentJson);
    }

    public boolean isNew() {
        return root.getVersion() == Versionable.NEW_VERSION;
    }
}
