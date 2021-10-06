package com.example.myandroidexperiment3;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class MusicListFragment extends Fragment {


    MyBaseAdapter myBaseAdapter;
    ListView musicList;
    MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list, container, false);
        //fragment之间的通讯
        activity = (MainActivity) getActivity();

        musicList = view.findViewById(R.id.music_list);
        myBaseAdapter = new MyBaseAdapter();
        musicList.setAdapter(myBaseAdapter);


        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MusicContentFragment musicContentFragment = activity.musicContentFragment;
                musicContentFragment.setData(i);
                activity.toGetMusicContentView();
            }
        });
        return view;
    }



    class MyBaseAdapter extends BaseAdapter {
        @Override
        public int getCount(){return  Constant.names.length;}
        @Override
        public Object getItem(int i){return Constant.names[i];}
        @Override
        public long getItemId(int i){return i;}

        @Override
        public View getView(int i ,View convertView, ViewGroup parent) {
            View view=View.inflate(MusicListFragment.this.getContext(),R.layout.fragment_list_item,null);
            TextView tv_name=view.findViewById(R.id.item_name);
            ImageView iv=view.findViewById(R.id.iv);

            tv_name.setText(Constant.names[i]);
            iv.setImageResource(Constant.icons[i]);
            return view;
        }
    }



}