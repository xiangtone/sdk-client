package com.youka.sdk.adapter2;

import java.util.List;

import com.youka.sdk.adapter.MyBaseAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class AbcAdapter2<T> extends MyBaseAdapter<T> {

	private int layout;

	public AbcAdapter2(Context context, int layout, List<T> list) {
		super(context, list);
		this.layout = layout;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder2 holder = ViewHolder2.get(context, convertView, parent,
				layout, position);
		convert(holder, list.get(position));
		return holder.getConvertView();
	}

	public abstract void convert(ViewHolder2 holder, T t);

}
