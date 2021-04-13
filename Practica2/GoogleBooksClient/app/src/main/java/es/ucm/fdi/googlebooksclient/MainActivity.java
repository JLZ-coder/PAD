package es.ucm.fdi.googlebooksclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int BOOK_LOADER_ID = 1;

    private BookLoaderCallbacks bookLoaderCallbacks = new BookLoaderCallbacks();
    private EditText input_authors;
    private EditText input_title;
    private RadioGroup radio;
    public static RecyclerView recycler;
    public static TextView result_title;
    public static BooksResultListAdapter mAdapter;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        setContentView(R.layout.activity_main);

        input_authors = findViewById(R.id.form_input_authors);
        input_title = findViewById(R.id.form_input_title);
        radio = findViewById(R.id.form_radio);
        recycler = findViewById(R.id.form_recycler);
        result_title = findViewById(R.id.form_title_result);

        LoaderManager loaderManager = LoaderManager.getInstance(this);
        if(loaderManager.getLoader(BOOK_LOADER_ID) != null){
            loaderManager.initLoader(BOOK_LOADER_ID,null, bookLoaderCallbacks);
        }

        mAdapter = new BooksResultListAdapter(this, new ArrayList<>());
        recycler.setAdapter(mAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        /*ArrayList<BookInfo> prueba_list = new ArrayList<BookInfo>();
        prueba_list.add(new BookInfo("yo", "yo", "https://www.youtube.com/watch?v=a-TRAgxFOkA"));
        prueba_list.add(new BookInfo("yo", "yo", "https://www.youtube.com/watch?v=a-TRAgxFOkA"));
        updateBooksResultList(prueba_list);*/
    }

    public void searchBooks (View view) {

        hideKeyboardFrom(this, view);

        String queryString = input_authors.getText().toString() + " " + input_title.getText().toString();
        if (queryString.equals(" ")) {
            input_authors.setError("Escriba al menos un autor o titulo");
            input_title.setError("Escriba al menos un autor o titulo");
        }
        else {
            int radioButtonID = radio.getCheckedRadioButtonId();
            RadioButton r_button= radio.findViewById(radioButtonID);
            String printType = r_button.getText().toString();

            result_title.setVisibility(View.VISIBLE);
            result_title.setText("Loading...");

            Bundle queryBundle = new Bundle();
            queryBundle.putString(BookLoaderCallbacks.EXTRA_QUERY, queryString);
            queryBundle.putString(BookLoaderCallbacks.EXTRA_PRINT_TYPE, printType);
            LoaderManager.getInstance(this).restartLoader(BOOK_LOADER_ID, queryBundle, bookLoaderCallbacks);
        }
    }

    public void searchBookInfo (View view) {

        TextView v = view.findViewById(R.id.book_info);

        Uri url = Uri.parse(v.getText().toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, url);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("ImplicitIntents", "Can't handle this intent!");
        }

    }

    public static void updateBooksResultList(List<BookInfo> bookInfos) {
        result_title.setVisibility(View.VISIBLE);
        if (mAdapter.getItemCount() == 0) {
            result_title.setText("No Results Found");
        }
        else {
            result_title.setText("Results");
        }

        recycler.setVisibility(View.VISIBLE);

        mAdapter.setBooksData(bookInfos);
        mAdapter.notifyDataSetChanged();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }
}