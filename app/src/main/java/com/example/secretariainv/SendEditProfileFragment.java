package com.example.secretariainv;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendEditProfileFragment extends Fragment {

    CardView cardView;
    ImageView arrowbasicinfo;
    ImageView arrowcontactinfo;
    ImageView arrowchurchinfo;
    Group hiddenGroupbasicinfo;
    Group hiddenGroupcontactinfo;
    Group hiddenGroupchurchinfo;
    List<String> ArrayGenero =  new ArrayList<String>();
    List<String> ArrayEstadoCivil =  new ArrayList<String>();
    List<String> ArrayFuncao = new ArrayList<String>();
    List<String> ArrayIgreja = new ArrayList<String>();

    FloatingActionButton buttonPickImage;
    ImageView imageProfile;
    SharedPreferences sharedPreferences;
    Button sendToFireBase;
    DatabaseReference databaseReference;


    ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>(){
                @Override
                public void onActivityResult(Uri uri) {
                    if( uri != null ) {
                        imageProfile.setImageURI(uri);
                        imageProfile.buildDrawingCache();
                        Bitmap bmap = imageProfile.getDrawingCache();

                        sharedPreferences = getActivity().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor =sharedPreferences.edit();
                        editor.putString("ProfileImgEncodedbase64", encodeTobase64(bmap));
                        editor.commit();
                        Toast.makeText(getActivity(), "Saved Image Profile", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_send_edit_profile, container, false);


        buttonPickImage = view.findViewById(R.id.floatinButtonPickProfileImage);
        imageProfile = view.findViewById(R.id.avatarimg);
        sendToFireBase = view.findViewById(R.id.buttonSendToFirebase);
        sharedPreferences = getActivity().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE);

        if (null != getActivity().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE).getString("ProfileImgEncodedbase64", null)) {
            imageProfile.setImageBitmap(SendEditProfileFragment.decodeBase64(getActivity().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE).getString("ProfileImgEncodedbase64", null)));
        } else {
            imageProfile.setImageResource(R.drawable.baseline_account_circle_24);
        }

        buttonPickImage.setOnClickListener(v -> mGetContent.launch("image/*"));

        // Get data
        SharedPreferences sp;
        //Set buttons to many forms to registration Basic Info, etc.
        Button buttonEditBasic = view.findViewById(R.id.button_edit_basicdata);
        Button buttonEditContact = view.findViewById(R.id.button_edit_contactdata);
        Button buttonEditChurch = view.findViewById(R.id.button_edit_churchdata);
        buttonEditChurch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_sendEditProfileFragment_to_churchInfo);
            }
        });
        buttonEditContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_sendEditProfileFragment_to_contactInfo);
            }
        });
        buttonEditBasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_sendEditProfileFragment_to_basicInfo);
            }
        });
        // Insert Content to list Basic Data of Registration
        ArrayGenero.add("Selecionar");
        ArrayGenero.add("Feminino");
        ArrayGenero.add("Masculino");
        ArrayEstadoCivil.add("Selecionar");
        ArrayEstadoCivil.add("Casado(a)");
        ArrayEstadoCivil.add("Solteiro(a)");
        ArrayEstadoCivil.add("Divorciado(a)");
        ArrayEstadoCivil.add("Viuvo(a)");
        ArrayFuncao.add("Selecionar");
        ArrayFuncao.add("Visitante");
        ArrayFuncao.add("Membro");
        ArrayFuncao.add("Diacono/Diaconiza");
        ArrayFuncao.add("Lider de Celula");
        ArrayFuncao.add("Co-Lider de Celula");
        ArrayFuncao.add("Professor Ministerio Infantil");

        ArrayIgreja.add("Selecionar");
        ArrayIgreja.add("Sede");
        ArrayIgreja.add("Resende Costa");
        ArrayIgreja.add("São Tiago");

        sp = getActivity().getSharedPreferences("BasicInfoRegister", Context.MODE_PRIVATE);
        ArrayList<DataToListViewRegistration> arrayBasicInfo = new ArrayList<>();
        arrayBasicInfo.add(new DataToListViewRegistration("Nome", sp.getString("name", "-")));
        arrayBasicInfo.add(new DataToListViewRegistration("CPF", sp.getString("cpf", "-")));
        arrayBasicInfo.add(new DataToListViewRegistration("Genero", ArrayGenero.get(Integer.parseInt(sp.getString("gender", "0")))));
        arrayBasicInfo.add(new DataToListViewRegistration("Data Nascimento", sp.getString("nasc", "01/01/01")));
        arrayBasicInfo.add(new DataToListViewRegistration("Estado Civil", ArrayEstadoCivil.get(Integer.parseInt(sp.getString("civil", "0")))));

        sp = getContext().getSharedPreferences("ContactInfoRegister", Context.MODE_PRIVATE);
        ArrayList<DataToListViewRegistration> arrayContactInfo = new ArrayList<>();
        arrayContactInfo.add(new DataToListViewRegistration("Cep", sp.getString("cep", "-")));
        arrayContactInfo.add(new DataToListViewRegistration("Numero", sp.getString("numero", "-")));
        arrayContactInfo.add(new DataToListViewRegistration("e-mail", sp.getString("email", "-")));
        arrayContactInfo.add(new DataToListViewRegistration("Telefone", sp.getString("phone", "-")));

        sp = getContext().getSharedPreferences("ChurcInfoRegister", Context.MODE_PRIVATE);
        ArrayList<DataToListViewRegistration> arrayChurchInfo = new ArrayList<>();
        arrayChurchInfo.add(new DataToListViewRegistration("Função", ArrayFuncao.get(Integer.parseInt(sp.getString("funcao", "0")))));
        arrayChurchInfo.add(new DataToListViewRegistration("Data de Entrada", sp.getString("data_entrada", "")));
        arrayChurchInfo.add(new DataToListViewRegistration("Igreja de Origem", ArrayIgreja.get(Integer.parseInt(sp.getString("igreja_origem", "0")))));
        arrayChurchInfo.add(new DataToListViewRegistration("Data de Batismo", sp.getString("data_batismo", "")));
        if( sp.getString("batizado", "sim").equals("sim")){
            arrayChurchInfo.add(new DataToListViewRegistration("E Batizado", "Sim"));
        } else {
            arrayChurchInfo.add(new DataToListViewRegistration("E Batizado", "Não"));
        }


        PersonalAdapter personalAdapter_basicInfo = new PersonalAdapter(getActivity(), R.layout.layout_listview_registration, arrayBasicInfo);
        PersonalAdapter personalAdapter_contactinfo = new PersonalAdapter(getActivity(), R.layout.layout_listview_registration, arrayContactInfo);
        PersonalAdapter personalAdapter_churchinfo = new PersonalAdapter(getActivity(), R.layout.layout_listview_registration, arrayChurchInfo);
        ListView listview_basicinfo = (ListView) view.findViewById(R.id.listabasicinfo);
        ListView listview_contacinfo = (ListView) view.findViewById(R.id.listacontactinfo);
        ListView listview_churchinfo = (ListView) view.findViewById(R.id.listachurchinfo);
        listview_basicinfo.setAdapter(personalAdapter_basicInfo);
        listview_churchinfo.setAdapter(personalAdapter_churchinfo);
        listview_contacinfo.setAdapter(personalAdapter_contactinfo);

        // Set the collapse forms show
        arrowbasicinfo = view.findViewById(R.id.arrowbasicinfo);
        hiddenGroupbasicinfo = view.findViewById(R.id.card_groupbasicinfo);
        arrowcontactinfo = view.findViewById(R.id.arrowcontactinfo);
        hiddenGroupcontactinfo = view.findViewById(R.id.card_groupcontactinfo);
        arrowchurchinfo = view.findViewById(R.id.arrowchurchinfo);
        hiddenGroupchurchinfo = view.findViewById(R.id.card_groupchurchinfo);


        arrowbasicinfo.setOnClickListener(hiddenshow -> {
            if(hiddenGroupbasicinfo.getVisibility() == View.VISIBLE){

                hiddenGroupbasicinfo.setVisibility(View.GONE);
                arrowbasicinfo.setImageResource(R.drawable.baseline_arrow_downward_24);
            }
            else {
                hiddenGroupbasicinfo.setVisibility(View.VISIBLE);
                arrowbasicinfo.setImageResource(R.drawable.baseline_arrow_upward_24);
            }
            });

        arrowchurchinfo.setOnClickListener(hiddenshow -> {
            if(hiddenGroupchurchinfo.getVisibility() == View.VISIBLE){

                hiddenGroupchurchinfo.setVisibility(View.GONE);
                arrowchurchinfo.setImageResource(R.drawable.baseline_arrow_downward_24);
            }
            else {
                hiddenGroupchurchinfo.setVisibility(View.VISIBLE);
                arrowchurchinfo.setImageResource(R.drawable.baseline_arrow_upward_24);
            }
        });

        arrowcontactinfo.setOnClickListener(hiddenshow -> {
            if(hiddenGroupcontactinfo.getVisibility() == View.VISIBLE){

                hiddenGroupcontactinfo.setVisibility(View.GONE);
                arrowcontactinfo.setImageResource(R.drawable.baseline_arrow_downward_24);
            }
            else {
                hiddenGroupcontactinfo.setVisibility(View.VISIBLE);
                arrowcontactinfo.setImageResource(R.drawable.baseline_arrow_upward_24);
            }
        });

        sendToFireBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(null == FirebaseAuth.getInstance().getCurrentUser() ){
                    Toast.makeText(getContext(), "Sem conta NovaVida", Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(), "Cadastro Não Enviado!", Toast.LENGTH_LONG).show();
                    return;
                }

                SharedPreferences sp;
                sp = getContext().getSharedPreferences("BasicInfoRegister", Context.MODE_PRIVATE);
                if ( sp.getString("checkoudata", "no").equals("no"))
                {
                    Toast.makeText(getContext(), "Revise as Informações", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Dados Não Envidados!", Toast.LENGTH_SHORT).show();
                    return;
                }
                sp = getContext().getSharedPreferences("ContactInfoRegister", Context.MODE_PRIVATE);
                if ( sp.getString("checkoudata", "no").equals("no"))
                {
                    Toast.makeText(getContext(), "Revise as Informações", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Dados Não Envidados!", Toast.LENGTH_SHORT).show();
                    return;
                }
                sp = getContext().getSharedPreferences("ChurcInfoRegister", Context.MODE_PRIVATE);
                if ( sp.getString("checkoudata", "no").equals("no"))
                {
                    Toast.makeText(getContext(), "Revise as Informações", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Dados Não Envidados!", Toast.LENGTH_SHORT).show();
                    return;
                }
                sp = getContext().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE);
                if ( sp.getString("ProfileImgEncodedbase64", "").equals(""))
                {
                    Toast.makeText(getContext(), "Adicione uma foto de perfil", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Dados Não Envidados!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String uidUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                sp = getContext().getSharedPreferences("BasicInfoRegister", Context.MODE_PRIVATE);
                SharedPreferences.Editor clearAll = sp.edit();
                databaseReference = FirebaseDatabase.getInstance().getReference();
                // Store InfoCadastro
                databaseReference.child("Analise_Membros").child(uidUser).child("Nome").setValue(sp.getString("name", ""));
                databaseReference.child("Analise_Membros").child(uidUser).child("CPF").setValue(sp.getString("cpf", ""));
                databaseReference.child("Analise_Membros").child(uidUser).child("Genero").setValue(sp.getString("gender", "0"));
                databaseReference.child("Analise_Membros").child(uidUser).child("Data_Nascimento").setValue(sp.getString("nasc", ""));
                databaseReference.child("Analise_Membros").child(uidUser).child("Civil").setValue(sp.getString("civil", "0"));
                clearAll.clear();
                clearAll.apply();
                sp = getContext().getSharedPreferences("ContactInfoRegister", Context.MODE_PRIVATE);
                databaseReference.child("Analise_Membros").child(uidUser).child("CEP").setValue(sp.getString("cep", ""));
                databaseReference.child("Analise_Membros").child(uidUser).child("Numero").setValue(sp.getString("numero", ""));
                databaseReference.child("Analise_Membros").child(uidUser).child("Email").setValue(sp.getString("email", ""));
                databaseReference.child("Analise_Membros").child(uidUser).child("Telefone").setValue(sp.getString("phone", ""));
                clearAll = sp.edit();
                clearAll.clear();
                clearAll.apply();

                sp =  getContext().getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE);
                databaseReference.child("Analise_Membros").child(uidUser).child("Profile_Img").setValue(sp.getString("ProfileImgEncodedbase64", ""));
                sp = getContext().getSharedPreferences("ChurcInfoRegister", Context.MODE_PRIVATE);
                databaseReference.child("Analise_Membros").child(uidUser).child("Funcao").setValue(sp.getString("funcao", "0"));
                databaseReference.child("Analise_Membros").child(uidUser).child("Igreja_Origem").setValue(sp.getString("igreja_origem", ""));
                databaseReference.child("Analise_Membros").child(uidUser).child("Data_Entrada").setValue(sp.getString("data_entrada", ""));
                databaseReference.child("Analise_Membros").child(uidUser).child("Data_Batismo").setValue(sp.getString("data_batismo", ""));
                databaseReference.child("Analise_Membros").child(uidUser).child("Batizado").setValue(sp.getString("batizado", ""));
                databaseReference.child("Analise_Membros").child(uidUser).child("Situacao_Cadastral").setValue("analise");

                clearAll = sp.edit();
                clearAll.clear();
                clearAll.apply();
                SharedPreferences situation =  view.getContext().getSharedPreferences("situation", Context.MODE_PRIVATE);
                if(situation.getString("situacao", "false").equals("ativo") ){
                    databaseReference.child("Membros_Ativos").child(uidUser).child("Situacao_Cadastral").setValue("analise");
                }
                situation.edit().putString("situacao", "analise").apply();
                Toast.makeText(getContext(), "Dados Enviados com Sucesso!", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    public static class DataToListViewRegistration {
    // Objetcs of Basic Array Info
        String firstString;
        String SecondString;

        public DataToListViewRegistration(String firststring, String secondstring) {
            firstString = firststring;
            SecondString = secondstring;
        }
        public String getFirstString() {
            return firstString;
        }

        public void setFirstString(String firstString) {
            this.firstString = firstString;
        }

        public String getSecondString() {
            return SecondString;
        }

        public void setSecondString(String secondString) {
            SecondString = secondString;
        }
    }


    public class PersonalAdapter extends ArrayAdapter<DataToListViewRegistration> {

        private Context mContext;
        private int mResource;

        public PersonalAdapter(@NonNull Context context, int resource, @NonNull ArrayList<DataToListViewRegistration> objects) {
            super(context, resource, objects);
            mContext = context;
            mResource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView  = layoutInflater.inflate(mResource, parent, false);
            TextView firsTitle = convertView.findViewById(R.id.firstLine);
            TextView secondTitle = convertView.findViewById(R.id.secondLine);
            firsTitle.setText(getItem(position).getFirstString());
            secondTitle.setText(getItem(position).getSecondString());

            return convertView;
        }
    }

    public static class DateValidator{
        public static void dateformat( EditText dataNasc){
            dataNasc.addTextChangedListener(new TextWatcher() {
                String current = "";
                String ddmmyyyy = "DDMMYYYY";
                Calendar cal = Calendar.getInstance();
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (!s.toString().equals(current)) {
                        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                        String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                        int cl = clean.length();
                        int sel = cl;
                        for (int i = 2; i <= cl && i < 6; i += 2) {
                            sel++;
                        }
                        //Fix for pressing delete next to a forward slash
                        if (clean.equals(cleanC)) sel--;

                        if (clean.length() < 8) {
                            clean = clean + ddmmyyyy.substring(clean.length());
                        } else {
                            //This part makes sure that when we finish entering numbers
                            //the date is correct, fixing it otherwise
                            int day = Integer.parseInt(clean.substring(0, 2));
                            int mon = Integer.parseInt(clean.substring(2, 4));
                            int year = Integer.parseInt(clean.substring(4, 8));

                            mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                            cal.set(Calendar.MONTH, mon - 1);
                            year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                            cal.set(Calendar.YEAR, year);
                            // ^ first set year for the line below to work correctly
                            //with leap years - otherwise, date e.g. 29/02/2012
                            //would be automatically corrected to 28/02/2012

                            day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                            clean = String.format("%02d%02d%02d", day, mon, year);
                        }

                        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                                clean.substring(2, 4),
                                clean.substring(4, 8));

                        sel = sel < 0 ? 0 : sel;
                        current = clean;
                        dataNasc.setText(current);
                        dataNasc.setSelection(sel < current.length() ? sel : current.length());
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }
    }

    public static class TelefoneValidator {
        private static final String TELEFONE_PATTERN =
                "^\\(?(\\d{2,3})\\)?[-.\\s]?(\\d{4,5})[-.\\s]?(\\d{4})$";


        public static boolean validarTelefone(String telefone) {

            Pattern pattern = Pattern.compile(TELEFONE_PATTERN);
            Matcher matcher = pattern.matcher(telefone);
            return matcher.matches();
        }
    }

    public static class CEPValidator {
        private static final String CEP_PATTERN = "\\d{8}";


        public static boolean validarCep(String cep) {

            Pattern pattern = Pattern.compile(CEP_PATTERN);
            Matcher matcher = pattern.matcher(cep);
            return matcher.matches();
        }
    }

    public static class CPFValidator {

        public static boolean validarCPF(String cpf) {
            // Removendo caracteres especiais do CPF
            cpf = cpf.replaceAll("\\D+", "");
            // Verificando se o CPF possui 11 dígitos
            if (cpf.length() != 11) {
                return false;
            }

            // Verificando se todos os dígitos são iguais
            boolean todosDigitosIguais = true;
            for (int i = 1; i < 11; i++) {
                if (cpf.charAt(i) != cpf.charAt(0)) {
                    todosDigitosIguais = false;
                    break;
                }
            }

            if (todosDigitosIguais) {
                return false;
            }

            // Verificando os dígitos verificadores
            int digito1 = calcularDigitoVerificador(cpf.substring(0, 9));
            int digito2 = calcularDigitoVerificador(cpf.substring(0, 9) + digito1);

            // Comparando os dígitos verificadores com os dígitos informados no CPF
            return cpf.equals(cpf.substring(0, 9) + digito1 + digito2);
        }

        private static int calcularDigitoVerificador(String cpfParcial) {
            int soma = 0;
            int peso = cpfParcial.length() + 1;

            for (int i = 0; i < cpfParcial.length(); i++) {
                soma += Character.getNumericValue(cpfParcial.charAt(i)) * peso;
                peso--;
            }

            int resto = soma % 11;
            int digito = 11 - resto;

            if (digito == 10 || digito == 11) {
                return 0;
            } else {
                return digito;
            }
        }
    }

}
