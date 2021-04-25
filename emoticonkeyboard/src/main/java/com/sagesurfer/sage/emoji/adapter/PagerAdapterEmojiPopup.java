package com.sagesurfer.sage.emoji.adapter;

import android.app.Activity;
import android.os.Parcelable;
import android.view.View;
import android.widget.GridView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sagesurfer.emoticonkeyboard.R;
import com.sagesurfer.sage.emoji.adapter.EmojiGridviewImageAdapter.EmojiClickInterface;
import com.sagesurfer.sage.emoji.mapping.EmojiDrawableArrayListCarCategory;
import com.sagesurfer.sage.emoji.mapping.EmojiDrawableArrayListNatureCategory;
import com.sagesurfer.sage.emoji.mapping.EmojiDrawableArrayListObjectsCategory;
import com.sagesurfer.sage.emoji.mapping.EmojiDrawableArrayListPeopleCategory;
import com.sagesurfer.sage.emoji.mapping.EmojiDrawableArrayListSymbolsCategory;
import com.sagesurfer.sage.emoji.mapping.EmojiHashmapUnicodeToDrawable;
import com.sagesurfer.sage.emoji.mapping.RecentlyUsedEmoji;
import com.sagesurfer.sage.emoji.mapping.RecentlyUsedEmoji.RefreshGridViewAdapterListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PagerAdapterEmojiPopup extends PagerAdapter implements RefreshGridViewAdapterListener {

	private final transient Activity context;
	private int mLength = 0;
    private final int osVersion;
	private final EmojiClickInterface clickListener;
    private EmojiGridviewImageAdapter gridviewImageAdapterFirstTab;

	public PagerAdapterEmojiPopup(Activity context, int length, int backgroundColor, int textColor,
			EmojiClickInterface clickListener) {
		this.clickListener = clickListener;
		this.context = context;
		mLength = length;
		// osVersion =
		// Integer.parseInt(preference.getPrefernce(StaticMembers.PREF_ANDROID_OS_VERSION));
		osVersion = 16;
	}

	@Override
	public int getCount() {
		return mLength;
	}

	@SuppressWarnings("deprecation")
    @Override
	public Object instantiateItem(View container, int pagePosition) {
		View view = context.getLayoutInflater().inflate(R.layout.gridview_layout_emoji_popup, null);
		GridView gridView = (GridView) view.findViewById(R.id.grid_view_emojipopup);
        EmojiGridviewImageAdapter gridviewImageAdapter;

		switch (pagePosition) {
		case 0:
			// Log.d("emoji", "First page of pager");
			if (RecentlyUsedEmoji.emojiDrawableArrayListRecentCategory.size() == 0) {
				// Log.d("Replace","Inside recent tab page osVersion is "+osVersion);
				if (osVersion >= 11) {
					// Set set =
					// preference.getArrayList(StaticMembers.PREFSAVE_ARRAYLIST__KEY);
					HashSet<String> set = new HashSet<>();
					List<String> listUnicode = new ArrayList<>(set);
					RecentlyUsedEmoji.emojiUnicodeArrayListRecentcategory = (ArrayList<String>) listUnicode;
					ArrayList<Integer> emojiDrawable = new ArrayList<>();
					int recentEmojiCount = listUnicode.size();
					for (int j = 0; j < recentEmojiCount; j++) {
						String key = listUnicode.get(j);
						emojiDrawable.add(EmojiHashmapUnicodeToDrawable.emojiMap.get(key));
					}
					RecentlyUsedEmoji.emojiDrawableArrayListRecentCategory = emojiDrawable;
					gridviewImageAdapterFirstTab = new EmojiGridviewImageAdapter(context,
							RecentlyUsedEmoji.emojiDrawableArrayListRecentCategory, clickListener);
					gridView.setAdapter(gridviewImageAdapterFirstTab);
					// gridView.setOnItemClickListener(this);
					((ViewPager) container).addView(view, 0);
				} else {
					// Log.d("Replace","its 2.3 version ");
					gridviewImageAdapterFirstTab = new EmojiGridviewImageAdapter(context,
							RecentlyUsedEmoji.emojiDrawableArrayListRecentCategory, clickListener);
					gridView.setAdapter(gridviewImageAdapterFirstTab);
					// gridView.setOnItemClickListener(this);
					((ViewPager) container).addView(view, 0);
				}
			} else {
				gridviewImageAdapterFirstTab = new EmojiGridviewImageAdapter(context,
						RecentlyUsedEmoji.emojiDrawableArrayListRecentCategory, clickListener);
				gridView.setAdapter(gridviewImageAdapterFirstTab);
				// gridView.setOnItemClickListener(this);
				((ViewPager) container).addView(view, 0);
			}
			break;
		case 1:
			gridviewImageAdapter = new EmojiGridviewImageAdapter(context,
					EmojiDrawableArrayListPeopleCategory.emojiPeople, clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			// gridView.setOnItemClickListener(this);
			((ViewPager) container).addView(view, 0);

			break;
		case 2:
			gridviewImageAdapter = new EmojiGridviewImageAdapter(context,
					EmojiDrawableArrayListNatureCategory.emojiNature, clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			// gridView.setOnItemClickListener(this);
			((ViewPager) container).addView(view, 0);
			break;
		case 3:
			gridviewImageAdapter = new EmojiGridviewImageAdapter(context,
					EmojiDrawableArrayListObjectsCategory.emojiObjects, clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			// gridView.setOnItemClickListener(this);
			((ViewPager) container).addView(view, 0);
			break;
		case 4:
			gridviewImageAdapter = new EmojiGridviewImageAdapter(context, EmojiDrawableArrayListCarCategory.emojicars,
					clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			// gridView.setOnItemClickListener(this);
			((ViewPager) container).addView(view, 0);
			break;
		case 5:
			gridviewImageAdapter = new EmojiGridviewImageAdapter(context,
					EmojiDrawableArrayListSymbolsCategory.emojiSymbols, clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			// gridView.setOnItemClickListener(this);
			((ViewPager) container).addView(view, 0);
			break;
		default:
			gridviewImageAdapter = new EmojiGridviewImageAdapter(context,
					EmojiDrawableArrayListPeopleCategory.emojiPeople, clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			// gridView.setOnItemClickListener(this);
			((ViewPager) container).addView(view, 0);
			break;
		}

		return view;
	}

	@SuppressWarnings("deprecation")
    @Override
	public void destroyItem(View container, int position, Object view) {
		((ViewPager) container).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@SuppressWarnings("deprecation")
    @Override
	public void finishUpdate(View container) {
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@SuppressWarnings("deprecation")
    @Override
	public void startUpdate(View container) {
	}

	@Override
	public void onNewEmojiUsed() {
		gridviewImageAdapterFirstTab.notifyDataSetChanged();
	}

}
