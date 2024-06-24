package br.com.devhub.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.devhub.R;

public class ProfileFragment extends Fragment {

    protected View view;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView txtUsername = view.findViewById(R.id.txtUsername);
        TextView txtEmail = view.findViewById(R.id.txtEmail);

        // Recupera o usuário logado
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Define o nome do usuário
        txtUsername.setText(user.getDisplayName());
        txtEmail.setText(user.getEmail());

        // Define ação do botão para editar perfil do usuário
        onEditProfile();

        // Define ação do botão de sair da conta
        onExitAccount();

        return view;
    }


    protected void onEditProfile() {
        Button btnMyData = view.findViewById(R.id.btnMyData);
        btnMyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfile editProfile = new EditProfile();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, editProfile).commit();
            }
        });
    }


    protected void onExitAccount() {
        Button btnExitAccount = view.findViewById(R.id.exitAccount);
        btnExitAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                try {
                    // Desloga usuário
                    mAuth.signOut();
                    Thread.sleep(1000);

                    // Volta para tela de login
                    LoginFragment loginFragment = new LoginFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, loginFragment).commit();

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}