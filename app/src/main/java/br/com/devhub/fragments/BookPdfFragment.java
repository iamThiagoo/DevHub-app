package br.com.devhub.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;

import java.net.URI;
import java.net.URISyntaxException;

import br.com.devhub.R;
import br.com.devhub.classes.Book;


public class BookPdfFragment extends Fragment {

    private View view;
    private Book book;

    public BookPdfFragment() {
        // Required empty public constructor
    }

    public static BookPdfFragment newInstance(String param1, String param2) {
        BookPdfFragment fragment = new BookPdfFragment();
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
        view = inflater.inflate(R.layout.fragment_book_pdf, container, false);

        // Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("Preview - " + book.getName());
        toolbar.setNavigationOnClickListener(v -> activity.getOnBackPressedDispatcher().onBackPressed());

/*        PDFView pdfView = view.findViewById(R.id.pdfView);
        Uri uri = Uri.parse(book.getFile());
        pdfView.fromUri(uri).load();*/

        return view;
    }
}