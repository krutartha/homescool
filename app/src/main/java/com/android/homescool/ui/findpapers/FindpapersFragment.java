package com.android.homescool.ui.findpapers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.homescool.R;
import com.android.homescool.UploadPaper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FindpapersFragment extends Fragment {

    private FindpapersViewModel findpapersViewModel;

    FloatingActionButton fabAddPaper;

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

        fabAddPaper = (FloatingActionButton) root.findViewById(R.id.fab_add_paper);

        fabAddPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UploadPaper.class));
            }
        });

        return root;
    }
}