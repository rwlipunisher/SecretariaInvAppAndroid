package rwl.churchbasic.secretariainv;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends Fragment {

    EditText email;
    EditText passwd;
    TextView nomeUsuario, situacaoCadastral, funcaoIgreja;
    Button loginButton, logoutButton, criarContaApp;
    ImageView imageViewLoginFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_login, container, false);

        imageViewLoginFragment = view.findViewById(R.id.profileImgLoginFragment);
        email = view.findViewById(R.id.emailTextView);
        nomeUsuario = view.findViewById(R.id.nomeUser);
        situacaoCadastral = view.findViewById(R.id.situacaoCadastralTextView);
        funcaoIgreja = view.findViewById(R.id.funcaoIgreja);
        passwd = view.findViewById(R.id.passwdTextView);
        loginButton = view.findViewById(R.id.buttonEntrar);
        logoutButton = view.findViewById(R.id.buttonSair);
        criarContaApp = view.findViewById(R.id.buttonCriarContaApp);
        LoginFragment.checkWithSetData(view, imageViewLoginFragment, nomeUsuario, situacaoCadastral, funcaoIgreja, loginButton, criarContaApp, email, passwd, logoutButton);

        criarContaApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_1, new SignUpFragment());
                transaction.commit();
            }
        });

        imageViewLoginFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getActivity().getSharedPreferences("BasicInfoRegister", Context.MODE_PRIVATE);
                sp.edit().clear().apply();
                sp = getActivity().getSharedPreferences("ContactInfoRegister", Context.MODE_PRIVATE);
                sp.edit().clear().apply();
                sp = getActivity().getSharedPreferences("ChurcInfoRegister", Context.MODE_PRIVATE);
                sp.edit().clear().apply();
                sp = getActivity().getSharedPreferences("situation", Context.MODE_PRIVATE);
                sp.edit().clear().apply();
                sp = getActivity().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE);
                sp.edit().clear().apply();
                sp = getActivity().getSharedPreferences("situation", Context.MODE_PRIVATE);
                sp.edit().clear().apply();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction  transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_1, new LoginFragment());
                transaction.commit();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences sp = getActivity().getSharedPreferences("BasicInfoRegister", Context.MODE_PRIVATE);
                sp.edit().clear().apply();
                sp = getActivity().getSharedPreferences("ContactInfoRegister", Context.MODE_PRIVATE);
                sp.edit().clear().apply();
                sp = getActivity().getSharedPreferences("ChurcInfoRegister", Context.MODE_PRIVATE);
                sp.edit().clear().apply();
                sp = getActivity().getSharedPreferences("situation", Context.MODE_PRIVATE);
                sp.edit().clear().apply();
                sp = getActivity().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE);
                sp.edit().clear().apply();
                sp = getActivity().getSharedPreferences("situation", Context.MODE_PRIVATE);
                sp.edit().clear().apply();

                nomeUsuario.setText("");
                situacaoCadastral.setVisibility(View.GONE);
                funcaoIgreja.setVisibility(View.GONE);
                loginButton.setVisibility(View.VISIBLE);
                criarContaApp.setVisibility(View.VISIBLE);
                email.setVisibility(View.VISIBLE);
                passwd.setVisibility(View.VISIBLE);
                logoutButton.setVisibility(View.GONE);
                imageViewLoginFragment.setVisibility(View.GONE);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userEmail, userPasswd;
                userEmail = String.valueOf(email.getText());
                userPasswd = String.valueOf(passwd.getText());
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(getActivity(), "Digite um e-mail", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(userPasswd)){
                    Toast.makeText(getActivity(), "Digite uma Senha", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(userEmail, userPasswd)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction  transaction = fragmentManager.beginTransaction();
                                    transaction.replace(R.id.nav_host_fragment_1, new LoginFragment());
                                    transaction.commit();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getActivity(), "Dados não Encontrados. Em caso de duvidas, fale com a Secretaria NovaVida", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                }
        });

        return view;
    }

    public static void checkWithSetData(View view, ImageView imageView, TextView textView, TextView textView1, TextView textView2, Button loginButton, Button criarContaApp, EditText email, EditText passwd, Button logoutButton){

        FirebaseUser user;
        FirebaseAuth firebaseAuth;
        DatabaseReference databaseReference;
        String userId;

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if(user == null ){
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            textView1.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
            criarContaApp.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);
            passwd.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            return;
        }

        if( view.getContext().getSharedPreferences("situation", Context.MODE_PRIVATE).getString("readDataBase", "false").equals("true")){

            SharedPreferences situation = view.getContext().getSharedPreferences("situation", Context.MODE_PRIVATE);
            if( situation.getString("situacao", "false").equals("ativo")){
                SharedPreferences basic, church, profileImg;
                basic = view.getContext().getSharedPreferences("BasicInfoRegister", Context.MODE_PRIVATE);
                church = view.getContext().getSharedPreferences("ChurcInfoRegister", Context.MODE_PRIVATE);
                profileImg = view.getContext().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE);
                List<String> ArrayFuncao = new ArrayList<String>();
                ArrayFuncao.add("Selecionar");
                ArrayFuncao.add("Visitante");
                ArrayFuncao.add("Membro");
                ArrayFuncao.add("Diacono/Diaconiza");
                ArrayFuncao.add("Lider de Celula");
                ArrayFuncao.add("Co-Lider de Celula");
                ArrayFuncao.add("Professor Ministerio Infantil");

                textView.setText(basic.getString("name", ""));
                textView1.setText("Ativo na NovaVida como:");
                textView2.setText(ArrayFuncao.get((Integer.parseInt(church.getString("funcao", "0")))));
                textView.setVisibility(View.VISIBLE);
                textView1.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                Bitmap imgProfile = SendEditProfileFragment.decodeBase64(profileImg.getString("ProfileImgEncodedbase64", ""));
                imageView.setImageBitmap(imgProfile);
                loginButton.setVisibility(View.GONE);
                criarContaApp.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
                passwd.setVisibility(View.GONE);
                logoutButton.setVisibility(View.VISIBLE);

            } else if(situation.getString("situacao", "false").equals("analise") ){

                Toast.makeText(view.getContext(), "Cadastro em Analise!", Toast.LENGTH_LONG).show();
                textView.setVisibility(View.GONE);
                textView1.setText("Cadastro NovaVida em Analise");
                textView1.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.baseline_account_circle_24);
                loginButton.setVisibility(View.GONE);
                criarContaApp.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
                passwd.setVisibility(View.GONE);
                logoutButton.setVisibility(View.VISIBLE);

            } else if(situation.getString("situacao", "false").equals("visitante")){
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.baseline_account_circle_24);
                textView.setText("Visitante");
                textView.setVisibility(View.VISIBLE);
                textView1.setVisibility(View.GONE);
                textView2.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                criarContaApp.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
                passwd.setVisibility(View.GONE);
                logoutButton.setVisibility(View.VISIBLE);
            }
            else {
               // Toast.makeText(view.getContext(), "Erro Lembrar de Tirar", Toast.LENGTH_SHORT).show();
            }

            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Membros_Ativos");
        userId = user.getUid();
        databaseReference.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if ( task.isSuccessful()) {

                    if (task.getResult().exists() && task.getResult().child("Situacao_Cadastral").getValue().toString().equals("ativo")) {

                        SharedPreferences situation =  view.getContext().getSharedPreferences("situation", Context.MODE_PRIVATE);
                        Toast.makeText(view.getContext(), "Seja Bem Vindo! :) ", Toast.LENGTH_LONG).show();
                        situation.edit().putString("situacao", "ativo").apply();

                        SharedPreferences basicInfo = view.getContext().getSharedPreferences("BasicInfoRegister", Context.MODE_PRIVATE);
                        SharedPreferences.Editor basicInfoEditor = basicInfo.edit();
                        basicInfoEditor.putString("name", task.getResult().child("Nome").getValue().toString());
                        basicInfoEditor.putString("cpf", task.getResult().child("CPF").getValue().toString());
                        basicInfoEditor.putString("nasc", task.getResult().child("Data_Nascimento").getValue().toString());
                        basicInfoEditor.putString("gender", task.getResult().child("Genero").getValue().toString());
                        basicInfoEditor.putString("civil", task.getResult().child("Civil").getValue().toString());
                        basicInfoEditor.apply();

                        SharedPreferences contactInfo = view.getContext().getSharedPreferences("ContactInfoRegister", Context.MODE_PRIVATE);
                        SharedPreferences.Editor contactInfoEditor = contactInfo.edit();
                        contactInfoEditor.putString("email", task.getResult().child("Email").getValue().toString());
                        contactInfoEditor.putString("phone", task.getResult().child("Telefone").getValue().toString());
                        contactInfoEditor.putString("cep", task.getResult().child("CEP").getValue().toString());
                        contactInfoEditor.putString("numero", task.getResult().child("Numero").getValue().toString());
                        contactInfoEditor.apply();

                        SharedPreferences churchInfo = view.getContext().getSharedPreferences("ChurcInfoRegister", Context.MODE_PRIVATE);
                        SharedPreferences.Editor churchInfoEditor = churchInfo.edit();
                        churchInfoEditor.putString("funcao", task.getResult().child("Funcao").getValue().toString());
                        churchInfoEditor.putString("data_entrada", task.getResult().child("Data_Entrada").getValue().toString());
                        churchInfoEditor.putString("data_batismo", task.getResult().child("Data_Batismo").getValue().toString());
                        churchInfoEditor.putString("igreja_origem", task.getResult().child("Igreja_Origem").getValue().toString());
                        churchInfoEditor.apply();

                        SharedPreferences.Editor imgProfileEdit = view.getContext().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE).edit();
                        imgProfileEdit.putString("ProfileImgEncodedbase64", task.getResult().child("Profile_Img").getValue().toString());
                        imgProfileEdit.apply();

                        situation.edit().putString("readDataBase", "true").apply();


                        List<String> ArrayFuncao = new ArrayList<String>();
                        ArrayFuncao.add("Selecionar");
                        ArrayFuncao.add("Membro");
                        ArrayFuncao.add("Diacono/Diaconiza");
                        ArrayFuncao.add("Lider de Celula");
                        ArrayFuncao.add("Co-Lider de Celula");

                        textView.setText(basicInfo.getString("name", ""));
                        textView1.setText("Ativo na NovaVida como:");
                        textView2.setText(ArrayFuncao.get((Integer.parseInt(churchInfo.getString("funcao", "0")))));
                        textView.setVisibility(View.VISIBLE);
                        textView1.setVisibility(View.VISIBLE);
                        textView2.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        Bitmap imgProfile = SendEditProfileFragment.decodeBase64(task.getResult().child("Profile_Img").getValue().toString());
                        imageView.setImageBitmap(imgProfile);
                        loginButton.setVisibility(View.GONE);
                        criarContaApp.setVisibility(View.GONE);
                        email.setVisibility(View.GONE);
                        passwd.setVisibility(View.GONE);
                        logoutButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        FirebaseAuth firebaseAuth1 = FirebaseAuth.getInstance();;
                        FirebaseUser user1 = firebaseAuth1.getCurrentUser();
                        DatabaseReference databaseReference1;
                        databaseReference1 = FirebaseDatabase.getInstance().getReference("Analise_Membros");
                        String userId1 = user1.getUid();
                        databaseReference1.child(userId1).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if ( task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        SharedPreferences situation =  view.getContext().getSharedPreferences("situation", Context.MODE_PRIVATE);
                                        situation.edit().putString("situacao", "analise").apply();
                                        situation.edit().putString("readDataBase", "true").apply();

                                        SharedPreferences.Editor imgProfileEdit = view.getContext().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE).edit();
                                        imgProfileEdit.putString("ProfileImgEncodedbase64", task.getResult().child("Profile_Img").getValue().toString());
                                        imgProfileEdit.apply();

                                        SharedPreferences basicInfo = view.getContext().getSharedPreferences("BasicInfoRegister", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor basicInfoEditor = basicInfo.edit();
                                        basicInfoEditor.putString("name", task.getResult().child("Nome").getValue().toString());
                                        basicInfoEditor.apply();

                                        Toast.makeText(view.getContext(), "Cadastro em Analise!", Toast.LENGTH_LONG).show();
                                        textView.setVisibility(View.GONE);
                                        textView1.setText("Cadastro NovaVida em Analise");
                                        textView1.setVisibility(View.VISIBLE);
                                        textView2.setVisibility(View.GONE);
                                        imageView.setVisibility(View.VISIBLE);
                                        imageView.setImageResource(R.drawable.baseline_account_circle_24);
                                        loginButton.setVisibility(View.GONE);
                                        criarContaApp.setVisibility(View.GONE);
                                        email.setVisibility(View.GONE);
                                        passwd.setVisibility(View.GONE);
                                        logoutButton.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        SharedPreferences situation =  view.getContext().getSharedPreferences("situation", Context.MODE_PRIVATE);
                                        situation.edit().putString("situacao", "visitante").apply();
                                        situation.edit().putString("readDataBase", "true").apply();
                                        imageView.setVisibility(View.VISIBLE);
                                        imageView.setImageResource(R.drawable.baseline_account_circle_24);
                                        textView.setText("Visitante");
                                        textView.setVisibility(View.VISIBLE);
                                        textView1.setVisibility(View.GONE);
                                        textView2.setVisibility(View.GONE);
                                        loginButton.setVisibility(View.GONE);
                                        criarContaApp.setVisibility(View.GONE);
                                        email.setVisibility(View.GONE);
                                        passwd.setVisibility(View.GONE);
                                        logoutButton.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(view.getContext(), "Erro de Conexão", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
