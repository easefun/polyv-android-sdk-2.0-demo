package com.easefun.polyvsdk.adapter;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PolyvPlayerFragmentAdapter extends FragmentPagerAdapter {
	List<Fragment> fragmentList = new ArrayList<Fragment>();

	public PolyvPlayerFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	public PolyvPlayerFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
		super(fm);
		this.fragmentList = fragmentList;
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}

}
