/*
* Copyright 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package music.app.my.music.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import music.app.my.music.DrawerActivity;
import music.app.my.music.R;
import music.app.my.music.player.MusicService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaSessionCompat;

/**
 * Helper class to manage notification channels, and create notifications.
 */
public class NotificationHelper extends ContextWrapper {

    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "default";
    public static final String SECONDARY_CHANNEL = "second";
    private MediaSessionCompat mediaSession;
    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param ctx The application context
     */
    public NotificationHelper(Context ctx, MediaSessionCompat media) {
        super(ctx);
        mediaSession = media;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                    getString(R.string.noti_channel_default), NotificationManager.IMPORTANCE_DEFAULT);
            chan1.setLightColor(Color.GREEN);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(chan1);

            NotificationChannel chan2 = new NotificationChannel(SECONDARY_CHANNEL,
                    getString(R.string.noti_channel_second), NotificationManager.IMPORTANCE_HIGH);
            chan2.setLightColor(Color.BLUE);
            chan2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(chan2);
        }

    }

    /**
     * Get a notification of type 1
     *
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     *
     * @param title the title of the notification
     * @param body the body text for the notification
     * @return the builder as it keeps a reference to the notification (since API 24)
    */
    public NotificationCompat.Builder getNotification1(String title, String body, Bitmap albumart) {
        Intent resultIntent = new Intent(this, DrawerActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        int pr = android.R.drawable.ic_media_pause;
        if(title.contains("Paused"))
            pr = android.R.drawable.ic_media_play;


        Intent pi = new Intent(this, MusicService.class);
        pi.setAction(MusicService.ACTION_TOGGLE_PLAYBACK);
        PendingIntent togglepi = PendingIntent.getService(this, 21, pi, PendingIntent.FLAG_UPDATE_CURRENT );
        NotificationCompat.Action pp = new NotificationCompat.Action(pr, "Play/Pause", togglepi);
        //previous
	   Intent prei = new Intent(this, MusicService.class);
	   prei.setAction(MusicService.ACTION_PREVIOUS);
	   PendingIntent prevpi = PendingIntent.getService(this, 22, prei, PendingIntent.FLAG_UPDATE_CURRENT );
	   NotificationCompat.Action previous = new NotificationCompat.Action(android.R.drawable.ic_media_previous, "Previous", prevpi);

	   //next
	   Intent nexi = new Intent(this, MusicService.class);
	   nexi.setAction(MusicService.ACTION_NEXT);
	   PendingIntent nextpi = PendingIntent.getService(this, 23, nexi, PendingIntent.FLAG_UPDATE_CURRENT );
	   NotificationCompat.Action next = new NotificationCompat.Action(android.R.drawable.ic_media_next, "Next", nextpi);

//
        return new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL)
                .setContentTitle(title)
                .setColor(Color.GREEN)
                .setLargeIcon(albumart)
                .setSmallIcon(getSmallIcon()).setContentIntent(resultPendingIntent).setOngoing(true)
                .setContentText(body).addAction(previous).addAction(pp).addAction(next)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0,1,2));
               //  .setAutoCancel(true);
    }

    /**
     * Build notification for secondary channel.
     *
     * @param title Title for notification.
     * @param body Message for notification.
     * @return A Notification.Builder configured with the selected channel and details
     */
    public NotificationCompat.Builder getNotification2(String title, String body) {
        return new NotificationCompat.Builder(getApplicationContext(), SECONDARY_CHANNEL)
                 .setContentTitle(title)
                 .setContentText(body)
                 .setSmallIcon(getSmallIcon())
                 .setAutoCancel(true);
    }

    /**
     * Send a notification.
     *
     * @param id The ID of the notification
     * @param notification The notification object
     */
    public Notification notify(int id, NotificationCompat.Builder notification) {
        Notification r =  notification.build();
        getManager().notify(id,r);
        return r;
    }

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private int getSmallIcon() {
        return R.drawable.android_icon3small;
    }

    /**
     * Get the notification manager.
     *
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}
