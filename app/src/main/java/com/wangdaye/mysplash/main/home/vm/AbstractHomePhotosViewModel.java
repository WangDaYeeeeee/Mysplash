package com.wangdaye.mysplash.main.home.vm;

import com.wangdaye.mysplash.common.basic.model.ListResource;
import com.wangdaye.mysplash.common.basic.vm.PagerViewModel;
import com.wangdaye.mysplash.common.bus.event.DownloadEvent;
import com.wangdaye.mysplash.common.network.json.Photo;
import com.wangdaye.mysplash.common.bus.MessageBus;
import com.wangdaye.mysplash.common.bus.event.PhotoEvent;
import com.wangdaye.mysplash.common.presenter.event.DownloadEventResponsePresenter;
import com.wangdaye.mysplash.common.presenter.event.PhotoEventResponsePresenter;
import com.wangdaye.mysplash.main.home.HomePhotosViewRepository;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Home pager model.
 * */
public abstract class AbstractHomePhotosViewModel extends PagerViewModel<Photo> {

    private HomePhotosViewRepository repository;
    private PhotoEventResponsePresenter photoEventResponsePresenter;
    private DownloadEventResponsePresenter downloadEventResponsePresenter;

    private Disposable photoEventDisposable;
    private Disposable downloadEventDisposable;

    private Consumer<PhotoEvent> photoEventConsumer = photoEvent ->
            photoEventResponsePresenter.updatePhoto(getListResource(), photoEvent.photo, false);

    private Consumer<DownloadEvent> downloadEventConsumer = event ->
            downloadEventResponsePresenter.updatePhoto(getListResource(), event, false);

    private MutableLiveData<String> photosOrder;

    private List<Integer> pageList;
    private String latestOrder;
    private String randomTxt;

    public AbstractHomePhotosViewModel(HomePhotosViewRepository repository,
                                       PhotoEventResponsePresenter photoEventResponsePresenter,
                                       DownloadEventResponsePresenter downloadEventResponsePresenter) {
        super();

        this.repository = repository;
        this.photoEventResponsePresenter = photoEventResponsePresenter;
        this.downloadEventResponsePresenter = downloadEventResponsePresenter;

        this.photoEventDisposable = MessageBus.getInstance()
                .toObservable(PhotoEvent.class)
                .subscribe(photoEventConsumer);
        this.downloadEventDisposable = MessageBus.getInstance()
                .toObservable(DownloadEvent.class)
                .subscribe(downloadEventConsumer);

        this.photosOrder = null;
        this.pageList = null;
        this.latestOrder = null;
        this.randomTxt = null;
    }

    public void init(ListResource<Photo> defaultResource,
                     String defaultOrder, List<Integer> pageList, String randomTxt) {
        boolean init = super.init(defaultResource);

        if (photosOrder == null) {
            photosOrder = new MutableLiveData<>();
            photosOrder.setValue(defaultOrder);
        }
        if (this.pageList == null) {
            this.pageList = pageList;
        }
        if (this.latestOrder == null) {
            this.latestOrder = defaultOrder;
        }
        this.randomTxt = randomTxt;

        if (init) {
            refresh();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        
        repository.cancel();
        photoEventResponsePresenter.clearResponse();
        downloadEventResponsePresenter.clearResponse();

        photoEventDisposable.dispose();
        downloadEventDisposable.dispose();
    }

    @Override
    public void refresh() {
        getPhotos(true);
    }

    @Override
    public void load() {
        getPhotos(false);
    }

    private void getPhotos(boolean refresh) {
        if (photosOrder.getValue() != null && randomTxt != null
                && photosOrder.getValue().equals(randomTxt)) {
            getPhotosRandom(refresh);
        } else {
            getPhotosOrderly(refresh);
        }
    }

    abstract void getPhotosOrderly(boolean refresh);

    abstract void getPhotosRandom(boolean refresh);

    HomePhotosViewRepository getRepository() {
        return repository;
    }

    public MutableLiveData<String> getPhotosOrder() {
        return photosOrder;
    }

    public void setPhotosOrder(String order) {
        photosOrder.setValue(order);
    }

    List<Integer> getPageList() {
        return pageList;
    }

    public String getLatestOrder() {
        return latestOrder;
    }

    public void setLatestOrder(String latestOrder) {
        this.latestOrder = latestOrder;
    }
}
