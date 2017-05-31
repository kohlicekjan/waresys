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
import cz.kohlicek.bpini.model.Tag;

public class TagAdapter extends BaseRecyclerViewAdapter<Tag> {

    public TagAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.card_tag, parent, false);
                return new TagViewHolder(view);
            case VIEW_TYPE_LOADING:
                view = LayoutInflater.from(context).inflate(R.layout.row_loading, parent, false);
                return new LoadingViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < data.size()) {
            Tag tag = data.get(position);

            TagViewHolder tagHolder = (TagViewHolder) holder;
            tagHolder.tagUid.setText(tag.getUid());

            int type_id = context.getResources().getIdentifier("tag_type_" + tag.getType(), "string", context.getPackageName());
            tagHolder.tagType.setText(context.getResources().getString(type_id));

            switch (tag.getType()) {
                case Tag.TYPE_UNKNOWN:
                    tagHolder.tagType.setTextColor(context.getColor(android.R.color.holo_blue_dark));
                    break;
                case Tag.TYPE_MODE:
                    tagHolder.tagType.setTextColor(context.getColor(android.R.color.holo_orange_dark));
                    break;
                default:
                    tagHolder.tagType.setTextColor(context.getColor(android.R.color.black));
            }

            if (tag.getItem() != null) {
                tagHolder.textItem.setVisibility(View.VISIBLE);
                tagHolder.tagItem.setVisibility(View.VISIBLE);
                tagHolder.tagItem.setText(tag.getItem().getName());
            }

            tagHolder.tagCreated.setText(tag.getCreatedFormat("dd.MM.yyyy HH:mm"));
            tagHolder.tagUpdated.setText(tag.getUpdatedFormat("dd.MM.yyyy HH:mm"));
        }
    }


    class TagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tag_uid)
        public TextView tagUid;
        @BindView(R.id.tag_type)
        public TextView tagType;
        @BindView(R.id.tag_item)
        public TextView tagItem;
        @BindView(R.id.text_item)
        public TextView textItem;
        @BindView(R.id.data_created)
        public TextView tagCreated;
        @BindView(R.id.data_updated)
        public TextView tagUpdated;


        public TagViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (onClickListener != null) {
                itemView.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClick(v, getLayoutPosition(), get(getLayoutPosition()));
        }
    }

}
