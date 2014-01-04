package com.skillshot.android.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.skillshot.android.MapActivity;
import com.skillshot.android.R;

public class FilterDialogFragment extends DialogFragment {
	private ArrayList<String> filterCheckboxes;
	private FilterDialogListener mListener;
	
	public interface FilterDialogListener {
        public void onFilterCheckboxes(ArrayList<String> filters);
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (FilterDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement FilterDialogListener");
        }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		
		// restore checked states
		final List<String> checkbox_strings = Arrays.asList(getResources().getStringArray(R.array.map_filter_checkboxes));
		boolean[] filters_checked = new boolean[checkbox_strings.size()]; 
		Arrays.fill(filters_checked, false);
		if (args != null) {
			if (args.containsKey(MapActivity.FILTER_ALL_AGES)) {
				int filterIndex = checkbox_strings.indexOf(getResources().getString(R.string.all_ages));
				filters_checked[filterIndex] = args.getBoolean(MapActivity.FILTER_ALL_AGES);
			}
		}
		
		// Use the Builder class for convenient dialog construction
		filterCheckboxes = new ArrayList<String>();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Filter locations")
				.setPositiveButton(R.string.close, null)
				.setMultiChoiceItems(R.array.map_filter_checkboxes, filters_checked,
						new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								String resource = checkbox_strings.get(which);
								if (isChecked) {
									filterCheckboxes.add(resource);
								} else if (filterCheckboxes.contains(resource)) {
									filterCheckboxes.remove(resource);
								}
								mListener.onFilterCheckboxes(filterCheckboxes);
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
