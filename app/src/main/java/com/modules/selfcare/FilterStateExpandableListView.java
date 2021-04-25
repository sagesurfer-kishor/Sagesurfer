package com.modules.selfcare;

import android.content.Context;
import android.widget.ExpandableListView;

public class FilterStateExpandableListView extends ExpandableListView
{

        public FilterStateExpandableListView(Context context) {
			super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            //999999 is a size in pixels. ExpandableListView requires a maximum height in order to do measurement calculations.
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(999999, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
}