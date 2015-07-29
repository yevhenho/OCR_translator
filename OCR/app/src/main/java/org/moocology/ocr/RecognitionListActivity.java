package org.moocology.ocr;


import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;


public class RecognitionListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "RecognitionListActivity";
    private static final int LOADER_ID = 0;
    private RecognitionCursorAdapter cursorAdapter;
    DisplayImageOptions options;
    private LoaderManager.LoaderCallbacks<Cursor> myLoaderCallBacks;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
           // setContentView(R.layout.recognition_activity);

          // LoaderManager.enableDebugLogging(true);

            ListView listView = getListView();
            listView.setDividerHeight(4);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                      long id, boolean checked) {
                    //  update the title in the CAB
                    int selectedCount = getListView().getCheckedItemCount();
                    setSubtitle(mode, selectedCount);
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    // Respond to clicks on the actions in the CAB
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_recognition:
                            deleteSelectedItems();
                            mode.finish(); // Action picked, so close the CAB
                            fillData();
                            return true;
                        default:
                            return false;
                    }
                }
                private void deleteSelectedItems() {
                    long[] checked=getListView().getCheckedItemIds();
                    for(int i=0;i<checked.length;i++){
                        Uri uri = Uri.parse(CONST.CONTENT_URI + "/"+checked[i]);
                       getContentResolver().delete(uri, null, null);
                    }
                }

                private void setSubtitle(ActionMode mode, int selectedCount) {
                    switch (selectedCount) {
                        case 0:
                            mode.setSubtitle(null);
                            break;
                        default:
                            mode.setTitle("Selected "+String.valueOf(selectedCount)+" items");
                            break;
                    }
                }
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    // Inflate the menu for the CAB
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.recognition_list_item_context, menu);
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    // Here you can make any necessary updates to the activity when
                    // the CAB is removed. By default, selected items are deselected/unchecked.
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    // Here you can perform updates to the CAB due to
                    // an invalidate() request
                    return false;
                }
            });



            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_menu_add)
                    .showImageForEmptyUri(R.drawable.ic_menu_add)
                    .showImageOnFail(R.drawable.ic_menu_add)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new RoundedBitmapDisplayer(20))
                    .build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
            ImageLoader.getInstance().init(config);

            fillData();

        }

       @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        startItemActivity(id);
    }

    private void startItemActivity(long id) {
        Intent i = new Intent(this, RecognitionActivity.class);
        i.putExtra(CONST.Recognition_ID, id);
        Log.d(TAG,"position"+String.valueOf(id));
        startActivity(i);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar

        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.recognition_list_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_recognition:
                startChooser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startChooser() {
        FragmentManager fm =  this.getFragmentManager();
        ChooserFragment dialog = ChooserFragment.newInstance();
        dialog.show(fm, "ss");
    }

    private void fillData() {

        cursorAdapter=new RecognitionCursorAdapter(this, null,  0);
        setListAdapter(cursorAdapter);


        myLoaderCallBacks = this;
        LoaderManager loaderManager = getLoaderManager();
        Bundle args = null;


             loaderManager.initLoader(LOADER_ID, args, myLoaderCallBacks);

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = new String[]{CONST._ID,
                CONST.DESC, CONST.DATE,CONST.LANG,CONST.URI,CONST.STATUS,CONST.TRANSLATION
        };

               CursorLoader loader = new CursorLoader(this,
                CONST.CONTENT_URI, projection, null, null, null);
        Log.d(TAG, "onCreateLoaded");
        return loader;


    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
        Log.d(TAG, "onLoadFinished");

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");
        cursorAdapter.swapCursor(null);

    }
    static  class   ViewHolder  {
        int keyDESC;
        int keyDATE;
        int keyURI ;
        int keySTATUS;
        int keyTRANSLATION;

        TextView descTextView;
        TextView translatorTextView;
        TextView timedateTextView;
        // TextView langTextView;
        ImageView imageView;
        ImageView statusImageView;
    }
    private class RecognitionCursorAdapter extends CursorAdapter {
        private LayoutInflater cursorInflater;


        public RecognitionCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            cursorInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            return cursorInflater.inflate(R.layout.list_recognition_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder();

                initHolder(view, holder);

                holder.keyDESC = cursor.getColumnIndexOrThrow(CONST.DESC);
                holder.keyURI = cursor.getColumnIndexOrThrow(CONST.URI);
                holder.keyDATE = cursor.getColumnIndexOrThrow(CONST.DATE);
                holder.keySTATUS = cursor.getColumnIndexOrThrow(CONST.STATUS);
                holder.keyTRANSLATION = cursor.getColumnIndexOrThrow(CONST.TRANSLATION);


                view.setTag(holder);
            }

            filloutHolder(cursor, holder);
        }

        public void filloutHolder(Cursor cursor, ViewHolder holder) {
            holder.timedateTextView.setText(cursor.getString(holder.keyDATE));
            holder.descTextView.setText(cursor.getString(holder.keyDESC));
            ImageLoader.getInstance().displayImage(cursor.getString(holder.keyURI), holder.imageView, options);
            if(cursor.getString(holder.keyTRANSLATION)!=null){holder.translatorTextView.setVisibility(View.VISIBLE);}
            else{
                holder.translatorTextView.setVisibility(View.GONE);
            }
            if(cursor.getString(holder.keySTATUS)!=null){
                holder.statusImageView.setVisibility(View.VISIBLE);
                switch (Integer.valueOf(cursor.getString(holder.keySTATUS))) {
                    case CONST.STATUS_NO_RECOGNIZED:
                        holder.statusImageView.setImageDrawable(getResources().getDrawable(CONST.DRAWABLE_NO));
                        break;
                    case CONST.STATUS_RECOGNIZED:
                        holder.statusImageView.setImageDrawable(getResources().getDrawable(CONST.DRAWABLE_YES));
                        break;

                }

              /*      if(Integer.valueOf(cursor.getString(holder.keySTATUS))== CONST.STATUS_NO_RECOGNIZED){
                    holder.statusImageView.setImageDrawable(getResources().getDrawable(CONST.DRAWABLE_NO));
                }
                if(Integer.valueOf(cursor.getString(holder.keySTATUS))==CONST.STATUS_RECOGNIZED){
                    holder.statusImageView.setImageDrawable(getResources().getDrawable(CONST.DRAWABLE_YES));
                }*/
            } else{holder.statusImageView.setVisibility(View.INVISIBLE);
            }
        }

        public void initHolder(View view, ViewHolder holder) {
            holder.imageView= (ImageView) view
                    .findViewById(R.id.Recognition_list_item_ImageView);
            holder.descTextView = (TextView) view
                    .findViewById(R.id.Recognition_list_item_descTextView);
            holder.timedateTextView = (TextView) view
                    .findViewById(R.id.Recognition_list_item_timedateTextView);
            holder.statusImageView = (ImageView) view
                    .findViewById(R.id.Recognition_list_item_statusImageView);
            holder.translatorTextView = (TextView) view
                    .findViewById(R.id.Recognition_list_item_translatorTextView);
        }

    }
}

