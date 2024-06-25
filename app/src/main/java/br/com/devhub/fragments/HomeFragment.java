package br.com.devhub.fragments;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.devhub.R;
import br.com.devhub.activities.MainActivity;
import br.com.devhub.adapter.ImageAdapter;
import br.com.devhub.classes.Book;

public class HomeFragment extends Fragment {

    protected View view;
    protected ArrayList<Book> books = new ArrayList<Book>();;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageView banner = view.findViewById(R.id.banner);
        String imageUrl = "https://images.unsplash.com/photo-1573142143200-2a6d95ae7352?q=80&w=2670&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
        Glide.with(this).load(imageUrl).into(banner);

        // Muda frase de boas-vindas ao usuário
        onSetWelcomeMessage();

        // Inicializa os carousels
        onCarouselNominees();

        return view;
    }


    @SuppressLint("SetTextI18n")
    protected void onSetWelcomeMessage() {

        TextView hello = view.findViewById(R.id.helloUser);
        String greetingMessage = getGreetingMessage();
        String name;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String fullname = user.getDisplayName();
            String[] nameParts = fullname.trim().split("\\s+");
            name = nameParts[0];

        } else {
            name = "visitante";
        }

        hello.setText(greetingMessage + ", " + name + "!");
    }

    private String getGreetingMessage() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 0 && hour < 12) {
            return "Bom dia";
        } else if (hour >= 12 && hour < 18) {
            return "Boa tarde";
        } else {
            return "Boa noite";
        }
    }


    protected void onCarouselNominees() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference booksReference = db.collection("books");

        booksReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {

                        books.clear();

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Book book = new Book(
                                    document.getId(),
                                    document.getString("name"),
                                    document.getString("description"),
                                    document.getString("author"),
                                    document.getString("thumbnail"),
                                    document.getString("file"),
                                    document.getString("created_by")
                            );

                            books.add(book);
                        }

                        onBookListener(books);
                    }
                } else {
                    task.getException().printStackTrace();
                    onBookListener(null);
                }
            }
        });
    }


    public void onBookListener(ArrayList<Book> books) {
        if (books != null) {
            RecyclerView recyclerView = view.findViewById(R.id.recycler);
            ImageAdapter adapter = new ImageAdapter(view.getContext(), books);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
                @Override
                public void onClick(Book book) {
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
    }

}