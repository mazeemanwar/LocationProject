package com.driverconnex.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.driverconnex.app.HomeActivity;
import com.driverconnex.app.R;
import com.driverconnex.singletons.DCSavingsSingleton;

/**
 * Dashboard for DriverConnex app.
 * 
 * @author Adrian Klimczak
 * 
 */

public class DriverConnexFragment extends Fragment {
	private DriverConnexPagerAdapter driverConnexPagerAdapter;
	private static ViewPager mViewPager;
	private static int selectedPageIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_driverconnex, container,
				false);
		driverConnexPagerAdapter = new DriverConnexPagerAdapter(getActivity()
				.getSupportFragmentManager());

		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(driverConnexPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				selectedPageIndex = position;

				switch (position) {
				case 0:
					driverConnexPagerAdapter.getRatingSectionFragment()
							.pageViewed();
					break;
				case 1:
					driverConnexPagerAdapter.getSavingsSectionFragment()
							.pageViewed();
					break;
				case 2:
					driverConnexPagerAdapter.getCommunitySectionFragment()
							.pageViewed();
					break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
		});

		// Performs initials calculations for savings.
		if (!DCSavingsSingleton.IsInitialCalculationPerformed())
			driverConnexPagerAdapter.getSavingsSectionFragment()
					.calculateSavings();

		// Create tab bar, by passing reference to linear layout
		LinearLayout tabBar = (LinearLayout) view.findViewById(R.id.tabBar);

		HomeActivity.createTabBar(getActivity(), tabBar, "");

		return view;
	}

	public static int getSelectedPageIndex() {
		return selectedPageIndex;
	}
}
