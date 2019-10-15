package com.android.homescool.ui.findpapers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FindpapersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FindpapersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is find papers fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}