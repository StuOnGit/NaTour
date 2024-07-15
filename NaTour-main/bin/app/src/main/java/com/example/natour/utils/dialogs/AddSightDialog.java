package com.example.natour.utils.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.natour.R;
import com.example.natour.enumeration.SightTypeEnum;

import org.osmdroid.util.GeoPoint;

public class AddSightDialog extends DialogFragment {

    private Spinner spinnerTypology;
    private TextView name;
    private Button buttonOk;
    private ImageView closeButton;
    String namePointOfInterest;
    String typology;
    GeoPoint point;

    public AddSightDialog(GeoPoint point) {
        this.point = point;
    }

    public interface OnInputListener{
        void sendInput(String namePointOfInterest, String typologyPointOfInterest, GeoPoint p);
    }

    public OnInputListener onInputListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.add_point_of_interest_dialog, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onInputListener = (OnInputListener) getActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = view.findViewById(R.id.textInputNamePointOfInterest);
        spinnerTypology = view.findViewById(R.id.spinnerTypologyPointOfInterest);
        buttonOk = view.findViewById(R.id.buttonOk);
        closeButton = view.findViewById(R.id.close_dialog);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.typologyPointOfInterest, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypology.setAdapter(adapter);



        buttonOk.setOnClickListener(view1 -> {
            namePointOfInterest = name.getText().toString();
            typology = spinnerTypology.getSelectedItem().toString();
            if(!namePointOfInterest.isEmpty() && !typology.isEmpty()){
                onInputListener.sendInput(namePointOfInterest, typology, point);
                dismiss();
            }
            else{
               if(namePointOfInterest.isEmpty()){
                   name.setError("Inserire un nome");
               }
            }
        });

        closeButton.setOnClickListener(view12 -> dismiss());
    }



    public SightTypeEnum getTypology(){
        SightTypeEnum sighTypeEnum = null;
        if(typology.equals("Grotta") ){
            sighTypeEnum = SightTypeEnum.GROTTA;
        }else if(typology.equals("Sorgente")){
            sighTypeEnum = SightTypeEnum.SORGENTE;
        }else if(typology.equals("Punto Panoramico")){
            sighTypeEnum = SightTypeEnum.PUNTO_PANORAMICO;
        }else if(typology.equals("Area Pic-Nic")){
            sighTypeEnum = SightTypeEnum.AREA_PIC_NIC;
        }else if(typology.equals("Baita")){
            sighTypeEnum = SightTypeEnum.BAITA;
        }else if(typology.equals("Flora")){
            sighTypeEnum = SightTypeEnum.FLORA;
        }else if(typology.equals("Luogo di Interesse Artistico")){
            sighTypeEnum = SightTypeEnum.LUOGO_DI_INTERESSE_ARTISTICO;
        }else if(typology.equals("Altro")){
            sighTypeEnum = SightTypeEnum.ALTRO;
        }
        return sighTypeEnum;
    }

}
