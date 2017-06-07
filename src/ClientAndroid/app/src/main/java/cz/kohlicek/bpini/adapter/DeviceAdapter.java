package cz.kohlicek.bpini.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;
import cz.kohlicek.bpini.model.Device;

public class DeviceAdapter extends BaseAdapter<Device> {


    private OnCheckedChangeListener onCheckedChangeListener;

    public DeviceAdapter(Context context) {
        super(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_ITEM:
                view = LayoutInflater.from(context).inflate(R.layout.card_device, parent, false);
                return new DeviceViewHolder(view);
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
            Device device = data.get(position);

            DeviceViewHolder deviceHolder = (DeviceViewHolder) holder;
            deviceHolder.deviceClientId.setText(device.getClientId());

            int status_id = context.getResources().getIdentifier("device_status_" + device.getStatus(), "string", context.getPackageName());
            deviceHolder.deviceStatus.setText(context.getResources().getString(status_id));

            switch (device.getStatus()) {
                case Device.STATUS_ACTIVE:
                    deviceHolder.deviceStatus.setTextColor(context.getColor(R.color.colorAccent));
                    break;
                case Device.STATUS_ERROR:
                    deviceHolder.deviceStatus.setTextColor(context.getColor(android.R.color.holo_red_light));
                    break;
                default:
                    deviceHolder.deviceStatus.setTextColor(context.getColor(android.R.color.black));
            }

            if (onCheckedChangeListener != null) {
                deviceHolder.deviceAllowed.setOnCheckedChangeListener(null);
                deviceHolder.deviceAllowed.setChecked(device.isAllowed());
                deviceHolder.deviceAllowed.setOnCheckedChangeListener(deviceHolder);
            }

            deviceHolder.deviceVersion.setText(device.getVersion());
            deviceHolder.deviceIpAddress.setText(device.getIpAddress());
            deviceHolder.deviceSerialNumber.setText(device.getSerialNumber());
            deviceHolder.deviceCreated.setText(device.getCreatedFormat("dd.MM.yyyy HH:mm"));
            deviceHolder.deviceUpdated.setText(device.getUpdatedFormat("dd.MM.yyyy HH:mm"));
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        this.onCheckedChangeListener = l;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position, Device data);
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener, View.OnCreateContextMenuListener {

        @BindView(R.id.device_client_id)
        public TextView deviceClientId;
        @BindView(R.id.device_version)
        public TextView deviceVersion;
        @BindView(R.id.device_allowed)
        public Switch deviceAllowed;
        @BindView(R.id.device_status)
        public TextView deviceStatus;
        @BindView(R.id.device_ip_address)
        public TextView deviceIpAddress;
        @BindView(R.id.device_serial_number)
        public TextView deviceSerialNumber;
        @BindView(R.id.data_created)
        public TextView deviceCreated;
        @BindView(R.id.data_updated)
        public TextView deviceUpdated;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (onCheckedChangeListener != null) {
                deviceAllowed.setOnCheckedChangeListener(this);
            }
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            onCheckedChangeListener.onCheckedChanged(buttonView, isChecked, getLayoutPosition(), get(getLayoutPosition()));
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            setSelected(get(getAdapterPosition()));
            setSelectedPosition(getAdapterPosition());

            menu.setHeaderTitle(getSelected().getClientId());
            menu.add(Menu.NONE, 1, 1, getSelected().isAllowed() ? R.string.device_list_context_menu_disabled : R.string.device_list_context_menu_enabled);
            menu.add(Menu.NONE, 2, 2, R.string.context_menu_delete);
        }
    }

}
