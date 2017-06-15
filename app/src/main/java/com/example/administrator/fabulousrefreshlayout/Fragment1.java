package com.example.administrator.fabulousrefreshlayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by zhjh on 2017/6/15.
 */

public class Fragment1 extends Fragment implements PullUpLoadLayout.OnReleaseListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initview();
    }

    private void initview() {
        final RecyclerView view = (RecyclerView) getView().findViewById(R.id.list);
        view.setLayoutManager(new LinearLayoutManager(getContext()));
        view.setAdapter(new MyAdapter());
        PullUpLoadLayout pullUpLoadLayout = (PullUpLoadLayout) getView().findViewById(R.id.pull_up_layout);
        pullUpLoadLayout.setUpper(new PullUpLoadLayout.IUpSlidable() {
            @Override
            public boolean canPullUp() {
                if (view == null) return false;
                if (view.computeVerticalScrollExtent() + view.computeVerticalScrollOffset()
                        >= view.computeVerticalScrollRange())
                    return true;
                return false;
            }
        });
        pullUpLoadLayout.setOnReleaseListener(this);
    }

    @Override
    public void onRelease() {
if (watcher!=null){
    watcher.onRelease();
}
    }

    static class MyAdapter extends RecyclerView.Adapter<MyViewholder>{


        @Override
        public MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new TextView(parent.getContext());
            return new MyViewholder(view);
        }

        @Override
        public void onBindViewHolder(MyViewholder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 50;
        }
    }

    static class MyViewholder extends RecyclerView.ViewHolder{

        public MyViewholder(View itemView) {
            super(itemView);
            TextView tv = (TextView) itemView;
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(10,10,10,10);
            tv.setText("我就是啊哈哈");
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(),"你点到我了",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    private ReleaseWatcher watcher;
    public void setWatcher(ReleaseWatcher watcher){
        this.watcher = watcher;
    }

    public interface ReleaseWatcher{
        void onRelease();
    }

}
