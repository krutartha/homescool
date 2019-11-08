package com.android.homescool.ui.discussions;

import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;



public class DiscussionsViewModel extends ViewModel {


    private MutableLiveData<String> mText;

    public DiscussionsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is discussion fragment");

    }

    public LiveData<String> getText() {
        return mText;
    }
}