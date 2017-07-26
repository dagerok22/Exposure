package com.noveogroup.evgeny.awersomeproject.ui.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noveogroup.evgeny.awersomeproject.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import clarifai2.dto.prediction.Concept;


public class TagListRecyclerViewAdapter extends RecyclerView.Adapter<TagListRecyclerViewAdapter.ViewHolder> {
    @BindView(R.id.tag_text)
    TextView tagTextView;
    private List<Concept> data;

    public TagListRecyclerViewAdapter(List<Concept> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setTag(data.get(position).name());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tag_text)
        TextView tag;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        void setTag(String tagString) {
            tag.setText(tagString);
        }
    }
}