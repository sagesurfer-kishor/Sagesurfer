package screen.message_information;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.uikit.Avatar;
import com.cometchat.pro.uikit.R;

import java.util.List;

import utils.Utils;

public class AdapterReadReceipts extends RecyclerView.Adapter<AdapterReadReceipts.ReadReceiptsViewHolder> {
    private List<MessageReceipt> messageReceipts;
    private final Context mContext;

    public AdapterReadReceipts(List<MessageReceipt> messageReceipts, Context mContext) {
        this.messageReceipts = messageReceipts;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ReadReceiptsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cometchat_receipts_row, parent, false);
        return new ReadReceiptsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadReceiptsViewHolder holder, int position) {
        MessageReceipt messageReceipt=messageReceipts.get(position);

        holder.tvName.setText(messageReceipt.getSender().getName());
        if (messageReceipt.getReadAt()!=0)
            holder.tvRead.setText(Utils.getReceiptDate(messageReceipt.getReadAt()));
        if (messageReceipt.getDeliveredAt()!=0)
            holder.tvDelivery.setText(Utils.getReceiptDate(messageReceipt.getDeliveredAt()));
        if (messageReceipt.getSender().getAvatar()!=null)
            holder.ivAvatar.setAvatar(messageReceipt.getSender().getAvatar());
        else
            holder.ivAvatar.setInitials(messageReceipt.getSender().getName());
    }

    @Override
    public int getItemCount() {
        return messageReceipts.size();
    }

    class ReadReceiptsViewHolder extends  RecyclerView.ViewHolder {
        TextView tvName,tvDelivery,tvRead;
        Avatar ivAvatar;
        public ReadReceiptsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tvName);
            tvDelivery=itemView.findViewById(R.id.tvDelivery);
            tvRead=itemView.findViewById(R.id.tvRead);
            ivAvatar=itemView.findViewById(R.id.ivAvatar);
        }
    }
}
