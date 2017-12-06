package com.csc285.android.z_track;


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

import java.io.File;

/**
 *
 * Created by nick on 11/8/2017.
 */

public class ShowLargePictureFragment extends DialogFragment {

    private static final String ARG_PIC = "large_picture";
    public static final String EXTRA_PIC =
            "com.csc285.android.z_track.large_picture";
    private ImageView mLargePicture;
    private File mPhotoFile;

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String pic_Crime = (String) getArguments().getSerializable(ARG_PIC);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_show_pictore, null);

        mLargePicture = (ImageView) v.findViewById(R.id.marker_photo_large);
//        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(pic_Crime);
        mPhotoFile = new File(getActivity().getApplicationContext().getFilesDir(), pic_Crime);
        Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
        mLargePicture.setImageBitmap(bitmap);

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

    public static ShowLargePictureFragment newInstance(String pCrime) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PIC, pCrime);
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
