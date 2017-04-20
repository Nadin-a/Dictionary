package com.example.android.vocabulary.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.vocabulary.R;
import com.example.android.vocabulary.data.WordContract;

public class MyWordRecyclerViewAdapter extends RecyclerView.Adapter<MyWordRecyclerViewAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public MyWordRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int idIndex = mCursor.getColumnIndex(WordContract.WordEntry._ID);
        int langIndex = mCursor.getColumnIndex(WordContract.WordEntry.COLUMN_LANGUAGES);
        int wordIndex = mCursor.getColumnIndex(WordContract.WordEntry.COLUMN_WORD);
        int translatedWordIndex = mCursor.getColumnIndex(WordContract.WordEntry.COLUMN_TRANSLATE);

        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(idIndex);
        String languages = mCursor.getString(langIndex);
        String word = mCursor.getString(wordIndex);
        String translated_word = mCursor.getString(translatedWordIndex);


        holder.mView.setTag(id);
        holder.mLanguages.setText(languages);
        holder.mWord.setText(word);
        holder.mTranslatedWord.setText(translated_word);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLanguages;
        public final TextView mWord;
        public final TextView mTranslatedWord;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLanguages = (TextView) view.findViewById(R.id.tvLanguages);
            mWord = (TextView) view.findViewById(R.id.tvWord);
            mTranslatedWord = (TextView) view.findViewById(R.id.tvTranslatedWord);
        }
    }
}
