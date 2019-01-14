import com.badlogic.gdx.graphics.Texture;

public class Card extends BaseActor {

	private String name; 
	private int attack; 
	private int health; 
	private int mana; 
	private BaseActor texture; 
	public float offsetX;
	public float offsetY;
	public float originalX;
	public float originalY;
	public boolean dragable; 
	public boolean placed; 

	public Card (String name1, int attk, int hlth, int mana1) {
		super (); 
		name = name1; 
		attack = attk; 
		health = hlth; 
		mana = mana1; 
		dragable = true;
		placed = false; 
	}

	public String getName () {
		return name; 
	}

	public int getAttack () {
		return attack; 
	}

	public int getHealth () {
		return health; 
	}

	public int getMana () {
		return mana;
	}

	public BaseActor getTexture () {
		return texture; 
	}
	
	public void setHealth (int newHealth) {
		health = newHealth; 
	}

	public void setAttack (int newAttack) {
		attack = newAttack;
	}
}
