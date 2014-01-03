package com.skillshot.android.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.skillshot.android.LocationActivity;
import com.skillshot.android.R;
import com.skillshot.android.rest.model.Machine;

public class MachineAdapter extends ArrayAdapter<Machine> {
	Context context; 
    int layoutResourceId;    
    ArrayList<Machine> data = null;
    boolean isLoggedIn;
    boolean enabled = true;
    
	public MachineAdapter(Context context, int layoutResourceId, ArrayList<Machine> data, boolean isLoggedIn) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.isLoggedIn = isLoggedIn;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MachineHolder holder = null;
        LocationActivity activity = ((LocationActivity)context);
        if(row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new MachineHolder();
            holder.title = (TextView)row.findViewById(R.id.title);
            if (isLoggedIn) {
	            holder.delete = (ImageButton)row.findViewById(R.id.delete);
	            holder.delete.setOnClickListener(activity.new MachineDeleteClickListener());
	    		holder.delete.getDrawable().setColorFilter(LocationActivity.BUTTON_COLOR, Mode.SRC_ATOP);
	            holder.delete.setVisibility(View.VISIBLE);
            }
            
            row.setTag(holder);
        } else {
            holder = (MachineHolder)row.getTag();
        }
        
        Machine machine = data.get(position);
        holder.title.setText(machine.getTitle().getName());
        if (holder.delete != null) {
        	holder.delete.setTag(machine);
            holder.delete.setEnabled(enabled);
        }
        
        return row;
    }
    
    static class MachineHolder
    {
        TextView title;
        ImageButton delete;
    }
    
    @Override
    public boolean isEnabled(int position) {
        return isLoggedIn && enabled;
    }

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		notifyDataSetChanged();
	}
    
}
