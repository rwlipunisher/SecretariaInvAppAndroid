package rwl.churchbasic.secretariainv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class CadastrosHome extends Fragment {


    Button buttonCadastroMembros, buttonCadastroVisitantes, buttonCadastroFilhos;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null ){
            Toast.makeText(getContext(), "Crie ou Entre em uma conta NovaVida para Acessar", Toast.LENGTH_LONG).show();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.nav_host_fragment_1, new LoginFragment());
            transaction.commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_cadastros_home, container, false);

        buttonCadastroMembros = view.findViewById(R.id.buttonMyNVForm);
        buttonCadastroVisitantes = view.findViewById(R.id.buttonVisitantes);
        buttonCadastroFilhos = view.findViewById(R.id.buttonCriancas);

        ImageView imageProfile = view.findViewById(R.id.imageProfileCadastrosHome);
        TextView subtitle = view.findViewById(R.id.textView12);

        if (null != getActivity().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE).getString("ProfileImgEncodedbase64", null)) {
            imageProfile.setImageBitmap(SendEditProfileFragment.decodeBase64(getActivity().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE).getString("ProfileImgEncodedbase64", null)));
        } else {
            imageProfile.setImageResource(R.drawable.baseline_account_circle_24);
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("situation", Context.MODE_PRIVATE);

        if( sharedPreferences.getString("situacao", "false").equals("ativo")){
            buttonCadastroFilhos.setVisibility(View.VISIBLE);
            buttonCadastroMembros.setVisibility(View.VISIBLE);
            buttonCadastroVisitantes.setVisibility(View.VISIBLE);
            subtitle.setText("Conta NovaVida Ativa");
        } else if( sharedPreferences.getString("situacao", "false").equals("analise")){
            buttonCadastroFilhos.setVisibility(View.GONE);
            buttonCadastroMembros.setVisibility(View.GONE);
            buttonCadastroVisitantes.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Cadastro em Analise", Toast.LENGTH_LONG).show();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.nav_host_fragment_1, new HomeFragment());
            transaction.commit();
        } else if( sharedPreferences.getString("situacao", "false").equals("visitante") ){
            buttonCadastroFilhos.setVisibility(View.GONE);
            buttonCadastroMembros.setVisibility(View.VISIBLE);
            buttonCadastroVisitantes.setVisibility(View.GONE);
            subtitle.setText("Ative sua conta NovaVida");
        }

        buttonCadastroMembros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_cadastrosHome_to_sendEditProfileFragment);
            }
        });
        buttonCadastroVisitantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_cadastrosHome_to_cadastroComoVisitante);
            }
        });

        buttonCadastroFilhos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_cadastrosHome_to_cadastroCriancas);
            }
        });
        return view;
    }
}