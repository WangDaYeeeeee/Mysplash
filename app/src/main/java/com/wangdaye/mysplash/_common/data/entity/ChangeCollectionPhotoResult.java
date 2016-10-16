package com.wangdaye.mysplash._common.data.entity;

/**
 * Add photo to collection result.
 * */

public class ChangeCollectionPhotoResult {
    public Photo photo;
    public Collection collection;

    /**
     * id : eUO1o53muso
     * username : crew
     * name : Crew
     * bio : Work with the best designers and developers without breaking the bank. Creators of Unsplash.
     * profile_image : {"small":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"}
     * links : {"self":"https://api.unsplash.com/users/crew","html":"https://unsplash.com/crew","photos":"https://api.unsplash.com/users/crew/photos","likes":"https://api.unsplash.com/users/crew/likes","portfolio":"https://api.unsplash.com/users/crew/portfolio"}
     */

    public User user;
    /**
     * self : https://api.unsplash.com/collections/206
     * html : https://unsplash.com/collections/206/makers-cat-and-ben
     * photos : https://api.unsplash.com/collections/206/photos
     */

    public Links links;

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
         * portfolio : https://api.unsplash.com/users/crew/portfolio
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
            public String portfolio;
        }
    }

    public static class Links {
        public String self;
        public String html;
        public String photos;
    }
}
