package com.noveogroup.evgeny.awersomeproject.ui.recycler;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.model.Task;
import com.noveogroup.evgeny.awersomeproject.util.DateTransformerUtil;
import com.noveogroup.evgeny.awersomeproject.util.LocationUtil;
import com.noveogroup.evgeny.awersomeproject.util.StringUtil;

import java.util.List;
import java.util.Locale;


public class TaskListRecyclerViewAdapter extends RecyclerView.Adapter<TaskListRecyclerViewAdapter.ViewHolder> {
    private Location currentLocation;
    private List<Task> dataSet;

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setDataSet(List<Task> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = dataSet.get(position);

        holder.title.setText(task.getName());
        holder.author.setText(task.getAuthorName());
        holder.tags.setText(StringUtil.getTagsString(task.getTags()));
        holder.age.setText(DateTransformerUtil.getAgeOfTask(task.getDate(), holder.title.getContext()));
        holder.rating.setText(String.format(Locale.ENGLISH, "%.1f", task.getRating()));
        currentLocation = LocationUtil.getLastUpdatedLocation();8
        if (currentLocation != null) {
            holder.distance.setText(String.format(Locale.ENGLISH, "%.1f km", (LocationUtil.getDistance(
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    new LatLng(task.getLat(), task.getLng()))) / 1000));
        } else {
            holder.distance.setText(R.string.find_user_coords);
        }

        Glide.with(holder.title.getContext())
                .load(task.getImageUrl())
                .centerCrop()
                .into(holder.imageView);

    }

    public List<Task> getItems() {
        return dataSet;
    }

    public void setItems(final List<Task> newItems) {
        dataSet.clear();
        dataSet.addAll(newItems);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView tags;
        TextView rating;
        TextView author;
        TextView age;
        TextView distance;

        ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.image);
            title = v.findViewById(R.id.title);
            tags = v.findViewById(R.id.tags);
            author = v.findViewById(R.id.author);
            age = v.findViewById(R.id.age);
            rating = v.findViewById(R.id.rating);
            distance = v.findViewById(R.id.distance);
        }
    }
}