package com.sagesurfer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.icons.GetDrawerIcon;
import com.sagesurfer.library.ChangeCase;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.models.DrawerMenu_;

import org.w3c.dom.Text;

import java.util.List;

public class DrawerMenuAdapter extends RecyclerView.Adapter<DrawerMenuAdapter.DrawerMenuViewHolder> {
    private final Context _context;
    private final List<DrawerMenu_> headerList;
    LayoutInflater layoutInflater;
    private static final String TAG = "DrawerMenuAdapter";
    public DrawerMenuAdapter(@NonNull Context context, List<DrawerMenu_> headerList) {
        this._context = context;
        this.headerList = headerList;
    }

    @NonNull
    @Override
    public DrawerMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.drawer_list_group_layout, parent, false);
        DrawerMenuViewHolder viewHolder = new DrawerMenuViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DrawerMenuViewHolder holder, int position) {
        DrawerMenu_ item = headerList.get(position);
        String menu[] = item.getMenu().split(" ");
        if (!menu[0].equalsIgnoreCase("Line")) {
            holder.view_separator.setVisibility(View.GONE);
            holder.titleText.setText(ChangeCase.toTitleCase(item.getMenu()));
            //int color = Color.parseColor("#333333");
            //holder.icon.setColorFilter(color);
            Log.i(TAG, "onBindViewHolder: menu ids -> "+item.getId());
            holder.icon.setImageResource(GetDrawerIcon.get(item.getId()));
            holder.indicator.setVisibility(View.GONE);

            if (item.getCounter() > 0) {
                holder.counterText.setVisibility(View.VISIBLE);
                holder.counterText.setText(GetCounters.convertCounter(item.getCounter()));
            } else {
                holder.counterText.setVisibility(View.GONE);
            }
        } else {
            holder.view_separator.setVisibility(View.VISIBLE);
            holder.titleText.setVisibility(View.GONE);
            holder.icon.setVisibility(View.GONE);
            holder.counterText.setVisibility(View.GONE);
            holder.indicator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return headerList.size();
    }

    class DrawerMenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView titleText, counterText;
        AppCompatImageView icon;
        ImageView indicator;
        View view_separator;

        public DrawerMenuViewHolder(@NonNull View view) {
            super(view);
            titleText = view.findViewById(R.id.drawer_list_group_title);
            counterText = view.findViewById(R.id.drawer_list_group_counter);
            icon = view.findViewById(R.id.drawer_list_group_icon);
            indicator = view.findViewById(R.id.drawer_list_group_indicator);
            view_separator = view.findViewById(R.id.view_separator);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            ((MainActivity)_context).onDrawerMenuItemClickListner(headerList.get(position));
        }
    }
}
