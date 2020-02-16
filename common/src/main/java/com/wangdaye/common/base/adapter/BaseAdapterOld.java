package com.wangdaye.common.base.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Base adapter.
 *
 * A base adapter class for {@link RecyclerView}.
 * M  : model
 * VM : view model
 * VH : view holder
 * */
@Deprecated
public abstract class BaseAdapterOld<M, VM extends BaseAdapterOld.ViewModel, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private Context context;
    private List<VM> viewModelList;
    private ReadWriteLock lock;

    private final Executor mainThreadExecutor;
    private final Executor backgroundThreadExecutor;

    public interface ViewModel {
        boolean areItemsTheSame(ViewModel newModel);
        boolean areContentsTheSame(ViewModel newModel);
        Object getChangePayload(ViewModel newModel);
    }

    private static class MainThreadExecutor implements Executor {

        final Handler handler = new Handler(Looper.getMainLooper());

        MainThreadExecutor() {}

        @Override
        public void execute(@NonNull Runnable command) {
            handler.post(command);
        }
    }

    public BaseAdapterOld(Context context) {
        this(context, new ArrayList<>());
    }

    public BaseAdapterOld(Context context, @NonNull List<M> list) {
        this.context = context;
        this.viewModelList = getViewModelList(list);
        this.lock = new ReentrantReadWriteLock();

        this.mainThreadExecutor = new MainThreadExecutor();
        this.backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public final void onBindViewHolder(@NonNull VH holder, int position) {
        lock.readLock().lock();
        onBindViewHolder(holder, getItem(position));
        lock.readLock().unlock();
    }

    protected abstract void onBindViewHolder(@NonNull VH holder, VM model);

    @Override
    public final void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        lock.readLock().lock();
        onBindViewHolder(holder, getItem(position), payloads);
        lock.readLock().unlock();
    }

    protected abstract void onBindViewHolder(@NonNull VH holder, VM model, @NonNull List<Object> payloads);

    protected Context getContext() {
        return context;
    }

    @NonNull
    protected List<VM> getCurrentList() {
        lock.readLock().lock();
        List<VM> list = Collections.unmodifiableList(viewModelList);
        lock.readLock().unlock();

        return list;
    }

    protected VM getItem(int position) {
        lock.readLock().lock();
        VM vm = viewModelList.get(position);
        lock.readLock().unlock();

        return vm;
    }

    @Override
    public int getItemCount() {
        lock.readLock().lock();
        int size = viewModelList.size();
        lock.readLock().unlock();

        return size;
    }

    protected abstract VM getViewModel(M model);

    protected List<VM> getViewModelList(@NonNull List<M> list) {
        List<VM> viewModelList = new ArrayList<>(list.size());
        for (M m : list) {
            viewModelList.add(getViewModel(m));
        }
        return viewModelList;
    }

    public void addItem(M model) {
        lock.writeLock().lock();

        viewModelList.add(getViewModel(model));
        notifyItemInserted(viewModelList.size() - 1);

        lock.writeLock().unlock();
    }

    public void addItems(@NonNull List<M> list) {
        lock.writeLock().lock();

        int oldCount = getItemCount();
        viewModelList.addAll(getViewModelList(list));
        int newCount = getItemCount();

        notifyItemRangeInserted(oldCount, newCount - oldCount);

        lock.writeLock().unlock();
    }

    public void removeItem(M model) {
        lock.writeLock().lock();

        VM vm = getViewModel(model);
        for (int i = 0; i < viewModelList.size(); i ++) {
            if (viewModelList.get(i).areItemsTheSame(vm)) {
                viewModelList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }

        lock.writeLock().unlock();
    }

    public void updateItem(M model) {
        lock.writeLock().lock();

        VM vm = getViewModel(model);
        for (int i = 0; i < viewModelList.size(); i ++) {
            if (viewModelList.get(i).areItemsTheSame(vm)) {
                VM old = viewModelList.get(i);
                viewModelList.set(i, vm);
                notifyItemChanged(i, old.getChangePayload(vm));
                break;
            }
        }

        lock.writeLock().unlock();
    }

    public void update(@NonNull List<M> list) {
        submitList(getViewModelList(list), null);
    }

    protected void submitList(@NonNull List<VM> newList, @Nullable Runnable completedCallback) {
        backgroundThreadExecutor.execute(() -> {
            // read old list and generate a diff result.

            lock.readLock().lock();

            List<VM> oldList = new ArrayList<>(viewModelList);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {

                @Override
                public int getOldListSize() {
                    return oldList.size();
                }

                @Override
                public int getNewListSize() {
                    return newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldList.get(oldItemPosition).areItemsTheSame(newList.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldList.get(oldItemPosition).areContentsTheSame(newList.get(newItemPosition));
                }

                @Nullable
                @Override
                public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                    return oldList.get(oldItemPosition).getChangePayload(newList.get(newItemPosition));
                }
            }, false);

            lock.readLock().unlock();

            // write list.

            lock.writeLock().lock();

            viewModelList.clear();
            viewModelList.addAll(newList);

            lock.writeLock().unlock();

            // notify.

            mainThreadExecutor.execute(() -> {
                result.dispatchUpdatesTo(this);
                if (completedCallback != null) {
                    completedCallback.run();
                }
            });
        });
    }
}
