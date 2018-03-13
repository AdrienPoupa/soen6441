package controllers;

import models.twitter.Status;
import play.cache.SyncCacheApi;

import java.util.List;
import java.util.concurrent.Callable;

public class MockSyncCacheApi implements SyncCacheApi {

    List<models.twitter.Status> cachedStatuses;

    public List<Status> getCachedStatuses() {
        return cachedStatuses;
    }

    public void setCachedStatuses(List<Status> cachedStatuses) {
        this.cachedStatuses = cachedStatuses;
    }

    @Override
    public <T> T get(String key) {
        return null;
    }

    @Override
    public <T> T getOrElseUpdate(String key, Callable<T> block, int expiration) {
        return null;
    }

    @Override
    public <T> T getOrElseUpdate(String key, Callable<T> block) {
        return null;
    }

    @Override
    public void set(String key, Object value, int expiration) {
        cachedStatuses = (List<Status>) value;
    }

    @Override
    public void set(String key, Object value) {

    }

    @Override
    public void remove(String key) {

    }
}