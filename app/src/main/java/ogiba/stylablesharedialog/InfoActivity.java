package ogiba.stylablesharedialog;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    private View githubRow;
    private View devRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setupTitle();
        setupToolbar();
        bindViews();
        setupViewListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.github_row:
                openBrowser(R.string.info_activity_github_address);
                break;
            case R.id.developer_row:
                openBrowser(R.string.info_activity_linkedin_address);
                break;
        }
    }

    private void setupTitle() {
        this.setTitle(R.string.info_activity_title);
    }

    private void setupToolbar(){
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void bindViews() {
        this.githubRow = this.findViewById(R.id.github_row);
        this.devRow = this.findViewById(R.id.developer_row);
    }

    private void setupViewListeners() {
        this.githubRow.setOnClickListener(this);
        this.devRow.setOnClickListener(this);
    }

    private void openBrowser(int resAddress){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getResources().getString(resAddress)));
        startActivity(intent);
    }
}
