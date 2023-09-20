package rwl.churchbasic.secretariainv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ContactInfo extends Fragment {


    EditText email;
    EditText phone;
    EditText cep;
    EditText numero;

    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_info, container, false);
        Button buttonSaveContactInfo = view.findViewById(R.id.buttonSaveContatcInfo);
        phone = view.findViewById(R.id.editTextPhone);
        cep = view.findViewById(R.id.editTextTextPostalAddress);
        numero = view.findViewById(R.id.editTextText);

        sp = getActivity().getSharedPreferences("ContactInfoRegister", Context.MODE_PRIVATE);
        phone.setText(sp.getString("phone", ""));
        cep.setText(sp.getString("cep", ""));
        numero.setText(sp.getString("numero", ""));

        buttonSaveContactInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( !SendEditProfileFragment.TelefoneValidator.validarTelefone(phone.getText().toString()) ){
                    Toast.makeText(getContext(), "Digite um telefone Valido", Toast.LENGTH_SHORT).show();
                }else if ( !SendEditProfileFragment.CEPValidator.validarCep(cep.getText().toString())){
                    Toast.makeText(getContext(), "Digite um CEP Valido", Toast.LENGTH_SHORT).show();
                } else if ( numero.getText().toString().equals("") ) {
                    Toast.makeText(getContext(), "Digite o numero da residencia", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = sp.edit();
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    editor.putString("checkoudata", "yes");
                    editor.putString("email", user.getEmail());
                    editor.putString("phone", phone.getText().toString());
                    editor.putString("cep", cep.getText().toString());
                    editor.putString("numero", numero.getText().toString());
                    editor.apply();
                    Toast.makeText(getActivity(), "Informantion Saved", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(view).navigate(R.id.action_contactInfo_to_sendEditProfileFragment);
                }
            }
        });

        return view;
    }
}