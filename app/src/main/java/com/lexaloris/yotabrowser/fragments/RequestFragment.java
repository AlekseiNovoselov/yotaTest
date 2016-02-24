package com.lexaloris.yotabrowser.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lexaloris.yotabrowser.R;
import com.lexaloris.yotabrowser.utils.NetworkHelper;

public class RequestFragment extends Fragment {

    Button mBtnSubmit;
    EditText mInputField;

    private OnRequestListener mListener;

    public static RequestFragment newInstance() {
        RequestFragment requestFragment = new RequestFragment();
        return requestFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.request_fragment, null);

        mInputField = (EditText) view.findViewById(R.id.etInputField);
        mInputField.clearFocus();
        mBtnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkHelper.isInternet(getContext())) {
                    Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                } else {
                    final String urlText = mInputField.getText().toString();
                    if (URLUtil.isValidUrl(urlText)) {
                        hideKeyBoard();
                        mListener.sendRequest(urlText);
                    } else {
                        Toast.makeText(getContext(), R.string.invalid_url, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    private void hideKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public interface OnRequestListener {
        void sendRequest(String urlText);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnRequestListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
