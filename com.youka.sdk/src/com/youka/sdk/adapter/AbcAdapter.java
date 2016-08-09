package com.youka.sdk.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class AbcAdapter<T> extends MyBaseAdapter<T> {

	private String layout;

	public AbcAdapter(Context context, String layout, List<T> list) {
		super(context, list);
		this.layout = layout;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.get(context, convertView, parent,
				layout, position);
		convert(holder, list.get(position));
		return holder.getConvertView();
	}

	public abstract void convert(ViewHolder holder, T t);

}
