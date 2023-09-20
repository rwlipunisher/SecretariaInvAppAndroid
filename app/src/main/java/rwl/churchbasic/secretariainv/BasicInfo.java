package rwl.churchbasic.secretariainv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;


public class BasicInfo extends Fragment {

    EditText fullName;
    EditText cpf;
    EditText dataNasc;
    String fullNameStr;
    SharedPreferences sp;
    Spinner spinner2;
    Spinner spinner3;

    List<String> spinnerArrayGenero =  new ArrayList<String>();
    List<String> spinnerArrayEstadoCivil =  new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basic_info, container, false);
        Button buttonSaveBasicInfo = view.findViewById(R.id.button_save_basic_info);
        fullName = view.findViewById(R.id.fullName);
        cpf = view.findViewById(R.id.cpf);
        dataNasc = view.findViewById(R.id.editText);
        spinner2 = view.findViewById(R.id.spinner2);
        spinner3 = view.findViewById(R.id.spinner3);

        SendEditProfileFragment.DateValidator.dateformat(dataNasc);
        // The Basic info register set sharedPreferences
        sp = getActivity().getSharedPreferences("BasicInfoRegister", Context.MODE_PRIVATE);
        fullName.setText(sp.getString("name", ""));
        cpf.setText(sp.getString("cpf", ""));
        dataNasc.setText(sp.getString("nasc", ""));
        spinner2.setSelection(Integer.parseInt(sp.getString("gender", "0")));
        spinner3.setSelection(Integer.parseInt(sp.getString("civil", "0")));


        // Fill spinner Gender and Civil State
        spinnerArrayGenero.add("Selecionar");
        spinnerArrayGenero.add("Feminino");
        spinnerArrayGenero.add("Masculino");
        spinnerArrayEstadoCivil.add("Selecionar");
        spinnerArrayEstadoCivil.add("Casado(a)");
        spinnerArrayEstadoCivil.add("Solteiro(a)");
        spinnerArrayEstadoCivil.add("Divorciado(a)");
        spinnerArrayEstadoCivil.add("Viuvo(a)");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArrayEstadoCivil);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) view.findViewById(R.id.spinner3);
        sItems.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArrayGenero);
        sItems = view.findViewById(R.id.spinner2);
        sItems.setAdapter(adapter);
        // If button save of basic info save be clicked
        buttonSaveBasicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( fullName.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Por favor Preencha o campo Nome", Toast.LENGTH_SHORT).show();
                } else if ( !SendEditProfileFragment.CPFValidator.validarCPF(cpf.getText().toString())){
                    Toast.makeText(getContext(), "CPF Invalido por favor Preencha novamente", Toast.LENGTH_SHORT).show();
                } else if ( !dataNasc.getText().toString().replaceAll("\\D+", "").matches("[0-9]+") || dataNasc.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Digite uma data de nascimento valida", Toast.LENGTH_SHORT).show();
                } else if ( spinner2.getSelectedItemPosition() == 0) {
                    Toast.makeText(getContext(), "Selecione seu Gênero", Toast.LENGTH_SHORT).show();
                } else if ( spinner3.getSelectedItemPosition() == 0) {
                    Toast.makeText(getContext(), "Selecione um Estado Civil", Toast.LENGTH_SHORT).show();
                } else {
                    //savlar e voltar
                    fullNameStr = fullName.getText().toString();
                    sp = getActivity().getSharedPreferences("BasicInfoRegister", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("checkoudata", "yes");
                    editor.putString("name", fullNameStr);
                    editor.putString("cpf", cpf.getText().toString());
                    editor.putString("nasc", dataNasc.getText().toString());
                    editor.putString("gender", Integer.toString(spinner2.getSelectedItemPosition()));
                    editor.putString("civil", Integer.toString(spinner3.getSelectedItemPosition()));
                    editor.apply();
                    Toast.makeText(getActivity(), "Aguardando Envio", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(view).navigate(R.id.action_basicInfo_to_sendEditProfileFragment);
                }
            }
        });

        return view;
    }
}