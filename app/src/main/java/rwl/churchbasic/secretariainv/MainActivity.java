package rwl.churchbasic.secretariainv;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(POST_NOTIFICATIONS);
            }
        }
    }

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
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Fale com o Adm do Sistema, falha de Notificações", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d("Aquiii", token);

                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
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

        FirebaseMessaging.getInstance().subscribeToTopic("geral")
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