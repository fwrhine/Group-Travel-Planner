package com.example.pplki18.grouptravelplanner.utils;

        import android.content.ClipData.Item;
        import android.support.v7.widget.RecyclerView;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.FrameLayout;
        import android.widget.TextView;

        import com.example.pplki18.grouptravelplanner.R;

        import java.util.List;

public class Adapter_NewPlan extends RecyclerView.Adapter<Adapter_NewPlan.ViewHolder>{
    private static final int VIEW_TYPE_TOP = 0;
    private static final int VIEW_TYPE_MIDDLE = 1;
    private static final int VIEW_TYPE_BOTTOM = 2;
    private List<Item> mItems;

// ...

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mItemTitle;
        TextView mItemSubtitle;
        FrameLayout mItemLine;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemTitle = (TextView) itemView.findViewById(R.id.item_title);
            mItemSubtitle = (TextView) itemView.findViewById(R.id.item_subtitle);
            mItemLine = (FrameLayout) itemView.findViewById(R.id.item_line);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Item item = mItems.get(position);
        // Populate views...
        switch(holder.getItemViewType()) {
            case VIEW_TYPE_TOP:
                // The top of the line has to be rounded
                holder.mItemLine.setBackgroundColor(R.drawable.line_bg_top);
                break;
            case VIEW_TYPE_MIDDLE:
                // Only the color could be enough
                // but a drawable can be used to make the cap rounded also here
                holder.mItemLine.setBackgroundColor(R.drawable.line_bg_middle);
                break;
            case VIEW_TYPE_BOTTOM:
                holder.mItemLine.setBackgroundColor(R.drawable.line_bg_bottom);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return VIEW_TYPE_TOP;
        } else if(position == mItems.size() - 1) {
            return VIEW_TYPE_BOTTOM;
        }
        return VIEW_TYPE_MIDDLE;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
