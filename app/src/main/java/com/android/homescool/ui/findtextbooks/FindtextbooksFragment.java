package com.android.homescool.ui.findtextbooks;

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
import com.google.firebase.ml.vision.FirebaseVision;

public class FindtextbooksFragment extends Fragment {

    private FindtextbooksViewModel findtextbooksViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        findtextbooksViewModel =
                ViewModelProviders.of(this).get(FindtextbooksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_findtextbooks, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        findtextbooksViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}