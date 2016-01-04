package com.mtk.app.fota;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mtk.btnotification.R;

public class CheckNewVersionButtonPreference extends Preference {

    private Button mButton;
    private View.OnClickListener mListener;

    public CheckNewVersionButtonPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWidgetLayoutResource(R.layout.check_new_version_button_pref_layout);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        // TODO Auto-generated method stub

        return LayoutInflater.from(getContext()).inflate(
                R.layout.check_new_version_button_pref_layout, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        // TODO Auto-generated method stub
        super.onBindView(view);
        mButton = (Button) view.findViewById(R.id.checkButton);
        mButton.setOnClickListener(mListener);
    }

    public void setButtonClickListener(View.OnClickListener listener) {
        if (listener == null) {
            return;
        }
        mListener = listener;
    }

}
