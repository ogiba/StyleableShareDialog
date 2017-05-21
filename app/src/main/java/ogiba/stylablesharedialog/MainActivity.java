package ogiba.stylablesharedialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;

import ogiba.styleablesharedialog.ShareDialog.ShareDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FileAsync.ResultListener {
    private static final String TEST_FILE_NAME = "testImage.jpg";
    private static final String TEST_DIRECTORY_NAME = "images";
    private static final String DEMO_AUTHORITY_IDENTIFIER = "ogiba.stylablesharedialog.fileprovider";
    private static final String PREFERENCES_NAME = "shareDialogPreferences";
    private static final String PREF_KEY_SAVED = "isSaved";

    private Button simpleShare;
    private Button simpleHorizontalShare;
    private Button simpleShareWithHeaderBtn;
    private Button simpleShareWithFooterBtn;
    private Button simpleShareWithHeaderAndFooterBtn;
    private Button simpleShareAsList;
    private Button simpleShareWithImage;
    private ShareDialog shareDialog;
    private boolean isFileLoaded;

    private String shareValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shareValue = "Test value";
        isFileLoaded = getSavedValue();

        loadImageAsync();
        bindViews();
        setupButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_info:
                navigateToInformation();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void bindViews() {
        this.simpleShare = (Button) findViewById(R.id.simple_share);
        this.simpleHorizontalShare = (Button) findViewById(R.id.simple_share_horizontal);
        this.simpleShareWithHeaderBtn = (Button) findViewById(R.id.simple_share_with_header);
        this.simpleShareWithFooterBtn = (Button) findViewById(R.id.simple_share_with_footer);
        this.simpleShareWithHeaderAndFooterBtn = (Button) findViewById(R.id.simple_share_with_both);
        this.simpleShareAsList = (Button) findViewById(R.id.simple_share_as_list);
        this.simpleShareWithImage = (Button) findViewById(R.id.simple_share_image);
    }

    private void setupButtons() {
        this.simpleShare.setOnClickListener(this);
        this.simpleHorizontalShare.setOnClickListener(this);
        this.simpleShareWithHeaderBtn.setOnClickListener(this);
        this.simpleShareWithFooterBtn.setOnClickListener(this);
        this.simpleShareWithHeaderAndFooterBtn.setOnClickListener(this);
        this.simpleShareAsList.setOnClickListener(this);
        this.simpleShareWithImage.setOnClickListener(this);

        this.simpleShareWithImage.setEnabled(isFileLoaded);
    }

    private void loadImageAsync() {
        if (!isFileLoaded) {
            Bitmap testImage = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.ic_launcher_styleablesharedialog_banner);
            FileAsync fileAsync = new FileAsync(this, TEST_DIRECTORY_NAME, TEST_FILE_NAME);
            fileAsync.setCallback(this);
            fileAsync.execute(testImage);
        }
    }

    @Override
    public void onExecuted(boolean isDone) {
        isFileLoaded = isDone;
        saveValue(isDone);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.simpleShareWithImage.setEnabled(getSavedValue());
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.simple_share:
                showSimpleShare();
                break;
            case R.id.simple_share_horizontal:
                showSimpleHorizontalShare();
                break;
            case R.id.simple_share_with_header:
                showSimpleShareWithHeader();
                break;
            case R.id.simple_share_with_footer:
                showSimpleShareWithFooter();
                break;
            case R.id.simple_share_with_both:
                showSimpleShareWithHeaderAndFooter();
                break;
            case R.id.simple_share_as_list:
                showSimpleShareInListForm();
                break;
            case R.id.simple_share_image:
                showFileShare();
                break;
        }
    }

    private void showSimpleShare() {
        ShareDialog.Builder builder = new ShareDialog.Builder();
        builder.setType(ShareDialog.TYPE_TEXT);
        builder.setShareContent(shareValue);
        this.shareDialog = builder.build();
        shareDialog.show(getSupportFragmentManager());
    }

    private void showSimpleHorizontalShare() {
        ShareDialog.Builder builder = new ShareDialog.Builder();
        builder.setType(ShareDialog.TYPE_TEXT);
        builder.changeOrientation(true);
        this.shareDialog = builder.build();
        shareDialog.setShareContent(shareValue);
        shareDialog.show(getSupportFragmentManager());
    }

    private void showSimpleShareWithHeader() {
        ShareDialog.Builder builder = new ShareDialog.Builder();
        builder.setType(ShareDialog.TYPE_TEXT);
        builder.setHeaderLayout(R.layout.dialog_top_container);
        this.shareDialog = builder.build();
        shareDialog.setShareContent(shareValue);
        shareDialog.show(getSupportFragmentManager());
    }

    private void showSimpleShareWithFooter() {
        ShareDialog.Builder builder = new ShareDialog.Builder();
        builder.setType(ShareDialog.TYPE_TEXT);
        builder.setFooterLayout(R.layout.dialog_bottom_container);
        this.shareDialog = builder.build();
        shareDialog.setShareContent(shareValue);
        shareDialog.show(getSupportFragmentManager());
    }

    private void showSimpleShareWithHeaderAndFooter() {
        ShareDialog.Builder builder = new ShareDialog.Builder();
        builder.setType(ShareDialog.TYPE_TEXT);
        builder.setHeaderLayout(R.layout.dialog_top_container);
        builder.setFooterLayout(R.layout.dialog_bottom_container);
        this.shareDialog = builder.build();
        shareDialog.setShareContent(shareValue);
        shareDialog.show(getSupportFragmentManager());
    }

    private void showSimpleShareInListForm() {
        ShareDialog.Builder builder = new ShareDialog.Builder();
        builder.setType(ShareDialog.TYPE_TEXT);
        builder.showAsList(true);
        this.shareDialog = builder.build();
        shareDialog.setShareContent(shareValue);
        shareDialog.show(getSupportFragmentManager());
    }

    private void showFileShare() {
        ShareDialog.Builder builder = new ShareDialog.Builder();
        builder.setType(ShareDialog.TYPE_IMAGE);
        builder.setShareContent(loadImage());
        this.shareDialog = builder.build();
        shareDialog.show(getSupportFragmentManager());
    }

    private String loadImage() {
        File directory = new File(MainActivity.this.getFilesDir(), TEST_DIRECTORY_NAME);
        File file = new File(directory, TEST_FILE_NAME);

        if (!file.exists())
            return "";

        Uri uri = FileProvider.getUriForFile(this, DEMO_AUTHORITY_IDENTIFIER, file);
        return uri.toString();
    }

    private void navigateToInformation() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    private void saveValue(boolean value){
        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_KEY_SAVED, value);
        editor.apply();
        editor.commit();
    }

    private boolean getSavedValue(){
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        return prefs.getBoolean(PREF_KEY_SAVED, false);
    }
}
