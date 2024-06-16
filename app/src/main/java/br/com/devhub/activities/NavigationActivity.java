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
import com.google.android.material.navigation.NavigationBarView;

import br.com.devhub.R;
import br.com.devhub.fragments.AddBookFragment;
import br.com.devhub.fragments.BooksFragment;
import br.com.devhub.fragments.HomeFragment;
import br.com.devhub.fragments.LoginFragment;
import br.com.devhub.fragments.UserBookFragment;

public class NavigationActivity extends AppCompatActivity {

    private HomeFragment homeFragment;
    private LoginFragment loginFragment;
    private UserBookFragment userBookFragment;
    private AddBookFragment addBookFragment;

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
        userBookFragment = new UserBookFragment();
        addBookFragment = new AddBookFragment();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
                }
                /*else if (itemId == R.id.books) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new BooksFragment()).commit();
                }*/
                else if (itemId == R.id.add_book) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new AddBookFragment()).commit();
                }
                else if (itemId == R.id.my_books) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new BooksFragment()).commit();
                }
                else if (itemId == R.id.login) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new LoginFragment()).commit();
                }

                return true;
            }
        });
    }
}