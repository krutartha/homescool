package com.android.homescool.ui.findtextbooks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FindtextbooksViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FindtextbooksViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is find textbooks fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}