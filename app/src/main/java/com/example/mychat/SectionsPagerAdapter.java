package com.example.mychat;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return new RequestsFragment();
        }else if(position==1){
            return new ChatsFragment();
        }else if (position==2){
            return new FriendsFragment();
        }
        else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0){
            return "Requests";
        }else if(position==1){
            return "Chats";
        }else if (position==2){
            return "Friends";
        }
        else{
            return null;
        }
    }
}
