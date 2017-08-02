package com.noveogroup.evgeny.awersomeproject.ui.recycler;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noveogroup.evgeny.awersomeproject.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TagListRecyclerViewAdapter extends RecyclerView.Adapter<TagListRecyclerViewAdapter.ViewHolder> {
    private List<ChosenTag> chosenTags;
    private ItemTapListener itemTapListener;
    private int notChooseColor;

    public void setNotChooseColor(int notChooseColor) {
        this.notChooseColor = notChooseColor;
    }

    public void setChooseColor(int chooseColor) {
        this.chooseColor = chooseColor;
    }

    private int chooseColor;
    private int choosenTextColor;
    private int notChoosenTextColor;
    private Context context;

    public TagListRecyclerViewAdapter(List<String> data, ItemTapListener itemTapListener, Context context) {
        this.context = context;
        this.chooseColor = ContextCompat.getColor(context, R.color.colorPrimary);
        this.choosenTextColor = ContextCompat.getColor(context, R.color.cosen_tag_text_color);
        this.notChooseColor = ContextCompat.getColor(context, R.color.cardview_light_background);
        this.notChoosenTextColor = ContextCompat.getColor(context, R.color.colorPrimary);
        this.itemTapListener = itemTapListener;
        this.chosenTags = new ArrayList<>();
        for (String tag : data) {
            chosenTags.add(new ChosenTag(tag));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setPosition(position);
    }

    @Override
    public int getItemCount() {
        return chosenTags.size();
    }

    public void setTagChosenState(String tag, boolean state) {
        int position = chosenTags.indexOf(new ChosenTag(tag));
        if (position != -1) {
            chosenTags.get(position).setChosen(state);
            this.notifyItemChanged(position);
        }
    }

    public interface ItemTapListener {
        void tagChosen(String tag, boolean checked);
    }

    private class ChosenTag {
        private String tag;
        private boolean chosen;

        ChosenTag(String tag) {
            this.tag = tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ChosenTag chosenTag = (ChosenTag) o;

            return tag != null ? tag.equals(chosenTag.tag) : chosenTag.tag == null;

        }

        @Override
        public int hashCode() {
            return tag != null ? tag.hashCode() : 0;
        }

        String getTag() {
            return tag;
        }

        boolean isChosen() {
            return chosen;
        }

        void setChosen(boolean chosen) {
            this.chosen = chosen;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tag_text)
        TextView tag;
        @BindView(R.id.tag_card)
        CardView cardView;
        int currentPosition;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @OnClick(R.id.tag_card)
        void onItemTap() {
            if (itemTapListener != null) {
                itemTapListener.tagChosen(tag.getText().toString(), chosenTags.get(currentPosition).isChosen());
            }
        }

        void setPosition(int position) {
            tag.setText(chosenTags.get(position).getTag());
            currentPosition = position;
            if (chosenTags.get(position).isChosen()) {
                cardView.setBackgroundColor(chooseColor);
                tag.setTextColor(choosenTextColor);
            } else {
                cardView.setBackgroundColor(notChooseColor);
                tag.setTextColor(notChoosenTextColor);
            }
        }
    }
}