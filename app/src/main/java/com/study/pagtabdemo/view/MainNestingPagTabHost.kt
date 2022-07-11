package com.study.pagtabdemo.view


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.study.pagtabdemo.R

import com.study.pagtabdemo.constants.MainNestingConstants.FUND_TAG
import com.study.pagtabdemo.constants.MainNestingConstants.HOME_TAG
import com.study.pagtabdemo.constants.MainNestingConstants.PORTFOLIO_TAG
import com.study.pagtabdemo.constants.MainNestingConstants.PRIVATE_TAG
import com.study.pagtabdemo.constants.MainNestingConstants.TRADE_TAG
import com.study.pagtabdemo.fragment.AppBaseFragment
import com.study.pagtabdemo.fragment.TestFragment

import java.util.*


class MainNestingPagTabHost : SNBFragmentTabHost {

    private var tag = "MainNestingBottomTabView"
    companion object {
        const val STEP_DEFAULT = 1
        const val STEP_TO_REFRESH = 2
        const val STEP_BACK_DEFAULT = 3
        const val STEP_DEFAULT_TO_REFRESH = 4

        const val FRAME_COUNT_PER_ANIMATION = 24 // 每个动画帧数
    }

    private var fragmentManager: FragmentManager? = null
    private var homeIndicatorView: View? = null
    private val tabManager = HashMap<String, View>()
    private var currentTag = HOME_TAG
    private var isHomeTabRefreshIcon = false
    private var portfolioIndicatorView: View? = null
    private var currentStep = STEP_DEFAULT



    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    fun getOptionalIconView(): View? {
        return  portfolioIndicatorView?.findViewById<View>(R.id.tab_icon_unselect)
    }

    fun initIndicatorView() {
        // 首页
        homeIndicatorView = getIndicatorView(HOME_TAG)
        addTab(newTabSpec(HOME_TAG).setIndicator(homeIndicatorView), TestFragment::class.java, getTabNameBundle(HOME_TAG))
        tabManager[HOME_TAG] = homeIndicatorView!!

        // 基金
        val fundIndicatorView = getIndicatorView(FUND_TAG)
        addTab(newTabSpec(FUND_TAG).setIndicator(fundIndicatorView), TestFragment::class.java, getTabNameBundle(FUND_TAG))
        tabManager[FUND_TAG] = fundIndicatorView

        // 私募
        val privateIndicatorView = getIndicatorView(PRIVATE_TAG)
        addTab(newTabSpec(PRIVATE_TAG).setIndicator(privateIndicatorView), TestFragment::class.java, getTabNameBundle(PRIVATE_TAG))
        tabManager[PRIVATE_TAG] = privateIndicatorView

        // 自选
        portfolioIndicatorView = getIndicatorView(PORTFOLIO_TAG)
        addTab(newTabSpec(PORTFOLIO_TAG).setIndicator(portfolioIndicatorView), TestFragment::class.java, getTabNameBundle(PORTFOLIO_TAG))
        tabManager[PORTFOLIO_TAG] = portfolioIndicatorView!!

        // 我的
        val myTagIndicatorView = getIndicatorView(TRADE_TAG)
        addTab(newTabSpec(TRADE_TAG).setIndicator(myTagIndicatorView), TestFragment::class.java, getTabNameBundle(TRADE_TAG))
        tabManager[TRADE_TAG] = myTagIndicatorView

        this.setOnTabChangedListener(tabChangeListener)
        tabChangeListener.invoke(HOME_TAG)
    }

    private fun getIndicatorView(tag: String): View {
        return if (tag == HOME_TAG) {
            View.inflate(context, R.layout.common_main_nesting_pag_home_tab_item, null).apply {
                findViewById<TextView>(R.id.tab_name)?.text = getIndicatorTitleByTag(tag)
            }
        } else {
            View.inflate(context, R.layout.common_main_nesting_pag_tab_item, null).apply {
                findViewById<TextView>(R.id.tab_name)?.text = getIndicatorTitleByTag(tag)
            }
        }
    }

    private fun getIndicatorTitleByTag(tag: String): String {
        return when (tag) {
            HOME_TAG -> "雪球"
            PORTFOLIO_TAG -> "自选"
            FUND_TAG -> "基金"
            TRADE_TAG -> "我的"
            PRIVATE_TAG -> "私募"
            else -> "abc"
        }
    }


    private fun getTabNameBundle(tag: String): Bundle {
        val bundle = Bundle()
        bundle.putString(TestFragment.ARG_TAB_NAME,getIndicatorTitleByTag(tag))
        return bundle
    }

    private val tabChangeListener = { tabId: String ->
        currentTag = tabId
        val it = tabManager.keys.iterator()
        while (it.hasNext()) {
            val key = it.next()
            when (currentTag) {
                key -> selectTab(key, tabManager[key])
                else -> unSelectTab(key, tabManager[key])
            }
        }

    }

    fun getCurrentStep():Int{
        return currentStep
    }

    private fun selectTab(tag: String, tab: View?) {
        setTabView(tab, true,tag,if (isHomeTabRefreshIcon) STEP_DEFAULT_TO_REFRESH else STEP_DEFAULT)
    }



    private fun unSelectTab(tag: String, tab: View?) {
        Log.d(tag,"unselect tag $tag")
        setTabView(tab, false, tag, if (isHomeTabRefreshIcon) STEP_DEFAULT_TO_REFRESH else STEP_DEFAULT)
    }

    private fun setTabView(tab: View?, select: Boolean, tag: String, step: Int) {
        tab?.findViewById<ImageView>(R.id.tab_icon_unselect)?.let { unselectImageView ->
            unselectImageView.visibility = if (!select) View.VISIBLE else View.GONE
            unselectImageView.setBackgroundResource(getUnselectBackgroundResource(tag))
        }
        tab?.findViewById<TextView>(R.id.tab_name)?.let { tabNameTextView ->
            tabNameTextView.visibility = if (select && tag == HOME_TAG) View.GONE else View.VISIBLE
            tabNameTextView.text = getIndicatorTitleByTag(tag)
            tabNameTextView.setTextColor(getTabNameTextColor(select))
        }
        onTabPAGAnimationListener?.onTabChange(tab,select, tag, step)
    }

    private fun getTabNameTextColor(select: Boolean) =
        if (select) Color.parseColor("#287DFF") else Color.parseColor("#797C86")

    private fun getUnselectBackgroundResource(tag: String): Int {
        return when (tag) {
            HOME_TAG -> {
                R.drawable.icon_bar_home_unselected_day
            }
            FUND_TAG -> {
                R.drawable.icon_bar_fund_unselected_day
            }
            PRIVATE_TAG -> {
                R.drawable.icon_bar_private_unselected_day
            }
            PORTFOLIO_TAG -> {
                R.drawable.icon_bar_portfolio_unselected_day
            }
            TRADE_TAG -> {
                R.drawable.icon_bar_my_unselected_day
            }
            else -> {
                0
            }
        }
    }


    private fun animateHomeTabIconToRefresh() {
        if (isHomeTabRefreshIcon) {
            return
        }

        isHomeTabRefreshIcon = true
        currentStep = STEP_TO_REFRESH
        onTabPAGAnimationListener?.animateHomeTabIconToRefresh(STEP_TO_REFRESH)
    }

    private fun animateHomeTabIconToDefault() {
        if (!isHomeTabRefreshIcon) {
            return
        }

        isHomeTabRefreshIcon = false
        currentStep = STEP_BACK_DEFAULT
        onTabPAGAnimationListener?.animateHomeTabIconToDefault(STEP_BACK_DEFAULT)

        homeIndicatorView?.findViewById<TextView>(R.id.tab_name)?.let {
            it.text = "雪球"
        }

    }

    override fun setCurrentTab(index: Int) {
        val isReSelect = index != -1 && index == currentTab
        if (isReSelect) {
            fragmentManager?.let {
                it.findFragmentByTag(currentTabTag).let { fragment ->
                    (fragment as? AppBaseFragment.Refreshable)?.refreshData()
                    return
                }
            }
        }

        try {
            super.setCurrentTab(index)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun setup(context: Context, manager: FragmentManager, containerId: Int) {
        this.fragmentManager = manager
        super.setup(context, manager, containerId)
    }

    override fun onAttachedToWindow() {
        try {
            super.onAttachedToWindow()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
//        EventBus.getDefault().unregister(this)
    }

    private var onTabPAGAnimationListener: OnTabPAGAnimationListener? = null

    fun setOnTabPAGAnimationListener(listener: OnTabPAGAnimationListener){
        this.onTabPAGAnimationListener = listener
    }

    interface OnTabPAGAnimationListener {
        fun onTabChange(tab: View?,select: Boolean, tag: String, step: Int)
        fun animateHomeTabIconToRefresh(step: Int)
        fun animateHomeTabIconToDefault(step: Int)
    }

}
