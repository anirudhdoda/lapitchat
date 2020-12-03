package com.example.android.lapitchat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                RequestsFragments requestsFragments = new RequestsFragments();
                return requestsFragments;
            }
            case 1: {
                ChatsFragments chatsFragments = new ChatsFragments();
                return chatsFragments;
            }
            case 2:{
                FriendsFragments friendsFragments=new FriendsFragments();
                return friendsFragments;
            }
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }
    public CharSequence getPageTitle(int position){
        switch (position) {
            case 0:{
                return "REQUESTS";
            }
            case 1:{
                return "CHATS";
            }
            case 2:{
                return "FRIENDS";
            }
            default:{
                return null;
            }

        }
    }
}
