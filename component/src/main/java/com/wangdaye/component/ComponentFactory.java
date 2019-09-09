package com.wangdaye.component;

import com.wangdaye.component.module.AboutModule;
import com.wangdaye.component.module.CollectionModule;
import com.wangdaye.component.module.MainModule;
import com.wangdaye.component.module.MeModule;
import com.wangdaye.component.module.PhotoModule;
import com.wangdaye.component.module.SearchModule;
import com.wangdaye.component.service.SettingsService;
import com.wangdaye.component.module.UserModule;
import com.wangdaye.component.service.DatabaseService;
import com.wangdaye.component.service.DownloaderService;
import com.wangdaye.component.service.MuzeiService;

public class ComponentFactory {

    private static class Inner {
        private static ComponentFactory instance = new ComponentFactory();
    }

    private static ComponentFactory getInstance() {
        return Inner.instance;
    }

    private DownloaderService downloaderService;
    private MuzeiService muzeiService;
    private DatabaseService databaseService;
    private SettingsService settingsService;

    private AboutModule aboutModule;
    private MainModule mainModule;
    private SearchModule searchModule;
    private CollectionModule collectionModule;
    private MeModule meModule;
    private UserModule userModule;
    private PhotoModule photoModule;

    private static final String SETTINGS_APPLICATION = "com.wangdaye.settings.SettingsApplication";
    private static final String DATABASE_APPLICATION = "com.wangdaye.db.DatabaseApplication";
    private static final String DOWNLOADER_APPLICATION = "com.wangdaye.downloader.DownloaderApplication";
    private static final String MUZEI_APPLICATION = "com.wangdaye.muzei.MuzeiApplication";

    private static final String ABOUT_APPLICATION = "com.wangdaye.about.AboutApplication";
    private static final String MAIN_APPLICATION = "com.wangdaye.main.MainApplication";
    private static final String SEARCH_APPLICATION = "com.wangdaye.search.SearchApplication";
    private static final String COLLECTION_APPLICATION = "com.wangdaye.collection.CollectionApplication";
    private static final String ME_APPLICATION = "com.wangdaye.me.MeApplication";
    private static final String USER_APPLICATION = "com.wangdaye.user.UserApplication";
    private static final String PHOTO_APPLICATION = "com.wangdaye.photo.PhotoApplication";

    public static String[] moduleApplications = {
            SETTINGS_APPLICATION,
            DATABASE_APPLICATION,
            DOWNLOADER_APPLICATION,
            MUZEI_APPLICATION,

            ABOUT_APPLICATION,
            MAIN_APPLICATION,
            SEARCH_APPLICATION,
            COLLECTION_APPLICATION,
            ME_APPLICATION,
            USER_APPLICATION,
            PHOTO_APPLICATION
    };

    public static DownloaderService getDownloaderService() {
        return getInstance().downloaderService;
    }

    public static void setDownloaderService(DownloaderService service) {
        getInstance().downloaderService = service;
    }

    public static MuzeiService getMuzeiService() {
        return getInstance().muzeiService;
    }

    public static void setMuzeiService(MuzeiService service) {
        getInstance().muzeiService = service;
    }

    public static DatabaseService getDatabaseService() {
        return getInstance().databaseService;
    }

    public static void setDatabaseService(DatabaseService service) {
        getInstance().databaseService = service;
    }

    public static SettingsService getSettingsService() {
        return getInstance().settingsService;
    }

    public static void setSettingsService(SettingsService module) {
        getInstance().settingsService = module;
    }

    public static AboutModule getAboutModule() {
        return getInstance().aboutModule;
    }

    public static void setAboutModule(AboutModule module) {
        getInstance().aboutModule = module;
    }

    public static MainModule getMainModule() {
        return getInstance().mainModule;
    }

    public static void setMainModule(MainModule module) {
        getInstance().mainModule = module;
    }

    public static SearchModule getSearchModule() {
        return getInstance().searchModule;
    }

    public static void setSearchModule(SearchModule module) {
        getInstance().searchModule = module;
    }

    public static CollectionModule getCollectionModule() {
        return getInstance().collectionModule;
    }

    public static void setCollectionModule(CollectionModule module) {
        getInstance().collectionModule = module;
    }

    public static MeModule getMeModule() {
        return getInstance().meModule;
    }

    public static void setMeModule(MeModule module) {
        getInstance().meModule = module;
    }

    public static UserModule getUserModule() {
        return getInstance().userModule;
    }

    public static void setUserModule(UserModule module) {
        getInstance().userModule = module;
    }

    public static PhotoModule getPhotoModule() {
        return getInstance().photoModule;
    }

    public static void setPhotoModule(PhotoModule module) {
        getInstance().photoModule = module;
    }
}
