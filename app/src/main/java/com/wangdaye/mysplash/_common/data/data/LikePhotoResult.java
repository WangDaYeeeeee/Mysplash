package com.wangdaye.mysplash._common.data.data;

/**
 * Like photo result.
 * */

public class LikePhotoResult {

    /**
     * id : LF8gK8-HGSg
     * width : 5245
     * height : 3497
     * color : #60544D
     * likes : 10
     * liked_by_user : true
     * urls : {"raw":"https://images.unsplash.com/1/type-away.jpg","full":"https://images.unsplash.com/1/type-away.jpg?q=80&fm=jpg","regular":"https://images.unsplash.com/1/type-away.jpg?q=80&fm=jpg&w=1080&fit=max","small":"https://images.unsplash.com/1/type-away.jpg?q=80&fm=jpg&w=400&fit=max","thumb":"https://images.unsplash.com/1/type-away.jpg?q=80&fm=jpg&w=200&fit=max"}
     * links : {"self":"http://api.unsplash.com/photos/LF8gK8-HGSg","html":"http://unsplash.com/photos/LF8gK8-HGSg","download":"http://unsplash.com/photos/LF8gK8-HGSg/download"}
     */

    public Photo photo;
    /**
     * id : 8VpB0GYJMZQ
     * username : williamnot
     * name : Thomas R.
     * links : {"self":"http://api.unsplash.com/users/williamnot","html":"http://api.unsplash.com/williamnot","photos":"http://api.unsplash.com/users/williamnot/photos","likes":"http://api.unsplash.com/users/williamnot/likes"}
     */

    public User user;

    public static class Photo {
        public String id;
        public int width;
        public int height;
        public String color;
        public int likes;
        public boolean liked_by_user;
        /**
         * raw : https://images.unsplash.com/1/type-away.jpg
         * full : https://images.unsplash.com/1/type-away.jpg?q=80&fm=jpg
         * regular : https://images.unsplash.com/1/type-away.jpg?q=80&fm=jpg&w=1080&fit=max
         * small : https://images.unsplash.com/1/type-away.jpg?q=80&fm=jpg&w=400&fit=max
         * thumb : https://images.unsplash.com/1/type-away.jpg?q=80&fm=jpg&w=200&fit=max
         */

        public Urls urls;
        /**
         * self : http://api.unsplash.com/photos/LF8gK8-HGSg
         * html : http://unsplash.com/photos/LF8gK8-HGSg
         * download : http://unsplash.com/photos/LF8gK8-HGSg/download
         */

        public Links links;

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
    }

    public static class User {
        public String id;
        public String username;
        public String name;
        /**
         * self : http://api.unsplash.com/users/williamnot
         * html : http://api.unsplash.com/williamnot
         * photos : http://api.unsplash.com/users/williamnot/photos
         * likes : http://api.unsplash.com/users/williamnot/likes
         */

        public Links links;

        public static class Links {
            public String self;
            public String html;
            public String photos;
            public String likes;
        }
    }
}
