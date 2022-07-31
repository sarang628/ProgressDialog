package com.sarang.screenprogressdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class UploadProgressDialog extends Dialog {
    private ProgressBar progressBar;
    private ProgressBar timerProgress;
    private TextView percentage;
    private TextView msg;
    private ImageView img;
    private Handler handler = new Handler();
    private String currentPath = "";
    private HashMap<String, Float> uploadMap = new HashMap<>();
    ArrayList<String> selectedImagePath;

    public UploadProgressDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_upload_progress);
        progressBar = findViewById(R.id.progress_bar);
        timerProgress = findViewById(R.id.timer_progress);
        percentage = findViewById(R.id.percentage);
        msg = findViewById(R.id.msg);
        img = findViewById(R.id.img);
        setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
    }

    public void showImage(String path) {
        if (currentPath.equals(path))
            return;

        currentPath = path;

        String realPath = path;

        if (selectedImagePath != null)
            for (String str : selectedImagePath) {
                if (str.contains(new File(path).getName())) {
                    realPath = str;
                    break;
                }
            }
        Log.d("TorangLog", realPath);

        img.setVisibility(View.VISIBLE);
        Glide.with(getContext())
                .load(realPath)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(img);
    }

    public void showImage(int resource) {
        img.setVisibility(View.VISIBLE);
        Glide.with(getContext())
                .load(resource)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(img);
    }

    public void hideImage() {
        img.setVisibility(View.GONE);
    }

    public void setProcessProgress(int processProgress) {
        Log.d("TorangLog", "processProgress:" + processProgress);
        progressBar.setProgress(processProgress);
        timerProgress.setProgress(processProgress);
        percentage.setText("" + (processProgress) + "%");

        if (processProgress == 100)
            setProgressMessage("image upload complete");
    }

    public void setProgressMessage(String progressMessage) {
        msg.setText(progressMessage);
    }

    public void done() {
        img.setVisibility(View.GONE);
        setProgressMessage("upload complete!");
        handler.postDelayed(() -> {
            handler.postDelayed(() -> dismiss(), 1000);
        }, 1000);
    }

    public void failed(String msg) {
        handler.postDelayed(() -> {
            setProgressMessage(msg);
            handler.postDelayed(() -> dismiss(), 1000);
        }, 1000);
    }

    private void start() {
        show();
        handler.postDelayed(() -> {
            setProgressMessage("ready..");
        }, 1000);
    }

    private void compress(ArrayList<String> images) {
        handler.postDelayed(() -> {
            setProgressMessage("compress image..");
        }, 1000);
    }

    private void upload(ArrayList<File> pathes) {
        handler.postDelayed(() -> {
            setProgressMessage("compress done\n" + TextUtils.join(",", pathes));
            handler.postDelayed(() -> done(), 1000);
        }, 1000);
    }

    public void setFileCount(ArrayList<String> selectedImagePath) {
        this.selectedImagePath = selectedImagePath;
        uploadMap = new HashMap<>();
        for (String path : selectedImagePath) {
            File file = new File(path);
            String name = file.getName();
            Log.d("TorangLog", name);
            uploadMap.put(name, (float) 0);
        }
    }

    public void update(File file, float progress) {
        showImage(file.getPath());
        uploadMap.put(file.getName(), progress);

        Set<String> keySets = uploadMap.keySet();

        int size = keySets.size();

        Iterator<String> itr = keySets.iterator();

        float totalProgress = 0;

        while (itr.hasNext()) {
            totalProgress += uploadMap.get(itr.next());
        }
        Log.d("TorangLog", "totalProgress:" + totalProgress);
        setProcessProgress((int) (totalProgress / size));
    }
}
