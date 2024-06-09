package br.com.devhub.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.devhub.R;

public class UserBookFragment extends Fragment {

    public UserBookFragment() {
        // Required empty public constructor
    }

    public static UserBookFragment newInstance(String param1, String param2) {
        UserBookFragment fragment = new UserBookFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_book, container, false);
    }
}