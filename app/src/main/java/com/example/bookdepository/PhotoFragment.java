package com.example.bookdepository;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Date;

public class PhotoFragment extends DialogFragment {

    public static final String ARG_PATH = "path";
    private ImageView mPhotoView;

    public static PhotoFragment newInstance(String path){
        Bundle args = new Bundle();
        args.putSerializable(ARG_PATH,path);

        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String path = getArguments().getString(ARG_PATH);
        View v = inflater.inflate(R.layout.dialog_photo,null);
        mPhotoView = (ImageView)v.findViewById(R.id.dialog_photo_image_view);
        Bitmap bitmap = PictureUtils.getScaledBitmap(path,getActivity());
        mPhotoView.setImageBitmap(bitmap);
        return v;
    }
}
