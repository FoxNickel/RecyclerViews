package cn.foxnickel.recyclerviews.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.foxnickel.recyclerviews.R;


public class GridRecyclerFragment extends Fragment {

    private View mRootView;

    public GridRecyclerFragment() {

    }

    public static GridRecyclerFragment newInstance() {
        GridRecyclerFragment fragment = new GridRecyclerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_grid_recycler, container, false);
        return mRootView;
    }
}
