package com.example.qrshare;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class InstaVPA extends FragmentStateAdapter {
    public InstaVPA(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new Link_Fragment();
        }
        return new Username_Fragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}