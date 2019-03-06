package com.wangdaye.mysplash.common.network.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Story.
 * */

public class Story implements Parcelable {

    /**
     * title : Frozen Bubbles
     * description : One of the best parts of online communities is the ability to be inspired by others and challenged to try new projects. I saw friends posting photos of frozen bubbles on Instagram. So I decided to try it on a morning when it was 10 degrees outside. My hands almost froze off, but it was worth it.
     * image_url : https://images.unsplash.com/photo-1483748231602-246e5b99359e?ixlib=rb-0.3.5&q=80&fm=jpg&crop=fit&cs=tinysrgb&w=2400&fit=crop&blend64=aHR0cHM6Ly9pbWFnZXMudW5zcGxhc2guY29tL2xvZ29zL3doaXRlLnBuZz9mbT1wbmcmaD04MA&mark64=aHR0cHM6Ly9hc3NldHMuaW1naXgubmV0L350ZXh0P3R4dDY0PTRvQ2NUMjVsSUc5bUlIUm9aU0JpWlhOMElIQmhjblJ6SUc5bUlHOXViR2x1WlNCamIyMXRkVzVwZEdsbGN5QnBjeUIwYUdVZ1lXSnBiR2wwZVNCMGJ5QmlaU0JwYm5Od2FYSmxaQ0JpZVNCdmRHaGxjbk1nWVc1a0lHTm9ZV3hzWlc1blpXUWdkRzhnZEhKNUlHNWxkeUJ3Y205cVpXTjBjeTRnU1NCellYY2dabkpwWlc1a2N5QndiM04wYVc1bklIQm9iM1J2Y3lCdlppQm1jbTk2Wlc0Z1luVmlZbXhsY3lCdmJpQkpibk4wWVdkeVlXMHVJRk52SUVrZ1pHVmphV1JsWkNCMGJ5QjBjbmtnYVhRZ2IyNGdZU0J0YjNKdWFXNW5JSGRvWlc0Z2FYUWdkMkZ6SURFd0lHUmxaM0psWlhNZ2IzVjBjMmxrWlM0Z1RYa2dhR0Z1WkhNZ1lXeHRiM04wSUdaeWIzcGxJRzltWml3Z1luVjBJR2wwSUhkaGN5QjNiM0owYUNCcGRDN2lnSjBLNG9DVVFXRnliMjRnUW5WeVpHVnVDZyZ0eHRhbGlnbj1ib3R0b20mdHh0Y2xyPUZGRkZGRiZ0eHRmb250PUhlbHZldGljYS1Cb2xkJnR4dGxpZz0xJnR4dHBhZD0xMDAmdHh0c2hhZD02JnR4dHNpemU9NjAmdz0xNDExLjc2NDcwNTg4MjM1Mw&balph=60&by=100&bx=100&bm=normal&markalign=left%2Cbottom&exp=-12&s=515459b793313005b24c0340785a1799
     */

    public String title;
    public String description;
    public String image_url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.image_url);
    }

    public Story() {
    }

    protected Story(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.image_url = in.readString();
    }

    public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel source) {
            return new Story(source);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };
}
