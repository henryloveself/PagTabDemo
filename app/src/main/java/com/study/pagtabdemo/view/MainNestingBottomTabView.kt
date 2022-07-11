package com.study.pagtabdemo.view


import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TabWidget
import androidx.fragment.app.FragmentManager
import com.study.pagtabdemo.R
import com.study.pagtabdemo.constants.MainNestingConstants
import com.study.pagtabdemo.util.UIUtil
import org.libpag.PAGComposition
import org.libpag.PAGFile
import org.libpag.PAGView

class MainNestingBottomTabView : FrameLayout {

    private var tag = "MainNestingBottomTabView"
    private var tabSize = 5
    private var pagComposition: PAGComposition? = null
    private var tabHostView: MainNestingPagTabHost? = null
    private var pagAnimationView: PAGView? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        View.inflate(context, R.layout.main_nesting_bottom_tab, this)
        tabHostView = findViewById(R.id.main_nesting_tab_host)
        pagAnimationView = findViewById(R.id.main_nesting_pag_view)
        initPAGLayer()
        initPAGAnimationListener()
    }

    private fun initPAGLayer() {
        pagComposition = PAGComposition.Make(UIUtil.getScreenWidth(context), 0)
    }

    private fun initPAGAnimationListener() {
        tabHostView?.setOnTabPAGAnimationListener(object :
            MainNestingPagTabHost.OnTabPAGAnimationListener {
            override fun onTabChange(tab: View?, select: Boolean, tag: String, step: Int) {
                if (select) {
                    Log.d(tag, "onTabChange:select:$select$tag")
                    var index = 0
                    when (tag) {
                        MainNestingConstants.HOME_TAG -> {
                            index = 0
                        }
                        MainNestingConstants.FUND_TAG -> {
                            index = 1
                        }
                        MainNestingConstants.PRIVATE_TAG -> {
                            index = 2
                        }
                        MainNestingConstants.PORTFOLIO_TAG -> {
                            index = 3
                        }
                        MainNestingConstants.TRADE_TAG -> {
                            index = 4
                        }
                    }
                    addLayer(index, step)
                    pagAnimationView?.setRepeatCount(1)
                    pagAnimationView?.play()
                    pagAnimationView?.addListener(object : PAGView.PAGViewListener {
                        override fun onAnimationStart(view: PAGView?) {

                        }

                        override fun onAnimationEnd(view: PAGView?) {
                            //将第一次默认选中图消失
                            if (tag == MainNestingConstants.HOME_TAG) {
                                tab?.findViewById<View>(R.id.tab_icon_default_select)?.visibility =
                                    View.GONE
                            }
                        }

                        override fun onAnimationCancel(view: PAGView?) {

                        }

                        override fun onAnimationRepeat(view: PAGView?) {

                        }
                    })
                }
            }

            override fun animateHomeTabIconToRefresh(step: Int) {
                addLayer(0, step)
                pagAnimationView?.post {
                    pagAnimationView?.play()
                }
            }

            override fun animateHomeTabIconToDefault(step: Int) {
                pagComposition?.removeAllLayers()
                addLayer(0, step)
                pagAnimationView?.post {
                    pagAnimationView?.play()
                }
            }
        })
    }

    private fun addLayer(index: Int, step: Int) {
        val itemWidth = (UIUtil.getScreenWidth(context) / tabSize).toFloat()
        pagComposition?.removeAllLayers()
        pagComposition?.addLayerAt(getPagFile(index, itemWidth, step), index)
        pagAnimationView?.composition = pagComposition
    }

    private fun getPagFile(column: Int, itemWidth: Float, step: Int): PAGFile? {
        val pagFile = PAGFile.Load(context.assets, getAnimationFilePath(column, step))
        if (pagFile != null) {
            val matrix = Matrix()
            //动画缩放，根据不同分辨率适配下
            val px = context.resources.displayMetrics.density
            val scale = px / 3
            Log.d(tag, "desity:" + px + "scale:" + scale)
            matrix.preScale(scale, scale)

            val marginTop = if (column == 0) {
                ((UIUtil.getDimension(
                    context,
                    R.dimen.main_nesting_tab_height
                ) / 2) - (pagFile.height() * scale / 2))//居中
            } else {
                UIUtil.dipToPix(context, 11f)//居上
            }
            matrix.postTranslate(
                itemWidth * column + (itemWidth - pagFile.width() * scale) / 2,
                marginTop
            )
            pagFile.setMatrix(matrix)
            pagFile.setDuration(1000)
        }
        return pagFile
    }

    private fun getAnimationFilePath(column: Int, step: Int): String {
        val postfix = ""
        return when (column) {
            0 -> {
                getHomeAnimationFilePath(step, postfix)
            }
            1 -> {
                "mainnesting/fund${postfix}.pag"
            }
            2 -> {
                "mainnesting/private${postfix}.pag"
            }
            3 -> {
                "mainnesting/portfolio${postfix}.pag"
            }
            4 -> {
                "mainnesting/trade${postfix}.pag"
            }
            else -> {
                ""
            }
        }
    }


    private fun getHomeAnimationFilePath(step: Int, postfix: String): String {
        return "mainnesting/home${step}.pag"
    }

    fun getCurrentTabTag(): String {
        return tabHostView?.currentTabTag ?: ""
    }

    fun getOptionalIconView(): View? {
        return tabHostView?.getOptionalIconView()
    }

    fun initIndicatorView() {
        tabHostView?.initIndicatorView()
    }

    fun getTabWidget(): TabWidget {
        return tabHostView!!.tabWidget
    }

    fun setup(context: Context, manager: FragmentManager, containerId: Int) {
        tabHostView?.setup(context, manager, containerId)
    }

    fun setCurrentTabByTag(tag: String) {
        tabHostView?.setCurrentTabByTag(tag)
    }
}