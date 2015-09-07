package com.scxh.music_player.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自定义ViewPager适配器
 * @author scxh
 *
 */
public class MyViewPagerAdapter extends PagerAdapter {
	private List<View> list = new ArrayList<View>();

	public void setPagerData(List<View> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		View child = list.get(position);
		container.addView(child);
		return child;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		View child = list.get(position);
		container.removeView(child);
	}

}