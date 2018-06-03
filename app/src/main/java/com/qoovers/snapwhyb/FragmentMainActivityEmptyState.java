package com.qoovers.snapwhyb;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class FragmentMainActivityEmptyState extends Fragment
{
    protected final String TAG = FragmentMainActivityEmptyState.class.getSimpleName();

    CameraButtonClickListener cameraButtonClickCallback;

    public interface CameraButtonClickListener {
        void onEmptyStateCameraButtonClick();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            cameraButtonClickCallback = (CameraButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    public FragmentMainActivityEmptyState() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_empty_state, container, false);

        LinearLayout button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraButtonClickCallback.onEmptyStateCameraButtonClick();
            }
        });

        return view;
    }
}
