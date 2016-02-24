package com.lexaloris.yotabrowser.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lexaloris.yotabrowser.R;
import com.lexaloris.yotabrowser.db.MyContentProvider;

public class ResponseFragment extends Fragment {

    SimpleCursorAdapter scAdapter;
    ListView lvData;

    public static ResponseFragment newInstance() {
        ResponseFragment responseFragment = new ResponseFragment();
        return responseFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.response_fragment, null);

        Cursor cursor = getActivity().getContentResolver().query(
                MyContentProvider.HTML_SOURCE_CONTENT_URI, null, null, null, null);
        getActivity().startManagingCursor(cursor);

        // формируем столбцы сопоставления
        String[] from = new String[] { MyContentProvider.COLUMN_TXT };
        int[] to = new int[] { R.id.tvText };

        scAdapter = new SimpleCursorAdapter(getContext(), R.layout.response_fragment_item, cursor, from, to, 0);
        lvData = (ListView) view.findViewById(R.id.lvData);
        lvData.setAdapter(scAdapter);


        return view;
    }
}
