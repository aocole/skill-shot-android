package com.skillshot.android.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.skillshot.android.LocationListActivity;
import com.skillshot.android.R;
import com.skillshot.android.rest.model.Location;

public class LocationAdapter extends ArrayAdapter<Location> {
	Context context; 
    int layoutResourceId;    
    ArrayList<Location> data = null;
    
	public LocationAdapter(Context context, int layoutResourceId, ArrayList<Location> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LocationHolder holder = null;
        LocationListActivity activity = ((LocationListActivity)context);
        if(row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new LocationHolder();
            holder.title = (TextView)row.findViewById(R.id.title);
            holder.distance = (TextView)row.findViewById(R.id.distance);
            holder.numGames = (TextView)row.findViewById(R.id.num_games);
            
            row.setTag(holder);
        } else {
            holder = (LocationHolder)row.getTag();
        }
        
        Location location = getItem(position);
        holder.title.setText(location.getName());
        holder.numGames.setText(activity.numGamesString(location.getNum_games()));
		if(activity.getUserLocation() != null) {
			holder.distance.setText(activity.userDistanceString(location));
		} else {
			holder.distance.setText("");
		}
        
        return row;
    }
    
    static class LocationHolder
    {
        TextView title;
        TextView distance;
        TextView numGames;
    }
    
}
