package com.modules.selfcare;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sagesurfer.collaborativecares.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public abstract class ChipViewAdapter extends Observable {

    private final Context mContext;
    private AttributeSet mAttributeSet;
    private final LayoutInflater mInflater;
    private List<Chip> mChipList;

    private int mChipSpacing;
    private int mLineSpacing;
    private int mChipPadding;
    private int mChipCornerRadius;
    private int mChipSidePadding;
    private int mChipTextSize;
    private int mChipRes;
    private int mChipBackgroundColor;
    private int mChipBackgroundColorSelected;
    private int mChipBackgroundRes;
    private boolean mHasBackground = true;
    private boolean mToleratingDuplicate = false;

    public abstract int getLayoutRes(int position);

    public abstract int getBackgroundRes(int position);

    public abstract int getBackgroundColor(int position);

    public abstract int getBackgroundColorSelected(int position);

    public abstract void onLayout(View view, int position);

    protected ChipViewAdapter(Context context) {
        this(context, null);
    }

    public ChipViewAdapter(Context context, AttributeSet attributeSet) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mChipList = new ArrayList<>();
        setAttributeSet(attributeSet);
    }

    private void init() {
        mChipSpacing = mContext.getResources().getDimensionPixelSize(R.dimen.chip_spacing);
        mLineSpacing = mContext.getResources().getDimensionPixelSize(R.dimen.chip_line_spacing);
        mChipPadding = mContext.getResources().getDimensionPixelSize(R.dimen.chip_padding);
        mChipSidePadding = mContext.getResources().getDimensionPixelSize(R.dimen.chip_side_padding);
        mChipCornerRadius = mContext.getResources().getDimensionPixelSize(R.dimen.chip_corner_radius);
        mChipBackgroundColor = getColor(R.color.chip_background);
        mChipBackgroundColorSelected = getColor(R.color.chip_background_selected);

        if (mAttributeSet != null) {
            TypedArray typedArray = mContext.getTheme().obtainStyledAttributes(mAttributeSet, R.styleable.ChipView, 0, 0);

            try {
                mChipSpacing = (int) typedArray.getDimension(R.styleable.ChipView_chip_spacing, mChipSpacing);
                mLineSpacing = (int) typedArray.getDimension(R.styleable.ChipView_chip_line_spacing, mLineSpacing);
                mChipPadding = (int) typedArray.getDimension(R.styleable.ChipView_chip_padding, mChipPadding);
                mChipSidePadding = (int) typedArray.getDimension(R.styleable.ChipView_chip_side_padding, mChipSidePadding);
                mChipCornerRadius = (int) typedArray.getDimension(R.styleable.ChipView_chip_corner_radius, mChipCornerRadius);
                mChipBackgroundColor = typedArray.getColor(R.styleable.ChipView_chip_background, mChipBackgroundColor);
                mChipBackgroundColorSelected = typedArray.getColor(R.styleable.ChipView_chip_background_selected, mChipBackgroundColorSelected);
                mChipBackgroundRes = typedArray.getResourceId(R.styleable.ChipView_chip_background_res, 0);
            } finally {
                typedArray.recycle();
            }
        }
    }

    @SuppressWarnings("deprecation")
    public View getView(ViewGroup parent, int position) {
        View view = null;
        Chip chip = getChip(position);

        if (chip != null) {
            int chipLayoutRes = (getLayoutRes(position) != 0 ? getLayoutRes(position) : getChipLayoutRes());
            Drawable chipBackground = generateBackgroundSelector(position);

            if (chipLayoutRes == 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, mChipSpacing, mLineSpacing);
                view = new LinearLayout(mContext);
                view.setLayoutParams(layoutParams);
                ((LinearLayout) view).setOrientation(LinearLayout.HORIZONTAL);
                ((LinearLayout) view).setGravity(Gravity.CENTER_VERTICAL);
                view.setPadding(mChipSidePadding, mChipPadding, mChipSidePadding, mChipPadding);

                TextView text = new TextView(mContext);
                text.setId(android.R.id.text1);
                ((LinearLayout) view).addView(text);
            } else {
                view = mInflater.inflate(chipLayoutRes, parent, false);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, (layoutParams.rightMargin > 0 ? layoutParams.rightMargin : mChipSpacing), (layoutParams.bottomMargin > 0 ? layoutParams.bottomMargin : mLineSpacing));
            }

            TextView text = (TextView) view.findViewById(android.R.id.text1);
            View content = view.findViewById(android.R.id.content);

            if (text != null) {
                String word = chip.getText();
                if (word.length() > 50) {
                    word = word.substring(0, 39);
                    word = word + "...";
                }
                text.setText(word);
                //text.setGravity(Gravity.CENTER);

                if (mChipTextSize > 0)
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, mChipTextSize);
            }

            if (mHasBackground) {
                if (content != null)
                    content.setBackground(chipBackground);
                else
                    view.setBackground(chipBackground);
            }

            onLayout(view, position);
        }

        return view;
    }

    private Drawable generateBackgroundSelector(int position) {
        if (getBackgroundRes(position) != 0)
            return mContext.getResources().getDrawable(getBackgroundRes(position));
        else if (mChipBackgroundRes != 0) {
            return mContext.getResources().getDrawable(mChipBackgroundRes);
        }

        int backgroundColor = (getBackgroundColor(position) != 0 ? getBackgroundColor(position) : mChipBackgroundColor);
        int backgroundColorSelected = (getBackgroundColorSelected(position) != 0 ? getBackgroundColorSelected(position) : mChipBackgroundColorSelected);

        // Default state
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setCornerRadius(mChipCornerRadius);
        background.setColor(backgroundColor);

        // Selected state
        GradientDrawable selectedBackground = new GradientDrawable();
        selectedBackground.setShape(GradientDrawable.RECTANGLE);
        selectedBackground.setCornerRadius(mChipCornerRadius);
        selectedBackground.setColor(backgroundColorSelected);

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, selectedBackground);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, selectedBackground);
        stateListDrawable.addState(StateSet.WILD_CARD, background);

        return stateListDrawable;
    }

    private void notifyUpdate() {
        setChanged();
        notifyObservers();
    }

    protected Chip getChip(int position) {
        return (position < count() ? mChipList.get(position) : null);
    }

    public void add(Chip chip) {
        if (!mChipList.contains(chip) || mToleratingDuplicate) {
            mChipList.add(chip);
            notifyUpdate();
        }
    }

    public void remove(Chip chip) {
        mChipList.remove(chip);
        notifyUpdate();
    }

    public int count() {
        return mChipList.size();
    }

    protected int getColor(@ColorRes int colorRes) {
        return mContext.getResources().getColor(colorRes);
    }

    public Context getContext() {
        return mContext;
    }

    public AttributeSet getAttributeSet() {
        return mAttributeSet;
    }

    private void setAttributeSet(AttributeSet attributeSet) {
        mAttributeSet = attributeSet;
        init();
    }

    public List<Chip> getChipList() {
        return mChipList;
    }

    public void setChipList(List<Chip> chipList) {
        mChipList = chipList;
        notifyUpdate();
    }

    public boolean isToleratingDuplicate() {
        return mToleratingDuplicate;
    }

    public void setToleratingDuplicate(boolean toleratingDuplicate) {
        mToleratingDuplicate = toleratingDuplicate;
    }

    public boolean hasBackground() {
        return mHasBackground;
    }

    public void setHasBackground(boolean hasBackground) {
        mHasBackground = hasBackground;
    }

    public int getChipSpacing() {
        return mChipSpacing;
    }

    public void setChipSpacing(int chipSpacing) {
        mChipSpacing = chipSpacing;
    }

    public int getLineSpacing() {
        return mLineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        mLineSpacing = lineSpacing;
    }

    public int getChipPadding() {
        return mChipPadding;
    }

    public void setChipPadding(int chipPadding) {
        mChipPadding = chipPadding;
    }

    public int getChipSidePadding() {
        return mChipSidePadding;
    }

    public void setChipSidePadding(int chipSidePadding) {
        mChipSidePadding = chipSidePadding;
    }

    public int getChipCornerRadius() {
        return mChipCornerRadius;
    }

    public void setChipCornerRadius(int chipCornerRadius) {
        mChipCornerRadius = chipCornerRadius;
    }

    public int getChipBackgroundColor() {
        return mChipBackgroundColor;
    }

    public void setChipBackgroundColor(@ColorInt int chipBackgroundColor) {
        mChipBackgroundColor = chipBackgroundColor;
    }

    public int getChipBackgroundColorSelected() {
        return mChipBackgroundColorSelected;
    }

    public void setChipBackgroundColorSelected(@ColorInt int chipBackgroundColorSelected) {
        mChipBackgroundColorSelected = chipBackgroundColorSelected;
    }

    public int getChipTextSize() {
        return mChipTextSize;
    }

    public void setChipTextSize(int chipTextSize) {
        mChipTextSize = chipTextSize;
    }

    public void setChipBackgroundRes(@DrawableRes int backgroundRes) {
        mChipBackgroundRes = backgroundRes;
    }

    public int getChipLayoutRes() {
        return mChipRes;
    }

    public void setChipLayoutRes(@LayoutRes int chipRes) {
        mChipRes = chipRes;
    }
}
