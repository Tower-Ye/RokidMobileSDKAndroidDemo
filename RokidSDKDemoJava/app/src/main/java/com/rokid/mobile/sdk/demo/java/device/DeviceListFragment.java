package com.rokid.mobile.sdk.demo.java.device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.rokid.mobile.lib.base.util.CollectionUtils;
import com.rokid.mobile.lib.entity.event.device.EventDeviceStatus;
import com.rokid.mobile.lib.xbase.device.callback.IPingDeviceCallback;
import com.rokid.mobile.sdk.RokidMobileSDK;
import com.rokid.mobile.sdk.bean.SDKDevice;
import com.rokid.mobile.sdk.demo.java.R;
import com.rokid.mobile.sdk.demo.java.base.BaseFragment;
import com.rokid.mobile.sdk.demo.java.base.item.DeviceItem;
import com.rokid.mobile.ui.recyclerview.adapter.BaseRVAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by tt on 2018/2/24.
 */

public class DeviceListFragment extends BaseFragment<DeviceFragmentPresenter> {

    @BindView(R.id.fragment_device_list_btn)
    Button deviceListBtn;

    @BindView(R.id.fragment_device_list_pb)
    ProgressBar progressBar;

    @BindView(R.id.fragment_device_list_rv)
    RecyclerView deviceRv;

    private BaseRVAdapter<DeviceItem> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device;
    }

    @Override
    protected DeviceFragmentPresenter initPresenter() {
        return new DeviceFragmentPresenter(this);
    }

    @Override
    protected void initVariables(View rootView, ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAdapter = new BaseRVAdapter<>();

        deviceRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        deviceRv.setAdapter(mAdapter);
    }

    @Override
    protected void initListeners() {
        deviceListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                deviceRv.setVisibility(View.GONE);
                mAdapter.clearAllItemView();
                getPresenter().getDeviceList();
            }
        });

        mAdapter.setOnItemViewClickListener(new BaseRVAdapter.OnItemViewClickListener<DeviceItem>() {
            @Override
            public void onItemViewClick(DeviceItem deviceItem, int sectionKey, int sectionItemPosition) {
                if (deviceItem == null) {
                    return;
                }

                if (deviceItem.getData() == null) {
                    return;
                }

//                showToast(deviceItem.getData().toString());
                pingDevice(deviceItem.getData());
            }
        });


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && null != deviceRv) {
            deviceRv.setVisibility(View.GONE);
        }
    }

    public void setDeviceListData(List<SDKDevice> itemList) {
        progressBar.setVisibility(View.GONE);
        if (CollectionUtils.isEmpty(itemList)) {
            showToast(getString(R.string.fragment_device_list_empty));
            deviceRv.setVisibility(View.GONE);
            return;
        }

        List<DeviceItem> deviceItemList = new ArrayList<>();
        for (SDKDevice rkDevice : itemList) {
            DeviceItem deviceItem = new DeviceItem(rkDevice);
            deviceItemList.add(deviceItem);
            setUnbindDeviceClick(deviceItem);
        }
        mAdapter.setItemViewList(deviceItemList);
        deviceRv.setVisibility(View.VISIBLE);
    }

    private void setUnbindDeviceClick(DeviceItem deviceItem) {
        deviceItem.setUnbindClickListener(new DeviceItem.UnbindClickListener() {
            @Override
            public void getCurrentItem(SDKDevice currentItem) {
                if (null == currentItem) {
                    return;
                }

                if (TextUtils.isEmpty(currentItem.getDeviceId())) {
                    return;
                }

                getPresenter().unbindDevice(currentItem.getDeviceId());
            }
        });
    }

    public void showToast(String text) {
        showToastShort(text);
    }

    private void pingDevice(SDKDevice sddDevice) {
        RokidMobileSDK.device.pingDevice(sddDevice, new IPingDeviceCallback() {

            @Override
            public void onSuccess(String deviceId, boolean isOnline) {
                showToast("获取设备状态成功,deviceId=" + deviceId + ",isOnline=" + isOnline);
            }

            @Override
            public void onFailed(String deviceId, String errorCode, String errorMsg) {
                showToast("获取设备状态失败，deviceId" + deviceId + ",errorCode=" + errorCode + "errorMsg= " + errorMsg);
            }
        });
    }


}
