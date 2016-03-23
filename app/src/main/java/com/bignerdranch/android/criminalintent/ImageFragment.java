package com.bignerdranch.android.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Martin on 3/22/16.
 */
public class ImageFragment extends DialogFragment {

    private static final String ARG_IMAGE = "image";

    public static ImageFragment newInstance(String string){
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE, string);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ImageView mImageView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        String image = getArguments().getString(ARG_IMAGE);

        Bitmap bitmap = PictureUtils.getScaledBitmap(image, getActivity());

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image, null);

        mImageView = (ImageView) v.findViewById(R.id.dialog_zoom_image);
        mImageView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.zoom_image_title).setPositiveButton(android.R.string.ok, null).create();
    }
}
