package com.example.secretariainv;

import static androidx.navigation.ui.NavigationUI.setupWithNavController;

import static com.google.android.gms.common.util.CollectionUtils.mutableSetOfWithSize;
import static com.google.android.gms.common.util.CollectionUtils.setOf;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null ){
            Toast.makeText(this, "Sem conta NovaVida", Toast.LENGTH_LONG).show();
            SharedPreferences sp = getSharedPreferences("BasicInfoRegister", Context.MODE_PRIVATE);
            sp.edit().clear().apply();
            sp = getSharedPreferences("ContactInfoRegister", Context.MODE_PRIVATE);
            sp.edit().clear().apply();
            sp = getSharedPreferences("ChurcInfoRegister", Context.MODE_PRIVATE);
            sp.edit().clear().apply();
            return;

        }


    }
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.barTop);
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.navigation_view_account);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseMessaging.getInstance().subscribeToTopic("cultos")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Done";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }
                    }
                });

        ImageView profileImg = navigationView.getHeaderView(0).findViewById(R.id.profileNavDrawer);
        TextView nameProfile = navigationView.getHeaderView(0).findViewById(R.id.nameProfileDrawer);
        ConstraintLayout  constraintLayout = navigationView.getHeaderView(0).findViewById(R.id.constraintDrawerLayout);
        if (null != getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE).getString("ProfileImgEncodedbase64", null)) {
            profileImg.setImageBitmap(SendEditProfileFragment.decodeBase64(getSharedPreferences("ProfileImageUriSetByUser", Context.MODE_PRIVATE).getString("ProfileImgEncodedbase64", null)));
            nameProfile.setText(getSharedPreferences("BasicInfoRegister", Context.MODE_PRIVATE).getString("name", "-"));
            constraintLayout.setBackgroundResource(R.drawable.img);
        } else {
            profileImg.setImageResource(R.drawable.baseline_account_circle_24);
            nameProfile.setText("Sem conta NovaVida");
            constraintLayout.setBackground(null);

        }

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Set the bottom navigation buttons actions
                int id = item.getItemId();
                if ( id == R.id.bottomButtonHome){
                    openFragment(new HomeFragment());
                    return true;
                } else if( id == R.id.bottomButtoncContent){
                    openFragment( new ContentFragment());
                    return true;
                }else if( id == R.id.bottomButtonSendData){
                    openFragment( new CardFragment());
                    return true;
                }else if( id == R.id.bottomButtonMore){
                    openFragment( new MoreFragment());
                    return true;
                }else if( id == R.id.bottomButtonHomeDonate){
                    openFragment( new DonateFragment());
                    return true;
                }
                return false;
            }
        });

        fragmentManager = getSupportFragmentManager();
        openFragment(new HomeFragment());

        }


    public static class DelConta extends Fragment{
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.layoutdelaccount, container, false);
            TextView textView = view.findViewById(R.id.textView21);
            TextView textView1 = view.findViewById(R.id.textView20);
            Button buttonSendDelAccount = view.findViewById(R.id.buttonSolicitarDelConta);

            buttonSendDelAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Solicitacoes_Deletar_Conta").child(userUID).child("motivo").setValue("Solicitacao");
                    textView.setVisibility(View.VISIBLE);
                    textView1.setVisibility(View.GONE);
                    buttonSendDelAccount.setVisibility(View.GONE);
                }
            });

            return view;
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Set the actions of NavDrawer menu buttons
        int id = item.getItemId();
        if (id == R.id.navDrawerButtonProfile){
            openFragment(new LoginFragment());
        } else if ( id == R.id.navDrawerButtonPrivacyPolicy){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://politicas.applink.com.br/Mjk5MDIxNA%3D%3D/privacy"));
            startActivity(browserIntent);
        }else if ( id == R.id.navDrawerButtonDelAccount) {
            if( FirebaseAuth.getInstance().getCurrentUser().getUid().equals(null)){
                Toast.makeText(MainActivity.this, "Sem Conta NovaVida", Toast.LENGTH_SHORT).show();
            } else {
                openFragment(new DelConta() );
            }
        }else if ( id == R.id.navDrawerButtonExitApp) {
            finishAffinity();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        // Set the left button action in toolbar
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction  transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment_1, new HomeFragment());
        transaction.commit();
    }
    public void openFragment(Fragment fragment){
        //Open fragment
        FragmentTransaction  transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment_1, fragment);
        transaction.commit();
    }
}