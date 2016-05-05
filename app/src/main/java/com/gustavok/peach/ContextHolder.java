package com.gustavok.peach;

import android.content.Context;

public final class ContextHolder {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ContextHolder.context = context;
    }
}
