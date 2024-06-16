package br.com.devhub.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.devhub.R;

public class EditProfile extends Fragment {

    private View view;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public EditProfile() {
        // Required empty public constructor
    }

    public static EditProfile newInstance() {
        EditProfile fragment = new EditProfile();
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
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Inicializa Firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Define ação para botão de Editar Perfil
        //onUpdatePassword();

        // Define ação para botão de Deletar Conta
        onDeleteAccount();

        return view;
    }


    protected void onDeleteAccount() {
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    // Finaliza sessão atual
                    mAuth.signOut();

                    // Mostra mensagem
                    Toast.makeText(requireActivity(), "Conta deletada com sucesso!", Toast.LENGTH_LONG).show();

                    // Volta para tela de login
                    LoginFragment loginFragment = new LoginFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, loginFragment).commit();
                }
            }
        });
    }
}