package com.example.wschat.functions;

import android.content.Context;
import android.content.res.Configuration;

public class MyStaticFunctions {
    Context appcontext;
    public MyStaticFunctions(Context appcontext) {
        this.appcontext = appcontext;
    }

    public boolean checkDarkTheme() {
        int nightModeFlags =
                this.appcontext.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        return Configuration.UI_MODE_NIGHT_YES == nightModeFlags;
    }
}
