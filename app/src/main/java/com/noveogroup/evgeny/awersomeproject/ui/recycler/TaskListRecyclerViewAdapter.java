package com.noveogroup.evgeny.awersomeproject.ui.recycler;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.model.Task;
import com.noveogroup.evgeny.awersomeproject.util.DateTransformerUtil;
import com.noveogroup.evgeny.awersomeproject.util.GlideLogger;
import com.noveogroup.evgeny.awersomeproject.util.ImageBlurUtil;
import com.noveogroup.evgeny.awersomeproject.util.LocationUtil;
import com.noveogroup.evgeny.awersomeproject.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class TaskListRecyclerViewAdapter extends RecyclerView.Adapter<TaskListRecyclerViewAdapter.ViewHolder> {
    private final Context context;
    private final FirebaseUser currentUser;
    private final String currentUserUid;
    private Location currentLocation;
    private List<Task> dataSet;

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setDataSet(List<Task> dataSet) {
        this.dataSet.clear();
        this.dataSet.addAll(dataSet);
    }

    public TaskListRecyclerViewAdapter(Context applicationContext, FirebaseUser currentUser) {
        context = applicationContext;
        this.currentUser = currentUser;
        dataSet = new ArrayList<>();
        currentUserUid = currentUser.getUid();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = dataSet.get(position);
        holder.title.setText(task.getName() + (task.isUserDone(currentUserUid) ? "[Done]" : ""));
        holder.author.setText(task.getAuthorName());
        holder.tags.setText(StringUtil.getTagsString(task.getTags()));
        holder.age.setText(DateTransformerUtil.getAgeOfTask(task.getDate(), holder.title.getContext()));
        holder.rating.setText(String.format(Locale.ENGLISH, "%.1f", task.getRating()));
        currentLocation = LocationUtil.getLastUpdatedLocation();
        if (currentLocation != null) {
            holder.distance.setText(String.format(Locale.ENGLISH, "%.1f km", (LocationUtil.getDistance(
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    new LatLng(task.getLat(), task.getLng()))) / 1000));
        } else {
            holder.distance.setText(R.string.find_user_coords);
        }

        if (task.isUserDone(currentUserUid)) {
            Glide.with(context)
                    .load(task.getImageUrl())
                    .bitmapTransform(new CenterCrop(holder.title.getContext()))
                    .into(holder.imageView);
        }else {
            Glide.with(context)
                    .load(task.getImageUrl())
                    .bitmapTransform(new CenterCrop(holder.title.getContext()), new BlurTransformation(holder.title.getContext(), 85))
                    .into(holder.imageView);
        }
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
        ContentFrameLayout topSectionContainer;

        ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.image);
            title = v.findViewById(R.id.title);
            tags = v.findViewById(R.id.tags);
            author = v.findViewById(R.id.author);
            age = v.findViewById(R.id.age);
            rating = v.findViewById(R.id.rating);
            distance = v.findViewById(R.id.distance);
            topSectionContainer = v.findViewById(R.id.top_section_container);
        }
    }
}