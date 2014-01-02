package com.skillshot.android.view;

import java.util.ArrayList;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.skillshot.android.rest.model.Title;

public class TitleAdapter extends ArrayAdapter<Title> {
    boolean enabled = true;
    
	public TitleAdapter(Context context, int layoutResourceId, ArrayList<Title> data) {
		super(context, layoutResourceId, data);
	}
	
    @Override
    public boolean isEnabled(int position) {
        return enabled;
    }

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
    
}
