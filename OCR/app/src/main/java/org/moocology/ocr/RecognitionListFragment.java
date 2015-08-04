package org.moocology.ocr;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

public class RecognitionListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 0;
    DisplayImageOptions options;
    boolean isTablet;
    int curPosition = -1;
    long curIndex = -1;
    private RecognitionCursorAdapter cursorAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        ListView listView = getListView();
        if (listView.getCount() == 0) {
            View empty = getActivity().findViewById(R.id.emptyList);
            empty.setVisibility(View.VISIBLE);
            listView.setEmptyView(empty);
        }
        listView.setDividerHeight(4);
        getListView().setSelected(false);
        View detailsFrame = getActivity().findViewById(R.id.details);
        isTablet = detailsFrame != null;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            curPosition = savedInstanceState.getInt("pos");
            curIndex = savedInstanceState.getLong("index");
        }

        if (isTablet) {
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        } else {
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
                    long[] checked = getListView().getCheckedItemIds();
                    for (long aChecked : checked) {
                        Uri uri = Uri.parse(CONST.CONTENT_URI + "/" + aChecked);
                        getActivity().getContentResolver().delete(uri, null, null);
                    }
                }

                private void setSubtitle(ActionMode mode, int selectedCount) {
                    switch (selectedCount) {
                        case 0:
                            mode.setSubtitle(null);
                            break;
                        default:
                            mode.setTitle("Selected " + String.valueOf(selectedCount) + " items");
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
        }

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_menu_add)
                .showImageForEmptyUri(R.drawable.ic_menu_add)
                .showImageOnFail(R.drawable.ic_menu_add)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).build();
        ImageLoader.getInstance().init(config);
        fillData();
    }

    void showDetails(int position, long index) {
        getListView().setItemChecked(position, true);
        FragmentManager manager = getFragmentManager();
        RecognitionFragment fragment = (RecognitionFragment) manager.findFragmentById(R.id.details);
        Uri uriRec = Uri.parse(CONST.CONTENT_URI + "/" + index);
        if (fragment == null || fragment.getShownIndex() != uriRec) {
            fragment = RecognitionFragment.newInstance(uriRec);
            manager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.details, fragment).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pos", curPosition);
        outState.putLong("index", curIndex);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (isTablet) {
            showDetails(position, id);
        } else {
            startItemActivity(id);
        }
    }

    private void startItemActivity(long id) {
        Intent i = new Intent(getActivity(), RecognitionActivity.class);
        i.putExtra(CONST.Recognition_ID, id);
        startActivity(i);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recognition_list_activity, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
        FragmentManager fm = this.getFragmentManager();
        ChooserFragment dialog = ChooserFragment.newInstance();
        dialog.show(fm, "ss");
    }

    private void fillData() {
        cursorAdapter = new RecognitionCursorAdapter(getActivity(), null, 0);
        setListAdapter(cursorAdapter);
        LoaderManager.LoaderCallbacks<Cursor> myLoaderCallBacks = this;
        LoaderManager loaderManager = getLoaderManager();
        Bundle args = null;
        loaderManager.initLoader(LOADER_ID, args, myLoaderCallBacks);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{CONST._ID,
                CONST.DESC, CONST.DATE, CONST.LANG, CONST.URI, CONST.STATUS, CONST.TRANSLATION};
        return new  CursorLoader(getActivity(), CONST.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }


    static class ViewHolder {
        int keyDESC;
        int keyDATE;
        int keyURI;
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
            if (cursor.getString(holder.keyTRANSLATION) != null) {
                holder.translatorTextView.setVisibility(View.VISIBLE);
            } else {
                holder.translatorTextView.setVisibility(View.GONE);
            }
            if (cursor.getString(holder.keySTATUS) != null) {
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
            } else {
                holder.statusImageView.setVisibility(View.INVISIBLE);
            }
        }

        public void initHolder(View view, ViewHolder holder) {
            holder.imageView = (ImageView) view
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
