package br.com.devhub.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.w3c.dom.Text;

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

        // Preenche os campos default
        TextView edtName = view.findViewById(R.id.edtName);
        TextView edtEmail = view.findViewById(R.id.edtEmail);

        edtName.setText(user.getDisplayName());
        edtEmail.setText(user.getEmail());

        // Define ação para botão de Editar Perfil
        onUpdatePassword();

        // Define ação para botão de Deletar Conta
        onDeleteAccount();

        return view;
    }


    public boolean onUpdatePassword() {

        TextView edtName = view.findViewById(R.id.edtName);
        TextView edtActualPassword = view.findViewById(R.id.edtActualPassword);
        TextView edtNewPassword = view.findViewById(R.id.edtNewPassword);

        String name = edtName.getText().toString();
        String actualPassword = edtActualPassword.getText().toString();
        String newPassword = edtNewPassword.getText().toString();

        // Nenhum campo está preenchido
        if (name.isEmpty() && actualPassword.isEmpty() && newPassword.isEmpty()) {
            edtName.setError("Preencha um dos campos para atualizar!");
            edtName.requestFocus();
            return false;
        }

        // Se apenas a atual senha estiver vazia
        if (actualPassword.isEmpty() && !newPassword.isEmpty()) {
            edtActualPassword.setError("Por favor, insira sua senha atual!");
            edtActualPassword.requestFocus();
            return false;

        }
        // Se apenas a nova senha estiver vazia
        else if (!actualPassword.isEmpty() && newPassword.isEmpty()) {
            edtNewPassword.setError("Por favor, insira sua nova senha!");
            edtNewPassword.requestFocus();
            return false;

        }
        // Se ambos os campos de senhas tiverem valor, faz a validação para alterar senha
        else if (!actualPassword.isEmpty() && !newPassword.isEmpty()) {

            // Senha deve ter pelo menos 8 caracteres (Firebase exige)
            if (edtNewPassword.getText().toString().length() < 8) {
                edtNewPassword.setError("A nova senha deve conter pelo menos 8 caracteres!");
                edtNewPassword.requestFocus();
                return false;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), edtActualPassword.getText().toString());

            // Reautentica o usuário para verificar se a senha que ele digitou, está ou não correta
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(edtNewPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireActivity(), "Senha alterada com sucesso!", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d("Update password failed", "Falha ao atualizar a senha: " + task.getException().getMessage());
                                }
                            }
                        });
                    } else {
                        // Senha atual incorreta
                        edtActualPassword.setError("Senha atual incorreta!");
                        edtActualPassword.requestFocus();
                    }
                }
            });
        }

        // Se o nome não estiver vazio
        if (!name.isEmpty()) {
            // Se o nome digitado for diferente do atual, atualiza
            if (!name.equals(user.getDisplayName())) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(edtName.getText().toString())
                        .build();

                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireActivity(), "Nome alterado com sucesso!", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("Fullname update failed", "Falha ao atualizar o nome do usuário: " + task.getException().getMessage());
                        }
                    }
                });
            }
        }

        return true;
    }


    protected void onDeleteAccount() {
        Button deleteButton = view.findViewById(R.id.btnDeleteAccount);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
    }
}