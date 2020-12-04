package com.sagesurfer.views;

import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;;
import android.view.View;

/**
 * Created by Monika on 8/1/2018.
 */

public class ItemOffSetDecoration extends RecyclerView.ItemDecoration {

    private int mItemOffset;

    public ItemOffSetDecoration(int itemOffset) {
        mItemOffset = itemOffset;
    }

    public ItemOffSetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
        this(context.getResources().getDimensionPixelSize(itemOffsetId));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
    }
}