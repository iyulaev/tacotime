package com.yulaev.tacotime;
import android.app.Application;

/**
 * Singleton pattern for storing global variables. See http://androidcookbook.com/Recipe.seam?recipeId=1218 and http://inchoo.net/mobile-development/android-development/android-global-variables/.
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
      sInstance.initializeInstance();
    }

    protected void initializeInstance() {
        // do all your initialization here
        //sessionHandler = new SessionHandler( 
        //    this.getSharedPreferences( "PREFS_PRIVATE", Context.MODE_PRIVATE ) );
    }
}