package com.ezreal.treevieewlib;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 为支持在文字显示不全自动左右滚动（跑马灯）效果
 * 强制将 TextView 的 Focused 设置为 true
 * Created by wudeng on 2017/12/1.
 */

public class MarqueeTextView extends android.support.v7.widget.AppCompatTextView {

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
