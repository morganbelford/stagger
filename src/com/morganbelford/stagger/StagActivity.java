package com.morganbelford.stagger;

import com.morganbelford.stagger.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.WindowManager;

public class StagActivity extends Activity {
    
    private static final int NUM_COLS = 3;
    private static final int MARGIN_DIPS = 10;
    
    private float _fScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_stag);
        
        StagLayout container = (StagLayout) findViewById(R.id.frame);
        
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);  
        _fScale = metrics.density;

        
        String[] testUrls = new String[] { 
                "http://www.westlord.com/wp-content/uploads/2010/10/French-Bulldog-Puppy-242x300.jpg", 
                "http://upload.wikimedia.org/wikipedia/en/b/b0/Cream_french_bulldog.jpg",
                "http://bulldogbreeds.com/breeders/pics/french_bulldog_64368.jpg",
                "http://www.drsfostersmith.com/images/articles/a-french-bulldog.jpg",
                "http://2.bp.blogspot.com/-ui2p5Z_DJIs/Tgdo09JKDbI/AAAAAAAAAQ8/aoTdw2m_bSc/s1600/Lilly+%25281%2529.jpg",
                "http://www.dogbreedinfo.com/images14/FrenchBulldog7.jpg",
                "http://dogsbreed.net/wp-content/uploads/2011/03/french-bulldog.jpg",
                "http://www.theflowerexpert.com/media/images/giftflowers/flowersandoccassions/valentinesdayflowers/sea-of-flowers.jpg.pagespeed.ce.BN9Gn4lM_r.jpg",
                "http://img4-2.sunset.timeinc.net/i/2008/12/image-adds-1217/alcatraz-flowers-galliardia-m.jpg?300:300",
                "http://images6.fanpop.com/image/photos/32600000/bt-jpgcarnation-jpgFlower-jpgred-rose-flow-flowers-32600653-1536-1020.jpg",
                "http://the-bistro.dk/wp-content/uploads/2011/07/Bird-of-Paradise.jpg",
                "http://2.bp.blogspot.com/_SG-mtHOcpiQ/TNwNO1DBCcI/AAAAAAAAALw/7Hrg5FogwfU/s1600/birds-of-paradise.jpg",
                "http://wac.450f.edgecastcdn.net/80450F/screencrush.com/files/2013/01/get-back-to-portlandia-tout.jpg",
                "http://3.bp.blogspot.com/-bVeFyAAgBVQ/T80r3BSAVZI/AAAAAAAABmc/JYy8Hxgl8_Q/s1600/portlandia.jpg",
                "http://media.oregonlive.com/ent_impact_tvfilm/photo/portlandia-season2jpg-7d0c21a9cb904f54.jpg",
                "https://twimg0-a.akamaihd.net/profile_images/1776615163/PortlandiaTV_04.jpg",
                "http://getvideoartwork.com/gallery/main.php?g2_view=core.DownloadItem&g2_itemId=85796&g2_serialNumber=1",
                "http://static.tvtome.com/images/genie_images/story/2011_usa/p/portlandia_foodcarts.jpg",
                "http://imgc.classistatic.com/cps/poc/130104/376r1/8728dl1_27.jpeg",
                
                };
        container.setUrls(testUrls, dipsToPx(MARGIN_DIPS), NUM_COLS);
    }
    
    private float dipsToPx(float dips)
    {
        return dips * _fScale;
    }
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_stag, menu);
        return true;
    }

}
