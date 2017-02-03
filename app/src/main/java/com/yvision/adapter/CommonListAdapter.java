package com.yvision.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class CommonListAdapter extends BaseAdapter{

	public Context context;
	public LayoutInflater inflater;
	public AdapterCallBack callBack; 
	public ArrayList entityList = new ArrayList();
	public boolean IsEnd=false;//翻页设置
	public static ArrayList<Boolean> isCheckedList = null;//用于标记checkBox值

	public CommonListAdapter(Context context,AdapterCallBack callBack){
		this.context = context;
		this.callBack = callBack;
		inflater = LayoutInflater.from(context);
	}
	
	public interface AdapterCallBack{
		void loadMore();
	}
	
	@Override
	public int getCount() {
		if(entityList != null){
			return entityList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return entityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	} 
	
	public ArrayList getEntityList() {
		return entityList;
	}

	public void setEntityList(ArrayList entityList) {
//		this.entityList = entityList;//
		Log.d("SJY", "父类--CommonListAdapter--set-entityList="+entityList);
		this.entityList.clear();
		this.entityList.addAll(entityList);
		notifyDataSetChanged();
	}
	
	public void addEntityList(ArrayList entityList) { 
		Log.d("SJY", "父类--CommonListAdapter--add-entityList="+entityList);
		this.entityList.addAll(entityList);
		notifyDataSetChanged();
	}
	public void insertEntityList(ArrayList entityList){
		Log.d("SJY","父类--CommonListAdapter--insert-entityList="+entityList);
		if(entityList != null){
			this.entityList.addAll(0,entityList);
		}
		notifyDataSetChanged();

	}
    public void addEntity(Object entity){
        this.entityList.add(entity);
        notifyDataSetChanged();
    }
	
	protected abstract View inflateConvertView();
	
	protected abstract void initViewData(int position, View convertView); 

	@Override
	public View getView(int position, View convertView, ViewGroup parent) { 
		if(convertView == null){
			convertView = inflateConvertView();
		} 
		Log.d("SJY", "CommonListAdapter--position="+position);//
		initViewData(position, convertView);
		//加载数据
		if(position == getCount() - 1  && !IsEnd){//19行加载
			callBack.loadMore();
		} 
		return convertView;
	}

}
