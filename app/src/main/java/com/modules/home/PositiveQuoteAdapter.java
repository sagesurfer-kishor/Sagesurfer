package com.modules.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.constant.General;
import com.sagesurfer.constant.Quote;
import com.sagesurfer.library.ChangeCase;
import com.squareup.picasso.Picasso;
import com.storage.preferences.Preferences;

/**
 * Created by Kailash Karankal on 9/20/2019.
 */
public class PositiveQuoteAdapter extends RecyclerView.Adapter<PositiveQuoteAdapter.MyViewHolder> {
    private Activity activity;
    private PositiveQuoteListener positiveQuoteListener;

    public PositiveQuoteAdapter(Activity activity, PositiveQuoteListener positiveQuoteListener) {
        this.activity = activity;
        this.positiveQuoteListener = positiveQuoteListener;
    }

    @Override
    public PositiveQuoteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;

        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(parent.getContext().getResources().getString(R.string.sage023)) ||
                Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase(parent.getContext().getResources().getString(R.string.sage025))) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.inner_student_quote_layout_file, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.inner_quote_layout_file, parent, false);
        }

        return new PositiveQuoteAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final PositiveQuoteAdapter.MyViewHolder holder, final int position) {
        if (position == 0) {
            holder.helloTxt.setVisibility(View.GONE);
            holder.mood.setVisibility(View.GONE);
            holder.quoteFooter.setVisibility(View.VISIBLE);
            holder.quote_bg.setVisibility(View.VISIBLE);

            if (Preferences.get(Quote.QUOTE_NAME).length() >= 125) {
                holder.quoteMoto.setText(Preferences.get(Quote.QUOTE_NAME).substring(0, 125) + "...");
            } else {
                holder.quoteMoto.setText(Preferences.get(Quote.QUOTE_NAME));
                holder.quoteMoto.setGravity(Gravity.CENTER);
            }
            holder.autherName.setText("- " + Preferences.get(Quote.QUOTE_AUTHER_NAME));

            try {
                Picasso.get().load(Preferences.get(Quote.QUOTE_IMAGE_PATH)).transform(new RoundedTransformation(15, 0))
                        .fit().into(holder.quote_bg);
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.quoteMoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(v.getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.quote_more_info_dialog);
                    assert dialog.getWindow() != null;
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    TextView subTitle = (TextView) dialog.findViewById(R.id.delete_confirmation_sub_title);
                    final RelativeLayout bgLayout = (RelativeLayout) dialog.findViewById(R.id.quote_bg_layout);
                    subTitle.setText(Preferences.get(Quote.QUOTE_NAME));

                    Glide.with(v.getContext()).load(Preferences.get(Quote.QUOTE_IMAGE_PATH)).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                            bgLayout.setBackground(resource);
                        }
                    });

                    TextView okButton = (TextView) dialog.findViewById(R.id.delete_confirmation_ok_button);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 10000);

                }
            });

            holder.commentQuote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentQuoteDialog(v);
                }
            });

            holder.likeQuote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.likeQuote.setImageResource(R.drawable.like_red);
                    holder.unLikeQuote.setImageResource(R.drawable.quote_dislike);
                    positiveQuoteListener.likeDislikeItemClicked("like");
                }
            });

            holder.unLikeQuote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.unLikeQuote.setImageResource(R.drawable.unlike_red);
                    holder.likeQuote.setImageResource(R.drawable.quote_like);
                    positiveQuoteListener.likeDislikeItemClicked("dislike");
                }
            });

            holder.closeQuote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    positiveQuoteListener.showMoodLayout();
                    Toast.makeText(v.getContext(), "Removed successfully for this session", Toast.LENGTH_LONG).show();
                }
            });

            holder.quote_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        } else if (position == 1) {
            holder.name.setText(ChangeCase.toTitleCase(Preferences.get(General.NAME)));
            holder.closeQuote.setVisibility(View.GONE);
            holder.quoteHeader.setPadding(10, 10, 10, 10);

            holder.mood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    positiveQuoteListener.positiveQuoteItemClicked();
                }
            });
        }
    }

    private void commentQuoteDialog(View view) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_positive_quote);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final Button buttonSubmit = (Button) dialog.findViewById(R.id.button_submit);
        final Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);
        final EditText quoteComment = (EditText) dialog.findViewById(R.id.edittext_comment_quote);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quote = quoteComment.getText().toString().trim();
                if (quoteValidation(v, quote)) {
                    positiveQuoteListener.commentQuoteSend(quote);
                    dialog.dismiss();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView helloTxt, name, quoteMoto, mood, autherName;
        LinearLayout quoteHeader, quoteFooter;
        ImageView commentQuote, likeQuote, unLikeQuote, closeQuote, quote_bg;

        MyViewHolder(View view) {
            super(view);
            helloTxt = (TextView) view.findViewById(R.id.textview_greeting);
            name = (TextView) view.findViewById(R.id.textview_username);
            quoteMoto = (TextView) view.findViewById(R.id.textview_moto);
            mood = (TextView) view.findViewById(R.id.textview_mood);
            autherName = (TextView) view.findViewById(R.id.auther_name);
            quoteHeader = (LinearLayout) view.findViewById(R.id.linearlayout_header);
            quoteFooter = (LinearLayout) view.findViewById(R.id.linearlayout_footer);

            closeQuote = (ImageView) view.findViewById(R.id.close_quote);
            commentQuote = (ImageView) view.findViewById(R.id.comment_quote);
            likeQuote = (ImageView) view.findViewById(R.id.like_quote);
            unLikeQuote = (ImageView) view.findViewById(R.id.unlike_quote);
            quote_bg = (ImageView) view.findViewById(R.id.quote_img);
        }
    }

    public class RoundedTransformation implements com.squareup.picasso.Transformation {
        private final int radius;
        private final int margin;

        public RoundedTransformation(final int radius, final int margin) {
            this.radius = radius;
            this.margin = margin;
        }

        @Override
        public Bitmap transform(final Bitmap source) {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

            Bitmap output = Bitmap.createBitmap(source.getWidth(),
                    source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

            if (source != output) {
                source.recycle();
            }
            return output;
        }

        @Override
        public String key() {
            return "rounded";
        }
    }

    public interface PositiveQuoteListener {
        void positiveQuoteItemClicked();

        void likeDislikeItemClicked(String likeDislike);

        void commentQuoteSend(String commentQuote);

        void showMoodLayout();
    }

    private boolean quoteValidation(View view, String quoteText) {
        if (quoteText == null || quoteText.trim().length() <= 0) {
            Toast.makeText(view.getContext(), "Comment: Min 2 char required", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
