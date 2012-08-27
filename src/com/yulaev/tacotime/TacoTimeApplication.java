package com.yulaev.tacotime;
import android.app.Application;

/**
 * Singleton pattern for storing global variables. See 
 * http://androidcookbook.com/Recipe.seam?recipeId=1218 and 
 * http://inchoo.net/mobile-development/android-development/android-global-variables/.
 * 
 * Not really used for anything, we use GameInfo to store most of the game state anyway.
 *
 */
public class TacoTimeApplication extends Application {

    private static TacoTimeApplication sInstance;

    //public static SessionHandler sharedSession;

    public static TacoTimeApplication getInstance() {
      return sInstance;
    }

    @Override
    public void onCreate() {
      super.onCreate();  
      sInstance = this;
    }

}
