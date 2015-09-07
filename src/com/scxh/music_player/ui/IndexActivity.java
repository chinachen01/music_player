package com.scxh.music_player.ui;

import com.scxh.music_player.R;
import com.scxh.music_player.dao.MusicImfoDao;
import com.scxh.music_player.service.MusicPlayService;
import com.scxh.music_player.service.MusicPlayService.IplayService;
import com.scxh.music_player.ui.tabspec.TabSpecMyActivity;

import android.app.Activity;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class IndexActivity extends TabActivity implements OnClickListener {
	private boolean timeFlag = false;
	private ImageButton mPlayBtn, mMyListBtn;
	private SeekBar mPlaySeekBar;
	private RadioGroup mRadioGroup;
	private TextView mCurrentTimeTxt, mDurationTxt, mTitleText;
	private ImageView mTitleImag;
	private boolean isPlaying; // 播放器是否处于播放状态
	private Handler mHandler = new Handler();
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
	// ================广播接受者===========
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(MusicPlayService.STATU_PLAY_PAUSE_MUSIC_ACTION)) {
					if (MusicPlayService.isPlaying) {
						mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_pause_normal);
					} else {
						mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_play_normal);
					}
			}
			if (action.equals(MusicPlayService.STATU_NEXT_MUSIC_ACTION)) {
				mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_pause_normal);
			}
			if (action.equals(PlayerActivity.STATU_TO_INDEX)) {
				if (!MusicPlayService.isPlaying) {
					mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_play_normal);
				} else {
					mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_pause_normal);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_index_layout);
		initTab();
		initCompanent();
		// 启动服务
		Intent intentService = new Intent(getApplicationContext(),
				MusicPlayService.class);
		startService(intentService);
		bindService(intentService, serviceConnetion, BIND_AUTO_CREATE);
		// 启动线程刷新界面信息
		setSeekBar();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MusicPlayService.STATU_PLAY_PAUSE_MUSIC_ACTION);
		filter.addAction(PlayerActivity.STATU_TO_INDEX);
		filter.addAction(MusicPlayService.STATU_NEXT_MUSIC_ACTION);
		registerReceiver(receiver, filter);

	}

	private void initTab() {
		final TabHost tabHost = getTabHost();
		TabSpec tabSpec1 = tabHost.newTabSpec("tab1");
		tabSpec1.setIndicator(getString(R.string.index_tab_spec1));
		tabSpec1.setContent(new Intent(this, TabSpecMyActivity.class));
		TabSpec tabSpec2 = tabHost.newTabSpec("tab2");
		tabSpec2.setIndicator(getString(R.string.index_tab_spec2));
		tabSpec2.setContent(new Intent(this, TabSpecMyActivity.class));
		TabSpec tabSpec3 = tabHost.newTabSpec("tab3");
		tabSpec3.setIndicator(getString(R.string.index_tab_spec3));
		tabSpec3.setContent(new Intent(this, TabSpecMyActivity.class));

		tabHost.addTab(tabSpec1);
		tabHost.addTab(tabSpec2);
		tabHost.addTab(tabSpec3);
		mRadioGroup = (RadioGroup) findViewById(R.id.tab_radio_groups);
		
		((RadioButton) mRadioGroup.getChildAt(0)).toggle();

		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tab_radio_main:
					tabHost.setCurrentTabByTag("tab1");
					break;
				case R.id.tab_radio_merchat:
					tabHost.setCurrentTabByTag("tab2");
					break;
				case R.id.tab_radio_more:
					tabHost.setCurrentTabByTag("tab3");
					break;
				}
			}
		});
	}

	private void initCompanent() {
		mPlayBtn = (ImageButton) findViewById(R.id.index_player_play_btn);
		if (MusicPlayService.isPlaying) {
			mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_pause_normal);
		} else {
			mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_play_normal);
		}
		mMyListBtn = (ImageButton) findViewById(R.id.index_player_bill_btn);
		mPlaySeekBar = (SeekBar) findViewById(R.id.index_player_seekbar);
		mCurrentTimeTxt = (TextView) findViewById(R.id.index_player_currentime_txt);
		mDurationTxt = (TextView) findViewById(R.id.index_player_duration_txt);
		mTitleText = (TextView) findViewById(R.id.index_player_title_txt);
		mTitleImag = (ImageView) findViewById(R.id.index_player_title_imag);
		mPlayBtn.setOnClickListener(this);
		mTitleImag.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.index_player_play_btn:
			isPlaying = mIScanService.servicestartOrPlaying();
			if (isPlaying) {
				mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_pause_normal);
			} else {
				mPlayBtn.setBackgroundResource(R.drawable.landscape_player_btn_play_normal);
			}
			break;
		case R.id.index_player_title_imag:
			Intent intent = new Intent(this, PlayerActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	/**
	 * 设置seekBar和时间进度
	 */
	private void setSeekBar() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (!timeFlag) {
					if (mIScanService != null) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								mPlaySeekBar.setProgress(mIScanService
										.serviceGetCurrentPostion());
								String currentTimeText = initTime(mIScanService
										.serviceGetCurrentPostion());
								mCurrentTimeTxt.setText(currentTimeText);
								int duration = mIScanService
										.serviceGetDurationTime();
								mPlaySeekBar.setMax(duration);
								String text = initTime(duration);
								mDurationTxt.setText(text);
								String title = MusicImfoDao.mMusicImfo.get(
										MusicPlayService.currentPosition)
										.getMUSIC_NAME();
								mTitleText.setText(title);
							}
						});
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		timeFlag = true;
		if (serviceConnetion != null)
			unbindService(serviceConnetion);
		if (receiver != null)
			unregisterReceiver(receiver);
	}
}
