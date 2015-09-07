package com.scxh.music_player.ui;

import com.scxh.music_player.R;
import com.scxh.music_player.dao.MusicImfoDao;
import com.scxh.music_player.util.MusicLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class LoadingActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading_layout);
		// 扫描数据
		MusicLoader musicLoader = new MusicLoader(
				getApplicationContext());
		MusicImfoDao.mMusicImfo= musicLoader.scanAllAudioFiles();
		Intent intent = new Intent(this, IndexActivity.class);
		startActivity(intent);
		finish();
	}
}
