package com.example.mvince.gridimagesearch.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mvince.gridimagesearch.R;
import com.example.mvince.gridimagesearch.models.SearchSettings;

/**
 * Created by mvince on 2/1/15.
 */
public class SettingsDialog extends DialogFragment implements View.OnClickListener {
    private Spinner spnrSize;
    private Spinner spnrColor;
    private Spinner spnrType;
    private EditText etSite;
    OnDataPass dataPasser;
    SearchSettings searchSettings;

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        dataPasser = (OnDataPass) a;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk:
                onSettingsOk();
                break;
            default:
                break;
        }
    }

    public interface OnDataPass {
        public void onDataPass(SearchSettings settings);
    }

    public SettingsDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container);
        getDialog().setTitle("Search Settings");
        searchSettings = (SearchSettings) getArguments().getSerializable("SearchSettings");
        setupViews(view);
        return view;
    }

    private void setupViews(View v) {
        spnrSize = (Spinner) v.findViewById(R.id.spnrSize);
        spnrColor = (Spinner) v.findViewById(R.id.spnrColor);
        spnrType = (Spinner) v.findViewById(R.id.spnrType);
        etSite = (EditText) v.findViewById(R.id.etSite);

        // Size
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> aSizes = ArrayAdapter.createFromResource(v.getContext(), R.array.sizes_array, R.layout.spinner_setting_item);
        // Specify the layout to use when the list of choices appears
        aSizes.setDropDownViewResource(R.layout.spinner_setting_item);
        // Apply the adapter to the spinner
        spnrSize.setAdapter(aSizes);

        // Color
        ArrayAdapter<CharSequence> aColors = ArrayAdapter.createFromResource(v.getContext(), R.array.colors_array, R.layout.spinner_setting_item);
        aColors.setDropDownViewResource(R.layout.spinner_setting_item);
        spnrColor.setAdapter(aColors);

        // Type
        ArrayAdapter<CharSequence> aTypes = ArrayAdapter.createFromResource(v.getContext(), R.array.types_array, R.layout.spinner_setting_item);
        aTypes.setDropDownViewResource(R.layout.spinner_setting_item);
        spnrType.setAdapter(aTypes);

        // Set current values
        spnrSize.setSelection(searchSettings.size);
        spnrColor.setSelection(searchSettings.color);
        spnrType.setSelection(searchSettings.type);
        etSite.setText(searchSettings.site);

        // Set OK button handler
        Button btnOk = (Button) v.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
    }

    public void onSettingsOk() {
        // Update settings
        searchSettings.size = spnrSize.getSelectedItemPosition();
        searchSettings.color = spnrColor.getSelectedItemPosition();
        searchSettings.type = spnrType.getSelectedItemPosition();
        searchSettings.site = etSite.getText().toString();
        dataPasser.onDataPass(searchSettings);
        dismiss();
    }
}