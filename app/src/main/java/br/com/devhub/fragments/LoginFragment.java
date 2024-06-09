package br.com.devhub.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

import br.com.devhub.R;
import br.com.devhub.activities.NavigationActivity;

public class LoginFragment extends Fragment {

    protected FirebaseAuth mAuth;
    protected EditText edtEmail;
    protected EditText edtPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Ação de Submit
        onSubmit(view);

        // Volta para Login Fragment
        onRegisterFragment(view);

        return view;
    }


    protected void onSubmit(View view) {
        Button submit = view.findViewById(R.id.btnSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtEmail = view.findViewById(R.id.edtEmail);
                edtPassword = view.findViewById(R.id.edtPassword);

                processLogin();
            }
        });
    }


    protected void processLogin() {
        if (fieldsAreNotEmpty() && fieldsAreValid()) {

            String email = String.valueOf(edtEmail.getText()).toLowerCase();
            String password = String.valueOf(edtPassword.getText());

            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FragmentManager fragmentManager = getParentFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container, new HomeFragment());
                            fragmentTransaction.commit();
                        } else {
                            Toast.makeText(getActivity(), "Login inválido... tente novamente!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }


    protected boolean fieldsAreNotEmpty() {
        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            edtEmail.setError("Por favor, insira seu e-mail!");
            edtEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(edtPassword.getText().toString())) {
            edtPassword.setError("Por favor, insira sua senha!");
            edtPassword.requestFocus();
            return false;
        }

        return true;
    }

    protected boolean fieldsAreValid() {
        if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
            edtEmail.setError("O e-mail deve ser válido!");
            edtEmail.requestFocus();
            return false;
        }

        if (edtPassword.getText().toString().length() < 8) {
            edtPassword.setError("A senha deve conter pelo menos 8 caracteres!");
            edtPassword.requestFocus();
            return false;
        }

        return true;
    }


    protected void onRegisterFragment(View view) {
        Button register = view.findViewById(R.id.btnRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new RegisterFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
}