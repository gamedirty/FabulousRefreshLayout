package com.example.administrator.fabulousrefreshlayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhjh on 2017/6/15.
 */

public class MainFragment extends Fragment implements Fragment1.ReleaseWatcher {

    Fragment1 fragment1 = new Fragment1();
    Fragment2 fragment2 = new Fragment2();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment1,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragment1.setWatcher(this);
        getFragmentManager().beginTransaction().replace(R.id.content,fragment1).commitAllowingStateLoss();
    }

    @Override
    public void onRelease() {
        FragmentTransaction tras = getFragmentManager().beginTransaction();
//        tras.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        tras.setCustomAnimations(R.anim.slide_up,R.anim.slide_bottom);
        FragmentTransaction re = tras.replace(R.id.content, fragment2);
        tras.commitAllowingStateLoss();
    }
}
