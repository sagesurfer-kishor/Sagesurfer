package com.sagesurfer.sage.emoji.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.widget.EditText;

import com.sagesurfer.emoticonkeyboard.R;

import java.util.ArrayList;

public class EmoticonHandler implements TextWatcher {

	private final EditText mEditor;

	private final ArrayList<ImageSpan> mEmoticonsToRemove = new ArrayList<>();

	private int emojiSize;

	public EmoticonHandler(EditText editor, Context context) {
		// Attach the handler to listen for text changes.
		mEditor = editor;
		mEditor.addTextChangedListener(this);
		emojiSize = (int) context.getResources().getDimension(R.dimen.emoji_size)
				/ (int) context.getResources().getDisplayMetrics().density + 7;

		int actsize = Integer.parseInt(context.getResources().getString(R.string.emoji_size_actual)) + 7;
		if (emojiSize != actsize) {
			emojiSize = actsize;
		}
	}

	public void insert(String emoticon, Drawable d) {
		// Create the ImageSpan
		// Drawable drawable = mEditor.getResources().getDrawable(resource);

        d.setBounds(0, 0, emojiSize, emojiSize);
		ImageSpan span = new ImageSpan(d, DynamicDrawableSpan.ALIGN_BOTTOM);

		// Get the selected text.
		int start = mEditor.getSelectionStart(),end = mEditor.getSelectionEnd();
		Editable message = mEditor.getEditableText();

		// Insert the emoticon.
		message.replace(start, end, emoticon);
		message.setSpan(span, start, start + emoticon.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	@Override
	public void beforeTextChanged(CharSequence text, int start, int count, int after) {
		// Check if some text will be removed.
		if (count > 0) {
			int end = start + count;
			Editable message = mEditor.getEditableText();
			ImageSpan[] list = message.getSpans(start, end, ImageSpan.class);

			for (ImageSpan span : list) {
				// Get only the emoticons that are inside of the changed
				// region.
				int spanStart = message.getSpanStart(span);
				int spanEnd = message.getSpanEnd(span);
				if ((spanStart < end) && (spanEnd > start)) {
					// Add to remove list
					mEmoticonsToRemove.add(span);
				}
			}
		}
	}

	@Override
	public void afterTextChanged(Editable text) {
		Editable message = mEditor.getEditableText();

		// Commit the emoticons to be removed.
		for (ImageSpan span : mEmoticonsToRemove) {
			int start = message.getSpanStart(span);
			int end = message.getSpanEnd(span);

			// Remove the span
			message.removeSpan(span);

			// Remove the remaining emoticon text.
			if (start != end) {
				message.delete(start, end);
			}
		}
		mEmoticonsToRemove.clear();
	}

	@Override
	public void onTextChanged(CharSequence text, int start, int before, int count) {
	}

}
