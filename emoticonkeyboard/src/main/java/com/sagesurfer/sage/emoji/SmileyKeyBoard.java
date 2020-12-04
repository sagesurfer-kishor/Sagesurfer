package com.sagesurfer.sage.emoji;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.viewpager.widget.ViewPager;

import com.sagesurfer.emoticonkeyboard.R;
import com.sagesurfer.sage.emoji.adapter.EmojiGridviewImageAdapter.EmojiClickInterface;
import com.sagesurfer.sage.emoji.adapter.FixedIconTabsAdapter;
import com.sagesurfer.sage.emoji.adapter.PagerAdapterEmojiPopup;
import com.sagesurfer.sage.emoji.custom.EmoticonHandler;
import com.sagesurfer.sage.emoji.mapping.EmojiDrawableArrayListCarCategory;
import com.sagesurfer.sage.emoji.mapping.EmojiDrawableArrayListNatureCategory;
import com.sagesurfer.sage.emoji.mapping.EmojiDrawableArrayListObjectsCategory;
import com.sagesurfer.sage.emoji.mapping.EmojiDrawableArrayListPeopleCategory;
import com.sagesurfer.sage.emoji.mapping.EmojiDrawableArrayListSymbolsCategory;
import com.sagesurfer.sage.emoji.mapping.EmojiUnicodeArrayListCarCategory;
import com.sagesurfer.sage.emoji.mapping.EmojiUnicodeArrayListNatureCategory;
import com.sagesurfer.sage.emoji.mapping.EmojiUnicodeArrayListObjectsCategory;
import com.sagesurfer.sage.emoji.mapping.EmojiUnicodeArrayListPeopleCategory;
import com.sagesurfer.sage.emoji.mapping.EmojiUnicodeArrayListSymbolsCategory;
import com.sagesurfer.sage.emoji.mapping.RecentlyUsedEmoji;
import com.sagesurfer.viewpager.FixedTabsView;


public class SmileyKeyBoard {
    private ViewPager viewPager;
	private PagerAdapterEmojiPopup adapterEmojiPopup;
	private static PopupWindow popupWindow;
	private static LinearLayout emoticonsCover;
	private int previousHeightDiffrence = 0, keyboardHeight, userKeyboardHeight = -1;
	private boolean isKeyBoardVisible;
    private Activity activity;
	private EmoticonHandler mEmoticonHandler;
	private int count = 0;
	private static ViewTreeObserver.OnGlobalLayoutListener layoutListener;

	public void enable(Activity activity, EmojiClickInterface clickListener, Integer emojiFooterId,
			final EditText edtMessage) {
		this.activity = activity;
        View popUpView = activity.getLayoutInflater().inflate(R.layout.activity_fixed_tabs, null);
		viewPager = (ViewPager) popUpView.findViewById(R.id.pager_popviewPopupEmoji);
		viewPager.setOffscreenPageLimit(6);
		emoticonsCover = (LinearLayout) activity.findViewById(emojiFooterId);
        ImageView ivEmojiClear = (ImageView) popUpView.findViewById(R.id.ivClearEmoji);
		adapterEmojiPopup = new PagerAdapterEmojiPopup(activity, 6, Color.CYAN, Color.BLACK, clickListener);
		viewPager.setAdapter(adapterEmojiPopup);
		viewPager.setCurrentItem(1);
		FixedTabsView fixedTabs = (FixedTabsView) popUpView.findViewById(R.id.fixed_tabsPopupEmoji);

		FixedIconTabsAdapter fixedIconTabsAdapter = new FixedIconTabsAdapter(activity);
		fixedTabs.setAdapter(fixedIconTabsAdapter);
		fixedTabs.setViewPager(viewPager);
		mEmoticonHandler = new EmoticonHandler(edtMessage, activity.getApplicationContext());
		// Creating a pop window for emoticons keyboard

		popupWindow = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT, keyboardHeight, false);

		ivEmojiClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				edtMessage.dispatchKeyEvent(event);
			}
		});

	}

	/**
	 * Set height for emoji keyboard.
	 * The emoji keyboard adjusts its height to match the height of your in-build keyboard. If the height is not
	 * matching, then you can use this function to set the height.
	 * @param height
	 *            Pass the height in pixels e.g 600 without any unit.<br>
	 *            Pass -1 to reset the keyboard height and let emoji keyboard to handle the height adjustment
	 */
	public void setKeyBoardHeight(int height) {
		userKeyboardHeight = height;
	}

	public void showKeyboard(View parentLayout) {
		calculateKeyboardHeight(parentLayout);
		if (!popupWindow.isShowing()) {
			if (userKeyboardHeight == -1) {
				popupWindow.setHeight(keyboardHeight);
			} else {
				popupWindow.setHeight(userKeyboardHeight);
			}
			if (isKeyBoardVisible) {
				checkKeyboardHeight(parentLayout, false);
				emoticonsCover.setVisibility(View.GONE);
			} else {
				checkKeyboardHeight(parentLayout, true);
				emoticonsCover.setVisibility(View.VISIBLE);
			}
			popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
		} else {
			checkKeyboardHeight(parentLayout, false);
			emoticonsCover.setVisibility(View.GONE);
			popupWindow.dismiss();
		}
	}

	public static void dismissKeyboard() {
		if (popupWindow.isShowing()) {
			emoticonsCover.setVisibility(View.GONE);
			popupWindow.dismiss();
		}
	}

	public static boolean isKeyboardVisibile() {
		return null != popupWindow && popupWindow.isShowing();
	}

	@SuppressLint("NewApi")
	public void checkKeyboardHeight(final View parentLayout) {
		layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
            @Override
			public void onGlobalLayout() {
				if (count < 10) {
					calculateKeyboardHeight(parentLayout);
					count++;
				} else {
					count = 0;
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						parentLayout.getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
					} else {
						parentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
					}
				}
			}
		};

		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

	}

	@SuppressLint("NewApi")
	private void checkKeyboardHeight(final View parentLayout, boolean removeLayoutListener) {
		layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				calculateKeyboardHeight(parentLayout);
			}
		};
		if (removeLayoutListener) {
			parentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
		} else {
			parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
		}
	}

	private void calculateKeyboardHeight(final View parentLayout) {
		Rect r = new Rect();
		parentLayout.getWindowVisibleDisplayFrame(r);

		int screenHeight = parentLayout.getRootView().getHeight();

		int heightDifference = screenHeight - (r.bottom);
		if (getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
				int bottomNavigationHeight;
				Resources resources = activity.getResources();
				int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
				if (resourceId > 0) {
					bottomNavigationHeight = resources.getDimensionPixelSize(resourceId);
				} else {
					bottomNavigationHeight = 100;
				}
				// heightDifference = heightDifference - bottomNavigationHeight;
			}
			if (previousHeightDiffrence - heightDifference > 50) {
				popupWindow.dismiss();
			}
			previousHeightDiffrence = heightDifference;
			if (heightDifference > 100) {
				isKeyBoardVisible = true;
				changeKeyboardHeight(heightDifference);
			} else {
				isKeyBoardVisible = false;

			}
		} else if (getScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
			heightDifference = screenHeight / 2 - 50;
			changeKeyboardHeight(heightDifference);
		}
	}

	@SuppressWarnings("deprecation")
    private int getScreenOrientation() {
		Display getOrient = activity.getWindowManager().getDefaultDisplay();
		int orientation = Configuration.ORIENTATION_UNDEFINED;
		if (getOrient.getWidth() == getOrient.getHeight()) {
			orientation = Configuration.ORIENTATION_SQUARE;
		} else {
			if (getOrient.getWidth() < getOrient.getHeight()) {
				orientation = Configuration.ORIENTATION_PORTRAIT;
			} else {
				orientation = Configuration.ORIENTATION_LANDSCAPE;
			}
		}
		return orientation;
	}

	private void changeKeyboardHeight(int height) {
		keyboardHeight = height;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, keyboardHeight);
		emoticonsCover.setLayoutParams(params);
	}

	public void enableFooterView(EditText messageField) {

		messageField.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissKeyboard();
			}
		});
	}

	public void getClickedEmoji(int gridviewItemPosition) {
		int pagePosition = viewPager.getCurrentItem();
		int drawableId;
		Drawable drawable = null;
		String unicodeToBeSend = "";
		switch (pagePosition) {

		case 0: {
			drawableId = RecentlyUsedEmoji.emojiDrawableArrayListRecentCategory.get(gridviewItemPosition);
			drawable = activity.getResources().getDrawable(drawableId);
			unicodeToBeSend = RecentlyUsedEmoji.emojiUnicodeArrayListRecentcategory.get(gridviewItemPosition);
			break;
		}
		case 1: {
			drawableId = EmojiDrawableArrayListPeopleCategory.emojiPeople.get(gridviewItemPosition);
			drawable = activity.getResources().getDrawable(drawableId);
			unicodeToBeSend = EmojiUnicodeArrayListPeopleCategory.unicodePeople.get(gridviewItemPosition);
			RecentlyUsedEmoji.addEmoji(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}
		case 2: {
			drawableId = EmojiDrawableArrayListNatureCategory.emojiNature.get(gridviewItemPosition);
			drawable = activity.getResources().getDrawable(drawableId);
			unicodeToBeSend = EmojiUnicodeArrayListNatureCategory.unicodeNature.get(gridviewItemPosition);
			RecentlyUsedEmoji.addEmoji(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}
		case 3: {
			drawableId = EmojiDrawableArrayListObjectsCategory.emojiObjects.get(gridviewItemPosition);
			drawable = activity.getResources().getDrawable(drawableId);
			unicodeToBeSend = EmojiUnicodeArrayListObjectsCategory.unicodeObjects.get(gridviewItemPosition);
			RecentlyUsedEmoji.addEmoji(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}
		case 4: {
			drawableId = EmojiDrawableArrayListCarCategory.emojicars.get(gridviewItemPosition);
			drawable = activity.getResources().getDrawable(drawableId);
			unicodeToBeSend = EmojiUnicodeArrayListCarCategory.unicodeCar.get(gridviewItemPosition);
			RecentlyUsedEmoji.addEmoji(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}

		case 5: {
			drawableId = EmojiDrawableArrayListSymbolsCategory.emojiSymbols.get(gridviewItemPosition);
			drawable = activity.getResources().getDrawable(drawableId);
			unicodeToBeSend = EmojiUnicodeArrayListSymbolsCategory.unicodeSymbols.get(gridviewItemPosition);
			RecentlyUsedEmoji.addEmoji(unicodeToBeSend, drawableId, adapterEmojiPopup);
			break;
		}

		default:
			break;
		}

		mEmoticonHandler.insert(unicodeToBeSend + " ", drawable);
	}
}
