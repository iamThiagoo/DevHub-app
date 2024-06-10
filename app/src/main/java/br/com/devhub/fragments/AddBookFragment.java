package br.com.devhub.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.devhub.R;
import br.com.devhub.classes.Book;

public class AddBookFragment extends Fragment {

    protected FirebaseAuth mAuth;
    protected FirebaseUser user;
    protected StorageReference storageReference;
    protected LinearProgressIndicator progress;
    protected Uri imageUri;
    protected Uri pdfUri;
    protected Button btnChooseFile;
    protected Button btnChoosePdfFile;
    protected Button btnSubmit;
    protected ImageView imageView;
    protected EditText edtName;
    protected EditText edtDescription;
    protected EditText edtAuthor;

    protected final ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            imageUri = result.getData().getData();
                            imageView.setImageURI(imageUri);
                            Glide.with(getContext()).load(imageUri).into(imageView);
                        } else {
                            Toast.makeText(getContext(), "Nenhuma imagem selecionada!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    protected final ActivityResultLauncher<Intent> pdfActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            pdfUri = result.getData().getData();
                            TextView file = getView().findViewById(R.id.file);
                            file.setText("Arquivo selecionado: " + pdfUri.getLastPathSegment());
                        } else {
                            Toast.makeText(getContext(), "Nenhum arquivo selecionado!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    public AddBookFragment() {
        // Required empty public constructor
    }

    public static AddBookFragment newInstance(String param1, String param2) {
        AddBookFragment fragment = new AddBookFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new LoginFragment());
            fragmentTransaction.commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);

        FirebaseApp.initializeApp(getContext());
        storageReference = FirebaseStorage.getInstance().getReference();

        // Seleciona imagem
        onSelectImage(view);

        // Seleciona arquivo
        onSelectPdf(view);

        // Ação submit
        onSubmit(view);

        return view;
    }


    protected void onSelectImage(View view) {
        imageView = view.findViewById(R.id.imageView);
        btnChooseFile = view.findViewById(R.id.btnChooseFile);

        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                imageActivityResultLauncher.launch(intent);
            }
        });
    }

    protected void onSelectPdf(View view) {
        TextView file = view.findViewById(R.id.file);
        btnChoosePdfFile = view.findViewById(R.id.btnChoosePdfFile);

        btnChoosePdfFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                pdfActivityResultLauncher.launch(Intent.createChooser(intent, "Selecione o arquivo"));
            }
        });
    }


    protected void onSubmit(View view) {
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtName = view.findViewById(R.id.edtName);
                edtDescription = view.findViewById(R.id.edtDescription);
                edtAuthor = view.findViewById(R.id.edtAuthor);
                progress = view.findViewById(R.id.progress_bar);

                processBook();
            }
        });
    }


    protected void processBook() {
        if (fieldsAreNotEmpty()) {
            if (imageUri != null) {
                progress.setVisibility(View.VISIBLE);
                StorageReference imagesReference = storageReference.child("images/" + UUID.randomUUID().toString());

                imagesReference.putFile(imageUri)
                    .addOnProgressListener(taskSnapshot -> {
                        double progressValue = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progress.setProgress((int) progressValue);
                    })
                    .addOnSuccessListener(taskSnapshot -> imagesReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        if (pdfUri != null) {
                            StorageReference pdfReference = storageReference.child("pdfs/" + UUID.randomUUID().toString() + ".pdf");
                            pdfReference.putFile(pdfUri)
                                .addOnProgressListener(taskSnapshot1 -> {
                                    double progressValue = (100.0 * taskSnapshot1.getBytesTransferred()) / taskSnapshot1.getTotalByteCount();
                                    progress.setProgress((int) progressValue);
                                })
                                .addOnSuccessListener(uploadTaskSnapshot -> pdfReference.getDownloadUrl().addOnSuccessListener(pdfUri -> {
                                    String pdfUrl = pdfUri.toString();
                                    saveBookToFirestore(imageUrl, pdfUrl);
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Erro ao obter URL do PDF!", Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", "Erro ao obter URL do PDF: " + e.getMessage());
                                }))
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Erro ao fazer upload do PDF!", Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", "Erro ao fazer upload do PDF: " + e.getMessage());
                                });
                        } else {
                            Toast.makeText(getContext(), "Nenhum PDF selecionado!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Erro ao obter URL da imagem!", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "Erro ao obter URL da imagem: " + e.getMessage());
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Erro ao fazer upload da imagem!", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "Erro ao fazer upload da imagem: " + e.getMessage());
                    });
            } else {
                Toast.makeText(getContext(), "Nenhuma imagem selecionada!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    protected void saveBookToFirestore(String imageUrl, String pdfUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String documentId = UUID.randomUUID().toString();
        Timestamp createdAt = new Timestamp(new Date());

        Map<String, Object> book = new HashMap<>();
        book.put("name", edtName.getText().toString());
        book.put("description", edtDescription.getText().toString());
        book.put("author", edtAuthor.getText().toString());
        book.put("thumbnail", imageUrl);
        book.put("file", pdfUrl);
        book.put("created_by", user.getUid());
        book.put("created_at", createdAt);

        db.collection("books").document(documentId).set(book)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Livro adicionado com sucesso!", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Erro ao salvar livro no Firestore!", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "Erro ao salvar livro no Firestore: " + e.getMessage());
            });
    }


    protected boolean fieldsAreNotEmpty() {
        if (TextUtils.isEmpty(edtName.getText().toString())) {
            edtName.setError("Por favor, insira o nome do livro!");
            edtName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(edtDescription.getText().toString())) {
            edtDescription.setError("Por favor, insira uma descrição do livro!");
            edtDescription.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(edtAuthor.getText().toString())) {
            edtAuthor.setError("Por favor, insira o autor do livro!");
            edtAuthor.requestFocus();
            return false;
        }

        return true;
    }
}