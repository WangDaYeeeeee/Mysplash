package com.wangdaye.mysplash._common.data.entity;

import java.util.List;

/**
 * Photo details.
 * */

public class PhotoDetails {

    /**
     * id : Dwu85P9SOIk
     * created_at : 2016-05-03T11:00:28-04:00
     * width : 2448
     * height : 3264
     * color : #6E633A
     * downloads : 1345
     * likes : 24
     * liked_by_user : false
     * exif : {"make":"Canon","model":"Canon EOS 40D","exposure_time":"0.011111111111111112","aperture":"4.970854","focal_length":"37","iso":100}
     * location : {"city":"Montreal","country":"Canada","position":{"latitude":45.4732984,"longitude":-73.6384879}}
     * current_user_collections : [{"id":206,"title":"Makers: Cat and Ben","published_at":"2016-01-12T18:16:09-05:00","curated":false,"cover_photo":{"id":"xCmvrpzctaQ","width":7360,"height":4912,"color":"#040C14","likes":12,"liked_by_user":false,"user":{"id":"eUO1o53muso","username":"crew","name":"Crew","profile_image":{"small":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"},"links":{"self":"https://api.unsplash.com/users/crew","html":"http://unsplash.com/crew","photos":"https://api.unsplash.com/users/crew/photos","likes":"https://api.unsplash.com/users/crew/likes"}},"urls":{"raw":"https://images.unsplash.com/photo-1452457807411-4979b707c5be","full":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy","regular":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=1080&fit=max","small":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=400&fit=max","thumb":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=200&fit=max"},"categories":[{"id":6,"title":"People","photo_count":9844,"links":{"self":"https://api.unsplash.com/categories/6","photos":"https://api.unsplash.com/categories/6/photos"}}],"links":{"self":"https://api.unsplash.com/photos/xCmvrpzctaQ","html":"https://unsplash.com/photos/xCmvrpzctaQ","download":"https://unsplash.com/photos/xCmvrpzctaQ/download"}},"user":{"id":"eUO1o53muso","username":"crew","name":"Crew","bio":"Work with the best designers and developers without breaking the bank. Creators of Unsplash.","profile_image":{"small":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"},"links":{"self":"https://api.unsplash.com/users/crew","html":"https://unsplash.com/crew","photos":"https://api.unsplash.com/users/crew/photos","likes":"https://api.unsplash.com/users/crew/likes"}},"links":{"self":"https://api.unsplash.com/collections/206","html":"https://unsplash.com/collections/206","photos":"https://api.unsplash.com/collections/206/photos"}}]
     * urls : {"raw":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d","full":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg","regular":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=1080&fit=max","small":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=400&fit=max","thumb":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=200&fit=max"}
     * categories : [{"id":4,"title":"Nature","photo_count":24783,"links":{"self":"https://api.unsplash.com/categories/4","photos":"https://api.unsplash.com/categories/4/photos"}}]
     * links : {"self":"https://api.unsplash.com/photos/Dwu85P9SOIk","html":"https://unsplash.com/photos/Dwu85P9SOIk","download":"https://unsplash.com/photos/Dwu85P9SOIk/download"}
     * user : {"id":"QPxL2MGqfrw","username":"exampleuser","name":"Joe Example","links":{"self":"https://api.unsplash.com/users/exampleuser","html":"https://unsplash.com/exampleuser","photos":"https://api.unsplash.com/users/exampleuser/photos","likes":"https://api.unsplash.com/users/exampleuser/likes"}}
     */

    public String id;
    public String created_at;
    public int width;
    public int height;
    public String color;
    public int downloads;
    public int likes;
    public boolean liked_by_user;
    /**
     * make : Canon
     * model : Canon EOS 40D
     * exposure_time : 0.011111111111111112
     * aperture : 4.970854
     * focal_length : 37
     * iso : 100
     */

    public Exif exif;
    /**
     * city : Montreal
     * country : Canada
     * position : {"latitude":45.4732984,"longitude":-73.6384879}
     */

    public Location location;
    /**
     * raw : https://images.unsplash.com/photo-1417325384643-aac51acc9e5d
     * full : https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg
     * regular : https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=1080&fit=max
     * small : https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=400&fit=max
     * thumb : https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=200&fit=max
     */

    public Urls urls;
    /**
     * self : https://api.unsplash.com/photos/Dwu85P9SOIk
     * html : https://unsplash.com/photos/Dwu85P9SOIk
     * download : https://unsplash.com/photos/Dwu85P9SOIk/download
     */

    public Links links;
    /**
     * id : QPxL2MGqfrw
     * username : exampleuser
     * name : Joe Example
     * links : {"self":"https://api.unsplash.com/users/exampleuser","html":"https://unsplash.com/exampleuser","photos":"https://api.unsplash.com/users/exampleuser/photos","likes":"https://api.unsplash.com/users/exampleuser/likes"}
     */

    public User user;
    /**
     * id : 206
     * title : Makers: Cat and Ben
     * published_at : 2016-01-12T18:16:09-05:00
     * curated : false
     * cover_photo : {"id":"xCmvrpzctaQ","width":7360,"height":4912,"color":"#040C14","likes":12,"liked_by_user":false,"user":{"id":"eUO1o53muso","username":"crew","name":"Crew","profile_image":{"small":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"},"links":{"self":"https://api.unsplash.com/users/crew","html":"http://unsplash.com/crew","photos":"https://api.unsplash.com/users/crew/photos","likes":"https://api.unsplash.com/users/crew/likes"}},"urls":{"raw":"https://images.unsplash.com/photo-1452457807411-4979b707c5be","full":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy","regular":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=1080&fit=max","small":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=400&fit=max","thumb":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=200&fit=max"},"categories":[{"id":6,"title":"People","photo_count":9844,"links":{"self":"https://api.unsplash.com/categories/6","photos":"https://api.unsplash.com/categories/6/photos"}}],"links":{"self":"https://api.unsplash.com/photos/xCmvrpzctaQ","html":"https://unsplash.com/photos/xCmvrpzctaQ","download":"https://unsplash.com/photos/xCmvrpzctaQ/download"}}
     * user : {"id":"eUO1o53muso","username":"crew","name":"Crew","bio":"Work with the best designers and developers without breaking the bank. Creators of Unsplash.","profile_image":{"small":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"},"links":{"self":"https://api.unsplash.com/users/crew","html":"https://unsplash.com/crew","photos":"https://api.unsplash.com/users/crew/photos","likes":"https://api.unsplash.com/users/crew/likes"}}
     * links : {"self":"https://api.unsplash.com/collections/206","html":"https://unsplash.com/collections/206","photos":"https://api.unsplash.com/collections/206/photos"}
     */

    public List<CurrentUserCollections> current_user_collections;
    /**
     * id : 4
     * title : Nature
     * photo_count : 24783
     * links : {"self":"https://api.unsplash.com/categories/4","photos":"https://api.unsplash.com/categories/4/photos"}
     */

    public List<Categories> categories;

    public static class Exif {
        public String make;
        public String model;
        public String exposure_time;
        public String aperture;
        public String focal_length;
        public int iso;
    }

    public static class Location {
        public String city;
        public String country;
        /**
         * latitude : 45.4732984
         * longitude : -73.6384879
         */

        public Position position;

        public static class Position {
            public double latitude;
            public double longitude;
        }
    }

    public static class Urls {
        public String raw;
        public String full;
        public String regular;
        public String small;
        public String thumb;
    }

    public static class Links {
        public String self;
        public String html;
        public String download;
    }

    public static class User {
        public String id;
        public String username;
        public String name;
        /**
         * self : https://api.unsplash.com/users/exampleuser
         * html : https://unsplash.com/exampleuser
         * photos : https://api.unsplash.com/users/exampleuser/photos
         * likes : https://api.unsplash.com/users/exampleuser/likes
         */

        public Links links;

        public static class Links {
            public String self;
            public String html;
            public String photos;
            public String likes;
        }
    }

    public static class CurrentUserCollections {
        public int id;
        public String title;
        public String published_at;
        public boolean curated;
        /**
         * id : xCmvrpzctaQ
         * width : 7360
         * height : 4912
         * color : #040C14
         * likes : 12
         * liked_by_user : false
         * user : {"id":"eUO1o53muso","username":"crew","name":"Crew","profile_image":{"small":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"},"links":{"self":"https://api.unsplash.com/users/crew","html":"http://unsplash.com/crew","photos":"https://api.unsplash.com/users/crew/photos","likes":"https://api.unsplash.com/users/crew/likes"}}
         * urls : {"raw":"https://images.unsplash.com/photo-1452457807411-4979b707c5be","full":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy","regular":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=1080&fit=max","small":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=400&fit=max","thumb":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=200&fit=max"}
         * categories : [{"id":6,"title":"People","photo_count":9844,"links":{"self":"https://api.unsplash.com/categories/6","photos":"https://api.unsplash.com/categories/6/photos"}}]
         * links : {"self":"https://api.unsplash.com/photos/xCmvrpzctaQ","html":"https://unsplash.com/photos/xCmvrpzctaQ","download":"https://unsplash.com/photos/xCmvrpzctaQ/download"}
         */

        public CoverPhoto cover_photo;
        /**
         * id : eUO1o53muso
         * username : crew
         * name : Crew
         * bio : Work with the best designers and developers without breaking the bank. Creators of Unsplash.
         * profile_image : {"small":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"}
         * links : {"self":"https://api.unsplash.com/users/crew","html":"https://unsplash.com/crew","photos":"https://api.unsplash.com/users/crew/photos","likes":"https://api.unsplash.com/users/crew/likes"}
         */

        public User user;
        /**
         * self : https://api.unsplash.com/collections/206
         * html : https://unsplash.com/collections/206
         * photos : https://api.unsplash.com/collections/206/photos
         */

        public Links links;

        public static class CoverPhoto {
            public String id;
            public int width;
            public int height;
            public String color;
            public int likes;
            public boolean liked_by_user;
            /**
             * id : eUO1o53muso
             * username : crew
             * name : Crew
             * profile_image : {"small":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"}
             * links : {"self":"https://api.unsplash.com/users/crew","html":"http://unsplash.com/crew","photos":"https://api.unsplash.com/users/crew/photos","likes":"https://api.unsplash.com/users/crew/likes"}
             */

            public User user;
            /**
             * raw : https://images.unsplash.com/photo-1452457807411-4979b707c5be
             * full : https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy
             * regular : https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=1080&fit=max
             * small : https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=400&fit=max
             * thumb : https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=200&fit=max
             */

            public Urls urls;
            /**
             * self : https://api.unsplash.com/photos/xCmvrpzctaQ
             * html : https://unsplash.com/photos/xCmvrpzctaQ
             * download : https://unsplash.com/photos/xCmvrpzctaQ/download
             */

            public Links links;
            /**
             * id : 6
             * title : People
             * photo_count : 9844
             * links : {"self":"https://api.unsplash.com/categories/6","photos":"https://api.unsplash.com/categories/6/photos"}
             */

            public List<Categories> categories;

            public static class User {
                public String id;
                public String username;
                public String name;
                /**
                 * small : https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32
                 * medium : https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64
                 * large : https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128
                 */

                public ProfileImage profile_image;
                /**
                 * self : https://api.unsplash.com/users/crew
                 * html : http://unsplash.com/crew
                 * photos : https://api.unsplash.com/users/crew/photos
                 * likes : https://api.unsplash.com/users/crew/likes
                 */

                public Links links;

                public static class ProfileImage {
                    public String small;
                    public String medium;
                    public String large;
                }

                public static class Links {
                    public String self;
                    public String html;
                    public String photos;
                    public String likes;
                }
            }

            public static class Urls {
                public String raw;
                public String full;
                public String regular;
                public String small;
                public String thumb;
            }

            public static class Links {
                public String self;
                public String html;
                public String download;
            }

            public static class Categories {
                public int id;
                public String title;
                public int photo_count;
                /**
                 * self : https://api.unsplash.com/categories/6
                 * photos : https://api.unsplash.com/categories/6/photos
                 */

                public Links links;

                public static class Links {
                    public String self;
                    public String photos;
                }
            }
        }

        public static class User {
            public String id;
            public String username;
            public String name;
            public String bio;
            /**
             * small : https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32
             * medium : https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64
             * large : https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128
             */

            public ProfileImage profile_image;
            /**
             * self : https://api.unsplash.com/users/crew
             * html : https://unsplash.com/crew
             * photos : https://api.unsplash.com/users/crew/photos
             * likes : https://api.unsplash.com/users/crew/likes
             */

            public Links links;

            public static class ProfileImage {
                public String small;
                public String medium;
                public String large;
            }

            public static class Links {
                public String self;
                public String html;
                public String photos;
                public String likes;
            }
        }

        public static class Links {
            public String self;
            public String html;
            public String photos;
        }
    }

    public static class Categories {
        public int id;
        public String title;
        public int photo_count;
        /**
         * self : https://api.unsplash.com/categories/4
         * photos : https://api.unsplash.com/categories/4/photos
         */

        public Links links;

        public static class Links {
            public String self;
            public String photos;
        }
    }
}
