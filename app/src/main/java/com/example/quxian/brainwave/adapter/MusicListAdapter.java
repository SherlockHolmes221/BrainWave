package com.example.quxian.brainwave.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.model.MusicData;
import com.example.quxian.brainwave.widget.AroundCircleView;

import java.util.List;

//自定义歌曲信息列表的适配器
public class MusicListAdapter extends BaseAdapter {

    private List<MusicData> mMusicList;
    private LayoutInflater mInflater;
    private OnMusicStateChangeListener listener;

    public void setOnMusicStateChangeListener(OnMusicStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnMusicStateChangeListener{
        void playOrPause();
    }

    public MusicListAdapter(Context context, List<MusicData> mMusicList) {
        this.mMusicList = mMusicList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Object getItem(int i) {
        return mMusicList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        MusicData musicInfo = mMusicList.get(i);
        ViewHolder viewHolder =null;

        if(view == null){
            view = mInflater.inflate(R.layout.item_music_list,viewGroup,false);
            viewHolder = new ViewHolder();
            //viewHolder.mTime = (TextView)view.findViewById(R.id.item_music_time);
            viewHolder.circle = view.findViewById(R.id.item_music_play);
            viewHolder.mSinger = (TextView) view.findViewById(R.id.item_music_singer);
            viewHolder.mSongName = (TextView) view.findViewById(R.id.item_music_name);

            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if(!musicInfo.isPlaying()){
            viewHolder.circle.setImageResource(R.drawable.ic_play);
        }else {
            viewHolder.circle.setImageResource(R.drawable.ic_pause);
        }
        viewHolder.mSongName.setText(musicInfo.getMusicName());
        viewHolder.mSinger.setText(musicInfo.getMusicAuthor());
        viewHolder.circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.playOrPause();
            }
        });
        //viewHolder.mTime.setText(String.valueOf(musicInfo.getmTime()));
        return view;
    }

    public final class ViewHolder{
        TextView mSinger;
        TextView mSongName;
        AroundCircleView circle;
        //TextView mTime;
    }
}

