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

import com.application.ui.fragment.FragmentLogIn;
import com.application.ui.fragment.FragmentSignUp;
import com.application.ui.fragment.FragmentPremium;

/**
 * @author Vikalp Patel (vikalppatelce@yahoo.com)
 * @category Ui Helper
 * 
 */
public class ViewPagerAdapterText extends FragmentPagerAdapter {
	final int PAGE_COUNT = 2;
	private final String[] TITLES = { "Login", "Registration" };

	public ViewPagerAdapterText(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int item) {
		switch (item) {
		case 0:
			FragmentLogIn fragmentLogIn = new FragmentLogIn();
			return fragmentLogIn;
		case 1:
			FragmentSignUp fragmentSignUp = new FragmentSignUp();
			return fragmentSignUp;
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return PAGE_COUNT;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position];
	}
}
