package com.sagesurfer.adapters;

import android.app.Activity;
import android.content.Context;
 import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.models.CaseloadPeerNoteReject_;

import java.util.ArrayList;

/**
 * Created by Monika on 10/26/2018.
 */

public class CaseloadPeerNoteRejectReasonsAdapter extends RecyclerView.Adapter<CaseloadPeerNoteRejectReasonsAdapter.MyViewHolder> {

    private static final String TAG = CaseloadListAdapter.class.getSimpleName();
    public final ArrayList<CaseloadPeerNoteReject_> rejectReasonsList;
    private final Activity activity;
    private final Context mContext;
    public final CaseloadPeerNoteRejectReasonsAdapter.CaseloadListAdapterListener caseloadListAdapterListener;

    public CaseloadPeerNoteRejectReasonsAdapter(Activity activity, Context mContext, ArrayList<CaseloadPeerNoteReject_> rejectReasonsList, CaseloadListAdapterListener caseloadListAdapterListener) {
        this.rejectReasonsList = rejectReasonsList;
        this.activity = activity;
        this.mContext = mContext;
        this.caseloadListAdapterListener = caseloadListAdapterListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final RelativeLayout detailsRelativeLayout;
        final TextView nameText;
        final CheckBox checkbox;

        MyViewHolder(View view) {
            super(view);
            detailsRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativelayout_caseload_details);
            nameText = (TextView) view.findViewById(R.id.multi_select_text);
            checkbox = (CheckBox) view.findViewById(R.id.multi_select_check_box);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiselect_country_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        viewHolder.nameText.setText(rejectReasonsList.get(position).getName());
        boolean checked = rejectReasonsList.get(position).isSelected();
        if (checked) {
            viewHolder.checkbox.setChecked(true);
        } else {
            viewHolder.checkbox.setChecked(false);
        }
        viewHolder.checkbox.setTag(position);

        viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                boolean checked = rejectReasonsList.get(position).isSelected();
                if (checked) {
                    viewHolder.checkbox.setChecked(false);
                    rejectReasonsList.get(position).setSelected(false);
                } else {
                    viewHolder.checkbox.setChecked(true);
                    rejectReasonsList.get(position).setSelected(true);
                    if(rejectReasonsList.get(position).getReason_id() == 5) { //other
                        caseloadListAdapterListener.onOtherClicked();
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    public interface CaseloadListAdapterListener {
        void onOtherClicked();
    }
    @Override
    public long getItemId(int position) {
        return rejectReasonsList.get(position).getReason_id();
    }

    @Override
    public int getItemCount() {
        return rejectReasonsList.size();
    }
}
