package rwl.churchbasic.secretariainv;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CadastroCriancas extends Fragment {

    Button buttonSendCriancaToFirebase;
    EditText nomeCrianca, dataNascCrianca, cpfResponsavelCrianca;
    Spinner generoCrianca;

    List<String> spinnerArrayGenero =  new ArrayList<String>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view =  inflater.inflate(R.layout.fragment_cadastro_criancas, container, false);

        buttonSendCriancaToFirebase = view.findViewById(R.id.sendCriancaToFirebase);
        nomeCrianca = view.findViewById(R.id.nomeCrianca);
        dataNascCrianca = view.findViewById(R.id.nascCrianca);
        cpfResponsavelCrianca = view.findViewById(R.id.cpfResponsavel);
        generoCrianca = view.findViewById(R.id.generoCrianca);
        SendEditProfileFragment.DateValidator.dateformat(dataNascCrianca);


        spinnerArrayGenero.add("Selecionar");
        spinnerArrayGenero.add("Feminino");
        spinnerArrayGenero.add("Masculino");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArrayGenero);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        generoCrianca.setAdapter(adapter);


        buttonSendCriancaToFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( nomeCrianca.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Por favor Preencha o campo Nome", Toast.LENGTH_SHORT).show();
                }  else if ( !dataNascCrianca.getText().toString().replaceAll("\\D+", "").matches("[0-9]+") || dataNascCrianca.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Digite uma data de nascimento valida", Toast.LENGTH_SHORT).show();
                } else if ( !SendEditProfileFragment.CPFValidator.validarCPF(cpfResponsavelCrianca.getText().toString())) {
                    Toast.makeText(getContext(), "Digite um CPF Valido", Toast.LENGTH_SHORT).show();
                } else if ( generoCrianca.getSelectedItemPosition() == 0) {
                    Toast.makeText(getContext(), "Selecione um Genero", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    String uidUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    // Store InfoCadastro
                    databaseReference.child("Criancas").child(uidUser).child(nomeCrianca.getText().toString());
                    databaseReference.child("Criancas").child(uidUser).child(nomeCrianca.getText().toString()).child("Genero").setValue(generoCrianca.getSelectedItemPosition());
                    databaseReference.child("Criancas").child(uidUser).child(nomeCrianca.getText().toString()).child("Data_Nascimento").setValue(dataNascCrianca.getText().toString());
                    databaseReference.child("Criancas").child(uidUser).child(nomeCrianca.getText().toString()).child("CPF_Responsavel").setValue(cpfResponsavelCrianca.getText().toString());
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