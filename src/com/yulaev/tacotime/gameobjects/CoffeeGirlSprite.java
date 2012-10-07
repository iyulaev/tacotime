/** This class implements an instance of GameSprite. It loads all of the relevant
 * Bitmap resources to correctly render a CoffeeGirl (female) GameActor. 
 * 
 * For specific method documentation, please see the parent class GameSprite.
 */

package com.yulaev.tacotime.gameobjects;

import java.util.Arrays;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yulaev.tacotime.R;
import com.yulaev.tacotime.utility.CircularList;
import com.yulaev.tacotime.utility.DirectionBitmapMap;

public class CoffeeGirlSprite extends GameSprite {
	
	private final int default_direction = DirectionBitmapMap.DIRECTION_SOUTH;
	
	public CoffeeGirlSprite(Context caller) {
		super(caller);
		initSprite(caller);
	}

	/** initSprite() will populate all of the GameSprite data structures with Bitmaps that correspond to particular 
	 * GameActor headings and states. It is required before we cna render anything from this GameSprite instance.
	 */
	@Override
	protected void initSprite(Context caller) {
		Bitmap tempBitmap;
		
		hairBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_girl_north_hair);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_girl_south_hair);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_girl_east_hair);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_girl_west_hair);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
				
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_head_all);
		headBitmap = new DirectionBitmapMap(new CircularList<Bitmap>(1,tempBitmap));
		
		bodyBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_body_north);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_body_south);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_body_east);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_body_west);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		
		feetBitmap = new DirectionBitmapMap(true, default_direction);
		CircularList<Bitmap> northSouthList = new CircularList<Bitmap>( Arrays.asList(
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_feet_northsouth_f0),
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_feet_northsouth_f1),
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_feet_northsouth_f2)
			));
		feetBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, northSouthList);
		feetBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, northSouthList);
		feetBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(Arrays.asList(
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_feet_eastwest_f0),
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_feet_east_f1)
			)));
		feetBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(Arrays.asList(
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_feet_eastwest_f0),
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_feet_west_f1)
			)));
		
		handsBitmapEmpty = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_hand_front_empty);
		handsBitmapEmpty.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_hand_east_empty);
		handsBitmapEmpty.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_hand_west_empty);
		handsBitmapEmpty.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));

		handsBitmapEmpty.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1));
		
		handsBitmapHolding = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_hand_front_holding);
		handsBitmapHolding.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_hand_east_holding);
		handsBitmapHolding.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_hand_west_holding);
		handsBitmapHolding.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));	
		
		handsBitmapHolding.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1));
	}

}
