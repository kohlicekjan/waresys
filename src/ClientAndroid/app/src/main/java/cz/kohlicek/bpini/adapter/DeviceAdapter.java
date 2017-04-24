package cz.kohlicek.bpini.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;
import cz.kohlicek.bpini.model.Device;

public class DeviceAdapter extends BaseRecyclerViewAdapter<Device> {


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
            String status = context.getResources().getString(status_id);
            deviceHolder.deviceStatus.setText(status);

            int status_color;
            switch (device.getStatus()) {
                case Device.STATUS_UNKNOWN:
                    status_color = android.R.color.holo_blue_dark;
                    break;
                case Device.STATUS_ACTIVE:
                    status_color = R.color.colorAccent;
                    break;
                case Device.STATUS_ERROR:
                    status_color = android.R.color.holo_red_light;
                    break;
                default:
                    status_color = android.R.color.black;
            }
            deviceHolder.deviceStatus.setTextColor(context.getColor(status_color));


            deviceHolder.deviceAllowed.setChecked(device.isAllowed());
            deviceHolder.deviceVersion.setText(device.getVersion());
            deviceHolder.deviceIpAddress.setText(device.getIpAddress());
            deviceHolder.deviceSerialNumber.setText(device.getSerialNumber());
            deviceHolder.deviceCreated.setText(device.getCreatedFormat("dd.MM.yyyy HH:mm"));
            deviceHolder.deviceUpdated.setText(device.getUpdatedFormat("dd.MM.yyyy HH:mm"));
        }
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener{

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

            if(onCheckedChangeListener!=null) {
                deviceAllowed.setOnCheckedChangeListener(this);
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            onCheckedChangeListener.onCheckedChanged(buttonView, isChecked, getLayoutPosition(), get(getLayoutPosition()));
        }
    }

    private OnCheckedChangeListener onCheckedChangeListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        this.onCheckedChangeListener = l;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position, Device data);
    }

}
