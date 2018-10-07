package com.ikazme.tasklist.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ikazme.tasklist.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioNotesAdapter extends RecyclerView.Adapter<AudioNotesAdapter.ViewHolder> {

    private final List<String> mAudioNotes;
    private final Context mContext;

    public AudioNotesAdapter(List<String> mAudioNotes, Context mContext) {
        this.mAudioNotes = mAudioNotes;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AudioNotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.audio_note_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioNotesAdapter.ViewHolder viewHolder, int i) {
        final String name = mAudioNotes.get(i);
        viewHolder.audioNoteName.setText(name);
    }

    @Override
    public int getItemCount() {
        return mAudioNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.audioNoteName)
        TextView audioNoteName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
