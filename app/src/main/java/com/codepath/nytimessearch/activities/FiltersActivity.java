package com.codepath.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.data.Filters;
import com.codepath.nytimessearch.fragments.DatePickerFragment;

public class FiltersActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    EditText etDatePicker;
    DatePickerFragment fragment;
    Button saveButton;
    Spinner sortOrderSpinner;
    Filters filters;
    CheckBox artsCheckBox;
    CheckBox fashionCheckBox;
    CheckBox sportsCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        saveButton = (Button)findViewById(R.id.btnSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveValues();
                finish();
            }
        });

        sortOrderSpinner = (Spinner)findViewById(R.id.spSortOrder);
        etDatePicker = (EditText)findViewById(R.id.etBeginDate);
        fragment = new DatePickerFragment();
        etDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putParcelable("filters", filters);
                fragment.setArguments(args);
                fragment.show(getSupportFragmentManager(), "date_picker");
            }
        });
        artsCheckBox = (CheckBox)findViewById(R.id.cbArts);
        fashionCheckBox = (CheckBox)findViewById(R.id.cbFashion);
        sportsCheckBox = (CheckBox)findViewById(R.id.cbSports);
        getFilters();
        setupViews();
    }

    private void getFilters() {
        filters = getIntent().getParcelableExtra("filters");
        if (filters == null) {
            filters = Filters.getDefault();
        }
    }

    private void setupViews() {
        etDatePicker.setText(filters.getDateString());
        sortOrderSpinner.setSelection(filters.isSortOrderNewest() ? 1 : 0);
        artsCheckBox.setChecked(filters.isHasArts());
        fashionCheckBox.setChecked(filters.isHasFashion());
        sportsCheckBox.setChecked(filters.isHasSports());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        filters.setDayOfMonth(view.getDayOfMonth());
        filters.setYear(year);
        filters.setMonth(view.getMonth() + 1);
        etDatePicker.setText(filters.getDateString());
        Log.d("DEBUG", "Date: " + filters.getDateString());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void saveValues() {
        // set result for the parent activity.
        if (sortOrderSpinner.getSelectedItemPosition() == 0) {
            filters.setSortOrderNewest(false);
        } else {
            filters.setSortOrderNewest(true);
        }

        filters.setHasArts(artsCheckBox.isChecked());
        filters.setHasFashion(fashionCheckBox.isChecked());
        filters.setHasSports(sportsCheckBox.isChecked());
        Intent resultIntent = new Intent();
        resultIntent.putExtra("filters", filters);
        setResult(RESULT_OK, resultIntent);
        Log.d("DEBUG", filters.toString());
    }
}
