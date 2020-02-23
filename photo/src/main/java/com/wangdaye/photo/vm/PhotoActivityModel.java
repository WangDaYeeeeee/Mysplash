package com.wangdaye.photo.vm;

import com.wangdaye.base.resource.ListResource;
import com.wangdaye.base.resource.Resource;
import com.wangdaye.common.base.vm.BrowsableViewModel;
import com.wangdaye.common.bus.event.DownloadEvent;
import com.wangdaye.base.unsplash.Photo;
import com.wangdaye.common.bus.MessageBus;
import com.wangdaye.common.bus.event.PhotoEvent;
import com.wangdaye.photo.repository.PhotoActivityRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Photo activity model.
 * */
public class PhotoActivityModel extends BrowsableViewModel<Photo> {

    private MutableLiveData<ListResource<Photo>> listResource;
    private MutableLiveData<Boolean> componentsVisibility;
    private MutableLiveData<Boolean> previewVisibility;
    private PhotoActivityRepository repository;

    private Disposable photoEventDisposable;
    private Disposable downloadEventDisposable;

    private String photoId;
    private int initPage;
    private boolean multiPage;

    @Inject
    public PhotoActivityModel(PhotoActivityRepository repository) {
        super();

        this.listResource = null;
        this.componentsVisibility = null;
        this.repository = repository;

        this.photoEventDisposable = MessageBus.getInstance()
                .toObservable(PhotoEvent.class)
                .subscribe(photoEvent -> {
                    if (photoEvent.event == PhotoEvent.Event.COMPLETE) {
                        Photo photo = photoEvent.photo;
                        if (photo != null) {
                            updatePhotoList(photo);
                            if (photoId.equals(photo.id)) {
                                setPhoto(photo);
                            }
                        }
                    } else {
                        Photo photo = findPhoto(photoEvent.photoId);
                        if (photo != null) {
                            photo.liked_by_user = photoEvent.like;
                            if (photoEvent.photo != null) {
                                photo.current_user_collections = photoEvent.photo.current_user_collections;
                            }
                            updatePhotoList(photo);
                            if (photoId.equals(photo.id)) {
                                setPhoto(getResourceExtendsCurrentStatus(photo), false);
                            }
                        }
                    }
                });
        this.downloadEventDisposable = MessageBus.getInstance()
                .toObservable(DownloadEvent.class)
                .subscribe(event -> {
                    Photo photo = findPhoto(event.title);
                    if (photo != null) {
                        updatePhotoList(photo);
                        if (photoId.equals(photo.id)) {
                            setPhoto(getResourceExtendsCurrentStatus(photo), false);
                        }
                    }
                });

        this.photoId = null;
    }

    public void init(@NonNull List<Photo> defaultList, int defaultIndex) {
        boolean init = super.init(Resource.success(defaultList.get(defaultIndex)));

        this.listResource = new MutableLiveData<>();
        listResource.setValue(
                ListResource.refreshSuccess(
                        ListResource.error(0, 1),
                        defaultList
                )
        );

        this.componentsVisibility = new MutableLiveData<>();
        componentsVisibility.setValue(true);

        this.previewVisibility = new MutableLiveData<>();
        previewVisibility.setValue(false);

        if (this.photoId == null) {
            this.photoId = defaultList.get(defaultIndex).id;
        }
        this.initPage = defaultIndex;
        this.multiPage = true;

        if (init) {
            checkToRequestPhoto();
        }
    }

    public void init(@NonNull Photo photo) {
        boolean init = super.init(Resource.success(photo));

        List<Photo> list = new ArrayList<>();
        list.add(photo);
        this.listResource = new MutableLiveData<>();
        listResource.setValue(
                ListResource.refreshSuccess(
                        ListResource.error(0, 1),
                        list
                )
        );

        this.componentsVisibility = new MutableLiveData<>();
        componentsVisibility.setValue(true);

        this.previewVisibility = new MutableLiveData<>();
        previewVisibility.setValue(false);

        if (this.photoId == null) {
            this.photoId = photo.id;
        }
        this.initPage = 0;
        this.multiPage = false;

        if (init) {
            checkToRequestPhoto();
        }
    }

    public void init(@NonNull String photoId) {
        boolean init = super.init(Resource.loading(null));

        this.listResource = new MutableLiveData<>();
        listResource.setValue(
                ListResource.loading(
                        ListResource.error(0, 1)
                )
        );

        this.componentsVisibility = new MutableLiveData<>();
        componentsVisibility.setValue(true);

        this.previewVisibility = new MutableLiveData<>();
        previewVisibility.setValue(false);

        if (this.photoId == null) {
            this.photoId = photoId;
        }
        this.initPage = 0;
        this.multiPage = false;

        if (init) {
            checkToRequestPhoto();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        repository.cancel();
        photoEventDisposable.dispose();
        downloadEventDisposable.dispose();
    }

    public void checkToRequestPhoto() {
        if (getResource().getValue() != null) {
            Photo photo = getResource().getValue().data;
            if (photo == null || !photo.isComplete()) {
                repository.getAPhoto(this, photoId);
            }
        }
    }

    public MutableLiveData<ListResource<Photo>> getListResource() {
        return listResource;
    }

    public MutableLiveData<Boolean> getComponentsVisibility() {
        return componentsVisibility;
    }

    public MutableLiveData<Boolean> getPreviewVisibility() {
        return previewVisibility;
    }

    public boolean isMultiPage() {
        return multiPage;
    }

    public int getInitPage() {
        return initPage;
    }

    public void setPhoto(@NonNull Photo photo) {
        setPhoto(Resource.success(photo), true);
    }

    public void setPhoto(Resource<Photo> resource, boolean checkToRequest) {
        setResource(resource);
        if (resource.data != null) {
            photoId = resource.data.id;
        }
        if (checkToRequest) {
            checkToRequestPhoto();
        }
    }

    private void updatePhotoList(@NonNull Photo photo) {
        ListResource<Photo> resource = listResource.getValue();
        if (resource != null) {
            List<Photo> list = new ArrayList<>(resource.dataList);
            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i).id.equals(photo.id)) {
                    list.set(i, photo);
                }
            }
            listResource.setValue(ListResource.refreshSuccess(resource, list));
        }
    }

    @Nullable
    private Photo findPhoto(@NonNull String photoId) {
        ListResource<Photo> resource = listResource.getValue();
        if (resource != null) {
            List<Photo> list = resource.dataList;
            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i).id.equals(photoId)) {
                    return list.get(i);
                }
            }
        }
        return null;
    }

    private Resource<Photo> getResourceExtendsCurrentStatus(@NonNull Photo photo) {
        Resource<Photo> current = getResource().getValue();
        if (current != null) {
            switch (current.status) {
                case SUCCESS:
                    return Resource.success(photo);

                case ERROR:
                    return Resource.error(photo);

                case LOADING:
                    return Resource.loading(photo);
            }
        }
        return Resource.error(photo);
    }
}
