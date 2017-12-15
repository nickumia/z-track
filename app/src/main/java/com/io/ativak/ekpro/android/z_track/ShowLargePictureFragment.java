package com.io.ativak.ekpro.android.z_track;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 *
 * Created by nick on 11/8/2017.
 */

public class ShowLargePictureFragment extends DialogFragment {

    private static final String ARG_PIC = "large_picture";
    private static final String ARG_POS = "geo_position";
    public static final String EXTRA_PIC = "com.csc285.android.z_track.large_picture";
    public static final String EXTRA_POS = "com.csc285.android.z_track.geo_position";
    private ImageView mLargePicture;
    private TextView mPositionView;
    private File mPhotoFile;

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String pic_Crime = (String) getArguments().getSerializable(ARG_PIC);
        String position = (String) getArguments().getString(ARG_POS);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_show_pictore, null);

        mLargePicture = (ImageView) v.findViewById(R.id.marker_photo_large);
        mPositionView = (TextView) v.findViewById(R.id.positionView);
        mPhotoFile = new File(getActivity().getApplicationContext().getFilesDir(), pic_Crime);
        Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
        mLargePicture.setImageBitmap(bitmap);
        mPositionView.setText(position);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.show_large_picture_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                .create();
    }

    public static ShowLargePictureFragment newInstance(String pCrime, String position) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PIC, pCrime);
        args.putString(ARG_POS, position);
        ShowLargePictureFragment fragment = new ShowLargePictureFragment();
        fragment.setArguments(args);
        return fragment;
    }

//    private void sendResult(int resultCode, Date date) {
//        if (getTargetFragment() == null) { return; }
//        Intent intent = new Intent();
//        intent.putExtra(EXTRA_PIC, date);
//        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
//    }
}
