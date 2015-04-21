/**
 * 
 */
package com.application.ui.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.beans.Group;
import com.application.utils.Utilities;
import com.chat.ttogs.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class GroupGridAdapter extends BaseAdapter implements Filterable {

	private Context mContext;
	private ArrayList<Group> mListGroup;
	private ArrayList<Group> mListGroupFiltered;
	private GroupFilter GroupFilter;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions;

	public GroupGridAdapter(Context mContext, ArrayList<Group> mListGroup,
			ImageLoader mImageLoader, DisplayImageOptions mDisplayImageOptions) {
		this.mContext = mContext;
		this.mListGroup = mListGroup;
		this.mListGroupFiltered = mListGroup;
		this.mImageLoader = mImageLoader;
		this.mDisplayImageOptions = mDisplayImageOptions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListGroup.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v;
		ViewHolder holder = null;
		final Group mObj;

		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.item_group, null);
			holder = new ViewHolder();
			holder.mTextView = (TextView) v
					.findViewById(R.id.itemGroupTextView);
			holder.mImageView = (ImageView) v
					.findViewById(R.id.itemGroupImageView);
			holder.mTextViewCounter = (TextView)v.findViewById(R.id.itemGroupTextViewUnRead);
			v.setTag(holder);
		} else {
			v = convertView;
			holder = (ViewHolder) v.getTag();
		}

		mObj = mListGroup.get(position);

		holder.mTextView.setText(mObj.getGroupName());
		if(mObj.getGroupIsActive().equalsIgnoreCase("1")){
			holder.mTextView.setTextColor(Color.parseColor("#FFFFFF"));	
		}else{
			holder.mTextView.setTextColor(Color.parseColor("#FF0000"));
		}
		holder.mTextViewCounter.setText(mObj.getGroupUnreadCount());
		if(mObj.getGroupUnreadCount().equalsIgnoreCase("0")){
			holder.mTextViewCounter.setVisibility(View.INVISIBLE);
		}else{
			holder.mTextViewCounter.setVisibility(View.VISIBLE);
		}
		
		if(new File(mObj.getGroupImageLocal()).exists()){
			holder.mImageView.setImageBitmap(BitmapFactory.decodeFile(mObj.getGroupImageLocal()));
		}else{
			mImageLoader.displayImage(mObj.getGroupImagePath(), holder.mImageView, new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap mBitmap) {
					// TODO Auto-generated method stub
//					Utilities.writeBitmapToSDCardGroupImages(mBitmap, mObj.getGroupId());
					Utilities.writeBitmapToSDCardGroupImages(mBitmap, mObj.getGroupImagePath());
				}
				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					// TODO Auto-generated method stub
				}
			});
		}
		

		v.setTag(R.id.TAG_GROUP_ID, mObj.getGroupId());
		v.setTag(R.id.TAG_GROUP_NAME, mObj.getGroupName());
		v.setTag(R.id.TAG_GROUP_JABBER_ID, mObj.getGroupJabberId());
		v.setTag(R.id.TAG_GROUP_CITY_ID, mObj.getGroupCityId());
		v.setTag(R.id.TAG_GROUP_ACTIVE, mObj.getGroupIsActive());
//		v.setTag(R.id.TAG_GROUP_ACTIVE, "1");

		return v;
	}

	static class ViewHolder {
		TextView mTextView;
		ImageView mImageView;
		TextView mTextViewCounter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Filterable#getFilter()
	 */
	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		if (GroupFilter == null)
			GroupFilter = new GroupFilter();

		return GroupFilter;
	}

	public class GroupFilter extends Filter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Filter#performFiltering(java.lang.CharSequence)
		 */
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO Auto-generated method stub
			FilterResults results = new FilterResults();
			// We implement here the filter logic
			if (constraint == null || constraint.length() == 0) {
				// No filter implemented we return all the list
				results.values = mListGroupFiltered;
				results.count = mListGroupFiltered.size();
			} else {
				// We perform filtering operation
				List<Group> nPlanetList = new ArrayList<Group>();

				for (Group p : mListGroup) {
					if (p.getGroupName().toUpperCase()
							.startsWith(constraint.toString().toUpperCase()))
						nPlanetList.add(p);
				}

				results.values = nPlanetList;
				results.count = nPlanetList.size();

			}
			return results;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Filter#publishResults(java.lang.CharSequence,
		 * android.widget.Filter.FilterResults)
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// TODO Auto-generated method stub
			if (results.count == 0)
				notifyDataSetInvalidated();
			else {
				mListGroup = (ArrayList<Group>) results.values;
				notifyDataSetChanged();
			}
		}
	}

}
