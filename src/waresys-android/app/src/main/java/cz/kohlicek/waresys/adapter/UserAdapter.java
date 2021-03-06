package cz.kohlicek.waresys.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.waresys.R;
import cz.kohlicek.waresys.model.User;

public class UserAdapter extends BaseAdapter<User> {

    public UserAdapter(Context context) {
        super(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.card_user, parent, false);
                return new UserViewHolder(view);
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
            User user = data.get(position);

            UserViewHolder userHolder = (UserViewHolder) holder;

            userHolder.userUsername.setText(user.getUsername());
            userHolder.userFullname.setText(user.getFullname());

            String[] roles = user.getRoles().toArray(new String[0]);
            for (int i = 0; i < roles.length; i++) {
                int role_id = context.getResources().getIdentifier("user_role_" + roles[i], "string", context.getPackageName());
                roles[i] = context.getResources().getString(role_id);
            }
            userHolder.userRoles.setText(TextUtils.join(", ", roles));

            userHolder.userCreated.setText(user.getCreatedFormat("dd.MM.yyyy HH:mm"));
            userHolder.userUpdated.setText(user.getUpdatedFormat("dd.MM.yyyy HH:mm"));
        }
    }


    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        @BindView(R.id.user_username)
        public TextView userUsername;

        @BindView(R.id.user_fullname)
        public TextView userFullname;

        @BindView(R.id.user_roles)
        public TextView userRoles;

        @BindView(R.id.data_created)
        public TextView userCreated;
        @BindView(R.id.data_updated)
        public TextView userUpdated;


        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (onClickListener != null) {
                itemView.setOnClickListener(this);
            }
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClick(v, getLayoutPosition(), get(getLayoutPosition()));
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            setSelected(get(getAdapterPosition()));
            setSelectedPosition(getAdapterPosition());

            menu.setHeaderTitle(getSelected().getName());
            menu.add(Menu.NONE, 1, 1, R.string.context_menu_edit);
            menu.add(Menu.NONE, 2, 2, R.string.context_menu_delete);
        }
    }
}
