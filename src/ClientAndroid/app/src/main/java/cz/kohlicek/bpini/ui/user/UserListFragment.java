package cz.kohlicek.bpini.ui.user;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;
import cz.kohlicek.bpini.adapter.EndlessRecyclerViewScrollListener;
import cz.kohlicek.bpini.adapter.UserAdapter;
import cz.kohlicek.bpini.model.User;
import cz.kohlicek.bpini.service.BPINIClient;
import cz.kohlicek.bpini.service.BPINIService;
import cz.kohlicek.bpini.ui.view.EmptyRecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserListFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, UserAdapter.OnClickListener<User> {

    @BindView(R.id.recycler_view)
    EmptyRecyclerView recyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSnipSwipeRefreshLayout;

    @BindView(R.id.empty_view)
    View mEmptyView;

    @BindView(R.id.stub_no_connection)
    ViewStub stub;
    Snackbar snackbar;

    FloatingActionButton fab;

    private UserAdapter adapter;
    private BPINIService bpiniService;
    private LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(this, view);

        this.getActivity().setTitle(R.string.user_list_title);

        fab = (FloatingActionButton) this.getActivity().findViewById(R.id.fab_add);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(this);

        bpiniService = BPINIClient.getInstance(this.getContext());
        adapter = new UserAdapter(this.getContext());
        adapter.setOnClickListener(this);


        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public int getFooterViewType(int defaultNoFooterViewType) {
                return 1;
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                load(totalItemsCount, true);
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setEmptyView(mEmptyView);

        mSnipSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        mSnipSwipeRefreshLayout.setOnRefreshListener(this);

        load(0, true);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (snackbar != null)
            snackbar.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.support.design.R.id.snackbar_action:
                adapter.clear();
                load(0, true);
                break;
            case R.id.fab_add:
                Intent intent = new Intent(this.getContext(), UserFormActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClick(View v, int position, User data) {
        Intent intent = new Intent(this.getContext(), UserFormActivity.class);
        intent.putExtra(UserFormActivity.USER_ID, data.getId());
        startActivityForResult(intent, UserFormActivity.REQUEST_CODE);
    }

    @Override
    public void onRefresh() {
        adapter.clear();
        load(0, false);
        mSnipSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case UserFormActivity.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    //String result = data.getStringExtra("result");
                    onRefresh();
                }
                break;
        }
    }


    private void load(int skip, boolean loading) {
        adapter.setLoading(loading);
        if (loading) {
            recyclerView.setVisibility(View.VISIBLE);
            visibleNoConnection(false);
        }

        Call<List<User>> call = bpiniService.getUsers("-created", skip);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body());

                    recyclerView.setVisibility(View.VISIBLE);
                    visibleNoConnection(false);
                } else {

                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                visibleNoConnection(true);
            }
        });
    }

    private void visibleNoConnection(boolean visible) {
        if (visible) {
            snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), R.string.no_connection_message, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.no_connection_repeat, this);
            snackbar.show();
            stub.setVisibility(View.VISIBLE);
        } else {
            if (snackbar != null)
                snackbar.dismiss();
            stub.setVisibility(View.GONE);
        }
    }
}
