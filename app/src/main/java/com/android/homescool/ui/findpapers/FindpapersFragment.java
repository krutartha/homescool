package com.android.homescool.ui.findpapers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.homescool.R;

public class FindpapersFragment extends Fragment {

    private FindpapersViewModel findpapersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        findpapersViewModel =
                ViewModelProviders.of(this).get(FindpapersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_findpapers, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        findpapersViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}