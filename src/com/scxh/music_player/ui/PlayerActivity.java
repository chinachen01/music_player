package com.scxh.music_player.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.scxh.music_player.R;
import com.scxh.music_player.adapter.MusicBaseAdapter;
import com.scxh.music_player.adapter.MyViewPagerAdapter;
import com.scxh.music_player.dao.MusicImfoDao;
import com.scxh.music_player.inter.IPlayingStatu;
import com.scxh.music_player.lyric.Lyric;
import com.scxh.music_player.lyric.LyricView;
import com.scxh.music_player.lyric.PlayListItem;
import com.scxh.music_player.service.MusicPlayService;
import com.scxh.music_player.service.MusicPlayService.IplayService;
import com.scxh.music_player.util.UtilMusicLoad;

public class PlayerActivity extends Activity implements OnClickListener,
		IPlayingStatu, OnItemClickListener, OnSeekBarChangeListener,
		OnCompletionListener, OnPageChangeListener {

	private File rootDir = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
	private boolean isPlaying = true; // playingStatu代表当前的播放状态

	private SeekBar mPlayerSeekBar;
	private Button mPlayBtn, mNextBtn, mPreBtn, mSearchBtn;
	private TextView mCurrentTimeTxt, mDurationTimeTxt, mCurrentMusicTXT;
	private Handler mHandler = new Handler();
	private ListView mListView;
	private View v1, v2;
	private ViewPager mViewPager;
	private LyricView lyricView;
	private MusicBaseAdapter mMusicAdapter;
	private Lyric mLyric;
	private boolean isLyricShow = false;

	private boolean timeFlag = true;
	private boolean seekBarFlag = true;
	public static final String STATU_TO_INDEX = "com.scxh.musicPlayer.playeractivity.playBtn";
	// ===================服务========================
	private MusicPlayService.IplayService mIScanService;
	private ServiceConnection serviceConnetion = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mIScanService = (IplayService) service;
		}
	};
	// ===================广播接受者========================
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			// 接受到服务播放下一曲的通知,刷新歌曲标题及总的时间长度
			if (action.equals(MusicPlayService.STATU_NEXT_MUSIC_ACTION)) {
				mMusicAdapter.setPlayState(MusicPlayService.currentPosition);
//				mListView.setSelection(MusicPlayService.currentPosition);
				mCurrentMusicTXT.setText(MusicImfoDao.mMusicImfo.get(
						MusicPlayService.currentPosition).getMUSIC_NAME());
				mDurationTimeTxt.setText(MusicImfoDao.mMusicImfo.get(
						MusicPlayService.currentPosition).getMUSIC_DURATION());
				mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_pause_normal);
			}
			if (action.equals(MusicPlayService.STATU_PLAY_PAUSE_MUSIC_ACTION)) {
				if (MusicPlayService.isPlaying) {
					mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_pause_normal);
				} else {
					mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_play_normal);
				}
				Log.i("tag", "isPalyint >>>>" + MusicPlayService.isPlaying);
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 启动服务
		Intent intentService = new Intent(getApplicationContext(),
				MusicPlayService.class);
		startService(intentService);
		bindService(intentService, serviceConnetion, BIND_AUTO_CREATE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_player_layout);
		// 初始化控件,并设置监听事件
		mPlayerSeekBar = (SeekBar) findViewById(R.id.player_progress_seekbar);
		mPlayerSeekBar.setOnSeekBarChangeListener(this);
		mPlayBtn = (Button) findViewById(R.id.player_play_btn);
		mPlayBtn.setOnClickListener(this);
		mNextBtn = (Button) findViewById(R.id.player_next_btn);
		mNextBtn.setOnClickListener(this);
		mSearchBtn = (Button) findViewById(R.id.player_serch_btn);
		mSearchBtn.setOnClickListener(this);
		mPreBtn = (Button) findViewById(R.id.player_pre_btn);
		mPreBtn.setOnClickListener(this);
		mCurrentTimeTxt = (TextView) findViewById(R.id.player_currenttime_txt);
		mDurationTimeTxt = (TextView) findViewById(R.id.player_duraingtime_txt);
		mCurrentMusicTXT = (TextView) findViewById(R.id.player_current_music_txt);

		mCurrentMusicTXT.setText(MusicImfoDao.mMusicImfo.get(
				MusicPlayService.currentPosition).getMUSIC_NAME());
		mDurationTimeTxt.setText(MusicImfoDao.mMusicImfo.get(
				MusicPlayService.currentPosition).getMUSIC_DURATION());
		setViewPager();
		// 启动线程,刷新进度条
		setSeekBar();
		if (!MusicPlayService.isPlaying) {
			mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_play_normal);
		} else {
			mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_pause_normal);
		}
	}

	private void setViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.player_viewpager);
		MyViewPagerAdapter adapter = new MyViewPagerAdapter();
		mViewPager.setAdapter(adapter);
		ArrayList<View> list = new ArrayList<View>();
		v1 = LayoutInflater.from(this).inflate(R.layout.player_listview, null);
		v2 = LayoutInflater.from(this).inflate(R.layout.player_listview2, null);
		list.add(v1);
		list.add(v2);
		adapter.setPagerData(list);
		mViewPager.setOnPageChangeListener(this);
		// ListView初始化并设置监听事件
		mListView = (ListView) v1.findViewById(R.id.player_listview);
		mMusicAdapter = new MusicBaseAdapter(PlayerActivity.this,
				MusicImfoDao.mMusicImfo);
		mListView.setAdapter(mMusicAdapter);
		mMusicAdapter.setPlayState(MusicPlayService.currentPosition);
		mListView.setSelection(MusicPlayService.currentPosition);
		mListView.setOnItemClickListener(this);
		// 启动线程,通知系统扫描本地媒体,最好放入引导界面
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				UtilMusicLoad.showFile(getApplicationContext(), rootDir);
			}
		}).start();

		// 初始化lyricView;
		lyricView = (LyricView) v2.findViewById(R.id.player_lyricview);
	}

	class UIUpdateThread implements Runnable {
		long time = 1000;

		public void run() {
			while (isLyricShow) {
				if(mIScanService!=null) {
					if (mIScanService.serviceGetPlayingStatu()) {
						lyricView.updateIndex(mIScanService
								.serviceGetCurrentPostion());
						mHandler1.post(mUpdateResults);
					}
					try {
						Thread.sleep(time); 
	
					} catch (InterruptedException e) {
	
						e.printStackTrace();
					}
				}
			
			}
		}
	}

	/**
	 * 更新主界面
	 */
	Handler mHandler1 = new Handler();
	Runnable mUpdateResults = new Runnable() {
		public void run() {
			lyricView.invalidate();
		}
	};

	/**
	 * 同步显示歌词的相关属性设置,有歌词则显示,无则显示暂无歌词,歌词需与歌词保持同名
	 * 
	 * @param musicPath
	 *            歌曲的路径
	 */
	private void setLyricView(String musicPath) {
		String lyricPath = musicPath.replaceAll(".mp3", ".lrc");
		TextView txtView = (TextView) v2.findViewById(R.id.player_msg_nolyric);
		if (new File(lyricPath).exists()) {
			isLyricShow = true;
			lyricView.setVisibility(View.VISIBLE);
			txtView.setVisibility(View.INVISIBLE);
			setLyric(musicPath, lyricPath);
		} else {
			isLyricShow = false;
			lyricView.setVisibility(View.INVISIBLE);
			txtView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 同步显示歌词的相关属性设置,你应该使用setLyricView(String musicPath)
	 * 
	 * @param musicPath
	 *            歌曲路径,需与歌词名字相同
	 * @param lyricPath
	 *            歌词路径
	 */
	private void setLyric(String musicPath, String lyricPath) {
//		Thread.sleep(500);
		PlayListItem pli = new PlayListItem("", musicPath, 0L, true);
		mLyric = new Lyric(new File(lyricPath), pli);
		lyricView.setmLyric(mLyric);
		lyricView.setSentencelist(mLyric.list);
		lyricView.setNotCurrentPaintColor(Color.WHITE);
		lyricView.setCurrentPaintColor(Color.GREEN);
		lyricView.setLrcTextSize(48);
		lyricView.setTexttypeface(Typeface.SERIF);
//		lyricView.setBackgroundResource(R.drawable.main_bg);
		lyricView.setTextHeight(120);
		new Thread(new UIUpdateThread()).start();
	}

	/**
	 * 设置seekBar和时间进度
	 */
	private void setSeekBar() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (timeFlag) {
					if (mIScanService != null) {
						if (seekBarFlag) {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									mPlayerSeekBar.setProgress(mIScanService
											.serviceGetCurrentPostion());
									String currentTimeText = initTime(mIScanService
											.serviceGetCurrentPostion());
									mCurrentTimeTxt.setText(currentTimeText);
									int duration = mIScanService
											.serviceGetDurationTime();
									mPlayerSeekBar.setMax(duration);
									// String text = initTime(duration);
									// mDurationTimeTxt.setText(text);
								}
							});
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

		}).start();

	}

	/**
	 * 格式化时间
	 * 
	 * @param time
	 * @return
	 */
	private String initTime(int time) {
		int minTime = time / 1000 / 60;// 将时间划算成分
		int secTime = time / 1000 % 60;// 强时间划算成秒
		String timeTxt = "";
		if (secTime < 10)
			timeTxt = "0" + minTime + ":" + "0" + secTime;
		else
			timeTxt = "0" + minTime + ":" + secTime;
		return timeTxt;
	}

	// =================================监听事件===========================================
	/**
	 * 设置按键监听事件
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.player_play_btn:
			isPlaying = mIScanService.servicestartOrPlaying();
			if (isPlaying) {
				mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_pause_normal);
			} else {
				mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_play_normal);
			}
			Intent intent = new Intent();
			intent.setAction(STATU_TO_INDEX);
			sendBroadcast(intent);
			break;
		case R.id.player_next_btn:
			if (MusicPlayService.currentPosition < MusicImfoDao.mMusicImfo
					.size() - 1) {
				MusicPlayService.currentPosition++;
				mIScanService
						.serviceStartNewPlayer(MusicPlayService.currentPosition);
				mMusicAdapter.setPlayState(MusicPlayService.currentPosition);
				mMusicAdapter.notifyDataSetChanged();
				mListView.setSelection(MusicPlayService.currentPosition);
			}
			if (MusicPlayService.isPlaying) {
				mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_pause_normal);
			} else {
				mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_play_normal);
			}

			setLyricView(MusicImfoDao.mMusicImfo.get(
					MusicPlayService.currentPosition).getMUSIC_PATH());
			break;
		case R.id.player_pre_btn:
			if (MusicPlayService.currentPosition > 0) {
				MusicPlayService.currentPosition--;
				mIScanService
						.serviceStartNewPlayer(MusicPlayService.currentPosition);
				mMusicAdapter.setPlayState(MusicPlayService.currentPosition);
				mMusicAdapter.notifyDataSetChanged();
				mListView.setSelection(MusicPlayService.currentPosition);
			}
			if (MusicPlayService.isPlaying) {
				mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_pause_normal);
			} else {
				mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_play_normal);
			}
			setLyricView(MusicImfoDao.mMusicImfo.get(
					MusicPlayService.currentPosition).getMUSIC_PATH());
			break;
		case R.id.player_serch_btn:
			// 启动子线程ListView适配数据源,
			mMusicAdapter.setMusciImfoChange(MusicImfoDao.mMusicImfo);
			break;
		default:
			break;
		}
	}

	/**
	 * ListView监听事件
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		MusicPlayService.currentPosition = position;
		mIScanService.serviceStartNewPlayer(position);
		mMusicAdapter.setPlayState(MusicPlayService.currentPosition);
		mMusicAdapter.notifyDataSetChanged();
		mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_pause_normal);
		if(isLyricShow)
			setLyricView(MusicImfoDao.mMusicImfo.get(
				MusicPlayService.currentPosition).getMUSIC_PATH());
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		seekBarFlag = false;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		int msec = seekBar.getProgress();
		mIScanService.serviceSeekTo(msec);
		seekBarFlag = true;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub

		ImageView image1 = (ImageView) findViewById(R.id.pager1_btn);
		ImageView image2 = (ImageView) findViewById(R.id.pager2_btn);

		switch (arg0) {
		case 0:
			if (image1 != null) {
				image1.setBackgroundResource(R.drawable.page_now);
			}
			if (image2 != null) {
				image2.setBackgroundResource(R.drawable.page);
			}
			break;
		case 1:
			if (image1 != null) {
				image1.setBackgroundResource(R.drawable.page);
			}
			if (image2 != null) {
				image2.setBackgroundResource(R.drawable.page_now);
			}

			break;

		default:
			break;
		}
		if (arg0 == 1) {
			String musicPath = MusicImfoDao.mMusicImfo.get(
					MusicPlayService.currentPosition).getMUSIC_PATH();
			setLyricView(musicPath);
		}
	}

	// =================================监听事件===========================================
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MusicPlayService.STATU_NEXT_MUSIC_ACTION);
		filter.addAction(MusicPlayService.STATU_PLAY_PAUSE_MUSIC_ACTION);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (serviceConnetion != null)
			unbindService(serviceConnetion);
		if (receiver != null)
			unregisterReceiver(receiver);

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
