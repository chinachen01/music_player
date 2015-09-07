package com.scxh.music_player.adapter;

import java.util.ArrayList;

import com.scxh.music_player.R;
import com.scxh.music_player.modle.MusicParcelable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicBaseAdapter extends BaseAdapter {
//	private ArrayList<View> listView = new ArrayList<View>();
	private ArrayList<MusicParcelable> musicImfo;
	private LayoutInflater mInflater;
	private Context context;
	private int mCurPlayMusicIndex = -1;
	public MusicBaseAdapter (Context context,ArrayList<MusicParcelable> musicImfo) {
		mInflater = LayoutInflater.from(context);
		this.musicImfo = musicImfo;
		this.context = context;
	}
	public void setMusciImfoChange(ArrayList<MusicParcelable> musicImfo) {
		this.musicImfo = musicImfo;
		notifyDataSetChanged();
	}
	public void setPlayState(int playIndex) {
		mCurPlayMusicIndex = playIndex;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return musicImfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return musicImfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.item_music_listview, null);
			viewHolder = new ViewHolder();
			viewHolder.nameTxt = (TextView)convertView.findViewById(R.id.music_name);
			viewHolder.singerTxt = (TextView)convertView.findViewById(R.id.music_singer);
			viewHolder.durationTxt = (TextView)convertView.findViewById(R.id.music_durationtime);
			viewHolder.imag = (ImageView) convertView.findViewById(R.id.music_playe);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String name = (String) musicImfo.get(position).getMUSIC_NAME();
		String singer = (String) musicImfo.get(position).getMUSIC_SINGER();
		String duration = (String) musicImfo.get(position).getMUSIC_DURATION();
		viewHolder.nameTxt.setText(name);
		viewHolder.singerTxt.setText(singer);
		viewHolder.durationTxt.setText(duration);
		if(mCurPlayMusicIndex == position) {
			viewHolder.nameTxt.setTextColor(context.getResources().getColor(R.color.player_textview_green));
			viewHolder.singerTxt.setTextColor(context.getResources().getColor(R.color.player_textview_green));
			viewHolder.durationTxt.setTextColor(context.getResources().getColor(R.color.player_textview_green));
			viewHolder.imag.setVisibility(View.VISIBLE);
			
		}else{
			viewHolder.nameTxt.setTextColor(context.getResources().getColor(android.R.color.white));
			viewHolder.singerTxt.setTextColor(context.getResources().getColor(android.R.color.white));
			viewHolder.durationTxt.setTextColor(context.getResources().getColor(android.R.color.white));
			viewHolder.imag.setVisibility(View.INVISIBLE);
		}
//		listView.add(convertView);
		return convertView;
	}
	class ViewHolder{
		TextView nameTxt,singerTxt,durationTxt;
		ImageView imag;
	}
}
