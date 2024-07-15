package com.example.natour.utils.dialogs;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.natour.R;
import com.example.natour.data.NaCountry;
import com.example.natour.enumeration.DifficultyEnum;
import com.example.natour.utils.NaPathFilters;

import java.util.ArrayList;

public class FilterDialog extends DialogFragment {

    private static final String TAG = "FilterDialog";

    private ImageView close;
    private Spinner regionSpinner;
    private Spinner countrySpinner;
    private Spinner difficultSpinner;
    private CheckBox accessibileCheckBox;
    private EditText minDurationText;
    private Button resetFilter;
    private Button saveFilter;

    DifficultyEnum[] difficultyEnum = {DifficultyEnum.NULL,DifficultyEnum.LOW,  DifficultyEnum.AVERAGE, DifficultyEnum.HIGH,DifficultyEnum.EXPERT };
    ArrayAdapter<DifficultyEnum> adapterDifficult;
    ArrayAdapter<String> adapterCountry;
    ArrayAdapter<String> adapterRegion;
    private final NaPathFilters naPathFilters;
    private final ArrayList<NaCountry> naCountries;




    public FilterDialog(NaPathFilters naPathFilters, ArrayList<NaCountry> naCountries){
        this.naPathFilters = naPathFilters;
        this.naCountries = naCountries;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         return inflater.inflate(R.layout.filter_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setCancelable(false);
        setUpDialog(view);
        setUpDialog(naPathFilters);
        setUpButtons();
    }


    private void setUpDialog(View view){
        close = view.findViewById(R.id.close_filter_dialog);
        regionSpinner = view.findViewById(R.id.region_spinner);
        countrySpinner = view.findViewById(R.id.country_spinner);
        difficultSpinner = view.findViewById(R.id.difficult_spinner_filter);
        accessibileCheckBox = view.findViewById(R.id.checkBox_accessible);
        minDurationText = view.findViewById(R.id.duration_text_filter);
        resetFilter = view.findViewById(R.id.reset_filter);
        saveFilter =  view.findViewById(R.id.save_filter);
        adapterDifficult = new ArrayAdapter<>(view.getContext(), R.layout.support_simple_spinner_dropdown_item,difficultyEnum);
        difficultSpinner.setAdapter(adapterDifficult);
        ArrayList<String> countriesStr = new ArrayList<>();
        countriesStr.add("NULL");
        for (NaCountry naCountry :
                naCountries) {
            countriesStr.add(naCountry.getName());
        }
        adapterCountry = new ArrayAdapter<>(view.getContext(), R.layout.support_simple_spinner_dropdown_item, countriesStr);
        countrySpinner.setAdapter(adapterCountry);
    }


    private void setUpButtons(){
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        saveFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFilter();
                dismiss();
            }
        });
        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               resetFilter();
               dismiss();
            }
        });
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){
                    regionSpinner.setEnabled(true);
                    ArrayList<String> regions = naCountries.get(position-1).getRegions();
                    adapterRegion = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, regions);
                    regionSpinner.setAdapter(adapterRegion);
                    if(naPathFilters.getRegion()!= null){
                        regionSpinner.setSelection(((ArrayAdapter<String>)regionSpinner.getAdapter()).getPosition(naPathFilters.getRegion()));
                    }
                }else{
                    regionSpinner.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void resetFilter() {
        naPathFilters.setRegion(null);
        naPathFilters.setDifficult(null);
        naPathFilters.setCountry(null);
        naPathFilters.setMinDuration(null);
        naPathFilters.setAccessible(null);
        naPathFilters.setFiltered(false);
    }

    private void saveFilter(){
        String region = regionSpinner.getSelectedItem() == null ? null : regionSpinner.getSelectedItem().toString();
        if(region == null){
            System.out.println("regione è nulla");
        }else{
            System.out.println(region);
        }
        String country = countrySpinner.getSelectedItem().toString().equals("NULL") ? null : countrySpinner.getSelectedItem().toString();
        String difficult = difficultSpinner.getSelectedItem().toString().equals("NULL") ? null : difficultSpinner.getSelectedItem().toString();
        Boolean accessible = accessibileCheckBox.isChecked() ? true : null;
        Integer minDuration = minDurationText.getText().toString().equals("") ? null : Integer.parseInt(minDurationText.getText().toString());
        naPathFilters.setAccessible(accessible);
        naPathFilters.setCountry(country);
        naPathFilters.setMinDuration(minDuration);
        naPathFilters.setRegion(region);
        if(difficult == null){
            naPathFilters.setDifficult(null);
        }else{
            naPathFilters.setDifficult(DifficultyEnum.valueOf(difficult));
        }


        naPathFilters.setFiltered(true);
    }

    private void setUpDialog(NaPathFilters naPathFilters){
        if(naPathFilters.getDifficult() != null){
            difficultSpinner.setSelection(((ArrayAdapter<DifficultyEnum>)difficultSpinner.getAdapter()).getPosition(naPathFilters.getDifficult()));
        }
        if(naPathFilters.getCountry() != null){
            countrySpinner.setSelection(((ArrayAdapter<String>)countrySpinner.getAdapter()).getPosition(naPathFilters.getCountry()));
        }

        if(naPathFilters.getRegion() != null && naPathFilters.getCountry() != null){
            regionSpinner.setEnabled(true);
            ArrayList<String> regions = naCountries.get(((ArrayAdapter<String>)countrySpinner.getAdapter()).getPosition(naPathFilters.getCountry())-1).getRegions();
            adapterRegion = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, regions);
            regionSpinner.setAdapter(adapterRegion);
            regionSpinner.setSelection(((ArrayAdapter<String>)regionSpinner.getAdapter()).getPosition(naPathFilters.getRegion()));
        }

        if (naPathFilters.getAccessible() != null){
            accessibileCheckBox.setChecked(naPathFilters.getAccessible());
        }
        if(naPathFilters.getMinDuration() != null){
            minDurationText.setText(naPathFilters.getMinDuration().toString());
        }
    }

}
