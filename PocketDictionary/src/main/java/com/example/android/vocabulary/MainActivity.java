package com.example.android.vocabulary;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.vocabulary.adapter.MyWordRecyclerViewAdapter;
import com.example.android.vocabulary.data.WordContract;
import com.example.android.vocabulary.databinding.ActivityMainBinding;
import com.example.android.vocabulary.server.ServerApi;
import com.example.android.vocabulary.settings.SettingsActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    ActivityMainBinding mainBinding;
    private Gson mJson;
    ServerApi mApi;
    SharedPreferences mSharedPreferences;
    private static final String URL = "https://translate.yandex.net";
    private static final String KEY = "trnsl.1.1.20170418T135654Z.abe386d13c242153.01eeb408d1e0e4d3d66b80c40ba5b2f9c267e55f";

    private String mLanguages = "";
    private String mWord;
    private String mTranslatedWord;

    private static final int TASK_LOADER_ID = 0;
    MyWordRecyclerViewAdapter mAdapter;


    SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mJson = new GsonBuilder().create();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mLanguages = mSharedPreferences.getString(getString(R.string.language_key),
                getString(R.string.enru_values));


        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyWordRecyclerViewAdapter(this);
        mainBinding.recyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int id = (int) viewHolder.itemView.getTag();
                String stringId = Integer.toString(id);
                Uri uri = WordContract.WordEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();
                getContentResolver().delete(uri, null, null);
                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, MainActivity.this);
            }


        }).attachToRecyclerView(mainBinding.recyclerView);


        mainBinding.tvLanguage.setText(mLanguages);
        mainBinding.btnSave.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(mJson))
                .baseUrl(URL)
                .build();


        mApi = retrofit.create(ServerApi.class);


        mainBinding.bTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mWord = mainBinding.etWord.getText().toString();
                mWord = mWord.replace(" ", "");
                if (mWord.isEmpty()) {
                    Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    mainBinding.btnSave.setVisibility(View.GONE);
                    mainBinding.tvTranslate.setText("");
                    return;
                }
                Map<String, String> mapJson = new HashMap<String, String>();

                mapJson.put("key", KEY);
                mapJson.put("text", mWord);
                mapJson.put("lang", mLanguages);

                Call<Object> call = mApi.translate(mapJson);

                getTranslate(call);

            }
        });

        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }


    private void getTranslate(Call<Object> call) {
        try {
            Response<Object> response = call.execute();

            Map<String, String> map = mJson.fromJson(response.body().toString(), Map.class);

            for (Map.Entry e : map.entrySet()) {
                if (e.getKey().equals("text")) {
                    mTranslatedWord = e.getValue().toString().replace("[", "").replace("]", "");
                    mainBinding.tvTranslate.setText(mTranslatedWord);
                    mainBinding.btnSave.setVisibility(View.VISIBLE);
                }
            }
        }
        catch (JsonSyntaxException jse)
        {
            jse.printStackTrace();
            Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            Toast.makeText(MainActivity.this, getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(WordContract.WordEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            WordContract.WordEntry.COLUMN_LANGUAGES);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveClick(View v) {
        if (v.getId() == R.id.btnSave) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(WordContract.WordEntry.COLUMN_LANGUAGES, mLanguages);
            contentValues.put(WordContract.WordEntry.COLUMN_WORD, mWord);
            contentValues.put(WordContract.WordEntry.COLUMN_TRANSLATE, mTranslatedWord);

            getContentResolver().insert(WordContract.WordEntry.CONTENT_URI, contentValues);
            getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, MainActivity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }


}
