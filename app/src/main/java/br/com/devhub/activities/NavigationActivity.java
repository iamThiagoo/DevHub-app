package br.com.devhub.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import br.com.devhub.R;
import br.com.devhub.fragments.HomeFragment;
import br.com.devhub.fragments.LoginFragment;

public class NavigationActivity extends AppCompatActivity {

    private HomeFragment homeFragment;
    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Padrão da tela
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Configura navegação
        setBottomNavigationConfigs();

        // Inicializa o fragmento
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
    }

    protected void setBottomNavigationConfigs() {

        homeFragment  = new HomeFragment();
        loginFragment = new LoginFragment();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                    return true;
                } else if (itemId == R.id.login) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, loginFragment).commit();
                    return true;
                }

                return false;
            }
        });
    }
}