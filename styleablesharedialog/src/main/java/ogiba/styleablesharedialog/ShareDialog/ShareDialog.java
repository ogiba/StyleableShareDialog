package ogiba.styleablesharedialog.ShareDialog;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ogiba.styleablesharedialog.R;
import ogiba.styleablesharedialog.ShareDialog.Models.ShareActionModel;
import ogiba.styleablesharedialog.ShareDialog.Utils.DisplayType;
import ogiba.styleablesharedialog.ShareDialog.Utils.Ratio;
import ogiba.styleablesharedialog.ShareDialog.Utils.SizeType;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by ogiba on 09.01.2017.
 */

public class ShareDialog extends DialogFragment implements ShareItemsAdapter.OnShareActionSelect {
    public static final String TYPE_TEXT = "text/*";
    public static final String TYPE_IMAGE = "image/*";
    public static final String TYPE_IMAGE_JPEG = "image/jpeg";
    public static final String TYPE_IMAGE_PNG = "image/png";
    private static final String SHARE_DIALOG_TAG = "ShareDialog";

    private RecyclerView sharableAppList;
    @Nullable
    private TextView titleView;
    private ViewGroup headerContainer;
    private ViewGroup footerContainer;

    private ArrayList<ShareActionModel> shareActionModels;
    private ShareItemsAdapter adapter;
    private int screenOrientation;

    private String shareType;
    private String dialogTitle;
    private Integer dialogTitleTintColor;
    private Integer dialogTitleTintBackground;
    private Integer numberOfRows;
    private Integer headerLayoutID;
    private Integer footerLayoutID;
    private boolean isHorizontal;
    private boolean showAsList;
    private Ratio customDialogRatio;
    private Ratio customDialogLandscapeRatio;

    private SizeType sizeType = SizeType.FILL_WIDTH;
    private DisplayType displayType;

    private String shareTextContent;
    private ArrayList<String> shareListContent;

    /**
     * Create new instance of {@link ShareDialog} with custom type
     *
     * @param shareType - type of shared content in string.
     * @return currently created instance of {@link ShareDialog}
     */
    public static ShareDialog newInstance(String shareType) {
        Bundle args = new Bundle();
        args.putString(Builder.TAG_TYPE, shareType);
        ShareDialog fragment = new ShareDialog();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.StyleableDialog);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create new instance of {@link ShareDialog}
     *
     * @return currently created instance of {@link ShareDialog}
     */
    public static ShareDialog newInstance() {
        Bundle args = new Bundle();
        args.putString(Builder.TAG_TYPE, TYPE_TEXT);
        ShareDialog fragment = new ShareDialog();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.StyleableDialog);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create instance of {@link ShareDialog} with {@link Bundle}
     *
     * @param args {@link Bundle} arguments
     * @return current instance of {@link ShareDialog}
     */
    public static ShareDialog newInstance(Bundle args) {
        ShareDialog fragment = new ShareDialog();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.StyleableDialog);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Shows currently created instance of {@link ShareDialog}
     *
     * @param fragmentManager instance of {@link FragmentManager} used to shows {@link ShareDialog}
     */
    public void show(FragmentManager fragmentManager) {
        if (fragmentManager != null) {
            this.show(fragmentManager, SHARE_DIALOG_TAG);
        } else {
            Log.e("SHARE_DIALOG", "To show ShareDialog, FragmentManger cannot be null.");
        }
    }

    /**
     * Shows currently created instance of {@link ShareDialog} via {@link FragmentTransaction}
     *
     * @param fragmentTransaction instance of {@link FragmentTransaction} used to shows {@link ShareDialog}
     */
    public void show(FragmentTransaction fragmentTransaction) {
        if (fragmentTransaction != null)
            this.show(fragmentTransaction, SHARE_DIALOG_TAG);
        else
            Log.e("SHARE_DIALOG", "To show ShareDialog, FragmentTransaction cannot be null.");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseExtras();

        checkDeviceOrientation();

        if (savedInstanceState != null)
            parseSavedInstance(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        Ratio sizeRatio = checkDialogRatioSize();
        if (window != null) {
            Point size = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(size);
            window.setLayout((int) (size.x * sizeRatio.getX()), (int) (size.y * sizeRatio.getY()));
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
        outState.putStringArrayList(Builder.TAG_LIST_CONTENT, shareListContent);
    }

    @Override
    public void onSelect(ShareActionModel model, int position) {
        shareContent(model);
    }

    private void parseExtras() {
        Bundle args = getArguments();
        if (args != null) {
            this.shareType = args.getString(Builder.TAG_TYPE);
            this.parseTitle(args);
            this.dialogTitleTintColor = parseColor(args, Builder.TAG_TITLE_TINT);
            this.dialogTitleTintBackground = parseColor(args, Builder.TAG_TITLE_BACKGROUND);
            this.numberOfRows = args.getInt(Builder.TAG_ROWS_NUMBER);
            this.headerLayoutID = args.getInt(Builder.TAG_LAYOUT_HEADER);
            this.footerLayoutID = args.getInt(Builder.TAG_LAYOUT_FOOTER);
            this.isHorizontal = args.getBoolean(Builder.TAG_ORIENTATION_TAG);
            this.showAsList = args.getBoolean(Builder.TAG_LIST_FORM);
            this.customDialogRatio = args.getParcelable(Builder.TAG_RATIO_SIZE);
            this.customDialogLandscapeRatio = args.getParcelable(Builder.TAG_LANDSCAPE_RATIO_SIZE);

            this.parseShareTextContent(args);
            this.parseShareListContent(args);

            if (customDialogRatio != null)
                sizeType = SizeType.CUSTOM;
        }
    }

    private void parseTitle(Bundle args) {
        int titleRes = args.getInt(Builder.TAG_TITLE_RES);

        if (titleRes != 0) {
            dialogTitle = getResources().getString(titleRes);
        } else {
            dialogTitle = args.getString(Builder.TAG_TITLE);
        }
    }

    private int parseColor(Bundle args, String tag) {
        int tintColor = args.getInt(tag);

        try {
            return getResources().getColor(tintColor);
        } catch (Exception ex) {
            return tintColor;
        }
    }

    private void parseShareTextContent(Bundle args) {
        if (shareTextContent == null)
            shareTextContent = args.getString(Builder.TAG_TEXT_CONTENT);
    }

    private void parseShareListContent(Bundle args) {
        if (shareListContent == null)
            shareListContent = args.getStringArrayList(Builder.TAG_LIST_CONTENT);
    }

    private void checkDeviceOrientation() {
        Context context = getContext();
        try {
            if (context != null) {
                if (context.getSystemService(WINDOW_SERVICE) != null) {
                    Display display = ((WindowManager) getContext().getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
                    screenOrientation = display.getRotation();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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

    private Ratio checkDialogRatioSize() {
        final Ratio ratio;
        switch (sizeType) {
            case FILL_WIDTH:
                ratio = fullWidthRatioBehavior();
                break;
            case WINDOWED:
                ratio = new Ratio(0.8, 0.6);
                break;
            case CUSTOM:
                ratio = customRatioBehavior();
                break;
            default:
                ratio = new Ratio(1.0, 0.6);
                break;
        }

        return ratio;
    }

    private Ratio fullWidthRatioBehavior() {
        final Ratio ratio;
        if (!isHorizontal || screenOrientation == Surface.ROTATION_90 ||
                screenOrientation == Surface.ROTATION_270)
            ratio = new Ratio(1.0, 0.6);
        else {
            ratio = new Ratio(1.0, 0.5);
        }
        return ratio;
    }

    private Ratio customRatioBehavior() {
        final Ratio ratio;
        if (screenOrientation == Surface.ROTATION_90 || screenOrientation == Surface.ROTATION_270) {
            ratio = customDialogLandscapeRatio;
        } else {
            ratio = customDialogRatio;
        }
        return ratio;
    }

    private void setupAdapter() {
        displayType = checkDisplayType();

        this.adapter = new ShareItemsAdapter(getContext(), displayType);
        this.adapter.setCallbackListener(this);
    }

    private DisplayType checkDisplayType() {
        final DisplayType type;

        if (isHorizontal && !showAsList)
            type = DisplayType.HORIZONTAL;
        else if (!isHorizontal && showAsList)
            type = DisplayType.LIST;
        else
            type = DisplayType.DEFAULT;

        return type;
    }

    private void setupTitle() {
        if (titleView == null)
            return;

        if (dialogTitleTintColor != null && dialogTitleTintColor != 0)
            titleView.setTextColor(dialogTitleTintColor);

        if (dialogTitleTintBackground != null)
            titleView.setBackgroundColor(dialogTitleTintBackground);

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
        switch (displayType) {
            case DEFAULT:
                if (numberOfRows == null || numberOfRows == 0) {
                    numberOfRows = 4;

                    if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                            && isHorizontal)
                        numberOfRows = 2;
                }
                break;
            case HORIZONTAL:
                if (numberOfRows == null || numberOfRows == 0) {
                    numberOfRows = 3;

                    if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                            && isHorizontal)
                        numberOfRows = 2;
                }
                break;
            case LIST:
                numberOfRows = 1;
                break;
        }
    }

    private int checkManagerOrientation() {
        if (isHorizontal)
            return LinearLayoutManager.HORIZONTAL;
        else
            return LinearLayoutManager.VERTICAL;
    }

    private void shareContent(ShareActionModel model) {
        String intentAction = Intent.ACTION_SEND;

        if (shareListContent != null) {
            intentAction = Intent.ACTION_SEND_MULTIPLE;
        }

        Intent intent = new Intent(intentAction);
        if (model.getAppInfo() != null)
            intent.setComponent(new ComponentName(model.getAppInfo().activityInfo.packageName,
                    model.getAppInfo().activityInfo.name));
        intent.setType(shareType);

        this.checkShareContentType(model, intent);
    }

    private void checkShareContentType(ShareActionModel model, Intent intent) {
        String intentAction = Intent.ACTION_SEND;

        switch (shareType) {
            case TYPE_TEXT:
                final String contentToShare;
                if (shareListContent != null && shareListContent.size() > 1) {
                    StringBuilder builder = new StringBuilder();
                    for (String content : shareListContent) {
                        builder.append(content);
                        builder.append(System.getProperty("line.separator"));
                    }
                    contentToShare = builder.toString();
                } else {
                    contentToShare = shareTextContent;
                }
                intent.setAction(intentAction);
                intent.putExtra(Intent.EXTRA_TEXT, contentToShare);
                startActivity(intent);
                break;
            default:
                if (shareListContent != null && shareListContent.size() > 0) {
                    intentAction = Intent.ACTION_SEND_MULTIPLE;
                    final ArrayList<Uri> uriValues = parseStringsToUri(shareListContent);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriValues);
                    intent.setAction(intentAction);
                } else {
                    Uri fileUri = Uri.parse(shareTextContent);
                    intent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    intent.setAction(intentAction);
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
                break;
        }
    }

    private ArrayList<Uri> parseStringsToUri(ArrayList<String> values) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (String fileAddress : values) {
            Uri uri = Uri.parse(fileAddress);
            uris.add(uri);
        }
        return uris;
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

    /**
     * Allows to set content that will be shared using created instance of {@link ShareDialog}
     *
     * @param shareTextContent {@link String} value that will be shared
     */
    public void setShareContent(String shareTextContent) {
        this.shareTextContent = shareTextContent;
    }

    /**
     * Allows to set list of contents that will be shared using created instance of {@link ShareDialog}
     *
     * @param shareListContent {@link ArrayList<String>} that contains values to share
     */
    public void setShareContent(ArrayList<String> shareListContent) {
        this.shareListContent = shareListContent;
    }

    /**
     * {@link Builder} is responsible for setting up parameters of {@link ShareDialog}
     */
    public static class Builder {
        private static final String TAG_TYPE = "type";
        private static final String TAG_TITLE = "title";
        private static final String TAG_TITLE_RES = "titleFromResources";
        private static final String TAG_TITLE_TINT = "titleTintColor";
        private static final String TAG_TITLE_BACKGROUND = "titleBackgroundColor";
        private static final String TAG_LAYOUT_HEADER = "layoutHeader";
        private static final String TAG_LAYOUT_FOOTER = "layoutFooter";
        private static final String TAG_ROWS_NUMBER = "numberOfRows";
        private static final String TAG_ORIENTATION_TAG = "orientation";
        private static final String TAG_TEXT_CONTENT = "simpleTextContent";
        private static final String TAG_LIST_CONTENT = "listContent";
        private static final String TAG_LIST_FORM = "listForm";
        private static final String TAG_RATIO_SIZE = "ratioSize";
        private static final String TAG_LANDSCAPE_RATIO_SIZE = "landscapeRatioSize";

        private String type = TYPE_TEXT;
        private String shareTextValue;
        private String title;
        private Integer titleRes;
        private Integer titleTintColor;
        private Integer titleBackgroundColor;
        private Integer headerLayoutId;
        private Integer footerLayoutId;
        private Integer numberOfSections;
        private boolean isHorizontal = false;
        private boolean showAsList = false;
        private ArrayList<String> shareValues;
        private Ratio dialogRatio;
        private Ratio dialogLandscapeRatio;

        /**
         * Defines what kind of content will be shared via {@link ShareDialog}
         *
         * @param type {@link String} value that represent type of sharing content. For example: "text/*"
         * @return instance of currently created {@link ShareDialog.Builder}
         */
        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        /**
         * Allows to specifies title of {@link ShareDialog}. Methods works only for default header.
         * If custom header is set this method will not make changes on header
         *
         * @param title {@link String} value that represent title
         * @return instance of currently created {@link ShareDialog.Builder}
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Allows to specifies title of {@link ShareDialog}. Methods works only for default header.
         * If custom header is set this method will not make changes on header
         *
         * @param resID {@code int} value that represent string title from resource file
         * @return instance of currently created {@link ShareDialog.Builder}
         */
        public Builder setTitle(int resID) {
            this.titleRes = resID;
            return this;
        }

        /**
         * Allows to set color of default header title
         *
         * @param color {@link Integer} value that represents selected color
         * @return instance of currently created {@link ShareDialog.Builder}
         */
        public Builder setTitleTintColor(int color) {
            this.titleTintColor = color;
            return this;
        }

        /**
         * Allows to set background color of default header. This method do not take impact
         * if custom layout was added.
         *
         * @param color {@link Integer} value that represents selected color
         * @return instance of currently created {@link ShareDialog.Builder}
         * @deprecated use {@link #setHeaderBackgroundColor(int)} instead.
         */
        @Deprecated
        public Builder setTitleBackgroundColor(int color) {
            this.titleBackgroundColor = color;
            return this;
        }

        /**
         * Allows to set background color of default header. This method do not take impact
         * if custom layout was added.
         *
         * @param color {@link Integer} value that represents selected color
         * @return instance of currently created {@link ShareDialog.Builder}
         */
        public Builder setHeaderBackgroundColor(int color) {
            this.titleBackgroundColor = color;
            return this;
        }

        /**
         * Sets provided layout as header.
         *
         * @param layoutID {@link Integer} value that represent custom layout
         * @return instance of currently created {@link ShareDialog.Builder}
         */
        public Builder setHeaderLayout(int layoutID) {
            this.headerLayoutId = layoutID;
            return this;
        }

        /**
         * Sets provided layout as footer
         *
         * @param layoutID {@link Integer} value that represent custom layout
         * @return instance of currently created {@link ShareDialog.Builder}
         */
        public Builder setFooterLayout(int layoutID) {
            this.footerLayoutId = layoutID;
            return this;
        }

        /**
         * Not supported in current version
         *
         * @param numberOfSections number of elements sections
         * @return current instance of {@link ShareDialog.Builder}
         */
        private Builder setSectionNumber(Integer numberOfSections) {
            this.numberOfSections = numberOfSections;
            return this;
        }

        /**
         * Provides possibility to change orientation of {@link ShareDialog} {@link RecyclerView}
         *
         * @param isHorizontal {@link Boolean} flag that informs library to provide for user
         *                     {@link ShareDialog} in required orientation. Can be
         *                     switched between Vertical and Horizontal
         * @return current instance of {@link ShareDialog.Builder}
         */
        public Builder changeOrientation(boolean isHorizontal) {
            this.isHorizontal = isHorizontal;
            return this;
        }

        /**
         * Provides possibility to change items presentation from grid to list
         *
         * @param showAsList {@link Boolean} flag that informs library what kind of items styles is
         *                   required. Can be switched between Grid and List
         * @return current instance of {@link ShareDialog.Builder}
         */
        public Builder showAsList(boolean showAsList) {
            this.showAsList = showAsList;
            return this;
        }

        /**
         * Allows to set custom ratio of {@link ShareDialog} using built-in class {@link Ratio}.
         *
         * @param dialogRatio instance of class {@link Ratio}
         * @return current instance of {@link ShareDialog.Builder}
         */
        public Builder setSizeRatio(Ratio dialogRatio) {
            this.dialogRatio = dialogRatio;
            return this;
        }

        /**
         * Allows to set custom ratio for landscape orientation of {@link ShareDialog}. Works only when
         * custom ratio size is set.
         *
         * @param dialogLandscapeRatio instance of class {@link Ratio}
         * @return current instance of {@link ShareDialog.Builder}
         */
        public Builder setLandscapeRatio(Ratio dialogLandscapeRatio) {
            this.dialogLandscapeRatio = dialogLandscapeRatio;
            return this;
        }

        /**
         * Allows to set content that will be shared via {@link ShareDialog}. If method with the same name
         * from {@link ShareDialog} will be used this value will be replaced
         *
         * @param value {@link String} value that should be shared
         * @return current instance of {@link ShareDialog.Builder}
         */
        public Builder setShareContent(String value) {
            this.shareTextValue = value;
            return this;
        }

        /**
         * Allows to set list of contents that will be shared via {@link ShareDialog}. If method with the same name
         * from {@link ShareDialog} will be used this value will be replaced
         *
         * @param values {@link ArrayList<String>} value that should be shared
         * @return current instance of {@link ShareDialog.Builder}
         */
        public Builder setShareContent(ArrayList<String> values) {
            this.shareValues = values;
            return this;
        }

        /**
         * Collects all set properties and build new instance of {@link ShareDialog}
         *
         * @return {@link ShareDialog}
         */
        public ShareDialog build() {
            Bundle args = new Bundle();

            if (type != null)
                args.putString(TAG_TYPE, type);

            if (title != null)
                args.putString(TAG_TITLE, title);

            if (titleRes != null)
                args.putInt(TAG_TITLE_RES, titleRes);

            if (titleTintColor != null)
                args.putInt(TAG_TITLE_TINT, titleTintColor);

            if (titleBackgroundColor != null)
                args.putInt(TAG_TITLE_BACKGROUND, titleBackgroundColor);

            if (headerLayoutId != null)
                args.putInt(TAG_LAYOUT_HEADER, headerLayoutId);

            if (footerLayoutId != null)
                args.putInt(TAG_LAYOUT_FOOTER, footerLayoutId);

            if (numberOfSections != null && numberOfSections > 0)
                args.putInt(TAG_ROWS_NUMBER, numberOfSections);

            if (dialogRatio != null)
                args.putParcelable(TAG_RATIO_SIZE, dialogRatio);

            if (dialogLandscapeRatio != null)
                args.putParcelable(TAG_LANDSCAPE_RATIO_SIZE, dialogLandscapeRatio);

            if (shareTextValue != null)
                args.putString(TAG_TEXT_CONTENT, shareTextValue);

            if (shareValues != null)
                args.putStringArrayList(TAG_LIST_CONTENT, shareValues);

            args.putBoolean(TAG_ORIENTATION_TAG, isHorizontal);
            args.putBoolean(TAG_LIST_FORM, showAsList);

            return ShareDialog.newInstance(args);
        }
    }
}
