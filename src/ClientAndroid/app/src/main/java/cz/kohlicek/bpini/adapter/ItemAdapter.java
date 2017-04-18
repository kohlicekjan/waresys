package cz.kohlicek.bpini.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;
import cz.kohlicek.bpini.model.Item;


public class ItemAdapter extends BaseRecyclerViewAdapter<Item> {

    //private final ClickListener clickListener;


    public ItemAdapter(Context context) {
        super(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
                return new ItemViewHolder(view);
            case VIEW_TYPE_LOADING:
                view = LayoutInflater.from(context).inflate(R.layout.row_loading, parent, false);
                return new LoadingViewHolder(view);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (position < data.size()) {
            Item item = data.get(position);

            ItemViewHolder holder = (ItemViewHolder) viewHolder;

            holder.itemName.setText(item.getName());
            holder.itemDescription.setText(item.getDescription());
            holder.itemAmount.setText(item.getAmountToString());
            holder.itemUpdated.setText(item.getUpdatedFormat("dd.MM.yyyy HH:mm"));
            holder.itemCreated.setText(item.getCreatedFormat("dd.MM.yyyy HH:mm"));
        }
    }

//    public interface ClickListener {
//        void onClick(Item item);
//    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {// implements View.OnClickListener, View.OnCreateContextMenuListener

        @BindView(R.id.item_name)
        public TextView itemName;
        @BindView(R.id.item_description)
        public TextView itemDescription;
        @BindView(R.id.item_amount)
        public TextView itemAmount;
        @BindView(R.id.data_updated)
        public TextView itemUpdated;
        @BindView(R.id.data_created)
        public TextView itemCreated;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //itemView.setOnClickListener(this);
            //itemView.setOnCreateContextMenuListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            Item item = ItemAdapter.this.getItemPosition(getLayoutPosition());
//            //clickListener.onClick(item);
//        }

//        @Override
//        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//
//            Item item = getItemPosition(getAdapterPosition());
//
//            menu.setHeaderTitle(item.getName());
//            menu.add(Menu.NONE, 0, 0, "Detail položky");
//            menu.add(Menu.NONE, 1, 1, "Upravit");
//
//            menu.add(Menu.NONE, 2, 2, "Smazat položku");
//
//            if (item.getAmount() > 0) {
//                menu.getItem(2).setEnabled(false);
//            }
//
//        }

    }
}
