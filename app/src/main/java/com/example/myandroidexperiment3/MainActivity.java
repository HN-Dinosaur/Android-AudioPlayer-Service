package com.example.myandroidexperiment3;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static Handler handler;

    LinearLayout seekbarControl,songControl;
    SeekBar seekBar;
    ImageButton next;
    ImageButton previous;
    static ImageButton playOrPause;
    ImageButton musicStyle;
    TextView time;

    static MusicService.MusicControl musicControl;
    private MyServiceConn myServiceConn;

    static int position;
    static int musicType = 0;

    MusicListFragment musicListFragment = new MusicListFragment();
    static MusicContentFragment musicContentFragment = new MusicContentFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        toGetMusicListView();
    }

    void init(){
        seekBar = findViewById(R.id.seekbar);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        playOrPause = findViewById(R.id.playOrPause);
        musicStyle = findViewById(R.id.musicStyle);
        time = findViewById(R.id.time);
        seekbarControl = findViewById(R.id.seekBarControl);
        songControl = findViewById(R.id.songControl);

        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        playOrPause.setOnClickListener(this);
        musicStyle.setOnClickListener(this);

        Intent intent = new Intent(MainActivity.this,MusicService.class);
        myServiceConn = new MyServiceConn();
        bindService(intent, myServiceConn, BIND_AUTO_CREATE);



        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                Bundle bundle = msg.getData();//获取从子线程发送过来的音乐播放进度
                int duration = bundle.getInt("duration");
                int currentPosition = bundle.getInt("currentPosition");
                seekBar.setMax(duration);
                seekBar.setProgress(currentPosition);
                //歌曲总时长
                int minute = duration / 1000 / 60;
                int second = duration / 1000 % 60;
                String totalMinute = null;
                String totalSecond = null;
                String currMinute = null;
                String currSecond = null;
                if(minute < 10){//如果歌曲的时间中的分钟小于10
                    totalMinute = "0" + minute;//在分钟的前面加一个0
                }else{
                    totalMinute = minute+"";
                }
                if (second < 10){//如果歌曲中的秒钟小于10
                    totalSecond="0" + second;//在秒钟前面加一个0
                }else{
                    totalSecond=second+"";
                }
                //歌曲当前播放时长
                minute = currentPosition / 1000 / 60;
                second = currentPosition / 1000 % 60;
                if(minute<10){//如果歌曲的时间中的分钟小于10
                    currMinute = "0" + minute;//在分钟的前面加一个0
                }else{
                    currMinute = minute+" ";
                }
                if (second<10){//如果歌曲中的秒钟小于10
                    currSecond = "0" + second;//在秒钟前面加一个0
                }else{
                    currSecond = second+" ";
                }
                time.setText(currMinute + ":" + currSecond + "/" + totalMinute + ":" + totalSecond);
            }
        };

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //进度条改变调用该回调
            @Override
            public void onProgressChanged(SeekBar seekBar, int process, boolean b) {
                if(process == seekBar.getMax()){
                    musicContentFragment.animator.pause();
                }
            }
            //进度条开始
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            //进度条停止
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();//获取seekBar的进度
                musicControl.seekTo(progress);//改变播放进度
            }
        });




    }
    class MyServiceConn implements ServiceConnection {//用于实现连接服务
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            musicControl = (MusicService.MusicControl) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name){

        }
    }


    void toGetMusicListView(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,musicListFragment);
        fragmentTransaction.commit();
    }

    void toGetMusicContentView(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,musicContentFragment);
        fragmentTransaction.commit();
        //得到当前
        position = musicContentFragment.position;
        seekbarControl.setVisibility(View.VISIBLE);
        songControl.setVisibility(View.VISIBLE);
    }

    public static void changeSong(int position){
        musicContentFragment.imageView.setImageResource(Constant.icons[position]);
        musicContentFragment.textView.setText(Constant.names[position]);
        //释放原来的资源
        musicControl.releasePlayer();
        musicControl.play(position);
        playOrPause.setImageResource(R.mipmap.pause);
        musicContentFragment.animator.start();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.next:
                if(position < 2 ){
                    position += 1;
                }else{
                    position = 0;
                }
                changeSong(position);
                break;
            case R.id.previous:
                if(position > 0){
                    position -= 1;
                }else{
                    position = 2;
                }
                changeSong(position);
                break;
            case R.id.playOrPause:
              // 在播放
                if (musicControl.isPlay()){
                    playOrPause.setImageResource(R.mipmap.play);
                    musicControl.pausePlay();
                    musicContentFragment.animator.pause();
                }else{
                    playOrPause.setImageResource(R.mipmap.pause);
                    musicControl.continuePlay();
                    musicContentFragment.animator.start();
                }
                break;
            case R.id.musicStyle:
                musicType += 1;
                if(musicType > 2){
                    musicType = 0;
                }
                switch (musicType){
                    case 0:
                        musicStyle.setImageResource(R.mipmap.order);
                        break;
                    case 1:
                        musicStyle.setImageResource(R.mipmap.random);
                        break;
                    case 2:
                        musicStyle.setImageResource(R.mipmap.single);
                        break;
                }

                break;
        }
    }

}