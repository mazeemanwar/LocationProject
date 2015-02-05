package com.driverconnex.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Pager adapter for displaying pages. Used for navigation DriverConnex dashboard.
 * @author Adrian Klimczak
 *
 */

public class DriverConnexPagerAdapter extends FragmentPagerAdapter
{
	private RatingSectionFragment ratingSectionFragment = new RatingSectionFragment();
	private SavingsSectionFragment savingsSectionFragment = new SavingsSectionFragment();
	private CommunitySectionFragment communitySectionFragment = new CommunitySectionFragment();
	
	public DriverConnexPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}
	
    @Override
    public Fragment getItem(int i) 
    {
        switch (i) 
        {
            case 0:
            	 return ratingSectionFragment;
            case 1:
            	 return savingsSectionFragment;
            case 2:
                 return communitySectionFragment;
        }
        
        return null;
    }

    @Override
    public int getCount() {
    	// There are three pages
        return 3;
    }

	public RatingSectionFragment getRatingSectionFragment() {
		return ratingSectionFragment;
	}

	public SavingsSectionFragment getSavingsSectionFragment() {
		return savingsSectionFragment;
	}

	public CommunitySectionFragment getCommunitySectionFragment() {
		return communitySectionFragment;
	}
}
