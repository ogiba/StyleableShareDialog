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

    private String shareTextContent;
    private ArrayList<String> shareListContent;

    public ShareCore(Context context) {
        this.context = context;
    }

    public ShareCore(Context context, ShareListener callback) {
        this.context = context;
        this.callback = callback;
        this.shareType = TYPE_TEXT;
    }

    public ShareCore(Context context, String shareType) {
        this.context = context;
        this.shareType = shareType;
    }

    public ShareCore(Context context, String shareType, ShareListener callback) {
        this.context = context;
        this.shareType = shareType;
        this.callback = callback;
    }

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

    public void shareContent(ShareActionModel model) {
        String intentAction = Intent.ACTION_SEND;

        if (shareListContent != null) {
            intentAction = Intent.ACTION_SEND_MULTIPLE;
        }

 /*       Intent intent = new Intent(intentAction);
        if (model.getAppInfo() != null)
            intent.setComponent(new ComponentName(model.getAppInfo().activityInfo.packageName,
                    model.getAppInfo().activityInfo.name));
        intent.setType(shareType);*/

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
                    contentToShare = shareTextContent;
                }
//                intent.setAction(intentAction);
//                intent.putExtra(Intent.EXTRA_TEXT, contentToShare);
//                startActivity(intent);
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
                    /*intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriValues);
                    intent.setAction(intentAction);*/
                } else {
                    uriValues = new ArrayList<>();
                    final Uri fileUri = Uri.parse(shareTextContent);
                    uriValues.add(fileUri);
                    /*intent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    intent.setAction(intentAction);*/
                }
                /*intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);*/
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

    public void setBaseCallback(ShareTextListener baseCallback) {
        this.baseCallback = baseCallback;
    }

    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

    public String getShareType() {
        return shareType;
    }



    public interface ShareListener extends ShareTextListener {
        void onShareFile(ShareActionModel model, ArrayList<Uri> contentToShare, String intentAction);
    }

    public interface ShareTextListener {
        void onShareText(ShareActionModel model, String contentToShare, String intentAction);
    }
}
