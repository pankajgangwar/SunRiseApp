package iriemo.bangaloreweather;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Created by iriemo on 9/6/15.
 */
public class LocationEditTextPreference extends EditTextPreference{

    public LocationEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.LocationEditTextPreference,0,0);
        try{
            int minLength = a.getInteger(R.styleable.LocationEditTextPreference_minLength,4);
        }finally {
            a.recycle();
        }
    }
}
