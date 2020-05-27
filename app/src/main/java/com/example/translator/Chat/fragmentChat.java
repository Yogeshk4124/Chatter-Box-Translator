package com.example.translator.Chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.translator.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class fragmentChat extends Fragment{
    View v;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager pager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.chat_fragment, container, false);
        tabLayout=v.findViewById(R.id.tabLayout);
        pager=v.findViewById(R.id.pager);
        ifragmentchat cl=new ifragmentchat();
        ifragmentContacts contacts=new ifragmentContacts();
        tabLayout.setupWithViewPager(pager);
        ViewPagerAdapter pagerAdapter=new ViewPagerAdapter(getChildFragmentManager(),0);
        pagerAdapter.addFrag(cl,"Chats");
        pagerAdapter.addFrag(contacts,"Contacts");
        pager.setAdapter(pagerAdapter);
        return v;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment>fragments=new ArrayList<>();
        private List<String>title=new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        public void addFrag(Fragment fragment,String t){
            fragments.add(fragment);
            title.add(t);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public CharSequence getPageTitle(int pos){
            return title.get(pos);
        }
    }
}
