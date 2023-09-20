package rwl.churchbasic.secretariainv;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MoreFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        ImageView profileImg = view.findViewById(R.id.profileImageMoreFragment);
        TextView nameProfile = view.findViewById(R.id.textView);
        TextView infoProfile = view.findViewById(R.id.textView2);
        Button buttonBibleFragment = view.findViewById(R.id.buttonBible);

        buttonBibleFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_1, new BibleFragment());
                transaction.commit();
            }
        });

        if (null != getActivity().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE).getString("ProfileImgEncodedbase64", null)) {
            profileImg.setImageBitmap(SendEditProfileFragment.decodeBase64(getActivity().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE).getString("ProfileImgEncodedbase64", null)));
            nameProfile.setText(getActivity().getSharedPreferences("BasicInfoRegister", Context.MODE_PRIVATE).getString("name", "-"));
            if( getContext().getSharedPreferences("situation", Context.MODE_PRIVATE).getString("situacao", "").equals("ativo")){
             infoProfile.setText("Conta NovaVida Ativada");
            } else {
                infoProfile.setText("Conta NovaVida em Analise");
            }
        } else {
            profileImg.setImageResource(R.drawable.baseline_account_circle_24);
            nameProfile.setText("Sem conta NovaVida");
        }

        return view;
    }
}