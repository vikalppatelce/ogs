/**
 * 
 */
package com.application.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.application.beans.City;
import com.chat.ttogs.R;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class CityGridAdapter extends BaseAdapter implements Filterable {

	private Context mContext;
	private ArrayList<City> mListCity;
	private ArrayList<City> mListCityFiltered;
	private CityFilter cityFilter;

	public CityGridAdapter(Context mContext, ArrayList<City> mListCity) {
		this.mContext = mContext;
		this.mListCity = mListCity;
		this.mListCityFiltered = mListCity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListCity.size();
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
		final City mObj;

		if (convertView == null) {
			LayoutInflater li = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.item_city, null);
			holder = new ViewHolder();
			holder.mTextView = (TextView) v.findViewById(R.id.textViewCityName);
			holder.mTextViewCounter = (TextView)v.findViewById(R.id.textViewCityNameUnreadCounter);
			v.setTag(holder);
		} else {
			v = convertView;
			holder = (ViewHolder) v.getTag();
		}

		mObj = mListCity.get(position);

		holder.mTextView.setText(mObj.getCityName());
		
		if(mObj.getCityIsActive() == 1){
			holder.mTextView.setTextColor(Color.parseColor("#FFFFFF"));
		}else{
			holder.mTextView.setTextColor(Color.parseColor("#FF0000"));
		}

		if(mObj.getCityUnreadCount().equalsIgnoreCase("0")){
			holder.mTextViewCounter.setVisibility(View.GONE);
		}else{
			holder.mTextViewCounter.setVisibility(View.VISIBLE);
			holder.mTextViewCounter.setText(mObj.getCityUnreadCount());
		}
		v.setTag(R.id.TAG_CITY_ID, mObj.getCityId());
		v.setTag(R.id.TAG_CITY_NAME, mObj.getCityName());
		v.setTag(R.id.TAG_CITY_ACTIVE, mObj.getCityIsActive());

		return v;
	}

	static class ViewHolder {
		TextView mTextView;
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
		if (cityFilter == null)
			cityFilter = new CityFilter();

		return cityFilter;
	}

	public class CityFilter extends Filter {

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
				results.values = mListCityFiltered;
				results.count = mListCityFiltered.size();
			} else {
				// We perform filtering operation
				List<City> nPlanetList = new ArrayList<City>();

				for (City p : mListCity) {
					if (p.getCityName().toUpperCase()
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
				mListCity = (ArrayList<City>) results.values;
				notifyDataSetChanged();
			}
		}
	}

}
