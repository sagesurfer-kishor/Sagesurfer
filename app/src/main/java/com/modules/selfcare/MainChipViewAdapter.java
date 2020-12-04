package com.modules.selfcare;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.library.GetColor;

class MainChipViewAdapter extends ChipViewAdapter {

    MainChipViewAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutRes(int position) {
        return R.layout.chip_close_layout;
    }

    @Override
    public int getBackgroundColor(int position) {
        return getColor(GetColor.getHomeIconBackgroundColorColorParse(false));
    }

    @Override
    public int getBackgroundColorSelected(int position) {
        return 0;
    }

    @Override
    public int getBackgroundRes(int position) {
        return 0;
    }

    @Override
    public void onLayout(View view, int position) {
        Tag tag = (Tag) getChip(position);

        if (tag.getType() == 2)
            ((TextView) view.findViewById(android.R.id.text1)).setTextColor(getColor(GetColor.getHomeIconBackgroundColorColorParse(false)));
    }
}
