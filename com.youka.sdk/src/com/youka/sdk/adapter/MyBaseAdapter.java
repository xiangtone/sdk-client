package com.youka.sdk.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

/**
 * 
* @Description: (Adapter 基类)
* @author 张广涛
* @param <T>
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter  {

	protected LayoutInflater inflater;
	protected List<T> list;
	protected Context context;
	
	public MyBaseAdapter(Context context,List<T> list) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
