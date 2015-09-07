package com.scxh.music_player.ui.tabspec;

import java.util.ArrayList;
import java.util.List;

import com.scxh.music_player.R;
import com.scxh.music_player.ui.PlayerActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class TabSpecMyActivity extends Activity implements OnItemClickListener {
	private GridView mGridView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabspec_content_item);
		mGridView = (GridView) findViewById(R.id.tabspec_gridview);
		MyGridViewAdapter adapter = new MyGridViewAdapter(this, getData());
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(this);
		
	}
	class MyGridViewAdapter extends BaseAdapter {
		private List<Icon> list;
		private LayoutInflater inflater;

		public MyGridViewAdapter(Context context, List<Icon> list) {
			this.list = list;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.tabspec_gridview_item,
						null);
				holder = new ViewHolder();
				ImageView image = (ImageView) convertView
						.findViewById(R.id.gridview_imag);
				TextView txt = (TextView) convertView
						.findViewById(R.id.gridview_title_txt);
				holder.setImage(image);
				holder.setTitleTxt(txt);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Icon icon = (Icon) getItem(position);
			holder.getImage().setImageResource(icon.getImage());
			holder.getTitleTxt().setText(icon.getContent());
			return convertView;
		}

	}
	private List<Icon> getData() {
		List<Icon> list = new ArrayList<TabSpecMyActivity.Icon>();
		Icon icon = null;
		for (int i = 0; i < imags.length; i++) {
			icon = new Icon();
			icon.setImage(imags[i]);
			icon.setContent(titles[i]);
			list.add(icon);
		}
		return list;
	}
	private int[] imags = {R.drawable.mymusic_icon_allsongs_highlight,R.drawable.mymusic_icon_download_normal,R.drawable.mymusic_icon_history_highlight};
	private String[] titles = {"本地歌曲","下载","最近播放"};
	class Icon {
		 int image,count;
		 String content;
		public int getImage() {
			return image;
		}
		public void setImage(int image) {
			this.image = image;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}

	}

	class ViewHolder {
		ImageView image;
		TextView titleTxt,countTxt;
		public ImageView getImage() {
			return image;
		}
		public void setImage(ImageView image) {
			this.image = image;
		}
		public TextView getTitleTxt() {
			return titleTxt;
		}
		public void setTitleTxt(TextView titleTxt) {
			this.titleTxt = titleTxt;
		}
		public TextView getCountTxt() {
			return countTxt;
		}
		public void setCountTxt(TextView countTxt) {
			this.countTxt = countTxt;
		}
		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, PlayerActivity.class);
		startActivity(intent);
	}
}
