package com.example.moallemtask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseReference videoImages = FirebaseDatabase.getInstance().getReference("videos");
        videoImages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int id=0;//set id for displaying in slider
                for(DataSnapshot item_snapshot:dataSnapshot.getChildren()) {
                    //fetching videos from fire base database
                    //videos are stored in firebase storage and their links are stored in real time database
                    //videos links are fetched and then are passed to function for drawing them in slider
                    Log.d("item video ",item_snapshot.child("video").getValue().toString());
                    CreateVideoSlider(item_snapshot.child("video").getValue().toString(), id);//pass videos links to the function
                    id++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //create videos slider
    void CreateVideoSlider(final String URLSource, int ID)
    {
        LinearLayout linearLayout = findViewById(R.id.Videothumbnail);
        FrameLayout frameLayout = new FrameLayout(this);
        LinearLayout.LayoutParams ThumbnailParams = new LinearLayout.LayoutParams(600, 400);//parameters for videos thumbnail
        ThumbnailParams.gravity = Gravity.CENTER;
        ThumbnailParams.setMargins(20, 20, 20, 20);
        final ImageView videoThumbnailImage = new ImageView(this);
        final ImageButton PlayButton = new ImageButton(this);
        PlayButton.setBackgroundColor(Color.TRANSPARENT);
        PlayButton.setLayoutParams(ThumbnailParams);
        videoThumbnailImage.setLayoutParams(ThumbnailParams);
        frameLayout.addView(videoThumbnailImage);
        frameLayout.addView(PlayButton);
        linearLayout.addView(frameLayout);
        videoThumbnailImage.setId(ID);
        PlayButton.setId(ID);
        RoundedBitmapDrawable roundedBitmapDrawable = null;
        try {
            //calling function to get a rounded border thumbnail from videos link
            roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), retriveVideoFrameFromVideo(URLSource));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        final float roundPx;
        try {
            //adding video thumbnail to image view
            roundPx = (float) retriveVideoFrameFromVideo(URLSource).getWidth() * 0.06f;
            roundedBitmapDrawable.setCornerRadius(roundPx);
            videoThumbnailImage.setImageDrawable(roundedBitmapDrawable);


        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
//        try {
//            videoThumbnailImage.setImageBitmap(retriveVideoFrameFromVideo(ss));
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
        //adding play icon on button
        Picasso.get().load(R.drawable.download).into(PlayButton);
        PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on click open a new activity to play the video, video is being played automatically from firebase without
                //downloading it
                Intent intent = new Intent(getBaseContext(), videoplayer.class);
                intent.putExtra("URL", URLSource);
                startActivity(intent);
            }
        });

    }
    //retrieving video from firebase getting it to thumbnail
    public static Bitmap retriveVideoFrameFromVideo(String videoPath)throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable("error retrieving video"+ e.getMessage());
        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

}

