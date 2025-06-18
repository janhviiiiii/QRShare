package com.janhvi.qrshare.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.janhvi.qrshare.fragment.InstagramLinkFragment;
import com.janhvi.qrshare.fragment.InstagramUsernameFragment;

public class InstagramPagerAdapter extends FragmentStateAdapter {

    public InstagramPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return new InstagramUsernameFragment();
        else
            return new InstagramLinkFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

