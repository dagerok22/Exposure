package com.noveogroup.evgeny.awersomeproject.ui.recycler;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noveogroup.evgeny.awersomeproject.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import clarifai2.dto.prediction.Concept;


public class TagListRecyclerViewAdapter extends RecyclerView.Adapter<TagListRecyclerViewAdapter.ViewHolder> {
    private List<Concept> data;
    private TagChooseListener tagChooseListener;

    public TagListRecyclerViewAdapter(List<Concept> data, TagChooseListener tagChooseListener) {
        this.tagChooseListener = tagChooseListener;
        this.data = data;
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
        return data.size();
    }

    public interface TagChooseListener {
        //result boolean shows was tag accepted
        boolean tagChosen(String tag, boolean checked);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tag_text)
        TextView tag;
        @BindView(R.id.tag_card)
        CardView cardView;
        int currentPosition;
        boolean checked;


        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @OnClick(R.id.tag_card)
        void onCardClick() {

            if (tagChooseListener.tagChosen(tag.getText().toString(), !checked)) {
                if (checked) {
                    cardView.setBackgroundColor(ContextCompat.getColor(tag.getContext(), R.color.cardview_light_background));
                } else {
                    cardView.setBackgroundColor(ContextCompat.getColor(tag.getContext(), R.color.toolbar));
                }
                checked = !checked;
            }
        }

        public void setPosition(int position) {
            tag.setText(data.get(position).name());
            currentPosition = position;
        }
    }
}