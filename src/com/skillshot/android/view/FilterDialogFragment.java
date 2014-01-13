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
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.skillshot.android.LocationListActivity;
import com.skillshot.android.MapActivity;
import com.skillshot.android.R;

public class FilterDialogFragment extends DialogFragment {
	private ArrayList<String> filterCheckboxes;
	private FilterDialogListener filterListener;
	private SortingDialogListener sortingListener;
	private boolean sorting = true;

	public interface FilterDialogListener {
		public void onFilterCheckboxes(ArrayList<String> filters);
	}

	public interface SortingDialogListener {
		public void onSort(int which);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			filterListener = (FilterDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement FilterDialogListener");
		}
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			sortingListener = (SortingDialogListener) activity;
		} catch (ClassCastException e) {
			// nbd, we're probably in the map view without sorting
			sorting = false;
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.dialog_filter, null);
		builder.setView(view);
		
		Bundle args = getArguments();

		// restore checked states
		final List<String> checkboxStrings = Arrays.asList(getResources()
				.getStringArray(R.array.map_filter_checkboxes));
		boolean[] filtersChecked = new boolean[checkboxStrings.size()];
		Arrays.fill(filtersChecked, false);
		if (args != null) {
			if (args.containsKey(MapActivity.FILTER_ALL_AGES)) {
				int filterIndex = checkboxStrings.indexOf(getResources()
						.getString(R.string.all_ages));
				filtersChecked[filterIndex] = args
						.getBoolean(MapActivity.FILTER_ALL_AGES);
			}
		}

		int sortingChecked = R.string.alphabetical;
		if (args != null && args.get(LocationListActivity.FILTER_SORT) != null) {
			sortingChecked = (Integer) args.get(LocationListActivity.FILTER_SORT);
		}

		// Use the Builder class for convenient dialog construction
		filterCheckboxes = new ArrayList<String>();
		
		builder.setTitle("Filter locations")
				.setPositiveButton(R.string.close, null)
				
				// Filter checkboxes
				.setMultiChoiceItems(R.array.map_filter_checkboxes,
						filtersChecked,
						new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								String resource = checkboxStrings.get(which);
								if (isChecked) {
									filterCheckboxes.add(resource);
								} else if (filterCheckboxes.contains(resource)) {
									filterCheckboxes.remove(resource);
								}
								filterListener.onFilterCheckboxes(filterCheckboxes);
							}
						});

		if (sorting) {
			// Sorting radio buttons
			RadioGroup sortRadio = new RadioGroup(getActivity());
			sortRadio.addView(makeButton(R.string.alphabetical));
			sortRadio.addView(makeButton(R.string.distance));
			sortRadio.addView(makeButton(R.string.number_of_games));
			sortRadio.check(sortingChecked);
			
			sortRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int which) {
					sortingListener.onSort(which);
				}
			});
			
			TextView sortTitle = new TextView(getActivity());
			sortTitle.setTextAppearance(getActivity(), android.R.style.TextAppearance_Large);
			sortTitle.setText(R.string.sort);
			view.addView(sortTitle);
			view.addView(sortRadio);
		}
		
		return builder.create();
	}
	
	private RadioButton makeButton(int stringId) {
		RadioButton radio = new RadioButton(getActivity());
		radio.setText(getResources().getString(stringId));
		radio.setId(stringId);
		return radio;
	}
}
