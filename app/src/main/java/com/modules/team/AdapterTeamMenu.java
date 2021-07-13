package com.modules.team;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.Teams_;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AdapterTeamMenu extends RecyclerView.Adapter<AdapterTeamMenu.ViewHolderTeamMenu> {

    Context mContext;
    ArrayList<ModelTeamMenu> teamMenuList, list;
    private static final String TAG = "AdapterTeamMenu";

    public AdapterTeamMenu(Context mContext, ArrayList<ModelTeamMenu> list) {
        this.mContext = mContext;
        teamMenuList = list;
        Log.i(TAG, "AdapterTeamMenu: constructor");
    }

    @NonNull
    @Override
    public ViewHolderTeamMenu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_menu_list, parent, false);
        return new AdapterTeamMenu.ViewHolderTeamMenu(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTeamMenu holder, int position) {
        ModelTeamMenu item =teamMenuList.get(position);
        holder.tv_menu_name.setText(item.getName());
        holder.iv_menu.setImageDrawable(item.getImage_res());
    }

    @Override
    public int getItemCount() {
        return teamMenuList.size();
    }

    class ViewHolderTeamMenu extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView iv_menu;
        TextView tv_menu_name;
        public ViewHolderTeamMenu(@NonNull View itemView) {
            super(itemView);
            iv_menu=itemView.findViewById(R.id.iv_menu);
            tv_menu_name=itemView.findViewById(R.id.tv_menu_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position= getAdapterPosition();
            Log.i(TAG, "Adapter team menu item onClick: ");
            ((TeamDetailsActivity)mContext).performOnMenuItemClick(teamMenuList.get(position));
        }
    }
}
