package com.wangdaye.base.resource;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class ListResource<T> {

    @NonNull public final List<T> dataList;
    @NonNull public final State state;
    @NonNull private Event event;
    private final int dataPage;
    public final int perPage;

    public enum State {
        SUCCESS, ALL_LOADED, ERROR, REFRESHING, LOADING
    }

    public interface Event {
    }

    private ListResource(@NonNull List<T> dataList, @NonNull State state, @NonNull Event event,
                         int dataPage, int perPage) {
        this.dataList = dataList;
        this.state = state;
        this.event = event;
        this.dataPage = dataPage;
        this.perPage = perPage;
    }

    @NonNull
    public Event consumeEvent() {
        Event result = event;
        event = new None();
        return result;
    }

    public int getRequestPage() {
        switch (state) {
            case REFRESHING:
                return 1;

            case LOADING:
                return dataPage + 1;

            default:
                return dataPage;
        }
    }

    public static <T> ListResource<T> refreshSuccess(@NonNull ListResource<T> current, @NonNull List<T> newList) {
        List<T> list = current.dataList;
        list.clear();
        list.addAll(newList);
        return new ListResource<>(
                list,
                State.SUCCESS,
                new DataSetChanged(),
                1,
                current.perPage
        );
    }

    public static <T> ListResource<T> loadSuccess(@NonNull ListResource<T> current, @NonNull List<T> newList) {
        List<T> list = current.dataList;
        list.addAll(newList);

        Event event = current.event;
        if (event instanceof None) {
            event = new ItemRangeInserted(newList.size());
        } else if (event instanceof ItemRangeInserted) {
            event = new ItemRangeInserted(
                    ((ItemRangeInserted) event).increase + newList.size()
            );
        } else {
            event = new DataSetChanged();
        }

        return new ListResource<>(
                list,
                State.SUCCESS,
                event,
                current.dataPage + 1,
                current.perPage
        );
    }

    public static <T> ListResource<T> allLoaded(@NonNull ListResource<T> current, @NonNull List<T> newList) {
        List<T> list = current.dataList;
        list.addAll(newList);

        Event event = current.event;
        if (event instanceof None) {
            event = new ItemRangeInserted(newList.size());
        } else if (event instanceof ItemRangeInserted) {
            event = new ItemRangeInserted(
                    ((ItemRangeInserted) event).increase + newList.size()
            );
        } else {
            event = new DataSetChanged();
        }

        return new ListResource<>(
                list,
                State.ALL_LOADED,
                event,
                current.dataPage + 1,
                current.perPage
        );
    }

    public static <T> ListResource<T> error(int page, int perPage) {
        return new ListResource<>(
                new ArrayList<>(),
                State.ERROR,
                new None(),
                page,
                perPage);
    }

    public static <T> ListResource<T> error(@NonNull ListResource<T> current) {
        return new ListResource<>(
                current.dataList,
                State.ERROR,
                current.event,
                current.dataPage,
                current.perPage
        );
    }

    public static <T> ListResource<T> refreshing(int page, int perPage) {
        return new ListResource<>(
                new ArrayList<>(),
                State.REFRESHING,
                new None(),
                page,
                perPage
        );
    }

    public static <T> ListResource<T> refreshing(@NonNull ListResource<T> current) {
        return new ListResource<>(
                current.dataList,
                State.REFRESHING,
                current.event,
                current.dataPage,
                current.perPage
        );
    }

    public static <T> ListResource<T> initRefreshing(@NonNull ListResource<T> current) {
        List<T> list = current.dataList;
        list.clear();
        return new ListResource<>(
                list,
                State.REFRESHING,
                current.event,
                0,
                current.perPage
        );
    }

    public static <T> ListResource<T> loading(@NonNull ListResource<T> current) {
        return new ListResource<>(
                current.dataList,
                State.LOADING,
                current.event,
                current.dataPage,
                current.perPage
        );
    }

    public static <T> ListResource<T> insertItem(@NonNull ListResource<T> current,
                                                 @NonNull T item, int index) {
        List<T> list = current.dataList;
        list.add(index, item);

        Event event = current.event;
        if (event instanceof None) {
            event = new ItemInserted(index);
        } else {
            event = new DataSetChanged();
        }

        return new ListResource<>(
                list,
                current.state,
                event,
                current.dataPage,
                current.perPage
        );
    }

    public static <T> ListResource<T> changeItem(@NonNull ListResource<T> current,
                                                 @NonNull T item, int index) {
        List<T> list = current.dataList;
        list.set(index, item);

        Event event = current.event;
        if (event instanceof None
                || (event instanceof ItemChanged && ((ItemChanged) event).index == index)) {
            event = new ItemChanged(index);
        } else {
            event = new DataSetChanged();
        }

        return new ListResource<>(
                list,
                current.state,
                event,
                current.dataPage,
                current.perPage);
    }

    public static <T> ListResource<T> removeItem(@NonNull ListResource<T> current, int index) {
        List<T> list = current.dataList;
        list.remove(index);

        Event event = current.event;
        if (event instanceof None) {
            event = new ItemRemoved(index);
        } else {
            event = new DataSetChanged();
        }

        return new ListResource<>(
                list,
                current.state,
                event,
                current.dataPage,
                current.perPage);
    }

    // event.

    public static class None implements Event {
    }

    public static class DataSetChanged implements Event {
    }

    public static class ItemRangeInserted implements Event {

        public int increase;

        public ItemRangeInserted(int increase) {
            this.increase = increase;
        }
    }

    public static class ItemInserted implements Event {

        public int index;

        public ItemInserted(int index) {
            this.index = index;
        }
    }

    public static class ItemChanged implements Event {

        public int index;

        public ItemChanged(int index) {
            this.index = index;
        }
    }

    public static class ItemRemoved implements Event {

        public int index;

        public ItemRemoved(int index) {
            this.index = index;
        }
    }
}
