package org.coffeecats.coffeetime.gameobjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.coffeecats.coffeetime.R;
import org.coffeecats.coffeetime.utility.CircularList;
import org.coffeecats.coffeetime.utility.DirectionBitmapMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CustomerSprite extends GameSprite {
	
	private final int default_direction = DirectionBitmapMap.DIRECTION_SOUTH;
	
	//Set to true when all of the (static, class-shared) bitmaps have been loaded
	static boolean static_init_done = false;
	
	static ArrayList<DirectionBitmapMap> girlHairDBMs;
	static ArrayList<DirectionBitmapMap> girlBodyDBMs;
	static ArrayList<DirectionBitmapMap> girlArmDBMs;

	static ArrayList<DirectionBitmapMap> boyHairDBMs;
	static ArrayList<DirectionBitmapMap> boyBodyDBMs;
	static DirectionBitmapMap boyArmDBM;
	
	//head and feet are gender-neutral
	static DirectionBitmapMap headDBM;
	static DirectionBitmapMap feetDBM;
	
	static long initialized_customer_sprites; //this exists just to help us randomize things more
	
	public CustomerSprite(Context caller) {
		super(caller, false);
		initSprite(caller);
	}

	@Override
	protected void initSprite(Context caller) {
		initSpriteStatic(caller);
		
		//Figure out which DBMs we are to use for this particular CustomerSprite instance
		Random random = new Random(System.currentTimeMillis() + initialized_customer_sprites);
		int gender = random.nextInt(2);
		
		//Girl
		if(gender == 0) {
			int hair = random.nextInt(4);
			this.hairBitmap = girlHairDBMs.get(hair);
			
			int body = random.nextInt(3);
			this.bodyBitmap = girlBodyDBMs.get(body);
			
			//if the kimono body is chosen, select the appropriate arm bitmaps
			if(body == 2) {
				this.handsBitmapEmpty = girlArmDBMs.get(0); 
			} else {
				this.handsBitmapEmpty = girlArmDBMs.get(1); 
			}
		} else { //boy
			int hair = random.nextInt(5);
			this.hairBitmap = boyHairDBMs.get(hair);
			
			int body = random.nextInt(4);
			this.bodyBitmap = boyBodyDBMs.get(body);
			
			this.handsBitmapEmpty = boyArmDBM; 	
		}
		
		headBitmap = headDBM;
		feetBitmap = feetDBM;
	}
	
	/** Perform static initialization of the various lists of hair/body/arms/etc that will
	 * be shared amount all instances of CustomerSprites
	 * @param caller
	 */
	protected synchronized void initSpriteStatic(Context caller) {
		if(static_init_done) {
			initialized_customer_sprites += 439l; //just increment it by a large prime...
			return;
		} else {
			initialized_customer_sprites = 0l;
		}
		
		Bitmap tempBitmap;
		
		//initialize all of the lists of things
		
		girlHairDBMs = new ArrayList<DirectionBitmapMap>();
		
		DirectionBitmapMap hairBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_brown_bun_north);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_brown_hair_south);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_black_bun_east);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_black_bun_west);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		girlHairDBMs.add(hairBitmap);
		
		hairBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_black_bun_north);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_black_hair_south);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_black_bun_east);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_black_bun_west);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		girlHairDBMs.add(hairBitmap);
		
		hairBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_brown_hair_north);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_brown_hair_south);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_brown_hair_east);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_brown_hair_west);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		girlHairDBMs.add(hairBitmap);
		
		hairBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_black_hair_north);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_black_hair_south);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_black_hair_east);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_black_hair_west);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		girlHairDBMs.add(hairBitmap);
		
		
		
		
		girlBodyDBMs = new ArrayList<DirectionBitmapMap>();
		
		DirectionBitmapMap bodyBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_businesswoman_body_north);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_businesswoman_body_south);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_businesswoman_body_east);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		girlBodyDBMs.add(bodyBitmap);
		
		bodyBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_dress_north_south);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_dress_east_west);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		girlBodyDBMs.add(bodyBitmap);
		
		
		bodyBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_kimono_body_north);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_kimono_body_south);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_kimono_body_east_west);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		girlBodyDBMs.add(bodyBitmap);
		
		
		
		girlArmDBMs = new ArrayList<DirectionBitmapMap>();
		
		DirectionBitmapMap armBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_girl_kimonoarms_south);
		armBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		armBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		armBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		armBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		girlArmDBMs.add(armBitmap);
		
		armBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_hand_front_empty);
		armBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		armBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		armBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		armBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		girlArmDBMs.add(armBitmap);
		
		
		//Boy bitmaps load
		
		boyHairDBMs = new ArrayList<DirectionBitmapMap>();
		
		hairBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_businessman_hair_north);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_businessman_hair_south);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_boy_east_hair);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_boy_west_hair);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		boyHairDBMs.add(hairBitmap);
		
		hairBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_businessman_hair_brown_north);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_businessman_hair_brown_south);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_businessman_hair_brown_east);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_businessman_hair_brown_west);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		boyHairDBMs.add(hairBitmap);
		
		hairBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_businessman_hair_north);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_combover_hair_black_south);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_boy_east_hair);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_boy_west_hair);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		boyHairDBMs.add(hairBitmap);
		
		hairBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_businessman_hair_north);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_professor_hair_south);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_boy_east_hair);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_boy_west_hair);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		boyHairDBMs.add(hairBitmap);
		
		hairBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_ox_hair_north_south);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_ox_hair_north_south);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_boy_east_hair);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_boy_west_hair);
		hairBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		boyHairDBMs.add(hairBitmap);
		
		
		
		
		boyBodyDBMs = new ArrayList<DirectionBitmapMap>();
		
		bodyBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_hakama_body_north);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_hakama_body_south);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_hakama_body_east_west);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		boyBodyDBMs.add(bodyBitmap);
		
		bodyBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_kungfu_body_north);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_kungfu_body_south);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_kungfu_body_east_west);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		boyBodyDBMs.add(bodyBitmap);
		
		bodyBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_hakama_body_north);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_hakama_body_south);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_hakama_body_east_west);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		boyBodyDBMs.add(bodyBitmap);
		
		bodyBitmap = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_businessman_body_north);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_businessman_body_south);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_businessman_body_east);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_boy_businessman_body_west);
		bodyBitmap.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		boyBodyDBMs.add(bodyBitmap);
		
		
				
		boyArmDBM = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_all_arms);
		boyArmDBM.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		boyArmDBM.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		boyArmDBM.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		boyArmDBM.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		
		//Gender-neutral bitmaps load

		
		
		headDBM = new DirectionBitmapMap(true, default_direction);
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_both_head_north);
		headDBM.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_both_head_south);
		headDBM.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_both_head_east);
		headDBM.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(1,tempBitmap));
		tempBitmap = BitmapFactory.decodeResource(caller.getResources(), R.drawable.customer_both_head_west);
		headDBM.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(1,tempBitmap));
		
		feetDBM = new DirectionBitmapMap(true, default_direction);
		CircularList<Bitmap> northSouthList = new CircularList<Bitmap>( Arrays.asList(
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_feet_northsouth_f0),
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_feet_northsouth_f1),
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_feet_northsouth_f2)
			));
		feetDBM.setDirectionList(DirectionBitmapMap.DIRECTION_NORTH, northSouthList);
		feetDBM.setDirectionList(DirectionBitmapMap.DIRECTION_SOUTH, northSouthList);
		feetDBM.setDirectionList(DirectionBitmapMap.DIRECTION_EAST, new CircularList<Bitmap>(Arrays.asList(
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_feet_eastwest_f0),
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_feet_east_f1)
			)));
		feetDBM.setDirectionList(DirectionBitmapMap.DIRECTION_WEST, new CircularList<Bitmap>(Arrays.asList(
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_feet_eastwest_f0),
				BitmapFactory.decodeResource(caller.getResources(), R.drawable.coffeegirl_both_feet_west_f1)
			)));
		
		static_init_done = true;
	}

}
