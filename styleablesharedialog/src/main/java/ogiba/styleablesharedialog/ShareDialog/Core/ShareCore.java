package ogiba.styleablesharedialog.ShareDialog.Core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ogiba.styleablesharedialog.ShareDialog.Models.ShareActionModel;

/**
 * Created by ogiba on 27.06.2017.
 * <p>
 * Core of StyleableShareDialog library
 */

public class ShareCore {
    public static final String TYPE_TEXT = "text/*";
    public static final String TYPE_IMAGE = "image/*";
    public static final String TYPE_IMAGE_JPEG = "image/jpeg";
    public static final String TYPE_IMAGE_PNG = "image/png";

    private Context context;
    private ShareListener callback;
    private ShareTextListener baseCallback;

    private String shareType;

    private String shareContent;
    private ArrayList<String> shareListContent;

    /**
     * Base constructor of {@link ShareCore}
     *
     * @param context current instance of {@link Context} required to proper work of ShareCore
     */
    public ShareCore(Context context) {
        this.context = context;
        this.shareType = TYPE_TEXT;
    }

    /**
     * Extended base constructor of {@link ShareCore}. Additionally allows to set {@link ShareListener}
     * that is used to provide information about shared content
     *
     * @param context  current instance of {@link Context} required to proper work of ShareCore
     * @param callback pointer to class that implements {@link ShareListener} interface
     */
    public ShareCore(Context context, ShareListener callback) {
        this.context = context;
        this.callback = callback;
        this.shareType = TYPE_TEXT;
    }

    /**
     * Extended base constructor of {@link ShareCore}. Additionally allows to set {@link String} value
     * that represents type of sharing contents.
     *
     * @param context   current instance of {@link Context} required to proper work of ShareCore
     * @param shareType {@link String} value that represents current sharing content.
     */
    public ShareCore(Context context, String shareType) {
        this.context = context;
        this.shareType = shareType;
    }

    /**
     * Extended base constructor of {@link ShareCore}. Additionally allows to set {@link String} value
     * that represents type of sharing content and pointer to {@link ShareListener} to provide
     * information about shared content
     *
     * @param context   current instance of {@link Context} required to proper work of ShareCore
     * @param shareType {@link String} value that represents current sharing content.
     * @param callback  pointer to class that implements {@link ShareListener} interface
     */
    public ShareCore(Context context, String shareType, ShareListener callback) {
        this.context = context;
        this.shareType = shareType;
        this.callback = callback;
    }

    /**
     * Provide information about applications that allows to receive content with current share type
     *
     * @return {@link ArrayList<ShareActionModel>}
     */
    @Nullable
    public ArrayList<ShareActionModel> getShareableApps() {
        if (context == null)
            return null;

        PackageManager pm = context.getPackageManager();
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
     * Share provided content to selected {@link ShareActionModel}
     *
     * @param model selected {@link ShareActionModel} where content should be shared
     */
    public void shareContent(ShareActionModel model) {
        String intentAction = Intent.ACTION_SEND;

        if (shareListContent != null) {
            intentAction = Intent.ACTION_SEND_MULTIPLE;
        }

        this.checkShareContentType(model, intentAction);
    }

    private void checkShareContentType(ShareActionModel model, String intentAction) {
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
                    contentToShare = shareContent;
                }

                if (callback != null)
                    this.callback.onShareText(model, contentToShare, intentAction);
                else if (baseCallback != null)
                    this.baseCallback.onShareText(model, contentToShare, intentAction);
                break;
            default:
                final ArrayList<Uri> uriValues;
                if (shareListContent != null && shareListContent.size() > 0) {
                    intentAction = Intent.ACTION_SEND_MULTIPLE;
                    uriValues = parseStringsToUri(shareListContent);
                } else {
                    uriValues = new ArrayList<>();
                    final Uri fileUri = Uri.parse(shareContent);
                    uriValues.add(fileUri);
                }
                this.callback.onShareFile(model, uriValues, intentAction);
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

    /**
     * Allows to set base version of {@link ShareListener} that only provides information about
     * text type shared content
     *
     * @param baseCallback pointer to class that implements {@link ShareTextListener} interface
     */
    public void setBaseCallback(ShareTextListener baseCallback) {
        this.baseCallback = baseCallback;
    }

    /**
     * Allows to set type of current shared content
     *
     * @param shareType {@link String} value that inform about current sharing content type
     */
    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

    /**
     * Return current type of shared content
     *
     * @return {@link String}
     */
    public String getShareType() {
        return shareType;
    }

    /**
     * Allows to set content to share that should be shared
     *
     * @param shareContent {@link String} value that represent content
     */
    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    /**
     * Allows to set content to share that should be shared
     *
     * @param shareListContent {@link ArrayList<String>} value that represent contents
     */
    public void setShareContent(ArrayList<String> shareListContent) {
        this.shareListContent = shareListContent;
    }

    public interface ShareListener extends ShareTextListener {
        void onShareFile(ShareActionModel model, ArrayList<Uri> contentToShare, String intentAction);
    }

    public interface ShareTextListener {
        void onShareText(ShareActionModel model, String contentToShare, String intentAction);
    }
}
