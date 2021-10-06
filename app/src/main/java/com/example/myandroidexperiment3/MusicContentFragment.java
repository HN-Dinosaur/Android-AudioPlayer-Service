package com.example.myandroidexperiment3;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicContentFragment extends Fragment {

    ImageView imageView;
    TextView textView;

    //动画
    ObjectAnimator animator;

    MainActivity activity;
    Button back;
    int position = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public void setData(int position){
        this.position = position;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_content, container, false);

        imageView = view.findViewById(R.id.musicIcon);
        textView = view.findViewById(R.id.musicName);
        back = view.findViewById(R.id.back);
        activity = (MainActivity) getActivity();

        textView.setText(Constant.names[position]);
        imageView.setImageResource(Constant.icons[position]);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.toGetMusicListView();
            }
        });
        animator = ObjectAnimator.ofFloat(imageView,"rotation",0f,360.0f);
        animator.setDuration(10000);//动画旋转一周的时间为10秒
        animator.setInterpolator(new LinearInterpolator());//匀速
        animator.setRepeatCount(-1);//-1表示设置动画无限循环
        //放歌
        activity.changeSong(position);
        return view;
    }

}