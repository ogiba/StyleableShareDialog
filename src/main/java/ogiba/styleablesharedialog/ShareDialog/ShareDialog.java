package ogiba.styleablesharedialog.ShareDialog;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ogiba.styleablesharedialog.R;
import ogiba.styleablesharedialog.ShareDialog.Models.ShareActionModel;

/**
 * Created by ogiba on 09.01.2017.
 */

public class ShareDialog extends DialogFragment implements ShareItemsAdapter.OnShareActionSelect {
    public static final String TYPE_TEXT = "text/*";
    private static final String SHARE_DIALOG_TAG = "ShareDialog";

    private RecyclerView sharableAppList;
    @Nullable
    private TextView titleView;
    private ViewGroup headerContainer;
    private ViewGroup footerContainer;

    private ArrayList<ShareActionModel> shareActionModels;
    private ShareItemsAdapter adapter;

    private String shareType;
    private String dialogTitle;
    private Integer numberOfRows;
    private Integer headerLayoutID;
    private Integer footerLayoutID;
    private boolean isHorizontal;

    private String shareTextContent;

    public static ShareDialog newInstance(String shareType) {
        Bundle args = new Bundle();
        args.putString(Builder.TAG_TYPE_TEXT, shareType);
        ShareDialog fragment = new ShareDialog();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.StyleableDialog);
        fragment.setArguments(args);
        return fragment;
    }

    public static ShareDialog newInstance() {
        Bundle args = new Bundle();
        args.putString(Builder.TAG_TYPE_TEXT, TYPE_TEXT);
        ShareDialog fragment = new ShareDialog();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.StyleableDialog);
        fragment.setArguments(args);
        return fragment;
    }

    public static ShareDialog newInstance(Bundle args) {
        ShareDialog fragment = new ShareDialog();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.StyleableDialog);
        fragment.setArguments(args);
        return fragment;
    }

    public void show(FragmentManager fragmentManager) {
        this.show(fragmentManager, SHARE_DIALOG_TAG);
    }

    public void show(FragmentTransaction fragmentTransaction) {
        this.show(fragmentTransaction, SHARE_DIALOG_TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseExtras();

        if (savedInstanceState != null)
            parseSavedInstance(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            Point size = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(size);
            window.setLayout((int) (size.x * 1.0), (int) (size.y * 0.6));
            window.setGravity(Gravity.BOTTOM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_share, container, false);
        bindViews(view);

        setupAdapter();
        setupTitle();
        attachCustomLayoutToView();
        setupRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        shareActionModels = getSharableApps();

        if (shareActionModels.size() > 0)
            adapter.setItems(shareActionModels);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Builder.TAG_TEXT_CONTENT, shareTextContent);
    }

    @Override
    public void onSelect(ShareActionModel model, int position) {
        shareContent(model);
    }

    private void parseExtras() {
        Bundle args = getArguments();
        if (args != null) {
            this.shareType = args.getString(Builder.TAG_TYPE_TEXT);
            this.dialogTitle = args.getString(Builder.TAG_TITLE);
            this.numberOfRows = args.getInt(Builder.TAG_ROWS_NUMBER);
            this.headerLayoutID = args.getInt(Builder.TAG_LAYOUT_HEADER);
            this.footerLayoutID = args.getInt(Builder.TAG_LAYOUT_FOOTER);
            this.isHorizontal = args.getBoolean(Builder.TAG_ORIENTATION_TAG);
        }
    }

    private void parseSavedInstance(Bundle instance) {
        this.shareTextContent = instance.getString(Builder.TAG_TEXT_CONTENT);
    }

    private void attachCustomLayoutToView() {
        attachCustomHeader();
        attachCustomFooter();
    }

    private void attachCustomHeader() {
        if (headerLayoutID == null || headerLayoutID == 0)
            return;

        headerContainer.removeAllViews();
        LayoutInflater.from(getContext()).inflate(headerLayoutID, headerContainer);
    }

    private void attachCustomFooter() {
        if (footerLayoutID == null || footerLayoutID == 0)
            return;

        footerContainer.removeAllViews();
        footerContainer.setVisibility(View.VISIBLE);
        LayoutInflater.from(getContext()).inflate(footerLayoutID, footerContainer);
    }

    protected void bindViews(View layout) {
        this.headerContainer = (ViewGroup) layout.findViewById(R.id.above_container);
        this.footerContainer = (ViewGroup) layout.findViewById(R.id.below_container);
        this.sharableAppList = (RecyclerView) layout.findViewById(R.id.app_list);

        if (headerLayoutID == null || headerLayoutID == 0)
            this.titleView = (TextView) layout.findViewById(R.id.title);
    }

    private void setupAdapter() {
        this.adapter = new ShareItemsAdapter(getContext(), isHorizontal);
        this.adapter.setCallbackListener(this);
    }

    private void setupTitle() {
        if (titleView == null)
            return;

        if (dialogTitle != null && !dialogTitle.equals(""))
            titleView.setText(dialogTitle);
        else
            titleView.setText(R.string.default_dialog_title);
    }

    private void setupRecyclerView() {
        if (sharableAppList == null)
            return;

        checkNumberOfRows();
        int managerOrientation = checkManagerOrientation();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), numberOfRows,
                managerOrientation, false);
        sharableAppList.setLayoutManager(layoutManager);
        sharableAppList.setAdapter(adapter);
    }

    private void checkNumberOfRows() {
        if (numberOfRows == null || numberOfRows == 0) {
            numberOfRows = 4;

            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                    && isHorizontal)
                numberOfRows = 2;
        }
    }

    private int checkManagerOrientation() {
        if (isHorizontal)
            return LinearLayoutManager.HORIZONTAL;
        else
            return LinearLayoutManager.VERTICAL;
    }

    private void shareContent(ShareActionModel model) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (model.getAppInfo() != null)
            intent.setComponent(new ComponentName(model.getAppInfo().activityInfo.packageName,
                    model.getAppInfo().activityInfo.name));
        intent.setType(shareType);
        intent.putExtra(Intent.EXTRA_TEXT, shareTextContent);
        startActivity(intent);
    }

    protected ArrayList<ShareActionModel> getSharableApps() {
        PackageManager pm = getActivity().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(shareType);
        List<ResolveInfo> apps = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);

        ArrayList<ShareActionModel> shareActionModels = new ArrayList<>();

        for (ResolveInfo resolveInfo : apps) {
            shareActionModels.add(new ShareActionModel(resolveInfo));
        }

        return shareActionModels;
    }

    public void setShareContent(String shareTextContent) {
        this.shareTextContent = shareTextContent;
    }

    public static class Builder {
        private static final String TAG_TYPE_TEXT = "text";
        private static final String TAG_TITLE = "title";
        private static final String TAG_LAYOUT_HEADER = "layoutHeader";
        private static final String TAG_LAYOUT_FOOTER = "layoutFooter";
        private static final String TAG_ROWS_NUMBER = "numberOfRows";
        private static final String TAG_ORIENTATION_TAG = "orientation";
        private static final String TAG_TEXT_CONTENT = "simpleTextContent";

        private String type;
        private String title;
        private Integer headerLayoutId;
        private Integer footerLayoutId;
        private Integer numberOfRows;
        private boolean isHorizontal = false;

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setHeaderLayout(int layoutID) {
            this.headerLayoutId = layoutID;
            return this;
        }

        public Builder setFooterLayout(int layoutID) {
            this.footerLayoutId = layoutID;
            return this;
        }

        public Builder setRowsNumber(Integer numberOfRows) {
            this.numberOfRows = numberOfRows;
            return this;
        }

        public Builder changeOrientation(boolean isHorizontal) {
            this.isHorizontal = isHorizontal;
            return this;
        }

        public ShareDialog build() {
            Bundle args = new Bundle();

            if (type != null)
                args.putString(TAG_TYPE_TEXT, TYPE_TEXT);

            if (title != null)
                args.putString(TAG_TITLE, title);

            if (headerLayoutId != null)
                args.putInt(TAG_LAYOUT_HEADER, headerLayoutId);

            if (footerLayoutId != null)
                args.putInt(TAG_LAYOUT_FOOTER, footerLayoutId);

            if (numberOfRows != null)
                args.putInt(TAG_ROWS_NUMBER, numberOfRows);

            args.putBoolean(TAG_ORIENTATION_TAG, isHorizontal);

            return ShareDialog.newInstance(args);
        }
    }
}
