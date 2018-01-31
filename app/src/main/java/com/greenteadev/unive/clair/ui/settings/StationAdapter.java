package com.greenteadev.unive.clair.ui.settings;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import it.greenteadev.unive.clair.R;
import com.greenteadev.unive.clair.data.model.Station;

/**
 * Created by Hitech95 on 30/01/2018.
 */

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> {
    private List<Station> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public AppCompatTextView mTitle;
        public AppCompatTextView mDescription;

        public ViewHolder(View container, AppCompatTextView vTitle, AppCompatTextView vDescription) {
            super(container);
            mTitle = vTitle;
            mDescription = vDescription;
        }
    }

    public StationAdapter() {
        this(new ArrayList<>());
    }

    public StationAdapter(List<Station> myDataset) {
        mDataset = new ArrayList<>(myDataset);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout container = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bookmark_station, parent, false);

        AppCompatTextView title = container.findViewById(R.id.tv_station_name);
        AppCompatTextView description = container.findViewById(R.id.tv_station_description);

        ViewHolder vh = new ViewHolder(container, title, description);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Station station = mDataset.get(position);
        holder.mTitle.setText(station.nome());
        holder.mDescription.setText(String.format("%s - %s,%s",
                station.localita(), station.comune(), station.provincia()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addItem(Station station) {
        mDataset.add(station);
    }

    public void addAll(List<Station> stationList) {
        mDataset = stationList;
    }

    public void remove(Station station) {
        mDataset.remove(station);
    }

    public void remove(int index) {
        mDataset.remove(index);
    }

    public void clear() {
        mDataset.clear();
    }
}
