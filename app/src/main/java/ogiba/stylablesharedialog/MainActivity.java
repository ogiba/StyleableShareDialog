package ogiba.stylablesharedialog;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import ogiba.styleablesharedialog.ShareDialog.ShareDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button simpleShare;
    private Button simpleHorizontalShare;
    private Button simpleShareWithHeaderBtn;
    private Button simpleShareWithFooterBtn;
    private Button simpleShareWithHeaderAndFooterBtn;
    private Button simpleShareAsList;
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    private void setupButtons() {
        this.simpleShare.setOnClickListener(this);
        this.simpleHorizontalShare.setOnClickListener(this);
        this.simpleShareWithHeaderBtn.setOnClickListener(this);
        this.simpleShareWithFooterBtn.setOnClickListener(this);
        this.simpleShareWithHeaderAndFooterBtn.setOnClickListener(this);
        this.simpleShareAsList.setOnClickListener(this);
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
        }
    }

    private void showSimpleShare() {
        ShareDialog.Builder builder = new ShareDialog.Builder();
        builder.setType(ShareDialog.TYPE_TEXT);
        this.shareDialog = builder.build();
        shareDialog.setShareContent("Test value");
        shareDialog.show(getSupportFragmentManager());
    }

    private void showSimpleHorizontalShare() {
        ShareDialog.Builder builder = new ShareDialog.Builder();
        builder.setType(ShareDialog.TYPE_TEXT);
        builder.changeOrientation(true);
        this.shareDialog = builder.build();
        shareDialog.setShareContent("Test value");
        shareDialog.show(getSupportFragmentManager());
    }

    private void showSimpleShareWithHeader() {
        ShareDialog.Builder builder = new ShareDialog.Builder();
        builder.setType(ShareDialog.TYPE_TEXT);
        builder.setHeaderLayout(R.layout.dialog_top_container);
        this.shareDialog = builder.build();
        shareDialog.setShareContent("Test value");
        shareDialog.show(getSupportFragmentManager());
    }

    private void showSimpleShareWithFooter() {
        ShareDialog.Builder builder = new ShareDialog.Builder();
        builder.setType(ShareDialog.TYPE_TEXT);
        builder.setFooterLayout(R.layout.dialog_bottom_container);
        this.shareDialog = builder.build();
        shareDialog.setShareContent("Test value");
        shareDialog.show(getSupportFragmentManager());
    }

    private void showSimpleShareWithHeaderAndFooter() {
        ShareDialog.Builder builder = new ShareDialog.Builder();
        builder.setType(ShareDialog.TYPE_TEXT);
        builder.setHeaderLayout(R.layout.dialog_top_container);
        builder.setFooterLayout(R.layout.dialog_bottom_container);
        this.shareDialog = builder.build();
        shareDialog.setShareContent("Test value");
        shareDialog.show(getSupportFragmentManager());
    }

    private void showSimpleShareInListForm() {
        ShareDialog.Builder builder = new ShareDialog.Builder();
        builder.setType(ShareDialog.TYPE_TEXT);
        builder.showAsList(true);
        this.shareDialog = builder.build();
        shareDialog.setShareContent("Test value");
        shareDialog.show(getSupportFragmentManager());
    }

    private void navigateToInformation() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
}
