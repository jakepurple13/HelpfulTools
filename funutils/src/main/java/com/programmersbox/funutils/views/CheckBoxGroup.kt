package com.programmersbox.funutils.views

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.CompoundButtonCompat
import com.programmersbox.funutils.R
import com.programmersbox.funutils.views.CheckBoxGroup.OnCheckedChangeListener
import com.programmersbox.helpfulutils.animateChildren
import com.programmersbox.helpfulutils.colorFromTheme

class CheckBoxGroup : LinearLayout {

    private val mCheckedIds = mutableListOf<Int>()

    // tracks children checkbox buttons checked state
    private lateinit var mChildOnCheckedChangeListener: CompoundButton.OnCheckedChangeListener
    private lateinit var mPassThroughListener: PassThroughHierarchyChangeListener
    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null

    var isCheckGroupHeaderEnabled: Boolean = true
        private set(value) {
            field = value
            if (::headCheckBox.isInitialized)
                headCheckBox.visibility = if (isCheckGroupHeaderEnabled) View.VISIBLE else View.GONE
        }

    private var checkGroupHeaderTitle: String? = null

    @ColorInt
    private var checkGroupHeaderTitleColor: Int? = null

    @ColorInt
    private var checkGroupHeaderBackgroundColor: Int? = null

    private var innerMarginValue: Int? = null

    private var checkGroupHeaderMaxLines: Int = Int.MAX_VALUE

    private var checkGroupBoxTextSize: Float? = null

    @StyleRes
    private var checkGroupBoxTextAppearance: Int? = null

    @ColorInt
    private var checkGroupBoxColor: Int? = null

    private var groupCheckAction: Int = 0

    private var groupCustomAction: OnCustomGroupCheckListener? = null

    val headerCheckBox: CheckBox? get() = headCheckBox//getChildAt(0) as? CheckBox

    private lateinit var headCheckBox: CheckBox

    /**
     *
     * Returns the identifier of the selected checkbox button in this group.
     * Upon empty selection, the returned value is -1.
     *
     * @return the unique id of the selected checkbox button in this group
     * @see check
     * @see clearCheck
     */
    val checkedCheckboxButtonIds: MutableList<Int>
        @IdRes
        get() = HashSet(mCheckedIds).toMutableList()

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CheckBoxGroup)
        checkGroupHeaderTitle = a.getString(R.styleable.CheckBoxGroup_checkGroupHeaderTitle)
        checkGroupHeaderTitleColor = a.getColor(R.styleable.CheckBoxGroup_checkGroupHeaderTextColor, Color.BLACK)
        checkGroupHeaderBackgroundColor = a.getColor(R.styleable.CheckBoxGroup_checkGroupHeaderBackgroundColor, Color.TRANSPARENT)
        innerMarginValue = a.getDimensionPixelOffset(R.styleable.CheckBoxGroup_checkGroupChildInnerMargin, 10)
        checkGroupBoxTextSize = a.getDimension(R.styleable.CheckBoxGroup_checkGroupHeaderTextSize, -1f)
        checkGroupHeaderMaxLines = a.getInteger(R.styleable.CheckBoxGroup_checkGroupHeaderMaxLines, Int.MAX_VALUE)
        checkGroupBoxTextAppearance = a.getResourceId(R.styleable.CheckBoxGroup_checkGroupHeaderTextAppearance, -1)
        checkGroupBoxColor = a.getColor(R.styleable.CheckBoxGroup_boxColor, accentColor)
        isCheckGroupHeaderEnabled = a.getBoolean(R.styleable.CheckBoxGroup_isCheckGroupHeaderEnabled, true)
        groupCheckAction = a.getInt(R.styleable.CheckBoxGroup_groupCheckType, 0)
        a.recycle()
        mChildOnCheckedChangeListener = CheckedStateTracker()
        mPassThroughListener = PassThroughHierarchyChangeListener()
        super.setOnHierarchyChangeListener(mPassThroughListener)
        headCheckBox = getTitleCheckBox()
        if (groupCheckAction == GROUP_TYPE_ENABLE) {
            headCheckBox.isActivated = false
            headCheckBox.isChecked = true
        }
    }

    private val accentColor by lazy { context.colorFromTheme(R.attr.colorAccent, Color.BLACK) }

    /**
     *
     * Sets the selection to the checkbox button whose identifier is passed in
     * parameter. Using -1 as the selection identifier clears the selection;
     * such an operation is equivalent to invoking
     *
     * @param id the unique id of the checkbox button to select in this group
     */
    fun check(@IdRes id: Int) {
        val isChecked = mCheckedIds.contains(id)
        if (id != -1) setCheckedStateForView(id, isChecked)
        setCheckedId(id, isChecked)
    }

    private fun setCheckedId(@IdRes id: Int, isChecked: Boolean) {
        headCheckBox.setOnCheckedChangeListener(null)

        if (id == DEFAULT_HEADER_ID) {
            when (groupCheckAction) {
                GROUP_TYPE_CHECK -> children.drop(1).filterIsInstance<CheckBox>().forEach { it.isChecked = isChecked }
                GROUP_TYPE_ENABLE -> children.drop(1).filterIsInstance<CheckBox>().forEach { it.isEnabled = isChecked }
                GROUP_TYPE_CUSTOM -> groupCustomAction?.onGroupCheckChanged(this, isChecked)
            }
            headCheckBox.isChecked = isChecked
        } else {
            mCheckedIds.removeAll { it == -1 }
            if (mCheckedIds.contains(id) && !isChecked) {
                mCheckedIds.remove(id)
            } else {
                mCheckedIds.add(id)
            }
        }

        when (groupCheckAction) {
            GROUP_TYPE_CHECK -> defaultHeaderAction()
            GROUP_TYPE_ENABLE -> if (groupCheckAction == GROUP_TYPE_ENABLE && id == DEFAULT_HEADER_ID) headCheckBox.isChecked = isChecked
            GROUP_TYPE_CUSTOM -> groupCustomAction?.onGroupHeaderChange(this, headCheckBox) ?: defaultHeaderAction()
        }

        headCheckBox.setOnCheckedChangeListener(mChildOnCheckedChangeListener)

        mOnCheckedChangeListener?.onCheckedChanged(this, id, isChecked)
    }

    private fun defaultHeaderAction() {
        when (checkedCheckboxButtonIds.size) {
            0 -> {
                headCheckBox.isActivated = false
                headCheckBox.isChecked = false
            }
            childCount - 1 -> {
                headCheckBox.isActivated = false
                headCheckBox.isChecked = true
            }
            in 1..(childCount - 2) -> {
                headCheckBox.isActivated = true
            }
        }
    }

    fun setOnCustomGroupCheckListener(listener: OnCustomGroupCheckListener?) {
        if (groupCheckAction == GROUP_TYPE_CUSTOM) groupCustomAction = listener
    }

    fun setIsCheckGroupHeaderEnabled(visible: Boolean, animate: Boolean = true) {
        if (animate) {
            animateChildren { isCheckGroupHeaderEnabled = visible }
        } else {
            isCheckGroupHeaderEnabled = visible
        }
    }

    private fun setCheckedStateForView(@IdRes viewId: Int, checked: Boolean) {
        findViewById<CheckBox>(viewId)?.isChecked = checked
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams = LayoutParams(context, attrs)

    override fun setOnHierarchyChangeListener(listener: OnHierarchyChangeListener) {
        mPassThroughListener.mOnHierarchyChangeListener = listener
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        // checks the appropriate checkbox button as requested in the XML file
        mCheckedIds
            .filterNot { it <= 0 }
            .forEach {
                setCheckedStateForView(it, true)
                setCheckedId(it, true)
            }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        addCheckBoxHeader(0, params)
        val marginAddedParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        if (child is CheckBox) {
            innerMarginValue?.let {
                marginAddedParams.leftMargin = getInnerChildMargin(it)
                if (params is LinearLayout.LayoutParams) {
                    marginAddedParams.rightMargin = params.rightMargin
                    marginAddedParams.topMargin = params.topMargin
                    marginAddedParams.bottomMargin = params.bottomMargin
                }
            }
            CompoundButtonCompat.setButtonTintList(child, ColorStateList.valueOf(buttonColor))
            if (child.isChecked) setCheckedId(child.id, true)
        }
        super.addView(child, index, marginAddedParams)
    }

    private val buttonColor get() = checkGroupBoxColor ?: accentColor

    private fun getInnerChildMargin(dpValue: Int): Int = (dpValue * context.resources.displayMetrics.density).toInt() // margin in pixels

    private fun addCheckBoxHeader(index: Int, params: ViewGroup.LayoutParams?) {
        params?.let {
            if (childCount == 0 && isCheckGroupHeaderEnabled) {
                super.addView(headCheckBox, index, it)
            }
        }
    }

    private fun getTitleCheckBox() = CheckBox(context).apply {
        id = DEFAULT_HEADER_ID
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        checkGroupHeaderTitleColor?.let(this::setTextColor)
        checkGroupHeaderBackgroundColor?.let(this::setBackgroundColor)
        text = checkGroupHeaderTitle
        maxLines = checkGroupHeaderMaxLines
        checkGroupBoxTextAppearance?.let(this::setTextAppearance)
        checkGroupBoxTextSize?.let { f -> if (f != -1f) textSize = f }
        buttonDrawable = ContextCompat.getDrawable(context, R.drawable.checkbox_selector)
        CompoundButtonCompat.setButtonTintList(this, ColorStateList.valueOf(buttonColor))
    }

    /**
     *
     * Clears the selection. When the selection is cleared, no checkbox button in this group is selected null.
     */
    fun clearCheck() = check(-1)

    /**
     *
     * Register a callback to be invoked when the checked checkbox button
     * changes in this group.
     *
     * @param listener the callback to call on checked state change
     */
    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        mOnCheckedChangeListener = listener
    }

    fun setOnCheckedChangeListener(listener: (group: CheckBoxGroup, checkedId: Int, isChecked: Boolean) -> Unit) {
        mOnCheckedChangeListener = OnCheckedChangeListener(listener)
    }

    /**
     *
     * Interface definition for a callback to be invoked when the checked
     * checkbox button changed in this group.
     */
    fun interface OnCheckedChangeListener {
        /**
         *
         * Called when the checked checkbox button has changed. When the
         * selection is cleared, checkedId is -1.
         *
         * @param group     the group in which the checked checkbox button has changed
         * @param checkedId the unique identifier of the newly checked checkbox button
         */
        fun onCheckedChanged(group: CheckBoxGroup, @IdRes checkedId: Int, isChecked: Boolean)
    }

    /**
     * Use this if you set the [R.styleable.CheckBoxGroup_groupCheckType] to [GROUP_TYPE_CUSTOM]
     */
    fun interface OnCustomGroupCheckListener {
        /**
         * The action to do when the header is clicked
         */
        fun onGroupCheckChanged(group: CheckBoxGroup, isChecked: Boolean)

        /**
         * How to handle the [headerCheckBox] on changed
         */
        fun onGroupHeaderChange(group: CheckBoxGroup, headerCheckBox: CheckBox) = group.defaultHeaderAction()
    }

    /**
     *
     * This set of layout parameters defaults the width and the height of
     * the children to [LinearLayout.LayoutParams.WRAP_CONTENT] when they are not specified in the
     * XML file. Otherwise, this class used the value read from the XML file.
     *
     *
     * for a list of all child view attributes that this class supports.
     */
    @Suppress("unused")
    class LayoutParams : LinearLayout.LayoutParams {

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
        constructor(w: Int, h: Int) : super(w, h)
        constructor(w: Int, h: Int, initWeight: Float) : super(w, h, initWeight)
        constructor(p: ViewGroup.LayoutParams) : super(p)
        constructor(source: MarginLayoutParams) : super(source)

        /**
         *
         * Fixes the child's width to
         * [android.view.ViewGroup.LayoutParams.WRAP_CONTENT] and the child's
         * height to  [android.view.ViewGroup.LayoutParams.WRAP_CONTENT]
         * when not specified in the XML file.
         *
         * @param a          the styled attributes set
         * @param widthAttr  the width attribute to fetch
         * @param heightAttr the height attribute to fetch
         */
        override fun setBaseAttributes(a: TypedArray, widthAttr: Int, heightAttr: Int) {
            width = if (a.hasValue(widthAttr)) a.getLayoutDimension(widthAttr, "layout_width") else ViewGroup.LayoutParams.WRAP_CONTENT
            height = if (a.hasValue(heightAttr)) a.getLayoutDimension(heightAttr, "layout_height") else ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    private inner class CheckedStateTracker : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) = setCheckedId(buttonView.id, isChecked)
    }

    /**
     *
     * A pass-through listener acts upon the events and dispatches them
     * to another listener. This allows the table layout to set its own internal
     * hierarchy change listener without preventing the user to setup his.
     */
    private inner class PassThroughHierarchyChangeListener : OnHierarchyChangeListener {
        var mOnHierarchyChangeListener: OnHierarchyChangeListener? = null

        override fun onChildViewAdded(parent: View, child: View) {
            if (parent === this@CheckBoxGroup && child is CheckBox) {
                // generates an id if it's missing
                if (child.getId() == View.NO_ID) child.setId(View.generateViewId())
                child.setOnCheckedChangeListener(mChildOnCheckedChangeListener)
            }

            mOnHierarchyChangeListener?.onChildViewAdded(parent, child)
        }

        override fun onChildViewRemoved(parent: View, child: View) {
            if (parent === this@CheckBoxGroup && child is CheckBox) child.setOnCheckedChangeListener(null)
            mOnHierarchyChangeListener?.onChildViewRemoved(parent, child)
        }
    }

    companion object {
        const val DEFAULT_HEADER_ID: Int = 1234
        const val GROUP_TYPE_CHECK = 0
        const val GROUP_TYPE_ENABLE = 1
        const val GROUP_TYPE_CUSTOM = 2
    }
}