package com.study.pagtabdemo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.study.pagtabdemo.R

class TestFragment : AppBaseFragment() {

    private var tabName: String = ""
    private var containerView: View? = null
    private var tvTestView: TextView? = null

    companion object {

        const val ARG_TAB_NAME = "arg_tab_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabName = requireArguments().getString(ARG_TAB_NAME, "")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (containerView == null) {
            containerView = inflater.inflate(R.layout.fragment_test, container, false)
            tvTestView = containerView?.findViewById(R.id.tv_test)
            tvTestView?.text = tabName
        }
        return containerView
    }
}