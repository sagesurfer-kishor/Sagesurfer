package com.sagesurfer.sage.emoji.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sagesurfer.emoticonkeyboard.R;

import java.util.ArrayList;

public class EmojiGridviewImageAdapter extends BaseAdapter {

	private final ArrayList<Integer> mEmojiId;

	private final Context mContext;

	private final EmojiClickInterface emojiClickInterface;

	public EmojiGridviewImageAdapter(Context context, ArrayList<Integer> emojiId,
			EmojiClickInterface emojiClickInterface) {
		this.mContext = context;
		this.mEmojiId = emojiId;
		this.emojiClickInterface = emojiClickInterface;

	}

	@Override
	public int getCount() {
		return mEmojiId.size();
	}

	@Override
	public Object getItem(int position) {
		return mEmojiId.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	private static class Holder {
		ImageView imageView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		Holder holder = new Holder();
		if (null == view) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.gridview_customview, parent, false);
			holder.imageView = (ImageView) view.findViewById(R.id.ivCustoview_Gridview);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		holder.imageView.setImageResource(mEmojiId.get(position));
		final int pos = position;
		holder.imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				emojiClickInterface.getClickedEmoji(pos);
			}
		});

		return view;
	}

	public interface EmojiClickInterface {
		void getClickedEmoji(int gridviewItemPosition);
	}

}