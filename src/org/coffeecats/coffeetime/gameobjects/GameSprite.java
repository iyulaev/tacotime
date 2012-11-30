/** GameSprite is the abstract class that defines the set of bitmaps comprising a 2D sprite
 * in our game. Sprites are used for things like CoffeeGirl, where we draw a set of sprites for
 * a given GameActor heading direction / moving? / holding? configuration.
 */

package org.coffeecats.coffeetime.gameobjects;

import java.util.TreeMap;

import org.coffeecats.coffeetime.gameobjects.fooditemdefs.*;
import org.coffeecats.coffeetime.utility.CircularList;
import org.coffeecats.coffeetime.utility.DirectionBitmapMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;

public abstract class GameSprite {
	protected DirectionBitmapMap hairBitmap;
	protected DirectionBitmapMap headBitmap;
	protected DirectionBitmapMap bodyBitmap;
	protected DirectionBitmapMap feetBitmap;
	protected DirectionBitmapMap handsBitmapHolding;
	protected DirectionBitmapMap handsBitmapEmpty;
	//Map of held item name -> held item bitmap
	protected TreeMap< String, Bitmap > heldItemMap;
	
	//Represents the last time that we DREW this GameSprite
	protected long last_time_drawn;
	protected final long SPRITE_FRAME_PERIOD_MS = 250;
	
	
	public GameSprite(Context caller, boolean do_init_helditems) {
		last_time_drawn = -1L;
		
		
		//Load a map of held item name -> held item bitmap, for returning (and later drawing)
		//the item that the GameActor holds
		heldItemMap = new TreeMap<String, Bitmap>();
		
		if(do_init_helditems) {
		
			FoodItemBlendedDrink fbd = new FoodItemBlendedDrink(caller);
			heldItemMap.put(fbd.getName(), fbd.getBitmapActive());
			
			FoodItemCoffee fic = new FoodItemCoffee(caller);
			heldItemMap.put(fic.getName(), fic.getBitmapActive());
			
			FoodItemCupcake ficc = new FoodItemCupcake(caller);
			heldItemMap.put(ficc.getName(), ficc.getBitmapActive());
			
			FoodItemEspresso fio = new FoodItemEspresso(caller);
			heldItemMap.put(fio.getName(), fio.getBitmapActive());
			
			FoodItemPieSlice fips = new FoodItemPieSlice(caller);
			heldItemMap.put(fips.getName(), fips.getBitmapActive());
			
			FoodItemSandwich fis = new FoodItemSandwich(caller);
			heldItemMap.put(fis.getName(), fis.getBitmapActive());
			
			FoodItemNothing fin = new FoodItemNothing(caller);
			heldItemMap.put(fin.getName(), fin.getBitmapActive());
			
		}
	}
	
	public GameSprite(Context caller) {
		this(caller, true);
	}
	
	
	/** Responsible for initializing all of the data structures associated with this GameSprite, i.e.
	 * the various DirectionBitmapMaps that hold all of the bitmaps used to render the sprite.
	 * 
	 */
	protected abstract void initSprite(Context caller);
	
	/** Return the appropriate Bitmap from a given CircularList, depending on whether the character
	 * is moving (and hence whether it should be animated).
	 * @param is_moving Whether the character is currently moving (and hence the bitmap should be animated)
	 * @param bmpList The CircularList of Bitmaps that corresponds to the appropriate player feature and heading
	 * @return
	 */
	protected Bitmap getAppropriateBitmap(boolean is_moving, CircularList<Bitmap> bmpList) {
		if(!is_moving) {
			return (bmpList.getFirst());
		} else if(SystemClock.uptimeMillis() > last_time_drawn + SPRITE_FRAME_PERIOD_MS ) {
			last_time_drawn = SystemClock.uptimeMillis();
			return (bmpList.getNext());
		} else {
			return (bmpList.getCurrent());
		}
	}
	
	/** Gets the Bitmap for the GameActor's "head", given an appropriate heading vector. */
	public Bitmap getHeadBitmap(int vector_x, int vector_y) {
		CircularList<Bitmap> headList = headBitmap.getDirectionList(vector_x, vector_y);
		return(getAppropriateBitmap(vector_x==0 && vector_y==0, headList));
	}
	
	/** Gets the Bitmap for the GameActor's "body", given an appropriate heading vector. */
	public Bitmap getBodyBitmap(int vector_x, int vector_y) {
		CircularList<Bitmap> bodyList = bodyBitmap.getDirectionList(vector_x, vector_y);
		return(getAppropriateBitmap(!(vector_x==0 && vector_y==0), bodyList));
	}
	
	/** Gets the Bitmap for the GameActor's "hair", given an appropriate heading vector. */
	public Bitmap getHairBitmap(int vector_x, int vector_y) {
		CircularList<Bitmap> hairList = hairBitmap.getDirectionList(vector_x, vector_y);
		return(getAppropriateBitmap(vector_x==0 && vector_y==0, hairList));
	}
	
	/** Gets the Bitmap for the GameActor's "feet", given an appropriate heading vector. */
	public Bitmap getFeetBitmap(int vector_x, int vector_y) {
		CircularList<Bitmap> feetList = feetBitmap.getDirectionList(vector_x, vector_y);
		return(getAppropriateBitmap(vector_x==0 && vector_y==0, feetList));
	}
	
	/** Gets the Bitmap for the GameActor's "hands", given an appropriate heading vector, and
	 * a count of how many items are held. Currently we only care if the number of held items is
	 * non-zero. */
	public Bitmap getHandsBitmap(int vector_x, int vector_y, int held_items) {
		CircularList<Bitmap> handList;
		if(held_items > 0) handList = handsBitmapHolding.getDirectionList(vector_x, vector_y);
		else handList = handsBitmapEmpty.getDirectionList(vector_x, vector_y);
		return(getAppropriateBitmap(vector_x==0 && vector_y==0, handList));
	}
	
	/** Gets the appropriate Bitmap for the GameFoodItem that is held by the GameActor. Requires 
	 * that the name of the GameFoodItem be provided.
	 */
	public Bitmap getHeldItemBitmap(String itemHoldingName) {
		return(heldItemMap.get(itemHoldingName));
	}
}
