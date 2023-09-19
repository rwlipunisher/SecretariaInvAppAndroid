package com.example.secretariainv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChurchInfo extends Fragment {

    Spinner spinner4;
    Spinner spinner5;
    EditText data_entrada;
    EditText data_batismo;
    SharedPreferences sp;
    CheckBox checkBoxBatismo;

    List<String> spinnerArrayFuncao =  new ArrayList<String>();
    List<String> spinnerArrayIgreja =  new ArrayList<String>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_church_info, container, false);
        Button buttonSaveChurch = view.findViewById(R.id.button_save_churc);
        spinner4 = view.findViewById(R.id.spinner4);
        spinner5 = view.findViewById(R.id.spinner5);
        data_entrada = view.findViewById(R.id.editTextText2);
        data_batismo = view.findViewById(R.id.editTextText3);
        checkBoxBatismo = view.findViewById(R.id.checkBoxBatismo);


        SendEditProfileFragment.DateValidator.dateformat(data_batismo);
        SendEditProfileFragment.DateValidator.dateformat(data_entrada);

        sp = getActivity().getSharedPreferences("ChurcInfoRegister", Context.MODE_PRIVATE);
        spinner4.setSelection(Integer.parseInt(sp.getString("funcao", "0")));
        if ( sp.getString("batizado", "sim").equals("sim")){
            checkBoxBatismo.setChecked(false);
        } else {
            checkBoxBatismo.setChecked(true);
        }
        data_entrada.setText(sp.getString("data_entrada", ""));
        data_batismo.setText(sp.getString("data_batismo", ""));
        spinner5.setSelection(Integer.parseInt(sp.getString("igreja_origem", "0")));

        spinnerArrayFuncao.add("Selecionar");
        spinnerArrayFuncao.add("Visitante");
        spinnerArrayFuncao.add("Membro");
        spinnerArrayFuncao.add("Diacono/Diaconiza");
        spinnerArrayFuncao.add("Lider de Celula");
        spinnerArrayFuncao.add("Co-Lider de Celula");
        spinnerArrayFuncao.add("Professor Ministerio Infantil");
        spinnerArrayIgreja.add("Selecionar");
        spinnerArrayIgreja.add("Sede");
        spinnerArrayIgreja.add("Resende Costa");
        spinnerArrayIgreja.add("São Tiago");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArrayIgreja);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) view.findViewById(R.id.spinner5);
        sItems.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArrayFuncao);
        sItems = view.findViewById(R.id.spinner4);
        sItems.setAdapter(adapter);

        buttonSaveChurch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spinner4.getSelectedItemPosition() == 0 ){
                    Toast.makeText(getContext(), "Selecione uma Função", Toast.LENGTH_SHORT).show();
                } else if (!data_entrada.getText().toString().replaceAll("\\D+", "").matches("[0-9]+")) {
                    Toast.makeText(getContext(), "Digite a data de Entrada", Toast.LENGTH_SHORT).show();
                } else if (spinner5.getSelectedItemPosition() == 0 ) {
                    Toast.makeText(getContext(), "Selecione uma Igreja", Toast.LENGTH_SHORT).show();
                } else if (!data_batismo.getText().toString().replaceAll("\\D+", "").matches("[0-9]+") && checkBoxBatismo.isChecked() ) {
                    Toast.makeText(getContext(), "Data de batismo invalida", Toast.LENGTH_SHORT).show();
                }
                else {
                    sp = getActivity().getSharedPreferences("ChurcInfoRegister", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("checkoudata", "yes");
                    editor.putString("funcao", Integer.toString(spinner4.getSelectedItemPosition()));
                    editor.putString("data_entrada", data_entrada.getText().toString());
                    if ( checkBoxBatismo.isChecked()){
                        editor.putString("data_batismo", "");
                        editor.putString("batizado", "nao");
                    } else {
                        editor.putString("data_batismo", data_batismo.getText().toString());
                        editor.putString("batizado", "sim");
                    }

                    editor.putString("igreja_origem", Integer.toString(spinner5.getSelectedItemPosition()));
                    editor.apply();
                    Toast.makeText(getActivity(), "Informantion Saved", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(view).navigate(R.id.action_churchInfo_to_sendEditProfileFragment);

                }
            }
        });

        return view;
    }


}