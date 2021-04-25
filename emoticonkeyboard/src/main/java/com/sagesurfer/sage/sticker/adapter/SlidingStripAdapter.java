package com.sagesurfer.sage.sticker.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.viewpager.widget.PagerAdapter;

import com.sagesurfer.emoticonkeyboard.R;
import com.sagesurfer.sage.sticker.mapping.RecentlyUsedStickers;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListBear;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListGeek;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListGery;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListHappyGhost;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListLittleDyno;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListMomon;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListMonster;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListPenguin;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListPirate;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListSkaterBoy;
import com.sagesurfer.sage.sticker.mapping.StickerDrawableArrayListVampire;
import com.sagesurfer.sage.sticker.mapping.StickerHashmapPatternToDrawable;
import com.sagesurfer.viewpager.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/*import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.inscripts.sticker.mapping.RecentlyUsedStickers;
import com.inscripts.sticker.mapping.StickerDrawableArrayListBear;
import com.inscripts.sticker.mapping.StickerDrawableArrayListGeek;
import com.inscripts.sticker.mapping.StickerDrawableArrayListGery;
import com.inscripts.sticker.mapping.StickerDrawableArrayListHappyGhost;
import com.inscripts.sticker.mapping.StickerDrawableArrayListLittleDyno;
import com.inscripts.sticker.mapping.StickerDrawableArrayListMomon;
import com.inscripts.sticker.mapping.StickerDrawableArrayListMonster;
import com.inscripts.sticker.mapping.StickerDrawableArrayListPenguin;
import com.inscripts.sticker.mapping.StickerDrawableArrayListPirate;
import com.inscripts.sticker.mapping.StickerDrawableArrayListSkaterBoy;
import com.inscripts.sticker.mapping.StickerDrawableArrayListVampire;
import com.inscripts.sticker.mapping.StickerHashmapPatternToDrawable;*/

public class SlidingStripAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider,
		RecentlyUsedStickers.RefreshGridViewAdapterListener {

	private final Activity context;
	private final int osVersion;
	private final StickerGridviewImageAdapter.StickerClickInterface clickListener;
    private StickerGridviewImageAdapter gridviewImageAdapterFirstTab;

	private final int[] mIcons = { R.drawable.clock_tab, R.drawable.bear_1, R.drawable.geek_1, R.drawable.gery_1,
			R.drawable.happ_1, R.drawable.litt_1, R.drawable.momo_1, R.drawable.mons_1, R.drawable.peng_1,
			R.drawable.pira_1, R.drawable.skat_1, R.drawable.vamp_1 };

	public SlidingStripAdapter(Activity context, int backgroundColor, int textColor,
			StickerGridviewImageAdapter.StickerClickInterface clickListener) {
		this.context = context;
		this.clickListener = clickListener;
		osVersion = android.os.Build.VERSION.SDK_INT;
	}

	@Override
	public int getPageIconResId(int position) {
		return mIcons[position];
	}

	@Override
	public int getCount() {
		return mIcons.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int pagePosition) {
		View view = context.getLayoutInflater().inflate(R.layout.gridview_layout_sticker_popup, null);
		GridView gridView = (GridView) view.findViewById(R.id.grid_view_stickerpopup);
        StickerGridviewImageAdapter gridviewImageAdapter;
		switch (pagePosition) {
		case 0:
			if (osVersion >= 11) {
				HashSet<String> set = new HashSet<>();
				List<String> listUnicode = new ArrayList<>(set);
				RecentlyUsedStickers.stickerUnicodeArrayListRecentcategory = (ArrayList<String>) listUnicode;
				ArrayList<Integer> stickerDrawable = new ArrayList<>();
				int recentEmojiCount = listUnicode.size();
				for (int j = 0; j < recentEmojiCount; j++) {
					String key = listUnicode.get(j);
					stickerDrawable.add(StickerHashmapPatternToDrawable.stickerMap.get(key));
				}
				RecentlyUsedStickers.stickerDrawableArrayListRecentCategory = stickerDrawable;
				gridviewImageAdapterFirstTab = new StickerGridviewImageAdapter(context,
						RecentlyUsedStickers.stickerDrawableArrayListRecentCategory, clickListener);
				gridView.setAdapter(gridviewImageAdapterFirstTab);

				container.addView(view, 0);
			} else {
				gridviewImageAdapterFirstTab = new StickerGridviewImageAdapter(context,
						RecentlyUsedStickers.stickerDrawableArrayListRecentCategory, clickListener);
				gridView.setAdapter(gridviewImageAdapterFirstTab);

				container.addView(view, 0);
			}
			break;
		case 1:
			gridviewImageAdapter = new StickerGridviewImageAdapter(context, StickerDrawableArrayListBear.stickerBear,
					clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			container.addView(view, 0);

			break;
		case 2:
			gridviewImageAdapter = new StickerGridviewImageAdapter(context, StickerDrawableArrayListGeek.stickerGeek,
					clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			container.addView(view, 0);
			break;
		case 3:
			gridviewImageAdapter = new StickerGridviewImageAdapter(context, StickerDrawableArrayListGery.stickerGery,
					clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			container.addView(view, 0);
			break;
		case 4:
			gridviewImageAdapter = new StickerGridviewImageAdapter(context,
					StickerDrawableArrayListHappyGhost.stickerHappyGhost, clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			container.addView(view, 0);
			break;
		case 5:
			gridviewImageAdapter = new StickerGridviewImageAdapter(context,
					StickerDrawableArrayListLittleDyno.stickerLittleDyno, clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			container.addView(view, 0);
			break;
		case 6:
			gridviewImageAdapter = new StickerGridviewImageAdapter(context, StickerDrawableArrayListMomon.stickerMomon,
					clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			container.addView(view, 0);
			break;
		case 7:
			gridviewImageAdapter = new StickerGridviewImageAdapter(context,
					StickerDrawableArrayListMonster.stickerMonster, clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			container.addView(view, 0);
			break;
		case 8:
			gridviewImageAdapter = new StickerGridviewImageAdapter(context,
					StickerDrawableArrayListPenguin.stickerPenguin, clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			container.addView(view, 0);
			break;
		case 9:
			gridviewImageAdapter = new StickerGridviewImageAdapter(context,
					StickerDrawableArrayListPirate.stickerPirate, clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			container.addView(view, 0);
			break;
		case 10:
			gridviewImageAdapter = new StickerGridviewImageAdapter(context,
					StickerDrawableArrayListSkaterBoy.stickerSkaterBoy, clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			container.addView(view, 0);
			break;
		case 11:
			gridviewImageAdapter = new StickerGridviewImageAdapter(context,
					StickerDrawableArrayListVampire.stickerVampire, clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			container.addView(view, 0);
			break;
		default:
			gridviewImageAdapter = new StickerGridviewImageAdapter(context, StickerDrawableArrayListBear.stickerBear,
					clickListener);
			gridView.setAdapter(gridviewImageAdapter);
			container.addView(view, 0);
			break;
		}

		return view;
	}

	@Override
	public void onNewStickerUsed() {
		gridviewImageAdapterFirstTab.notifyDataSetChanged();
	}
}
