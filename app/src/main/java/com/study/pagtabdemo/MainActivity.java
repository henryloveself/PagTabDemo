package com.study.pagtabdemo;

import android.os.Bundle;
import android.widget.TabWidget;

import androidx.appcompat.app.AppCompatActivity;
import com.study.pagtabdemo.constants.MainNestingConstants;

import com.study.pagtabdemo.view.MainNestingBottomTabView;

public class MainActivity extends AppCompatActivity {

    private MainNestingBottomTabView tabHostView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        tabHostView = findViewById(R.id.main_tab_host);
        tabHostView.setup(this, getSupportFragmentManager(), R.id.main_nesting_content);
        tabHostView.initIndicatorView();
        tabHostView.getTabWidget().setShowDividers(TabWidget.SHOW_DIVIDER_NONE);
        tabHostView.setCurrentTabByTag(MainNestingConstants.HOME_TAG);

    }
}