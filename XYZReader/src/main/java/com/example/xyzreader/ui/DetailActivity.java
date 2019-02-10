package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.remote.Config;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.detail_image_view)
    ImageView articleImageView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.article_body)
    TextView articleBodyView;

    @BindView(R.id.author_view)
    TextView authorView;

    @BindView(R.id.fab_share_button)
    FloatingActionButton fab_button;

    @BindView(R.id.nestedScrollView)
    NestedScrollView scrollView;

    // declare variable to store uri provided by intent which opens the Activity
    private long currentArticleId;

    private Cursor mCursor;

    private final int ARTICLE_LOADER_ID = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        // Set the padding to match the Status Bar height (to avoid title being cut off by
        // transparent toolbar
        toolbar.setPadding(0, 25, 0, 0);


        // get intent object used to open this activity
        Intent intent = getIntent();

        // get data object inside the intent
        currentArticleId = intent.getLongExtra(Config.ARTICLE_ID_EXTRA, 0);

        getLoaderManager().initLoader(ARTICLE_LOADER_ID, null, this);



        fab_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(DetailActivity.this)
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                authorView.setVisibility(View.GONE);
            }
        });


    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(this, currentArticleId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        mCursor = cursor;

        if (mCursor != null){
            // Proceed with moving to the first row of the cursor and reading data from it
            // (This should be the only row in the cursor)
            mCursor.moveToFirst();

            //set content of views to display data about chosen article
            toolbar.setTitle(mCursor.getString(ArticleLoader.Query.TITLE));

            articleBodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)
                    .replaceAll("(\r\n|\n)", "<br />")));

            String authorDisplayString = "by " +
                    "" + mCursor.getString(ArticleLoader.Query.AUTHOR);

            authorView.setText(authorDisplayString);

            Glide.with(this)
                    .load(mCursor.getString(ArticleLoader.Query.THUMB_URL))
                    .into(articleImageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        toolbar.setTitle("N/A");
        articleBodyView.setText("N/A");
        articleImageView.setBackgroundColor(getResources().getColor(R.color.theme_primary));
    }

    /***
     * This method facilitates the function of Back navigation using Button in Toolbar on older versions
     * of Android
     * @param item the menu item that was clicked on
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
