package br.com.devhub.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.Executor;

import br.com.devhub.R;
import br.com.devhub.activities.NavigationActivity;

public class RegisterFragment extends Fragment {

    protected FirebaseAuth mAuth;
    protected EditText edtFullName;
    protected EditText edtEmail;
    protected EditText edtPassword;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Ação de Submit
        onSubmit(view);

        // Volta para Login Fragment
        onLoginFragment(view);

        return view;
    }


    protected void onSubmit(View view) {
        Button submit = view.findViewById(R.id.btnSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtFullName = view.findViewById(R.id.edtFullname);
                edtEmail = view.findViewById(R.id.edtEmail);
                edtPassword = view.findViewById(R.id.edtPassword);

                processNewUser();
            }
        });
    }


    protected void processNewUser() {
        if (fieldsAreNotEmpty() && fieldsAreValid()) {

            mAuth = FirebaseAuth.getInstance();

            String fullname = edtFullName.getText().toString();
            String email = edtEmail.getText().toString().toLowerCase();
            String password = edtPassword.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateProfile(fullname);
                        } else {
                            String message;

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                message = "Já existe uma conta com este e-mail!";
                            } else {
                                message = "Parece que algo deu errado durante o seu cadastro. Pedimos que tente o envio novamente e se persistir o erro, entre em contato com o nosso suporte.";
                            }

                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        }
    }


    protected void updateProfile(String fullname) {
        FirebaseUser user = mAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullname)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new HomeFragment());
                    fragmentTransaction.commit();
                }
            }
        });
    }


    protected void onLoginFragment(View view) {
        Button login = view.findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new LoginFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }


    protected boolean fieldsAreNotEmpty() {

        if (TextUtils.isEmpty(edtFullName.getText().toString())) {
            edtFullName.setError("Por favor, insira seu nome!");
            edtFullName.requestFocus();
            return false;
        }

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
}