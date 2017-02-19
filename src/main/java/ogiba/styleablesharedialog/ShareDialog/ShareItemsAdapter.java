package ogiba.styleablesharedialog.ShareDialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ogiba.styleablesharedialog.R;
import ogiba.styleablesharedialog.ShareDialog.Models.ShareActionModel;

/**
 * Created by ogiba on 08.02.2017.
 */

public class ShareItemsAdapter extends RecyclerView.Adapter<ShareItemsAdapter.SharableViewHolder> {
    private Context context;
    private List<ShareActionModel> items;
    private boolean isHorizontal;

    public ShareItemsAdapter(Context context, boolean isHorizontal) {
        this.context = context;
        this.isHorizontal = isHorizontal;
        this.items = new ArrayList<>();
    }


    @Override
    public SharableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int itemLayout = R.layout.item_share_action;
        if (isHorizontal)
            itemLayout = R.layout.item_share_action_horizontal;

        View layout = LayoutInflater.from(context).inflate(itemLayout, parent, false);
        return new SharableViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(SharableViewHolder holder, int position) {
        holder.label.setText(items.get(position).getAppInfo().loadLabel(context.getPackageManager()));
        Drawable icon = items.get(position).getAppInfo().loadIcon(context.getPackageManager());
        holder.icon.setBackground(icon);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<ShareActionModel> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public class SharableViewHolder extends RecyclerView.ViewHolder {
        public View item;
        public ImageView icon;
        public TextView label;

        public SharableViewHolder(View itemView) {
            super(itemView);
            this.item = itemView.findViewById(R.id.item);
            this.icon = (ImageView) itemView.findViewById(R.id.icon);
            this.label = (TextView) itemView.findViewById(R.id.label);
        }
    }
}
