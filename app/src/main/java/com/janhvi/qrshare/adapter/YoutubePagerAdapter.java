package com.janhvi.qrshare.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.janhvi.qrshare.fragment.FirstSocialFragment;
import com.janhvi.qrshare.fragment.SecondSocialFragment;
import com.janhvi.qrshare.utility.Constants;

public class YoutubePagerAdapter extends FragmentStateAdapter {

    public YoutubePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return new FirstSocialFragment(Constants.YOUTUBE);
        else
            return new SecondSocialFragment(Constants.YOUTUBE);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

