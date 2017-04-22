package com.wangdaye.mysplash.common.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.data.entity.unsplash.ActionObject;
import com.wangdaye.mysplash.common.data.entity.unsplash.NotificationResult;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.ui.widget.CircleImageView;
import com.wangdaye.mysplash.common.ui.widget.freedomSizeView.FreedomImageView;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.common.utils.manager.AuthManager;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Notification adapter.
 * 
 * Adapter for {@link RecyclerView} to show notifications.
 * 
 * */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context a;

    class ViewHolder extends RecyclerView.ViewHolder {
        // widget
        @BindView(R.id.item_notification_background)
        RelativeLayout background;

        @BindView(R.id.item_notification_avatar)
        CircleImageView avatar;

        @BindView(R.id.item_notification_verbIcon)
        ImageView verbIcon;

        @BindView(R.id.item_notification_imageContainer)
        RelativeLayout imageContainer;

        @BindView(R.id.item_notification_image)
        FreedomImageView image;

        @BindView(R.id.item_notification_title)
        TextView title;

        @BindView(R.id.item_notification_subtitle)
        TextView subtitle;

        @BindView(R.id.item_notification_time)
        TextView time;

        public ViewHolder(View itemView, int position) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (hasPhoto(position)) {
                image.setSize(
                        getNotification(position).objects.get(0).castToPhoto().width,
                        getNotification(position).objects.get(0).castToPhoto().height);
            }
            DisplayUtils.setTypeface(itemView.getContext(), subtitle);
        }

        void onBindView(final int position) {
            title.setText(getNotification(position).actors.get(0).name);
            bindAvatar(position);
            bindPhoto(position);
            bindTime(position);
            bindVerb(position);

            if (AuthManager.getInstance()
                    .getNotificationManager()
                    .isUnseenNotification(getNotification(position))) {
                title.setAlpha(1f);
                subtitle.setAlpha(1f);
                time.setAlpha(1f);
            } else {
                title.setAlpha(0.5f);
                subtitle.setAlpha(0.5f);
                time.setAlpha(0.5f);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                avatar.setTransitionName(
                        getNotification(position).actors.get(0).username + position + "-avatar");
                if (hasPhoto(position)) {
                    image.setTransitionName(
                            getNotification(position).objects.get(0).castToPhoto().id + position + "-image");
                }
            }
        }

        public void onRecycled() {
            ImageHelper.releaseImageView(image);
            ImageHelper.releaseImageView(avatar);
        }

        private void bindAvatar(final int position) {
            ImageHelper.loadAvatar(a, avatar, getNotification(position).actors.get(0), new ImageHelper.OnLoadImageListener() {
                @Override
                public void onLoadSucceed() {
                    if (!getNotification(position).actors.get(0).hasFadedIn) {
                        getNotification(position).actors.get(0).hasFadedIn = true;
                        ImageHelper.startSaturationAnimation(a, avatar);
                    }
                }

                @Override
                public void onLoadFailed() {
                    // do nothing.
                }
            });
        }

        private void bindPhoto(final int position) {
            if (hasPhoto(position)) {
                final Photo photo = getNotification(position).objects.get(0).castToPhoto();
                imageContainer.setVisibility(View.VISIBLE);
                float[] sizes = image.getSize();
                if (sizes[0] != photo.width || sizes[1] != photo.height) {
                    image.setSize(photo.width, photo.height);
                }
                ImageHelper.loadRegularPhoto(a, image, photo, new ImageHelper.OnLoadImageListener() {
                    @Override
                    public void onLoadSucceed() {
                        photo.loadPhotoSuccess = true;
                        if (!photo.hasFadedIn) {
                            photo.hasFadedIn = true;
                            updatePhoto(photo, position);
                            ImageHelper.startSaturationAnimation(a, image);
                        }
                    }

                    @Override
                    public void onLoadFailed() {
                        // do nothing.
                    }
                });
                imageContainer.setBackgroundColor(ImageHelper.computeCardBackgroundColor(a, photo.color));
            } else {
                imageContainer.setVisibility(View.GONE);
            }
        }

        private void bindTime(int position) {
            String timeTxt = getTime(getNotification(position).time);
            if (TextUtils.isEmpty(timeTxt)) {
                time.setVisibility(View.GONE);
            } else {
                time.setVisibility(View.VISIBLE);
                time.setText(timeTxt);
                Drawable drawable;
                if (ThemeManager.getInstance(a).isLightTheme()) {
                    drawable = a.getResources().getDrawable(R.drawable.ic_item_clock_light);
                } else {
                    drawable = a.getResources().getDrawable(R.drawable.ic_item_clock_dark);
                }
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                time.setCompoundDrawablesRelative(drawable, null, null, null);
            }
        }

        private void bindVerb(int position) {
            switch (getNotification(position).verb) {
                case NotificationResult.VERB_LIKED:
                    verbIcon.setImageResource(R.drawable.ic_verb_liked);
                    subtitle.setVisibility(View.VISIBLE);
                    subtitle.setText(
                            a.getString(R.string.liked)
                                    + " " + a.getString(R.string.your)
                                    + " " + a.getString(R.string.photo)
                                    + ".");
                    break;

                case NotificationResult.VERB_COLLECTED:
                    verbIcon.setImageResource(R.drawable.ic_verb_collected);
                    subtitle.setVisibility(View.VISIBLE);
                    subtitle.setText(
                            Html.fromHtml(
                                    a.getString(R.string.collected)
                                            + " " + a.getString(R.string.your)
                                            + " " + a.getString(R.string.photo) + " " + a.getString(R.string.to)
                                            + " <u>" + getNotification(position).targets.get(0).title + "</u>"
                                            + "."));
                    break;

                case NotificationResult.VERB_FOLLOWED:
                    verbIcon.setImageResource(
                            ThemeManager.getInstance(a).isLightTheme() ?
                                    R.drawable.ic_verb_followed_light : R.drawable.ic_verb_followed_dark);
                    subtitle.setVisibility(View.VISIBLE);
                    subtitle.setText(
                            a.getString(R.string.followed)
                                    + " " + a.getString(R.string.you)
                                    + ".");
                    break;

                case NotificationResult.VERB_RELEASE:
                    verbIcon.setImageResource(R.drawable.ic_verb_published);
                    subtitle.setVisibility(View.VISIBLE);
                    subtitle.setText(
                            a.getString(R.string.released)
                                    + " " + getNotification(position).objects.size()
                                    + " " + a.getString(R.string.photos)
                                    + ".");
                    break;

                case NotificationResult.VERB_PUBLISHED:
                    verbIcon.setImageResource(R.drawable.ic_verb_published);
                    subtitle.setVisibility(View.VISIBLE);
                    subtitle.setText(
                            a.getString(R.string.published)
                                    + " " + getNotification(position).objects.size()
                                    + " " + a.getString(R.string.photos)
                                    + ".");
                    break;

                case NotificationResult.VERB_CURATED:
                    verbIcon.setImageResource(R.drawable.ic_verb_curated);
                    subtitle.setVisibility(View.VISIBLE);
                    subtitle.setText(
                            Html.fromHtml(
                                    a.getString(R.string.curated)
                                            + " " + a.getString(R.string.your)
                                            + " " + a.getString(R.string.photo)
                                            + (getNotification(position).targets.size() > 0 ?
                                            " " + a.getString(R.string.to)
                                                    + " <u>" + getNotification(position).targets.get(0).title + "</u>"
                                            :
                                            "")
                                            + "."));
                    break;

                default:
                    subtitle.setVisibility(View.GONE);
                    break;
            }
        }

        // interface.

        @OnClick(R.id.item_notification_background) void clickItem() {
            if (a instanceof MysplashActivity) {
                switch (getNotification(getAdapterPosition()).verb) {
                    case NotificationResult.VERB_COLLECTED:
                    case NotificationResult.VERB_CURATED:
                        IntentHelper.startCollectionActivity(
                                (MysplashActivity) a,
                                avatar,
                                background,
                                getNotification(getAdapterPosition()).targets.get(0));
                        break;

                    case NotificationResult.VERB_RELEASE:
                    case NotificationResult.VERB_PUBLISHED:
                    case NotificationResult.VERB_FOLLOWED:
                        IntentHelper.startUserActivity(
                                (MysplashActivity) a,
                                avatar,
                                getNotification(getAdapterPosition()).actors.get(0),
                                UserActivity.PAGE_PHOTO);
                        break;

                    case NotificationResult.VERB_LIKED:
                        IntentHelper.startUserActivity(
                                (MysplashActivity) a,
                                avatar,
                                getNotification(getAdapterPosition()).actors.get(0),
                                UserActivity.PAGE_LIKE);
                        break;
                }
            }
        }

        @OnClick(R.id.item_notification_avatar) void clickAvatar() {
            if (a instanceof MysplashActivity) {
                IntentHelper.startUserActivity(
                        (MysplashActivity) a,
                        avatar,
                        getNotification(getAdapterPosition()).actors.get(0),
                        UserActivity.PAGE_PHOTO);
            }
        }

        @OnClick(R.id.item_notification_imageContainer) void clickImage() {
            if (a instanceof MysplashActivity) {
                IntentHelper.startPhotoActivity(
                        (MysplashActivity) a,
                        image,
                        imageContainer,
                        getNotification(getAdapterPosition()).objects.get(0).castToPhoto());
            }
        }

        @OnClick(R.id.item_notification_title) void clickTitle() {
            clickAvatar();
        }
    }

    public NotificationAdapter(Context a) {
        this.a = a;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBindView(position);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
    }

    @Override
    public int getItemCount() {
        return AuthManager.getInstance().getNotificationManager().getNotificationList().size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setActivity(MysplashActivity a) {
        this.a = a;
    }

    private void updatePhoto(Photo photo, int position) {
        NotificationResult result = getNotification(position);
        result.objects.set(0, new ActionObject(photo));
        AuthManager.getInstance()
                .getNotificationManager()
                .getNotificationList()
                .set(position, result);
    }

    private NotificationResult getNotification(int position) {
        return AuthManager.getInstance()
                .getNotificationManager()
                .getNotificationList()
                .get(position);
    }

    @Nullable
    private String getTime(String time) {
        try {
            Calendar calendar;

            int[] nowTimes = new int[6];
            calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            nowTimes[0] = calendar.get(Calendar.YEAR);
            nowTimes[1] = calendar.get(Calendar.MONTH);
            nowTimes[2] = calendar.get(Calendar.DAY_OF_MONTH);
            nowTimes[3] = calendar.get(Calendar.HOUR_OF_DAY);
            nowTimes[4] = calendar.get(Calendar.MINUTE);
            nowTimes[5] = calendar.get(Calendar.SECOND);

            String[] t = time.substring(0, 19).split("T");
            Date date = AuthManager.getInstance()
                    .getNotificationManager()
                    .getFormat().parse(t[0] + " " + t[1]);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            int itemTimes[] = new int[6];
            itemTimes[0] = calendar.get(Calendar.YEAR);
            itemTimes[1] = calendar.get(Calendar.MONTH);
            itemTimes[2] = calendar.get(Calendar.DAY_OF_MONTH);
            itemTimes[3] = calendar.get(Calendar.HOUR_OF_DAY);
            itemTimes[4] = calendar.get(Calendar.MINUTE);
            itemTimes[5] = calendar.get(Calendar.SECOND);

            if (itemTimes[0] != nowTimes[0]) {
                int delta = (nowTimes[0] - itemTimes[0]) * 12 + nowTimes[1] - itemTimes[1];
                if (delta > 12) {
                    return (nowTimes[0] - itemTimes[0]) + " " + a.getString(R.string.year_ago);
                } else {
                    return delta + " " + a.getString(R.string.month_ago);
                }
            } else if (itemTimes[1] != nowTimes[1]) {
                int delta = (nowTimes[1] - itemTimes[1]) * 30 + nowTimes[2] - itemTimes[2];
                if (delta > 30) {
                    return (nowTimes[1] - itemTimes[1]) + " " + a.getString(R.string.month_ago);
                } else {
                    return delta + " " + a.getString(R.string.day_ago);
                }
            } else if (itemTimes[2] != nowTimes[2]) {
                int delta = (nowTimes[2] - itemTimes[2]) * 24 + nowTimes[3] - itemTimes[3];
                if (delta > 24) {
                    return (nowTimes[2] - itemTimes[2]) + " " + a.getString(R.string.day_ago);
                } else {
                    return delta + " " + a.getString(R.string.hour_ago);
                }
            } else if (itemTimes[3] != nowTimes[3]) {
                int delta = (nowTimes[3] - itemTimes[3]) * 60 + nowTimes[4] - itemTimes[4];
                if (delta > 60) {
                    return (nowTimes[3] - itemTimes[3]) + " " + a.getString(R.string.hour_ago);
                } else {
                    return delta + " " + a.getString(R.string.minute_ago);
                }
            } else if (itemTimes[4] != nowTimes[4]) {
                int delta = (nowTimes[4] - itemTimes[4]) * 60 + nowTimes[5] - itemTimes[5];
                if (delta > 60) {
                    return (nowTimes[4] - itemTimes[4]) + " " + a.getString(R.string.minute_ago);
                } else {
                    return delta + " " + a.getString(R.string.second_ago);
                }
            } else {
                return (nowTimes[5] - itemTimes[5]) + " " + a.getString(R.string.second_ago);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean hasPhoto(int position) {
        return getNotification(position).verb.equals(NotificationResult.VERB_LIKED)
                || getNotification(position).verb.equals(NotificationResult.VERB_COLLECTED)
                || getNotification(position).verb.equals(NotificationResult.VERB_CURATED);
    }
}
