package br.com.devhub.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import br.com.devhub.R;

public class BooksFragment extends Fragment {

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_books, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);

        String imageUrl = "https://m.media-amazon.com/images/I/51E2055ZGUL._SL1000_.jpg";
        Glide.with(this).load(imageUrl).into(imageView);

        return view;
    }
}