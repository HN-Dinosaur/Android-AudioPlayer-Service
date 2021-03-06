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

                Bundle bundle = msg.getData();//???????????????????????????????????????????????????
                int duration = bundle.getInt("duration");
                int currentPosition = bundle.getInt("currentPosition");
                seekBar.setMax(duration);
                seekBar.setProgress(currentPosition);
                //???????????????
                int minute = duration / 1000 / 60;
                int second = duration / 1000 % 60;
                String totalMinute = null;
                String totalSecond = null;
                String currMinute = null;
                String currSecond = null;
                if(minute < 10){//???????????????????????????????????????10
                    totalMinute = "0" + minute;//???????????????????????????0
                }else{
                    totalMinute = minute+"";
                }
                if (second < 10){//??????????????????????????????10
                    totalSecond="0" + second;//????????????????????????0
                }else{
                    totalSecond=second+"";
                }
                //????????????????????????
                minute = currentPosition / 1000 / 60;
                second = currentPosition / 1000 % 60;
                if(minute<10){//???????????????????????????????????????10
                    currMinute = "0" + minute;//???????????????????????????0
                }else{
                    currMinute = minute+" ";
                }
                if (second<10){//??????????????????????????????10
                    currSecond = "0" + second;//????????????????????????0
                }else{
                    currSecond = second+" ";
                }
                time.setText(currMinute + ":" + currSecond + "/" + totalMinute + ":" + totalSecond);
            }
        };

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //??????????????????????????????
            @Override
            public void onProgressChanged(SeekBar seekBar, int process, boolean b) {
                if(process == seekBar.getMax()){
                    musicContentFragment.animator.pause();
                }
            }
            //???????????????
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            //???????????????
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();//??????seekBar?????????
                musicControl.seekTo(progress);//??????????????????
            }
        });




    }
    class MyServiceConn implements ServiceConnection {//????????????????????????
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
        //????????????
        position = musicContentFragment.position;
        seekbarControl.setVisibility(View.VISIBLE);
        songControl.setVisibility(View.VISIBLE);
    }

    public static void changeSong(int position){
        musicContentFragment.imageView.setImageResource(Constant.icons[position]);
        musicContentFragment.textView.setText(Constant.names[position]);
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
              // ?????????
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