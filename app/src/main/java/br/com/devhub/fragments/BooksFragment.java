package br.com.devhub.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;

import br.com.devhub.R;
import br.com.devhub.classes.Book;

public class BooksFragment extends Fragment {

    private ArrayList<Book> books = new ArrayList<Book>();
    protected FirebaseAuth mAuth;
    protected FirebaseUser user;
    private FirebaseFirestore db;
    private View view;

    public BooksFragment() {
        // Required empty public constructor
    }

    public static BooksFragment newInstance(String param1, String param2) {
        BooksFragment fragment = new BooksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new LoginFragment());
            fragmentTransaction.commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_books, container, false);

        // Resgata todos os livros do usuário
        getUserBooks();

        return view;
    }


    protected void getUserBooks() {

        if (user == null) {
            return;
        }

        books.clear();

        db.collection("user_books")
        .whereEqualTo("user_id", user.getUid())
        .get()
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        String bookId = document.getString("book_id");

                        db.collection("books").document(bookId).get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                DocumentSnapshot document2 = task2.getResult();
                                if (document2.exists()) {
                                    Book book = new Book(
                                            document2.getId(),
                                            document2.getString("name"),
                                            document2.getString("description"),
                                            document2.getString("author"),
                                            document2.getString("thumbnail"),
                                            document2.getString("file"),
                                            document2.getString("created_by")
                                    );

                                    books.add(book);
                                } else {
                                    Log.d("TAG", "Documento não encontrado.");
                                }

                                ListBooks(books);
                            } else {
                                Log.e("TAG", "Falha ao obter documento.", task.getException());
                            }
                        });
                    }
                } else {
                    TextView booksEmpty = view.findViewById(R.id.txtBooksEmpty);
                    booksEmpty.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(getContext(), "Erro ao verificar a biblioteca: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("TAG", "Erro ao carregar Meus Livros: " + task.getException().getMessage());
            }
        });
    }

    protected void ListBooks(ArrayList<Book> books) {
        GridLayout cardContainer = view.findViewById(R.id.gridLayout);
        cardContainer.removeAllViews();
        cardContainer.setColumnCount(2);

        for (Book book : books) {
            View cardView = getLayoutInflater().inflate(R.layout.card_layout, cardContainer, false);

            TextView textViewTitle = cardView.findViewById(R.id.textViewTitle);
            textViewTitle.setText(book.getName());

            ImageView imageView = cardView.findViewById(R.id.imageView);
            String imageUrl = book.getThumbnail();
            Glide.with(requireContext())
                    .load(imageUrl)
                    .into(imageView);

            cardContainer.addView(cardView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("book", (Parcelable) book);

                    BookFragment bookFragment = new BookFragment();
                    bookFragment.setArguments(bundle);

                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, bookFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }

        cardContainer.requestLayout();
    }
}