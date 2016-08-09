package com.youka.sdk.adapter;

import java.util.HashMap;

import com.youka.sdk.utils.AssetsUtils;


import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewHolder {

	private  HashMap<String,View> views;
	private View convertView;
    private  Context context;
    private int mPostion;
	
    private AssetsUtils assetsUtils;
	
	private  ViewHolder(Context context,ViewGroup viewGroup,String layout,int mPostion){
		this.views = new HashMap<String,View>();
		this.context=context;
		assetsUtils = new AssetsUtils((Activity) context);
		convertView = assetsUtils.findViewByFileName(layout);
		convertView.setTag(this);
		this.mPostion = mPostion;
	}
	
	
	public static ViewHolder get(Context context,View convertView,ViewGroup parent,String layout,int position){
		if(convertView==null){
			return new ViewHolder(context, parent, layout, position);
		}else {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mPostion = position;
			return viewHolder;
		}
	}
	
	

	
	
	
	public <T extends View> T getViewByTag(String tag) {
		View view = views.get(tag);
		if (view == null) {
			view = convertView.findViewWithTag(tag);
			views.put(tag, view);
		}
		return (T) view;
	}
	
	
	public View getConvertView(){
		return convertView;
	}
	
	public ViewHolder setText(String tag,String text){
		TextView txtView = getViewByTag(tag);
		txtView.setText(text);
		return this;
	}


	public <T extends View> T findViewWithTag(String tag) {
		View view = views.get(tag);
		if (view == null) {
			view = convertView.findViewWithTag(tag);
			views.put(tag, view);
		}
		return (T) view;
	}
	
	
	
	
	

}
