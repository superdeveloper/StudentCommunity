package com.ldceconnect.ldcecommunity.identicon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.ldceconnect.ldcecommunity.R;

public class IdenticonGenerator {
	public static int height = 5;
	public static int width = 5;
public WebView mWebView;
    private ViewGroup mContainer;
    public AppCompatActivity activity;
    public IdenticonGenerator(AppCompatActivity activity)
    {
        this.activity = activity;
    }
	public static Bitmap generate(String userName,
			HashGeneratorInterface hashGenerator) {
		
		byte[] hash = hashGenerator.generate(userName);

		Bitmap identicon = Bitmap.createBitmap(width, height, Config.ARGB_8888);

		// get byte values as unsigned ints
		int r = hash[0] & 255;
		int g = hash[1] & 255;
		int b = hash[2] & 255;

		int background = Color.parseColor("#00ffffff"); //transparent
		int foreground = Color.argb(130, r, g, b);

		for (int x = 0; x < width; x++) {
			
			//make identicon horizontally symmetrical 
			int i = x < 3 ? x : 4 - x;
			int pixelColor;
			for (int y = 0; y < height; y++) {
				
				if ((hash[i] >> y & 1) == 1)
					pixelColor = foreground;
				else
					pixelColor = background;

				identicon.setPixel(x, y, pixelColor);
			}
		}
		
		//scale image by 2 to add border
		Bitmap bmpWithBorder = Bitmap.createBitmap(12, 12, identicon.getConfig());
	    Canvas canvas = new Canvas(bmpWithBorder);
	    canvas.drawColor(background);
	    identicon = Bitmap.createScaledBitmap(identicon, 10, 10, false);
	    canvas.drawBitmap(identicon, 1, 1, null);
	    
		return bmpWithBorder;
	}

    public static Bitmap generatePlainColorBitmap(String colorCode)
    {
        int pixelColor = Color.parseColor(colorCode);

        Bitmap plainBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        for (int x = 0; x < width; x++) {

            //make identicon horizontally symmetrical
            int i = x < 3 ? x : 4 - x;

            for (int y = 0; y < height; y++) {

                plainBitmap.setPixel(x, y, pixelColor);
            }
        }

        return plainBitmap;

        /*Bitmap bmpWithBorder = Bitmap.createBitmap(12, 12, plainBitmap.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(pixelColor);
        plainBitmap = Bitmap.createScaledBitmap(plainBitmap, 10, 10, false);
        canvas.drawBitmap(plainBitmap, 1, 1, null);*/



    }
	/*public Bitmap generateIdenticon(String string)
    {
        mContainer = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.text_bubble, null);
        WebView v = (WebView)activity.findViewById(R.id.dummy_webview);
        v.loadUrl();

    }*/
}