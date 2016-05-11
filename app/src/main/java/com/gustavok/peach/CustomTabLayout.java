package com.gustavok.peach;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.reflect.Field;

public class CustomTabLayout extends TabLayout {

    private static final int WIDTH_INDEX = 0;
    private static final int DIVIDER_FACTOR = 2;
    private static final String SCROLLABLE_TAB_MIN_WIDTH = "mScrollableTabMinWidth";
    private static final String TAG = "CustomTabLayout";

    public CustomTabLayout(Context context) {
        super(context);
        initTabMinWidth();
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTabMinWidth();
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTabMinWidth();
    }

    private void initTabMinWidth() {
        int[] wh = Utils.getScreenSize(getContext());
        int tabMinWidth = wh[WIDTH_INDEX] / DIVIDER_FACTOR;
        Log.d(TAG, ".................................. tabMinWidth: " + tabMinWidth);

        Field field;
        try {
            field = TabLayout.class.getDeclaredField(SCROLLABLE_TAB_MIN_WIDTH);
            field.setAccessible(true);
            field.set(this, tabMinWidth);
            Log.d(TAG, "Successfully set tabMinWidth to " + tabMinWidth);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "Error finding field to set min width", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Error accessing field to set min width", e);
        }
    }

}

