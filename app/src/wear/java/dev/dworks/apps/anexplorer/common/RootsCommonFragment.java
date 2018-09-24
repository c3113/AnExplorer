package dev.dworks.apps.anexplorer.common;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;

import dev.dworks.apps.anexplorer.BaseActivity;
import dev.dworks.apps.anexplorer.DocumentsApplication;
import dev.dworks.apps.anexplorer.R;
import dev.dworks.apps.anexplorer.adapter.RootsCommonAdapter;
import dev.dworks.apps.anexplorer.adapter.RootsExpandableAdapter;
import dev.dworks.apps.anexplorer.fragment.RootsFragment;
import dev.dworks.apps.anexplorer.libcore.util.Objects;
import dev.dworks.apps.anexplorer.loader.RootsLoader;
import dev.dworks.apps.anexplorer.misc.AnalyticsManager;
import dev.dworks.apps.anexplorer.misc.CrashReportingManager;
import dev.dworks.apps.anexplorer.misc.RootsCache;
import dev.dworks.apps.anexplorer.misc.Utils;
import dev.dworks.apps.anexplorer.model.RootInfo;

import static dev.dworks.apps.anexplorer.DocumentsApplication.isTelevision;

public class RootsCommonFragment extends BaseFragment
        implements WearableNavigationDrawerView.OnItemSelectedListener{

    private RootsCommonAdapter mAdapter;
    private LoaderManager.LoaderCallbacks<Collection<RootInfo>> mCallbacks;

    private WearableNavigationDrawerView mNavigationDrawer;

    public static void show(FragmentManager fm, Intent includeApps) {
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_INCLUDE_APPS, includeApps);

        final RootsCommonFragment fragment = new RootsCommonFragment();
        fragment.setArguments(args);

        final FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container_roots, fragment);
        ft.commitAllowingStateLoss();
    }

    public static RootsCommonFragment get(FragmentManager fm) {
        return (RootsCommonFragment) fm.findFragmentById(R.id.container_roots);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_roots, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavigationDrawer = view.findViewById(R.id.navigation_drawer);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Context context = getActivity();
        final RootsCache roots = DocumentsApplication.getRootsCache(context);
        final BaseActivity.State state = ((BaseActivity) context).getDisplayState();

        mCallbacks = new LoaderManager.LoaderCallbacks<Collection<RootInfo>>() {
            @Override
            public Loader<Collection<RootInfo>> onCreateLoader(int id, Bundle args) {
                return new RootsLoader(context, roots, state);
            }

            @Override
            public void onLoadFinished(
                    Loader<Collection<RootInfo>> loader, Collection<RootInfo> result) {
                if (!isAdded()) return;

                final Intent includeApps = getArguments().getParcelable(EXTRA_INCLUDE_APPS);

                if (mAdapter == null) {
                    mAdapter = new RootsCommonAdapter(context, result, includeApps);
                    mNavigationDrawer.setAdapter(mAdapter);
                    //mNavigationDrawer.getController().peekDrawer();
                    //mNavigationDrawer.addOnItemSelectedListener(navigationAdapter);
                } else {
                    mAdapter.setData(result);
                }
            }

            @Override
            public void onLoaderReset(Loader<Collection<RootInfo>> loader) {
                mAdapter = null;
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(2, null, mCallbacks);
    }

    public void onCurrentRootChanged() {
        if (mAdapter == null || mNavigationDrawer == null) return;
    }

    @Override
    public void onItemSelected(int position) {
        final BaseActivity activity = BaseActivity.get(RootsCommonFragment.this);
        RootInfo rootInfo = mAdapter.getItem(position);
        if(RootInfo.isProFeature(rootInfo) && !DocumentsApplication.isPurchased()){
            DocumentsApplication.openPurchaseActivity(activity);
            return;
        }
        activity.onRootPicked(rootInfo, true);
        Bundle params = new Bundle();
        params.putString("type", rootInfo.title);
        AnalyticsManager.logEvent("navigate", rootInfo, params);
    }
}