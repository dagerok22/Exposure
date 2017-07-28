package com.noveogroup.evgeny.awersomeproject.ui.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noveogroup.evgeny.awersomeproject.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class TaskDetailsActivityFragment extends Fragment {

    public TaskDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_details, container, false);
    }
}
