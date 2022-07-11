package com.study.pagtabdemo.fragment;

import androidx.fragment.app.Fragment;

public class AppBaseFragment extends Fragment {

    public static interface Refreshable {
        void refreshData();
    }

}
