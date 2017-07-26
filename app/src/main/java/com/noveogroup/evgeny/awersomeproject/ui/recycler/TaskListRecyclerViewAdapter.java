package com.noveogroup.evgeny.awersomeproject.ui.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.model.Task;
import com.noveogroup.evgeny.awersomeproject.util.DateTransformerUtil;

import java.util.Iterator;
import java.util.List;


public class TaskListRecyclerViewAdapter extends RecyclerView.Adapter<TaskListRecyclerViewAdapter.ViewHolder> {
    private List<Task> dataSet;

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView tags;
        TextView author;
        TextView age;

        ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.image);
            title = v.findViewById(R.id.title);
            tags = v.findViewById(R.id.tags);
            author = v.findViewById(R.id.author);
            age = v.findViewById(R.id.age);
        }
    }

    public TaskListRecyclerViewAdapter(List<Task> data) {
        this.dataSet = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = dataSet.get(position);
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = task.getTags().iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next());
            if (iterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }
        holder.title.setText(task.getName());
        holder.author.setText(task.getAuthorName());
        holder.tags.setText(stringBuilder.toString());
        holder.age.setText(DateTransformerUtil.getAgeOfTask(task.getDate(), holder.title.getContext()));
        Glide.with(holder.title.getContext())
                .load(task.getImageUrl())
                .centerCrop()
                .into(holder.imageView);

    }

    public List<Task> getItems() {
        return dataSet;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(final List<Task> newItems) {
        dataSet.clear();
        dataSet.addAll(newItems);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}