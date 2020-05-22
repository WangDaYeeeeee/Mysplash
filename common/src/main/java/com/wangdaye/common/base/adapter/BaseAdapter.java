package com.wangdaye.common.base.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Base adapter.
 *
 * A base adapter class for {@link RecyclerView}.
 * M  : model
 * VM : view model
 * VH : view holder
 * */
public abstract class BaseAdapter<M, VM extends BaseAdapter.ViewModel, VH extends RecyclerView.ViewHolder>
        extends ListAdapter<VM, VH> {

    private Context context;

    public interface ViewModel {
        boolean areItemsTheSame(ViewModel newModel);
        boolean areContentsTheSame(ViewModel newModel);
        Object getChangePayload(ViewModel newModel);
    }

    public BaseAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    public BaseAdapter(Context context, @NonNull List<M> list) {
        super(new DiffUtil.ItemCallback<VM>() {
            @Override
            public boolean areItemsTheSame(@NonNull VM oldItem, @NonNull VM newItem) {
                return oldItem.areItemsTheSame(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull VM oldItem, @NonNull VM newItem) {
                return oldItem.areContentsTheSame(newItem);
            }

            @Nullable
            @Override
            public Object getChangePayload(@NonNull VM oldItem, @NonNull VM newItem) {
                return oldItem.getChangePayload(newItem);
            }
        });

        this.context = context;
        this.update(list);
    }

    @Override
    public final void onBindViewHolder(@NonNull VH holder, int position) {
        onBindViewHolder(holder, getItem(position));
    }

    protected abstract void onBindViewHolder(@NonNull VH holder, VM model);

    @Override
    public final void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        onBindViewHolder(holder, getItem(position), payloads);
    }

    protected abstract void onBindViewHolder(@NonNull VH holder, VM model, @NonNull List<Object> payloads);

    protected Context getContext() {
        return context;
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
        List<VM> vmList = new ArrayList<>(getCurrentList());
        vmList.add(getViewModel(model));
        submitList(vmList);
    }

    public void addItem(M model, int index) {
        List<VM> vmList = new ArrayList<>(getCurrentList());
        vmList.add(index, getViewModel(model));
        submitList(vmList);
    }

    public void addItems(@NonNull List<M> list) {
        List<VM> vmList = new ArrayList<>(getCurrentList());
        vmList.addAll(getViewModelList(list));
        submitList(vmList);
    }

    public void removeItem(M model) {
        List<VM> vmList = new ArrayList<>(getCurrentList());
        VM vm = getViewModel(model);

        for (int i = 0; i < vmList.size(); i ++) {
            if (vmList.get(i).areItemsTheSame(vm)) {
                vmList.remove(i);
                break;
            }
        }

        submitList(vmList);
    }

    public void updateItem(M model) {
        List<VM> vmList = new ArrayList<>(getCurrentList());
        VM vm = getViewModel(model);

        for (int i = 0; i < vmList.size(); i ++) {
            if (vmList.get(i).areItemsTheSame(vm)) {
                vmList.set(i, vm);
                break;
            }
        }

        submitList(vmList);
    }

    public void update(@NonNull List<M> list) {
        submitList(getViewModelList(list), null);
    }
}
