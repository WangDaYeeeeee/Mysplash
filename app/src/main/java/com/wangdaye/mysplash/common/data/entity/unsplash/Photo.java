package com.wangdaye.mysplash.common.data.entity.unsplash;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Size;
import android.text.TextUtils;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.common.basic.Previewable;
import com.wangdaye.mysplash.common.utils.manager.SettingsOptionManager;

import java.util.List;

/**
 * Photo.
 * */

public class Photo
        implements Parcelable, Previewable {
    // data
    public boolean loadPhotoSuccess = false;
    public boolean hasFadedIn = false;
    public boolean settingLike = false;
    public boolean complete = false;

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
     * current_user_collections : [{"id":206,"title":"Makers: Cat and Ben","published_at":"2016-01-12T18:16:09-05:00","curated":false,"cover_photo":{"id":"xCmvrpzctaQ","width":7360,"height":4912,"color":"#040C14","likes":12,"liked_by_user":false,"user":{"id":"eUO1o53muso","username":"crew","name":"Crew","portfolio_url":"https://crew.co/","bio":"Work with the best designers and developers without breaking the bank.","location":"Montreal","total_likes":0,"total_photos":74,"total_collections":52,"profile_image":{"small":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"},"links":{"self":"https://api.unsplash.com/users/crew","html":"http://unsplash.com/crew","photos":"https://api.unsplash.com/users/crew/photos","likes":"https://api.unsplash.com/users/crew/likes","portfolio":"https://api.unsplash.com/users/crew/portfolio"}},"urls":{"raw":"https://images.unsplash.com/photo-1452457807411-4979b707c5be","full":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy","regular":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=1080&fit=max","small":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=400&fit=max","thumb":"https://images.unsplash.com/photo-1452457807411-4979b707c5be?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=200&fit=max"},"categories":[{"id":6,"title":"People","photo_count":9844,"links":{"self":"https://api.unsplash.com/categories/6","photos":"https://api.unsplash.com/categories/6/photos"}}],"links":{"self":"https://api.unsplash.com/photos/xCmvrpzctaQ","html":"https://unsplash.com/photos/xCmvrpzctaQ","download":"https://unsplash.com/photos/xCmvrpzctaQ/download","download_location":"https://api.unsplash.com/photos/xCmvrpzctaQ/download"}},"user":{"id":"eUO1o53muso","username":"crew","name":"Crew","portfolio_url":"https://crew.co/","bio":"Work with the best designers and developers without breaking the bank.","location":"Montreal","total_likes":0,"total_photos":74,"total_collections":52,"profile_image":{"small":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1441298102341-b7ba36fdc35c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"},"links":{"self":"https://api.unsplash.com/users/crew","html":"https://unsplash.com/crew","photos":"https://api.unsplash.com/users/crew/photos","likes":"https://api.unsplash.com/users/crew/likes","portfolio":"https://api.unsplash.com/users/crew/portfolio"}},"links":{"self":"https://api.unsplash.com/collections/206","html":"https://unsplash.com/collections/206","photos":"https://api.unsplash.com/collections/206/photos"}}]
     * urls : {"raw":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d","full":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg","regular":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=1080&fit=max","small":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=400&fit=max","thumb":"https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=200&fit=max"}
     * categories : [{"id":4,"title":"Nature","photo_count":24783,"links":{"self":"https://api.unsplash.com/categories/4","photos":"https://api.unsplash.com/categories/4/photos"}}]
     * links : {"self":"https://api.unsplash.com/photos/Dwu85P9SOIk","html":"https://unsplash.com/photos/Dwu85P9SOIk","download":"https://unsplash.com/photos/Dwu85P9SOIk/download","download_location":"https://api.unsplash.com/photos/Dwu85P9SOIk/download"}
     * user : {"id":"QPxL2MGqfrw","username":"exampleuser","name":"Joe Example","portfolio_url":"https://example.com/","bio":"Just an everyday Joe","location":"Montreal","total_likes":5,"total_photos":10,"total_collections":13,"links":{"self":"https://api.unsplash.com/users/exampleuser","html":"https://unsplash.com/exampleuser","photos":"https://api.unsplash.com/users/exampleuser/photos","likes":"https://api.unsplash.com/users/exampleuser/likes","portfolio":"https://api.unsplash.com/users/exampleuser/portfolio"}}
     */
    public String id;
    public String created_at;
    public int width;
    public int height;
    public String color;
    public int views;
    public int downloads;
    public int likes;
    public boolean liked_by_user;

    public String description;
    public Exif exif;
    public Location location;
    public PhotoUrls urls;
    public PhotoLinks links;
    public Story story;
    public Stats stats;
    public User user;
    public List<Collection> current_user_collections;
    public List<Category> categories;
    public List<Tag> tags;

    /**
     * total : 6
     * type : photographer
     * results : [{"id":"UvRMcIeXq9Y","created_at":"2015-06-01T21:27:15-04:00","width":4592,"height":3448,"color":"#E2E2DA","likes":1143,"liked_by_user":false,"user":{"id":"o25aSDn-4q0","username":"aaronburden","name":"Aaron Burden","first_name":"Aaron","last_name":"Burden","portfolio_url":"http://aaronburden.com/","bio":"Thanks for taking the time to check out my Unsplash! I hope you can find creative ways to utilize these images. You can follow my quest of capturing the beauty of creation on Instagram @aaronburden and Twitter @theaaronburden. ","location":"Michigan","total_likes":1270,"total_photos":267,"total_collections":36,"profile_image":{"small":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=7955f035cc38f1c6596c6027ffb51c89","medium":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=0216b31f8b0372b60dabdb7cee2615e2","large":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=f3f2f225990b367deb1109c65704d1aa"},"links":{"self":"https://api.unsplash.com/users/aaronburden","html":"http://unsplash.com/@aaronburden","photos":"https://api.unsplash.com/users/aaronburden/photos","likes":"https://api.unsplash.com/users/aaronburden/likes","portfolio":"https://api.unsplash.com/users/aaronburden/portfolio","following":"https://api.unsplash.com/users/aaronburden/following","followers":"https://api.unsplash.com/users/aaronburden/followers"}},"current_user_collections":[],"urls":{"raw":"https://images.unsplash.com/photo-1433208406127-d9e1a0a1f1aa","full":"https://images.unsplash.com/photo-1433208406127-d9e1a0a1f1aa?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&s=04009f38ac363e9fc1db0a0e67552159","regular":"https://images.unsplash.com/photo-1433208406127-d9e1a0a1f1aa?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=5f1778d2da91df60e980ed33f07d3228","small":"https://images.unsplash.com/photo-1433208406127-d9e1a0a1f1aa?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=ead239b002096e5a15302f72ad301dc9","thumb":"https://images.unsplash.com/photo-1433208406127-d9e1a0a1f1aa?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=a4a446eedc3a724a683e9fdbd67487d1"},"categories":[{"id":4,"title":"Nature","photo_count":54184,"links":{"self":"https://api.unsplash.com/categories/4","photos":"https://api.unsplash.com/categories/4/photos"}}],"links":{"self":"https://api.unsplash.com/photos/UvRMcIeXq9Y","html":"http://unsplash.com/photos/UvRMcIeXq9Y","download":"http://unsplash.com/photos/UvRMcIeXq9Y/download","download_location":"https://api.unsplash.com/photos/UvRMcIeXq9Y/download"}},{"id":"ob6O_xd67O0","created_at":"2015-07-14T19:20:06-04:00","width":4592,"height":3448,"color":"#696B5A","likes":734,"liked_by_user":false,"user":{"id":"o25aSDn-4q0","username":"aaronburden","name":"Aaron Burden","first_name":"Aaron","last_name":"Burden","portfolio_url":"http://aaronburden.com/","bio":"Thanks for taking the time to check out my Unsplash! I hope you can find creative ways to utilize these images. You can follow my quest of capturing the beauty of creation on Instagram @aaronburden and Twitter @theaaronburden. ","location":"Michigan","total_likes":1270,"total_photos":267,"total_collections":36,"profile_image":{"small":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=7955f035cc38f1c6596c6027ffb51c89","medium":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=0216b31f8b0372b60dabdb7cee2615e2","large":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=f3f2f225990b367deb1109c65704d1aa"},"links":{"self":"https://api.unsplash.com/users/aaronburden","html":"http://unsplash.com/@aaronburden","photos":"https://api.unsplash.com/users/aaronburden/photos","likes":"https://api.unsplash.com/users/aaronburden/likes","portfolio":"https://api.unsplash.com/users/aaronburden/portfolio","following":"https://api.unsplash.com/users/aaronburden/following","followers":"https://api.unsplash.com/users/aaronburden/followers"}},"current_user_collections":[],"urls":{"raw":"https://images.unsplash.com/photo-1436915947297-3a94186c8133","full":"https://images.unsplash.com/photo-1436915947297-3a94186c8133?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&s=84963d599ab25dbeba09a57575066d7a","regular":"https://images.unsplash.com/photo-1436915947297-3a94186c8133?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=df1130c57c024c684749ce7eba4006e7","small":"https://images.unsplash.com/photo-1436915947297-3a94186c8133?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=d79fd47beb9a712f0dca442d90c3e3ee","thumb":"https://images.unsplash.com/photo-1436915947297-3a94186c8133?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=1ec127919c3dcbfbe1cbe4a622d9c941"},"categories":[{"id":4,"title":"Nature","photo_count":54184,"links":{"self":"https://api.unsplash.com/categories/4","photos":"https://api.unsplash.com/categories/4/photos"}},{"id":8,"title":"Objects","photo_count":13840,"links":{"self":"https://api.unsplash.com/categories/8","photos":"https://api.unsplash.com/categories/8/photos"}}],"links":{"self":"https://api.unsplash.com/photos/ob6O_xd67O0","html":"http://unsplash.com/photos/ob6O_xd67O0","download":"http://unsplash.com/photos/ob6O_xd67O0/download","download_location":"https://api.unsplash.com/photos/ob6O_xd67O0/download"}},{"id":"df47UDrfi8I","created_at":"2015-06-11T22:51:53-04:00","width":4592,"height":3448,"color":"#6A6B63","likes":474,"liked_by_user":false,"user":{"id":"o25aSDn-4q0","username":"aaronburden","name":"Aaron Burden","first_name":"Aaron","last_name":"Burden","portfolio_url":"http://aaronburden.com/","bio":"Thanks for taking the time to check out my Unsplash! I hope you can find creative ways to utilize these images. You can follow my quest of capturing the beauty of creation on Instagram @aaronburden and Twitter @theaaronburden. ","location":"Michigan","total_likes":1270,"total_photos":267,"total_collections":36,"profile_image":{"small":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=7955f035cc38f1c6596c6027ffb51c89","medium":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=0216b31f8b0372b60dabdb7cee2615e2","large":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=f3f2f225990b367deb1109c65704d1aa"},"links":{"self":"https://api.unsplash.com/users/aaronburden","html":"http://unsplash.com/@aaronburden","photos":"https://api.unsplash.com/users/aaronburden/photos","likes":"https://api.unsplash.com/users/aaronburden/likes","portfolio":"https://api.unsplash.com/users/aaronburden/portfolio","following":"https://api.unsplash.com/users/aaronburden/following","followers":"https://api.unsplash.com/users/aaronburden/followers"}},"current_user_collections":[],"urls":{"raw":"https://images.unsplash.com/photo-1434077471918-4ea96e6e45d5","full":"https://images.unsplash.com/photo-1434077471918-4ea96e6e45d5?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&s=b8ea6eabbe749bfe417f106c69e1f3d2","regular":"https://images.unsplash.com/photo-1434077471918-4ea96e6e45d5?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=5ad5a9691aacdc7160257e895bebf3e0","small":"https://images.unsplash.com/photo-1434077471918-4ea96e6e45d5?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=0a311535ca5400076f6e81f053099e05","thumb":"https://images.unsplash.com/photo-1434077471918-4ea96e6e45d5?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=19e2ba6caea16bc3e3caba37ac7be17e"},"categories":[{"id":4,"title":"Nature","photo_count":54184,"links":{"self":"https://api.unsplash.com/categories/4","photos":"https://api.unsplash.com/categories/4/photos"}},{"id":8,"title":"Objects","photo_count":13840,"links":{"self":"https://api.unsplash.com/categories/8","photos":"https://api.unsplash.com/categories/8/photos"}}],"links":{"self":"https://api.unsplash.com/photos/df47UDrfi8I","html":"http://unsplash.com/photos/df47UDrfi8I","download":"http://unsplash.com/photos/df47UDrfi8I/download","download_location":"https://api.unsplash.com/photos/df47UDrfi8I/download"}},{"id":"1zR3WNSTnvY","created_at":"2016-01-25T14:11:27-05:00","width":4356,"height":3083,"color":"#F4DE84","likes":415,"liked_by_user":false,"user":{"id":"o25aSDn-4q0","username":"aaronburden","name":"Aaron Burden","first_name":"Aaron","last_name":"Burden","portfolio_url":"http://aaronburden.com/","bio":"Thanks for taking the time to check out my Unsplash! I hope you can find creative ways to utilize these images. You can follow my quest of capturing the beauty of creation on Instagram @aaronburden and Twitter @theaaronburden. ","location":"Michigan","total_likes":1270,"total_photos":267,"total_collections":36,"profile_image":{"small":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=7955f035cc38f1c6596c6027ffb51c89","medium":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=0216b31f8b0372b60dabdb7cee2615e2","large":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=f3f2f225990b367deb1109c65704d1aa"},"links":{"self":"https://api.unsplash.com/users/aaronburden","html":"http://unsplash.com/@aaronburden","photos":"https://api.unsplash.com/users/aaronburden/photos","likes":"https://api.unsplash.com/users/aaronburden/likes","portfolio":"https://api.unsplash.com/users/aaronburden/portfolio","following":"https://api.unsplash.com/users/aaronburden/following","followers":"https://api.unsplash.com/users/aaronburden/followers"}},"current_user_collections":[],"urls":{"raw":"https://images.unsplash.com/photo-1453749024858-4bca89bd9edc","full":"https://images.unsplash.com/photo-1453749024858-4bca89bd9edc?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&s=9be44b0df6cf3cf0d66c103b63f8307f","regular":"https://images.unsplash.com/photo-1453749024858-4bca89bd9edc?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=e5cd6b85b5ae7758c20f7300b4bd7c71","small":"https://images.unsplash.com/photo-1453749024858-4bca89bd9edc?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=536d6f7d80f98008a7ff4dc5e246aebf","thumb":"https://images.unsplash.com/photo-1453749024858-4bca89bd9edc?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=c57c8fdd4e0acb997dc22fc0666f996f"},"categories":[{"id":8,"title":"Objects","photo_count":13840,"links":{"self":"https://api.unsplash.com/categories/8","photos":"https://api.unsplash.com/categories/8/photos"}}],"links":{"self":"https://api.unsplash.com/photos/1zR3WNSTnvY","html":"http://unsplash.com/photos/1zR3WNSTnvY","download":"http://unsplash.com/photos/1zR3WNSTnvY/download","download_location":"https://api.unsplash.com/photos/1zR3WNSTnvY/download"}},{"id":"b9drVB7xIOI","created_at":"2015-10-10T11:54:37-04:00","width":3024,"height":4032,"color":"#686154","likes":517,"liked_by_user":false,"user":{"id":"o25aSDn-4q0","username":"aaronburden","name":"Aaron Burden","first_name":"Aaron","last_name":"Burden","portfolio_url":"http://aaronburden.com/","bio":"Thanks for taking the time to check out my Unsplash! I hope you can find creative ways to utilize these images. You can follow my quest of capturing the beauty of creation on Instagram @aaronburden and Twitter @theaaronburden. ","location":"Michigan","total_likes":1270,"total_photos":267,"total_collections":36,"profile_image":{"small":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=7955f035cc38f1c6596c6027ffb51c89","medium":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=0216b31f8b0372b60dabdb7cee2615e2","large":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=f3f2f225990b367deb1109c65704d1aa"},"links":{"self":"https://api.unsplash.com/users/aaronburden","html":"http://unsplash.com/@aaronburden","photos":"https://api.unsplash.com/users/aaronburden/photos","likes":"https://api.unsplash.com/users/aaronburden/likes","portfolio":"https://api.unsplash.com/users/aaronburden/portfolio","following":"https://api.unsplash.com/users/aaronburden/following","followers":"https://api.unsplash.com/users/aaronburden/followers"}},"current_user_collections":[],"urls":{"raw":"https://images.unsplash.com/photo-1444492417251-9c84a5fa18e0","full":"https://images.unsplash.com/photo-1444492417251-9c84a5fa18e0?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&s=e3465d0d5494b445106f29ab9a589158","regular":"https://images.unsplash.com/photo-1444492417251-9c84a5fa18e0?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=3b316ee4aad9cd8c7dce9d2da6ec3c00","small":"https://images.unsplash.com/photo-1444492417251-9c84a5fa18e0?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=900fa491d0450e7d2af4af6d0cb3859a","thumb":"https://images.unsplash.com/photo-1444492417251-9c84a5fa18e0?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=eea8f6b121617a8b595de5d23e5068b5"},"categories":[{"id":4,"title":"Nature","photo_count":54184,"links":{"self":"https://api.unsplash.com/categories/4","photos":"https://api.unsplash.com/categories/4/photos"}}],"links":{"self":"https://api.unsplash.com/photos/b9drVB7xIOI","html":"http://unsplash.com/photos/b9drVB7xIOI","download":"http://unsplash.com/photos/b9drVB7xIOI/download","download_location":"https://api.unsplash.com/photos/b9drVB7xIOI/download"}},{"id":"y02jEX_B0O0","created_at":"2016-02-13T14:11:23-05:00","width":4592,"height":3448,"color":"#E5E4E4","likes":400,"liked_by_user":false,"user":{"id":"o25aSDn-4q0","username":"aaronburden","name":"Aaron Burden","first_name":"Aaron","last_name":"Burden","portfolio_url":"http://aaronburden.com/","bio":"Thanks for taking the time to check out my Unsplash! I hope you can find creative ways to utilize these images. You can follow my quest of capturing the beauty of creation on Instagram @aaronburden and Twitter @theaaronburden. ","location":"Michigan","total_likes":1270,"total_photos":267,"total_collections":36,"profile_image":{"small":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=7955f035cc38f1c6596c6027ffb51c89","medium":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=0216b31f8b0372b60dabdb7cee2615e2","large":"https://images.unsplash.com/profile-1456513912833-f86f468b21e2?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=f3f2f225990b367deb1109c65704d1aa"},"links":{"self":"https://api.unsplash.com/users/aaronburden","html":"http://unsplash.com/@aaronburden","photos":"https://api.unsplash.com/users/aaronburden/photos","likes":"https://api.unsplash.com/users/aaronburden/likes","portfolio":"https://api.unsplash.com/users/aaronburden/portfolio","following":"https://api.unsplash.com/users/aaronburden/following","followers":"https://api.unsplash.com/users/aaronburden/followers"}},"current_user_collections":[],"urls":{"raw":"https://images.unsplash.com/photo-1455390582262-044cdead277a","full":"https://images.unsplash.com/photo-1455390582262-044cdead277a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&s=6f42099f0a47a44bbdb709b3aaa5e812","regular":"https://images.unsplash.com/photo-1455390582262-044cdead277a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=27f031c03fe78fcae520fc3478267b7f","small":"https://images.unsplash.com/photo-1455390582262-044cdead277a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=5a0d09c2f5f5d16c998d43d820e44be7","thumb":"https://images.unsplash.com/photo-1455390582262-044cdead277a?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=d257ac956f92cb22affdae541e7ed958"},"categories":[{"id":8,"title":"Objects","photo_count":13840,"links":{"self":"https://api.unsplash.com/categories/8","photos":"https://api.unsplash.com/categories/8/photos"}}],"links":{"self":"https://api.unsplash.com/photos/y02jEX_B0O0","html":"http://unsplash.com/photos/y02jEX_B0O0","download":"http://unsplash.com/photos/y02jEX_B0O0/download","download_location":"https://api.unsplash.com/photos/y02jEX_B0O0/download"}}]
     */
    public RelatedPhotos related_photos;

    /**
     * total : 39
     * type : collected
     * results : [{"id":190053,"title":"Winter Warmth","description":null,"published_at":"2016-04-12T14:58:48-04:00","curated":false,"featured":false,"total_photos":91,"private":false,"share_key":"cdd59909bec7ba5acd3510fad04bcde9","cover_photo":{"id":"toxlLueLNDs","created_at":"2017-01-13T04:47:07-05:00","width":3072,"height":4174,"color":"#C6996D","likes":45,"liked_by_user":false,"user":{"id":"-Jcq3mWFbcs","username":"jillheyer","name":"Jill Heyer","first_name":"Jill","last_name":"Heyer","portfolio_url":"http://www.jillheyer.com","bio":"Web Designer @ Art Director","location":"Hamburg, Germany","total_likes":837,"total_photos":19,"total_collections":7,"profile_image":{"small":"https://images.unsplash.com/profile-1484238323837-cd040c916e2c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=d9ad14ebeda786c6b431b47f46f538c2","medium":"https://images.unsplash.com/profile-1484238323837-cd040c916e2c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=48e4b91473852248d152279f1afc4696","large":"https://images.unsplash.com/profile-1484238323837-cd040c916e2c?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=ba19f3aebc7fd62677d970f7a8ae5b3c"},"links":{"self":"https://api.unsplash.com/users/jillheyer","html":"http://unsplash.com/@jillheyer","photos":"https://api.unsplash.com/users/jillheyer/photos","likes":"https://api.unsplash.com/users/jillheyer/likes","portfolio":"https://api.unsplash.com/users/jillheyer/portfolio","following":"https://api.unsplash.com/users/jillheyer/following","followers":"https://api.unsplash.com/users/jillheyer/followers"}},"urls":{"raw":"https://images.unsplash.com/photo-1484300681262-5cca666b0954","full":"https://images.unsplash.com/photo-1484300681262-5cca666b0954?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&s=edd82fb6fb2eb8dea9d68c6a6bcb5f81","regular":"https://images.unsplash.com/photo-1484300681262-5cca666b0954?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=4392da122ccf56a14937e51d496a1023","small":"https://images.unsplash.com/photo-1484300681262-5cca666b0954?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=7941da072e3c5ec46f0c01e6a2cb97bb","thumb":"https://images.unsplash.com/photo-1484300681262-5cca666b0954?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=7cadf47ecfaf4045e129a470d7305dff"},"categories":[],"links":{"self":"https://api.unsplash.com/photos/toxlLueLNDs","html":"http://unsplash.com/photos/toxlLueLNDs","download":"http://unsplash.com/photos/toxlLueLNDs/download","download_location":"https://api.unsplash.com/photos/toxlLueLNDs/download"}},"user":{"id":"TqtLH8sI2cI","username":"christi_lee","name":"Christi Osterday","first_name":"Christi","last_name":"Osterday","portfolio_url":null,"bio":"","location":null,"total_likes":19,"total_photos":1,"total_collections":9,"profile_image":{"small":"https://images.unsplash.com/profile-1460478094470-ea9da01a6702?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=eaca9f39f445db59b89aab3fba4d7778","medium":"https://images.unsplash.com/profile-1460478094470-ea9da01a6702?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=40f408d2cb976a4c78a9799f66b07cd4","large":"https://images.unsplash.com/profile-1460478094470-ea9da01a6702?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=40f77d96148e16978dd6d95b8ea26547"},"links":{"self":"https://api.unsplash.com/users/christi_lee","html":"http://unsplash.com/@christi_lee","photos":"https://api.unsplash.com/users/christi_lee/photos","likes":"https://api.unsplash.com/users/christi_lee/likes","portfolio":"https://api.unsplash.com/users/christi_lee/portfolio","following":"https://api.unsplash.com/users/christi_lee/following","followers":"https://api.unsplash.com/users/christi_lee/followers"}},"links":{"self":"https://api.unsplash.com/collections/190053","html":"http://unsplash.com/collections/190053/winter-warmth","photos":"https://api.unsplash.com/collections/190053/photos","related":"https://api.unsplash.com/collections/190053/related"}},{"id":530467,"title":"Nature","description":null,"published_at":"2017-02-01T11:44:17-05:00","curated":false,"featured":false,"total_photos":41,"private":false,"share_key":"e572260a8fd676e1ae219f1a9c628d7e","cover_photo":{"id":"EfhCUc_fjrU","created_at":"2016-08-22T16:54:43-04:00","width":5261,"height":3507,"color":"#3B4C09","likes":471,"liked_by_user":false,"user":{"id":"XWcxMCIv52w","username":"andsmall","name":"Andrew Small","first_name":"Andrew","last_name":"Small","portfolio_url":"https://andrewsmall.myportfolio.com/","bio":"I say I'm a photographer but people tell me my pictures are beautiful so you can call me a beautiful image maker.","location":"Brighton","total_likes":41,"total_photos":15,"total_collections":0,"profile_image":{"small":"https://images.unsplash.com/profile-fb-1471711425-37e3bae51690.jpg?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=e15da1b07f33e949004321cbeea8d98e","medium":"https://images.unsplash.com/profile-fb-1471711425-37e3bae51690.jpg?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=b011e717f64b35610682269b3adcb261","large":"https://images.unsplash.com/profile-fb-1471711425-37e3bae51690.jpg?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=950c35ffbfd51bff177e8f50c5dec6b8"},"links":{"self":"https://api.unsplash.com/users/andsmall","html":"http://unsplash.com/@andsmall","photos":"https://api.unsplash.com/users/andsmall/photos","likes":"https://api.unsplash.com/users/andsmall/likes","portfolio":"https://api.unsplash.com/users/andsmall/portfolio","following":"https://api.unsplash.com/users/andsmall/following","followers":"https://api.unsplash.com/users/andsmall/followers"}},"urls":{"raw":"https://images.unsplash.com/photo-1471899236350-e3016bf1e69e","full":"https://images.unsplash.com/photo-1471899236350-e3016bf1e69e?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&s=4cb6bfc9135923873e5f8f6330301b28","regular":"https://images.unsplash.com/photo-1471899236350-e3016bf1e69e?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=3fcb45c45be2d7e43046bec15b1339fd","small":"https://images.unsplash.com/photo-1471899236350-e3016bf1e69e?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=16cec822378b0cddc3dc8d9537120492","thumb":"https://images.unsplash.com/photo-1471899236350-e3016bf1e69e?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=3d9b8e4e130c547f04ff6fc427657d68"},"categories":[],"links":{"self":"https://api.unsplash.com/photos/EfhCUc_fjrU","html":"http://unsplash.com/photos/EfhCUc_fjrU","download":"http://unsplash.com/photos/EfhCUc_fjrU/download","download_location":"https://api.unsplash.com/photos/EfhCUc_fjrU/download"}},"user":{"id":"dq73fpXUyDg","username":"lamodestartista","name":"Victoria Rivera","first_name":"Victoria","last_name":"Rivera","portfolio_url":null,"bio":"","location":null,"total_likes":166,"total_photos":0,"total_collections":7,"profile_image":{"small":"https://images.unsplash.com/profile-1486013733920-11308025cc59?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=33e1368cb9f8fbaeaf6e24dbb8500c96","medium":"https://images.unsplash.com/profile-1486013733920-11308025cc59?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=b442c5a29c5e145fbfc22d1b36bfe086","large":"https://images.unsplash.com/profile-1486013733920-11308025cc59?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=740b0742997d7ff11c23d136b1eb50b1"},"links":{"self":"https://api.unsplash.com/users/lamodestartista","html":"http://unsplash.com/@lamodestartista","photos":"https://api.unsplash.com/users/lamodestartista/photos","likes":"https://api.unsplash.com/users/lamodestartista/likes","portfolio":"https://api.unsplash.com/users/lamodestartista/portfolio","following":"https://api.unsplash.com/users/lamodestartista/following","followers":"https://api.unsplash.com/users/lamodestartista/followers"}},"links":{"self":"https://api.unsplash.com/collections/530467","html":"http://unsplash.com/collections/530467/nature","photos":"https://api.unsplash.com/collections/530467/photos","related":"https://api.unsplash.com/collections/530467/related"}},{"id":187159,"title":"snow village","description":null,"published_at":"2016-04-10T02:40:13-04:00","curated":false,"featured":true,"total_photos":150,"private":false,"share_key":"c4171c4429db6cf291e2947ceb96825f","cover_photo":{"id":"yIm6-EI2zaE","created_at":"2017-02-06T19:23:45-05:00","width":5488,"height":3639,"color":"#FEFCFA","likes":103,"liked_by_user":false,"user":{"id":"7MbBDfZqDOY","username":"wflwong","name":"Warren Wong","first_name":"Warren","last_name":"Wong","portfolio_url":"https://warrenflw.carbonmade.com/","bio":"","location":"Mississauga","total_likes":254,"total_photos":18,"total_collections":1,"profile_image":{"small":"https://images.unsplash.com/profile-1486500861697-f3b10421fc33?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=0ca54d8ef4713012691e26f9adbc89f4","medium":"https://images.unsplash.com/profile-1486500861697-f3b10421fc33?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=2ef8648ccbc2cb0852efbc4f411678fe","large":"https://images.unsplash.com/profile-1486500861697-f3b10421fc33?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=9af54403370549cda43cbcf9962e1b8a"},"links":{"self":"https://api.unsplash.com/users/wflwong","html":"http://unsplash.com/@wflwong","photos":"https://api.unsplash.com/users/wflwong/photos","likes":"https://api.unsplash.com/users/wflwong/likes","portfolio":"https://api.unsplash.com/users/wflwong/portfolio","following":"https://api.unsplash.com/users/wflwong/following","followers":"https://api.unsplash.com/users/wflwong/followers"}},"urls":{"raw":"https://images.unsplash.com/photo-1486426949255-2fb105c7e5af","full":"https://images.unsplash.com/photo-1486426949255-2fb105c7e5af?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&s=7dea700ffa6d94d0c1f2b37291482b4d","regular":"https://images.unsplash.com/photo-1486426949255-2fb105c7e5af?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=1629823914f9ea35334798b597539957","small":"https://images.unsplash.com/photo-1486426949255-2fb105c7e5af?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=59c9d2265e5cb9a59f64b9c91a3b6506","thumb":"https://images.unsplash.com/photo-1486426949255-2fb105c7e5af?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=05d5f786fa3005353d65eb95c85db1d3"},"categories":[],"links":{"self":"https://api.unsplash.com/photos/yIm6-EI2zaE","html":"http://unsplash.com/photos/yIm6-EI2zaE","download":"http://unsplash.com/photos/yIm6-EI2zaE/download","download_location":"https://api.unsplash.com/photos/yIm6-EI2zaE/download"}},"user":{"id":"VYBmNyjbVRg","username":"lovechip","name":"sieun yoo","first_name":"sieun","last_name":"yoo","portfolio_url":null,"bio":"","location":null,"total_likes":3986,"total_photos":0,"total_collections":15,"profile_image":{"small":"https://images.unsplash.com/profile-fb-1442995578-74a5fcfd1706.jpg?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=40d09b9f6df26ccda9299e0fe5387748","medium":"https://images.unsplash.com/profile-fb-1442995578-74a5fcfd1706.jpg?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=37fefef9c186b4837df923ed8c20bef2","large":"https://images.unsplash.com/profile-fb-1442995578-74a5fcfd1706.jpg?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=85cf5235fca1a55c85a408dc47b5fb8e"},"links":{"self":"https://api.unsplash.com/users/lovechip","html":"http://unsplash.com/@lovechip","photos":"https://api.unsplash.com/users/lovechip/photos","likes":"https://api.unsplash.com/users/lovechip/likes","portfolio":"https://api.unsplash.com/users/lovechip/portfolio","following":"https://api.unsplash.com/users/lovechip/following","followers":"https://api.unsplash.com/users/lovechip/followers"}},"links":{"self":"https://api.unsplash.com/collections/187159","html":"http://unsplash.com/collections/187159/snow-village","photos":"https://api.unsplash.com/collections/187159/photos","related":"https://api.unsplash.com/collections/187159/related"}}]
     */
    public RelatedCollections related_collections;

    public static class RelatedPhotos implements Parcelable {
        public int total;
        public String type;
        public List<Photo> results;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.total);
            dest.writeString(this.type);
            dest.writeTypedList(this.results);
        }

        public RelatedPhotos() {
        }

        protected RelatedPhotos(Parcel in) {
            this.total = in.readInt();
            this.type = in.readString();
            this.results = in.createTypedArrayList(Photo.CREATOR);
        }

        public static final Creator<RelatedPhotos> CREATOR = new Creator<RelatedPhotos>() {
            @Override
            public RelatedPhotos createFromParcel(Parcel source) {
                return new RelatedPhotos(source);
            }

            @Override
            public RelatedPhotos[] newArray(int size) {
                return new RelatedPhotos[size];
            }
        };
    }

    public static class RelatedCollections implements Parcelable {
        public int total;
        public String type;
        public List<Collection> results;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.total);
            dest.writeString(this.type);
            dest.writeTypedList(this.results);
        }

        public RelatedCollections() {
        }

        protected RelatedCollections(Parcel in) {
            this.total = in.readInt();
            this.type = in.readString();
            this.results = in.createTypedArrayList(Collection.CREATOR);
        }

        public static final Creator<RelatedCollections> CREATOR = new Creator<RelatedCollections>() {
            @Override
            public RelatedCollections createFromParcel(Parcel source) {
                return new RelatedCollections(source);
            }

            @Override
            public RelatedCollections[] newArray(int size) {
                return new RelatedCollections[size];
            }
        };
    }

    // data.

    public int getRegularWidth() {
        String parameter = Uri.parse(urls.regular).getQueryParameter("w");
        if (TextUtils.isEmpty(parameter)) {
            return 1080;
        }
        try {
            int w = Integer.parseInt(parameter);
            return w == 0 ? 1080 : w;
        } catch (Exception e) {
            return 1080;
        }
    }

    public int getRegularHeight() {
        return (int) (1.0 * height * getRegularWidth() / width);
    }

    public String getWallpaperSizeUrl(Context context) {
        double scaleRatio = 0.7
                * Math.max(
                context.getResources().getDisplayMetrics().widthPixels,
                context.getResources().getDisplayMetrics().heightPixels)
                / Math.min(width, height);
        int w = (int) (scaleRatio * width);
        int h = (int) (scaleRatio * height);
        return urls.raw + "?q=50&fm=jpg&w=" + w + "&h=" + h + "&fit=crop";
    }

    @Size(2)
    public int[] getWallpaperSize(Context context) {
        double scaleRatio = 0.7
                * Math.max(
                context.getResources().getDisplayMetrics().widthPixels,
                context.getResources().getDisplayMetrics().heightPixels)
                / Math.min(width, height);
        return new int[] {(int) (scaleRatio * width), (int) (scaleRatio * height)};
    }

    public String getRegularSizeUrl(Context context) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        float screenRatio = (float) (1.0 * screenWidth / screenHeight);
        float imageRatio = (float) (1.0 * width / height);

        if (imageRatio > screenRatio) {
            return urls.raw
                    + "?q=80&fm=jpg&h="
                    + (int) (Math.min(screenHeight * 0.5, height))
                    + "&fit=max";
        } else {
            return urls.raw
                    + "?q=80&fm=jpg&w="
                    + (int) (Math.min(screenWidth * 0.5, width))
                    + "&fit=max";
        }
    }

    // parcel.

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.loadPhotoSuccess ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasFadedIn ? (byte) 1 : (byte) 0);
        dest.writeByte(this.settingLike ? (byte) 1 : (byte) 0);
        dest.writeByte(this.complete ? (byte) 1 : (byte) 0);
        dest.writeString(this.id);
        dest.writeString(this.created_at);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.color);
        dest.writeInt(this.views);
        dest.writeInt(this.downloads);
        dest.writeInt(this.likes);
        dest.writeByte(this.liked_by_user ? (byte) 1 : (byte) 0);
        dest.writeString(this.description);
        dest.writeParcelable(this.exif, flags);
        dest.writeParcelable(this.location, flags);
        dest.writeParcelable(this.urls, flags);
        dest.writeParcelable(this.links, flags);
        dest.writeParcelable(this.story, flags);
        dest.writeParcelable(this.stats, flags);
        dest.writeParcelable(this.user, flags);
        dest.writeTypedList(this.current_user_collections);
        dest.writeTypedList(this.categories);
        dest.writeTypedList(this.tags);
        dest.writeParcelable(this.related_photos, flags);
        dest.writeParcelable(this.related_collections, flags);
    }

    public Photo() {
    }

    protected Photo(Parcel in) {
        this.loadPhotoSuccess = in.readByte() != 0;
        this.hasFadedIn = in.readByte() != 0;
        this.settingLike = in.readByte() != 0;
        this.complete = in.readByte() != 0;
        this.id = in.readString();
        this.created_at = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.color = in.readString();
        this.views = in.readInt();
        this.downloads = in.readInt();
        this.likes = in.readInt();
        this.liked_by_user = in.readByte() != 0;
        this.description = in.readString();
        this.exif = in.readParcelable(Exif.class.getClassLoader());
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.urls = in.readParcelable(PhotoUrls.class.getClassLoader());
        this.links = in.readParcelable(PhotoLinks.class.getClassLoader());
        this.story = in.readParcelable(Story.class.getClassLoader());
        this.stats = in.readParcelable(Stats.class.getClassLoader());
        this.user = in.readParcelable(User.class.getClassLoader());
        this.current_user_collections = in.createTypedArrayList(Collection.CREATOR);
        this.categories = in.createTypedArrayList(Category.CREATOR);
        this.tags = in.createTypedArrayList(Tag.CREATOR);
        this.related_photos = in.readParcelable(RelatedPhotos.class.getClassLoader());
        this.related_collections = in.readParcelable(RelatedCollections.class.getClassLoader());
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    // interface.

    @Override
    public String getRegularUrl() {
        return urls.regular;
    }

    @Override
    public String getFullUrl() {
        return urls.full;
    }

    @Override
    public String getDownloadUrl() {
        if (SettingsOptionManager.getInstance(Mysplash.getInstance())
                .getDownloadScale().equals("compact")) {
            return urls.full;
        } else {
            return urls.raw;
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Update load information for photo.
     *
     * @return Return true when load information has been changed. Otherwise return false.
     * */
    public boolean updateLoadInformation(Photo photo) {
        this.loadPhotoSuccess = photo.loadPhotoSuccess;
        if (this.hasFadedIn != photo.hasFadedIn) {
            this.hasFadedIn = photo.hasFadedIn;
            return true;
        } else {
            return false;
        }
    }
}
