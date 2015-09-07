package com.scxh.music_player.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.scxh.music_player.R;
import com.scxh.music_player.modle.MusicParcelable;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MusicLoader {
	private Context context;
	public MusicLoader(Context context) {
		this.context = context;
	}
	/**
	 * 读取Android本地数据库媒体信息
	 * @return
	 */
	public ArrayList<MusicParcelable> scanAllAudioFiles(){
		ArrayList<MusicParcelable> musicImfo = new ArrayList<MusicParcelable>();
		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if(cursor.moveToFirst()){
		 
		       while (!cursor.isAfterLast()) { 
		       
		        //�������
		        int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));  
		        //��������
		        String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));  
		        //������ר������MediaStore.Audio.Media.ALBUM
		        String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));  
		        //�����ĸ������� MediaStore.Audio.Media.ARTIST
		        String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));  
		        //�����ļ���·�� ��MediaStore.Audio.Media.DATA
		        String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));    
		        //�������ܲ���ʱ�� ��MediaStore.Audio.Media.DURATION
		        int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));    
		        //�����ļ��Ĵ�С ��MediaStore.Audio.Media.SIZE
		        Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
		        if(size>1024*800 && duration>60000){//����800K
		        	MusicParcelable music = new MusicParcelable();
		        	music.setMUSIC_NAME(tilte);
		        	music.setMUSIC_SINGER(artist);
		        	music.setMUSIC_DURATION(initTime(duration));
		        	music.setMUSIC_PATH(url);
		        	musicImfo.add(music);  
		        }
		        cursor.moveToNext(); 
		       } 
			}
		return musicImfo;
		}
	/**
	 * 格式化时间,转化为String类型
	 * @param time
	 * @return
	 */
	private String initTime(int time) {
		int minTime = time / 1000 / 60;// ��ʱ�仮��ɷ�
		int secTime = time / 1000 % 60;// ǿʱ�仮�����
		String timeTxt = "";
		if (secTime < 10)
			timeTxt = "0" + minTime + ":" + "0" + secTime;
		else
			timeTxt = "0" + minTime + ":" + secTime;
		return timeTxt;
	}
}