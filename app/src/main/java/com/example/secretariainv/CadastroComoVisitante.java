package com.example.secretariainv;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CadastroComoVisitante extends Fragment {

    EditText nomeVisitante, cepVisitante, nascimentoVisitante, whatssapVisitante;
    Spinner generoVisitante;
    Button sendToFirebaseVisitante;
    List<String> spinnerArrayGenero =  new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cadastro_como_visitante, container, false);

        nomeVisitante = view.findViewById(R.id.nomeVisitante);
        generoVisitante = view.findViewById(R.id.generoVisitante);
        cepVisitante = view.findViewById(R.id.cepVisitante);
        nascimentoVisitante = view.findViewById(R.id.nascimentoVisitante);
        whatssapVisitante = view.findViewById(R.id.whatsappVisitante);
        sendToFirebaseVisitante = view.findViewById(R.id.buttonEnviarVisitanteToFirebase);
        SendEditProfileFragment.DateValidator.dateformat(nascimentoVisitante);

        spinnerArrayGenero.add("Selecionar");
        spinnerArrayGenero.add("Feminino");
        spinnerArrayGenero.add("Masculino");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArrayGenero);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        generoVisitante.setAdapter(adapter);

        sendToFirebaseVisitante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( nomeVisitante.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Por favor Preencha o campo Nome", Toast.LENGTH_SHORT).show();
                } else if (!SendEditProfileFragment.TelefoneValidator.validarTelefone(whatssapVisitante.getText().toString())){
                    Toast.makeText(getContext(), "Telefone Invalido", Toast.LENGTH_SHORT).show();
                } else {

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    String uidUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    // Store InfoCadastro
                    databaseReference.child("Visitantes").child(uidUser).child(nomeVisitante.getText().toString());
                    databaseReference.child("Visitantes").child(uidUser).child(nomeVisitante.getText().toString()).child("Genero").setValue(generoVisitante.getSelectedItemPosition());
                    databaseReference.child("Visitantes").child(uidUser).child(nomeVisitante.getText().toString()).child("CEP").setValue(cepVisitante.getText().toString());
                    databaseReference.child("Visitantes").child(uidUser).child(nomeVisitante.getText().toString()).child("Nascimento").setValue(nascimentoVisitante.getText().toString());
                    databaseReference.child("Visitantes").child(uidUser).child(nomeVisitante.getText().toString()).child("Whatsapp").setValue(whatssapVisitante.getText().toString());

                    Toast.makeText(getContext(), "Dados Enviados com Sucesso", Toast.LENGTH_LONG).show();

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_1, new HomeFragment());
                    transaction.commit();
                }
            }
        });

        return view;
    }
}