package com.wangdaye.common.base.vm.pager;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wangdaye.base.resource.ListResource;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Pager view model.
 * */
public abstract class PagerViewModel<T> extends ViewModel {

    private MutableLiveData<ListResource<T>> listResource;
    private ReadWriteLock lock;

    public static class ListResourceWriter<T> {

        private MutableLiveData<ListResource<T>> listResource;

        ListResourceWriter(MutableLiveData<ListResource<T>> listResource) {
            this.listResource = listResource;
        }

        public void setListResource(ListResource<T> resource) {
            listResource.setValue(resource);
        }

        public void postListResource(ListResource<T> resource) {
            listResource.postValue(resource);
        }
    }

    public PagerViewModel() {
        this.listResource = null;
        this.lock = new ReentrantReadWriteLock();
    }

    protected boolean init(@NonNull ListResource<T> resource) {
        if (listResource == null) {
            listResource = new MutableLiveData<>();
            listResource.setValue(resource);
            return true;
        }
        return false;
    }

    public abstract void refresh();

    public abstract void load();

    public void observeListResource(@NonNull LifecycleOwner owner,
                                    @NonNull ListResourceObserver<T> observer) {
        listResource.observe(owner, resource -> observer.observe(this));
    }

    public void writeListResource(DataListWriter<T> writer) {
        lock.writeLock().lock();
        listResource.setValue(writer.execute(listResource.getValue()));
        lock.writeLock().unlock();
    }

    public void asynchronousWriteDataList(AsynchronousDataListWriter<T> writer) {
        Observable.create(emitter -> {
            lock.readLock().lock();
            writer.execute(new ListResourceWriter<>(listResource), listResource.getValue());
            lock.readLock().unlock();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void readDataList(DataListReader<T> reader) {
        assert listResource.getValue() != null;

        lock.readLock().lock();
        reader.execute(listResource.getValue().dataList);
        lock.readLock().unlock();
    }

    public void asynchronousReadDataList(DataListReader<T> reader) {
        assert listResource.getValue() != null;

        Observable.create(emitter -> {
            lock.readLock().lock();
            reader.execute(listResource.getValue().dataList);
            lock.readLock().unlock();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public int getListSize() {
        assert listResource.getValue() != null;

        lock.readLock().lock();
        int size = listResource.getValue().dataList.size();
        lock.readLock().unlock();

        return size;
    }

    public ListResource.State getListState() {
        assert listResource.getValue() != null;

        lock.readLock().lock();
        ListResource.State state = listResource.getValue().state;
        lock.readLock().unlock();

        return state;
    }

    public ListResource.Event consumeListEvent() {
        assert listResource.getValue() != null;

        lock.readLock().lock();
        ListResource.Event event = listResource.getValue().consumeEvent();
        lock.readLock().unlock();

        return event;
    }

    public int getListRequestPage() {
        assert listResource.getValue() != null;

        lock.readLock().lock();
        int page = listResource.getValue().getRequestPage();
        lock.readLock().unlock();

        return page;
    }

    public int getListPerPage() {
        assert listResource.getValue() != null;

        lock.readLock().lock();
        int perPage = listResource.getValue().perPage;
        lock.readLock().unlock();

        return perPage;
    }

    public interface DataListReader<T> {
        void execute(List<T> list);
    }

    public interface DataListWriter<T> {
        ListResource<T> execute(ListResource<T> resource);
    }

    public interface AsynchronousDataListWriter<T> {
        void execute(ListResourceWriter<T> writer, ListResource<T> resource);
    }

    public interface ListResourceObserver<T> {
        void observe(PagerViewModel<T> viewModel);
    }
}
