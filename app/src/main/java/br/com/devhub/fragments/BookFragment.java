package br.com.devhub.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.devhub.R;
import br.com.devhub.classes.Book;
import br.com.devhub.classes.DownloadBook;

public class BookFragment extends Fragment {

    private Book book;
    private View view;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;

    public BookFragment() {
        // Required empty public constructor
    }

    public static BookFragment newInstance() {
        BookFragment fragment = new BookFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = getArguments().getParcelable("book");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_book, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Adiciona infos do livro no Fragment
        setBookInfos();

        // Adiciona ação para preview do pdf
        onPreviewPdf();

        // Adiciona ação para baixar pdf
        onDownloadPdf();

        // Adiciona ação para adicionar em meus livros
        onAddMyBooks();

        return view;
    }

    private void setBookInfos() {

        // Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(book.getName());
        toolbar.setNavigationOnClickListener(v -> activity.getOnBackPressedDispatcher().onBackPressed());

        // Insere imagem do livro
        ImageView banner = view.findViewById(R.id.imageView);
        Glide.with(this).load(book.getThumbnail()).into(banner);

        // Insere os dados do livro
        TextView txtBookName        = view.findViewById(R.id.txtBookName);
        TextView txtBookCreatedBy   = view.findViewById(R.id.txtBookCreatedBy);
        TextView txtBookAuthor      = view.findViewById(R.id.txtBookAuthor);
        TextView txtBookDescription = view.findViewById(R.id.txtBookDescription);

        // Preenche os dados do livro
        txtBookName.setText(book.getName());
        txtBookCreatedBy.setText("Adicionado por " + book.getCreatedBy());
        txtBookAuthor.setText(book.getAuthor());
        txtBookDescription.setText(book.getDescription());
    }

    private void onPreviewPdf() {
        Button btnPreview = view.findViewById(R.id.btnPreview);

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("book", (Parcelable) book);

                BookPdfFragment bookPdfFragment = new BookPdfFragment();
                bookPdfFragment.setArguments(bundle);

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, bookPdfFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private void onDownloadPdf() {
        Button btnDownload = view.findViewById(R.id.btnDownload);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadBook(getContext()).execute(book.getFile(), book.getName());
            }
        });
    }

    private void onAddMyBooks() {
        Button btnAddMyBooks = view.findViewById(R.id.btnAddToMyBooks);

        if (mAuth.getCurrentUser() == null) {
            btnAddMyBooks.setVisibility(View.GONE);
        }

        btnAddMyBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userId = mAuth.getCurrentUser().getUid();

                // Consulta para verificar se o livro já foi adicionado pelo usuário
                db.collection("user_books")
                    .whereEqualTo("book_id", book.getId())
                    .whereEqualTo("user_id", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                insertBookToMyBooks();
                            } else {
                                Toast.makeText(getContext(), "Livro já está na sua biblioteca!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Erro ao verificar a biblioteca: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "Erro ao verificar a biblioteca: " + task.getException().getMessage());
                        }
                    });
            }
        });
    }

    private void insertBookToMyBooks() {
        String documentId = UUID.randomUUID().toString();

        Map<String, Object> userBook = new HashMap<>();
        userBook.put("book_id", book.getId());
        userBook.put("user_id", userId);

        db.collection("user_books").document(documentId).set(userBook)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Livro adicionado com sucesso a sua biblioteca!", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Erro ao salvar livro no Firestore!", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "Erro ao salvar livro no Firestore: " + e.getMessage());
            });
    }
}