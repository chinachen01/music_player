package com.scxh.music_player.service;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.scxh.music_player.R;
import com.scxh.music_player.dao.MusicImfoDao;
import com.scxh.music_player.inter.IPlayingStatu;
import com.scxh.music_player.ui.IndexActivity;

import java.io.IOException;

public class MusicPlayService extends Service implements IPlayingStatu, OnCompletionListener {
    public static int currentPosition = 0;// position代表播放第几首歌曲
    public static boolean isPlaying = false;
    private int musicStatu = LOOPING_PLAYING;// 歌曲的播放模式,默认为自动循环播放
    private MediaPlayer mp = new MediaPlayer();
    public static String STATU_NEXT_MUSIC_ACTION = "com.scxh.musicPlayer.servcie.nextMusicAction";
    public static String STATU_PLAY_PAUSE_MUSIC_ACTION = "com.scxh.musicPlayer.servcie.playOrPauseAction";
    public static String NOTIFY_PLAY_MUSIC = "com.scxh.musicPlayer.servcie.notifyPlayMsuic";
    public static String NOTIFY_NEXT_MUSIC = "com.scxh.musicPlayer.servcie.notifyNextMsuic";
    public static String NOTIFY_PRE_MUSIC = "com.scxh.musicPlayer.servcie.notifyPreMsuic";
    private int currentTime = 0;
    private Object obj = new Object(); //同步锁
    //=============广播接收者========================
    private BroadcastReceiver notifyReceiver = new BroadcastReceiver() {
        public void onReceive(android.content.Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(NOTIFY_PLAY_MUSIC)) {
                if (mp.isPlaying()) {
                    mp.pause();
                    sendNotification();
                    isPlaying = false;
                } else {
                    mp.start();
                    sendNotification();
                    isPlaying = true;
                }
                Intent intentPlayOrPause = new Intent(STATU_PLAY_PAUSE_MUSIC_ACTION);
                sendBroadcast(intentPlayOrPause);
            }
            if (action.equals(NOTIFY_NEXT_MUSIC)) {
                if (currentPosition < MusicImfoDao.mMusicImfo.size() - 1) {
                    currentPosition++;
                    startNewPlayer();
                }
            }
            if (action.equals(NOTIFY_PRE_MUSIC)) {
                if (currentPosition > 0) {
                    currentPosition--;
                    startNewPlayer();
                }
            }
        }

        ;
    };

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        try {
//			if(MusicImfoDao.mMusicImfo.size()!= 0) {
            System.out.println("service" + ">>>>>>");
            if (MusicImfoDao.mMusicImfo.size() != 0) {
                mp.setDataSource((String) MusicImfoDao.mMusicImfo.get(currentPosition).getMUSIC_PATH());
                mp.prepare();
                sendNotification();
            }
//			}
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mp.setOnCompletionListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(NOTIFY_PLAY_MUSIC);
        filter.addAction(NOTIFY_NEXT_MUSIC);
        filter.addAction(NOTIFY_PRE_MUSIC);
        registerReceiver(notifyReceiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return new ServiceBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

    //==========定义服务交互接口=========
    public interface IplayService {
        void serviceStartNewPlayer(int position);

        boolean servicestartOrPlaying();

        boolean serviceGetPlayingStatu();

        void serviceStartNextPlayer();

        void serviceStartPrePlayer();

        void serviceSeekTo(int msec);

        int serviceGetCurrentPostion();

        int serviceGetDurationTime();

    }

    public class ServiceBinder extends Binder implements IplayService {

        @Override
        public void serviceStartNewPlayer(int position) {
            // TODO Auto-generated method stub
            currentPosition = position;
            startNewPlayer();
        }

        @Override
        public boolean servicestartOrPlaying() {
            // TODO Auto-generated method stub
            if (mp.isPlaying()) {
                mp.pause();
                isPlaying = false;
                sendNotification();
                return false;
            } else {
                mp.start();
                isPlaying = true;
                sendNotification();
                return true;
            }
        }


        @Override
        public void serviceStartNextPlayer() {
            // TODO Auto-generated method stub
            if (currentPosition < MusicImfoDao.mMusicImfo.size() - 1) {
                currentPosition++;
                serviceStartNewPlayer(currentPosition);
            }
        }

        @Override
        public void serviceStartPrePlayer() {
            // TODO Auto-generated method stub
            if (currentPosition > 0) {
                currentPosition--;
                serviceStartNewPlayer(currentPosition);
            }
        }

        @Override
        public void serviceSeekTo(int msec) {
            // TODO Auto-generated method stub
            mp.seekTo(msec);
        }

        @Override
        public int serviceGetCurrentPostion() {
            // TODO Auto-generated method stub
            return mp.getCurrentPosition();
        }

        @Override
        public int serviceGetDurationTime() {
            // TODO Auto-generated method stub
            synchronized (obj) {
                if (mp.isPlaying()) {
                    currentTime = mp.getDuration();
                    return currentTime;
                }
                return currentTime;
            }
        }

        @Override
        public boolean serviceGetPlayingStatu() {
            // TODO Auto-generated method stub
            return mp.isPlaying();
        }

    }

    /**
     * 第一次播放歌曲时调用此方法
     */
    private int startNewPlayer() {
        try {
            synchronized (obj) {
                mp.reset();
                mp.setDataSource((String) MusicImfoDao.mMusicImfo.get(currentPosition).getMUSIC_PATH());
                // mp.prepareAsync();
                mp.prepare();
                mp.start();
            }
            isPlaying = true;
            Intent intent = new Intent(STATU_NEXT_MUSIC_ACTION);
            sendBroadcast(intent);
            sendNotification();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return mp.getDuration();

    }

    public void sendNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this);
        mBuilder.setSmallIcon(R.drawable.icon_music);
        // ------自定义notification界面
        RemoteViews view = new RemoteViews(getPackageName(),
                R.layout.notification_item);
        view.setTextViewText(R.id.notification_title_txt, MusicImfoDao.mMusicImfo.get(currentPosition).getMUSIC_NAME());
        view.setTextViewText(R.id.notification_singer_txt, MusicImfoDao.mMusicImfo.get(currentPosition).getMUSIC_SINGER());
        if (mp.isPlaying())
            view.setImageViewResource(R.id.notification_play_btn, R.drawable.btn_notification_player_stop_normal);
        else
            view.setImageViewResource(R.id.notification_play_btn, R.drawable.btn_notification_player_play_normal);
        mBuilder.setContent(view);

        // =======自定义notification事件处理===========
        //跳转到主界面
        Intent intent = new Intent(this, IndexActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 11, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notification_linear,
                notifyPendingIntent);
        // 暂停or播放歌曲
        Intent intentPlayBtn = new Intent(NOTIFY_PLAY_MUSIC);
        PendingIntent notifyPendingIntentPlayBtn = PendingIntent.getBroadcast(this, 12, intentPlayBtn, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notification_play_btn, notifyPendingIntentPlayBtn);

        // 播放下一首歌曲
        Intent intentNextBtn = new Intent(NOTIFY_NEXT_MUSIC);
        PendingIntent notifyPendingIntentNextBtn = PendingIntent.getBroadcast(this, 13, intentNextBtn, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notification_next_btn, notifyPendingIntentNextBtn);

        // 播放上一首歌曲
        Intent intentPreBtn = new Intent(NOTIFY_PRE_MUSIC);
        PendingIntent notifyPendingIntentPreBtn = PendingIntent.getBroadcast(this, 14, intentPreBtn, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notification_pre_btn, notifyPendingIntentPreBtn);


        NotificationManager mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyManager.notify(20, mBuilder.build());
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
        switch (musicStatu) {
            case LOOPING_PLAYING:
                if (currentPosition == MusicImfoDao.mMusicImfo.size() - 1)
                    currentPosition = 0;
                else
                    currentPosition++;
                startNewPlayer();
                break;
            case RANDOM_PLAYING:
                currentPosition = (int) (Math.random() * MusicImfoDao.mMusicImfo.size());
                startNewPlayer();
            case SINGLE_PLAYING:
                startNewPlayer();
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mp != null)
            mp.release();
        if (notifyReceiver != null)
            unregisterReceiver(notifyReceiver);
    }
}
