/* HISTORY
 * CATEGORY			 :- VIEW | HELPER 
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- PROVIDE FRAGMENT FOR TABS
 * NOTE: HANDLE WITH CARE 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * TU001      VIKALP PATEL     29/07/2014                       CREATED
 * --------------------------------------------------------------------------------------------------------------------
 * 
 * *****************************************METHODS INFORMATION******************************************************** 
 * ********************************************************************************************************************
 * DEVELOPER		  METHOD								DESCRIPTION
 * ********************************************************************************************************************
 * VIKALP PATEL                          			
 * ********************************************************************************************************************
 *
 */

package com.application.ui.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.application.ui.fragment.FragmentAttendance;
import com.application.ui.fragment.FragmentLead;
import com.application.ui.fragment.FragmentPremium;
import com.application.ui.view.PagerSlidingTabStrip.IconTabProvider;
import com.chat.ttogs.R;

/**
 * @author Vikalp Patel (vikalppatelce@yahoo.com)
 * @category Ui Helper
 * 
 */
public class ViewPagerAdapter extends FragmentPagerAdapter implements
		IconTabProvider {
	final int PAGE_COUNT = 3;
	private final int[] ICONS = { R.drawable.tab_icon_feed_home_selector,
			R.drawable.tab_icon_feed_friends_selector, R.drawable.tab_icon_feed_popular_selector,
			R.drawable.tab_icon_feed_report_selector };

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int item) {
		switch (item) {
		case 0:
			FragmentAttendance fragmentAttendance = new FragmentAttendance();
			return fragmentAttendance;
		case 1:
			FragmentPremium fragmentPremium = new FragmentPremium();
			return fragmentPremium;
		case 2:
			FragmentLead fragmentLead = new FragmentLead();
			return fragmentLead;
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return PAGE_COUNT;
	}

	@Override
	public int getPageIconResId(int position) {
		// TODO Auto-generated method stub
		return ICONS[position];
	}
}
