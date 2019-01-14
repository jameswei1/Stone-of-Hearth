import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import java.util.ArrayList;
import java.util.Collections;

public class GameScreen extends BaseScreen
{
	private BaseActor background;

	private int hp; 
	private int AIhp; 

	private Card card; 
	public ArrayList<Card> cardList1;
	public ArrayList<Card> cardList2;

	private ArrayList<Card> AIList; 
	private ArrayList<Card> AIPlayed; 

	private ArrayList<Card> playedCards; 

	private ArrayList <BaseActor> rectangles;
	private BaseActor rect; 
	private float hintTimer;

	private boolean turn; 
	private boolean begin; 

	// game world dimensions
	final int mapWidth = 800;
	final int mapHeight = 600;

	public GameScreen(BaseGame g)
	{
		super(g);
	}

	public void create() 
	{   
		turn = false; 
		background = new BaseActor();
		background.setTexture( new Texture(Gdx.files.internal("ASSets/background1.jpg")) );
		mainStage.addActor(background);
		rectangles = new ArrayList <BaseActor> (10); 

		playedCards = new ArrayList <Card> (5); 
		//Adds rectangles 
		for (int i = 0; i < 10; i++) {
			BaseActor rect = new BaseActor();
			rect.setTexture(  new Texture(Gdx.files.internal("ASSets/Rectangle.png"))  );
			rect.setWidth(95);
			rect.setHeight(141);
			rect.setOriginCenter();
			if (i < 5) {
				rect.setPosition(311 + 97*i, 231);
			}
			else {
				rect.setPosition(311 + 97*(i-5), 376);
			}
			rect.setRectangleBoundary();
			rectangles.add( rect );
			mainStage.addActor( rect );
		}
		//One card list for the cards on the right
		//One card list for the cards on the left
		cardList1 = new ArrayList<Card>(20);
		cardList2 = new ArrayList<Card>(20);
		

		//Names of cards (can be used in file names) 
		String [] cardNames = {"Firestorm", "Food", "Godzilla", "Karken", "Raptor", "Vampire", "Viserion", "Werepire", "Werewolf", "Ogre"};
		String [] cardNamesAI = {"Blizzard", "Dwarf", "Goblin", "Gul'dan", "Hulk-1", "Maiev", "Malfurion", "Racer", "Thrall-1", "Witch"};
		int [] attacks = {1,1,1,3,3,2,7,4,2,5};
		int [] health = {3,1,4,3,2,2,6,7,5,3};
		int [] attack = {1,4,2,3,1,3,4,4,3,3}; //ai
		int [] healths = {7,4,4,3,5,3,2,4,1,5}; //ai cards
		//AIs Cards
		AIList = new ArrayList<Card>(10);
		AIPlayed = new ArrayList <Card> (5);

		//For loop that adds cards to the AI's List of cards
		for (int i = 0; i < cardNamesAI.length; i++) {
			//Creating a temp card
			Card card = new Card(cardNamesAI[i], attack[i], healths[i], 1);
			//Using card name as file name
			String fileName = "ASSets/" + cardNamesAI[i] + ".jpg";
			card.setTexture( new Texture(Gdx.files.internal(fileName)) );
			card.setWidth(90);
			card.setHeight(135);
			card.setOriginCenter();
			card.setRectangleBoundary();
			//Adds the temp card to the list 
			AIList.add(card);
		}
		//Randomizes list
		Collections.shuffle(AIList);

		//Adds cards to the right side of the hand
		for (int k = 0; k < 10; k++) {
			int i = 0; 
			if (k<5) {
				i = k; 
			}
			else {
				i = k-5; 
			}
			//Attachs the attack and health arraylist index to the cardNames arrayList index  
			Card card = new Card(cardNames[i], attacks[i], health[i], 1);
			String fileName = "ASSets/" + cardNames[i] + ".jpg";
			card.setTexture( new Texture(Gdx.files.internal(fileName)) );
			card.setWidth(90);
			card.setHeight(135);
			card.setOriginCenter();
			card.setRectangleBoundary();

			//Attachs the addListener functionality to card
			card.addListener(
					new InputListener() 
					{
						public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) 
						{  
							if (!card.dragable)
								return false;

							card.setZIndex(1000); // render currently dragged card on top
							card.offsetX = x;
							card.offsetY = y;
							card.originalX = event.getStageX();
							card.originalY = event.getStageY();
							return true; // T = call other methods
						}

						public void touchDragged(InputEvent event, float x, float y, int pointer) 
						{
							if (!card.dragable)
								return;

							card.moveBy(x - card.offsetX, y - card.offsetY);
						}

						public void touchUp(InputEvent event, float x, float y, int pointer, int button) 
						{  
							boolean placed = false;
							//If the card is over a rectangle
							if (!card.placed) {
								for (BaseActor rect : rectangles)
								{
									if ( card.overlaps(rect, false) )
									{
										placed = true;
										float targetX = rectangles.get(playedCards.size()).getX() + rectangles.get(playedCards.size()).getOriginX() - card.getOriginX();
										float targetY = rectangles.get(playedCards.size()).getY() + rectangles.get(playedCards.size()).getOriginY() - card.getOriginY();

										card.dragable = false;
										card.addAction( Actions.moveTo( targetX, targetY, 0.5f ) );
										card.placed = true; 
										playedCards.add(card);
										cardList1.remove(card);
										battleCry (card, 0);
										for (int i = 0; i < playedCards.size(); i++) {
											playedCards.get(i).dragable = false; 
										}
										cardList1.get(0).dragable=false;
										cardList2.get(0).dragable=false;
										return;
									}
								}
							}
							else {
								for (Card card1: AIPlayed) {
									if (card.overlaps(card1, false)) {
										placed = true; 
										card.dragable = false; 
										card.addAction( Actions.moveTo( 
												card.originalX - card.offsetX, card.originalY - card.offsetY, 0.5f ) );
										playerAttack(card, card1);
										for (int i = 0; i < playedCards.size(); i++) {
											playedCards.get(i).dragable = false; 
										}
										cardList1.get(0).dragable=false;
										cardList2.get(0).dragable=false;
										return; 
									}
								}
							}
						//if the cards is placed in the ai pile the roar runs, 	the card returns to its original position and makes it not dragable 

							if (!placed) // overlapping but not right one, move off the pile
								card.addAction( Actions.moveTo( 
										card.originalX - card.offsetX, card.originalY - card.offsetY, 0.5f ) );

							if ( card.getX() < 0 ) {
								card.addAction( Actions.moveTo( 
										card.originalX - card.offsetX, card.originalY - card.offsetY ) );
							}
							if ( card.getX() + card.getWidth() > mapWidth ) {
								card.setX(mapWidth - card.getWidth());
							}
							if ( card.getY() < 0 ) {
								card.setY(0);
							}
							if ( card.getY() + card.getHeight() > mapHeight )
							{
								card.setY(mapHeight - card.getHeight());
							}
						}

					});

			card.setZIndex(5); // cards created later should render earlier (on bottom)
			cardList1.add(card);
		}
		//Randomizes list
		Collections.shuffle(cardList1); 
		//Gives all the cards position to where they spawn 
		for (int i = 0; i < cardList1.size(); i++) {
			cardList1.get(i).setPosition(0, 10);
		}

		//Same thing as last for loop but with second array list of cards
		for (int k = 0; k < 10; k++) {
			int i = 0; 
			if (k<5) {
				i = k; 
			}
			else {
				i = k-5; 
			}
			Card card = new Card(cardNames[i+5], attacks[i+5], health[i+5], 1);
			String fileName = "ASSets/" + cardNames[i+5] + ".jpg";
			card.setTexture( new Texture(Gdx.files.internal(fileName)) );
			card.setWidth(90);
			card.setHeight(135);
			card.setOriginCenter();
			card.setRectangleBoundary();


			card.addListener(
					new InputListener() 
					{
						public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) 
						{  
							if (!card.dragable)
								return false;

							card.setZIndex(1000); // render currently dragged card on top
							card.offsetX = x;
							card.offsetY = y;
							card.originalX = event.getStageX();
							card.originalY = event.getStageY();
							return true; // T = call other methods
						}

						public void touchDragged(InputEvent event, float x, float y, int pointer) 
						{
							if (!card.dragable)
								return;

							card.moveBy(x - card.offsetX, y - card.offsetY);
						}

						public void touchUp(InputEvent event, float x, float y, int pointer, int button) 
						{  
							boolean placed = false;
							if (!card.placed) {
								for (BaseActor rect : rectangles)
								{
									if ( card.overlaps(rect, false) )
									{
										placed = true;
										float targetX = rectangles.get(playedCards.size()).getX() + rectangles.get(playedCards.size()).getOriginX() - card.getOriginX();
										float targetY = rectangles.get(playedCards.size()).getY() + rectangles.get(playedCards.size()).getOriginY() - card.getOriginY();
										card.dragable = false;
										card.addAction( Actions.moveTo( targetX, targetY, 0.1f ) );
										card.placed = true; 
										playedCards.add(card);
										cardList2.remove(card);
										battleCry (card, 0);
										for (int i = 0; i < playedCards.size(); i++) {
											playedCards.get(i).dragable = false; 
										}
										cardList1.get(0).dragable=false;
										cardList2.get(0).dragable=false;
										return;
									}
								}
							}
							else {
								for (Card card1: AIPlayed) {
									if (card.overlaps(card1, false)) {
										placed = true; 
										card.dragable = false; 
										card.addAction( Actions.moveTo( 
												card.originalX - card.offsetX, card.originalY - card.offsetY, 0.5f ) );
										playerAttack(card, card1);
										for (int i = 0; i < playedCards.size(); i++) {
											playedCards.get(i).dragable = false; 
										}
										cardList1.get(0).dragable=false;
										cardList2.get(0).dragable=false;
										return; 
									}
								}
							}

							if (!placed) { // overlapping but not right one, move off the pile
								card.addAction( Actions.moveTo( 
										card.originalX - card.offsetX, card.originalY - card.offsetY, 0.5f ) );
								return;
							}
						}
					});

			card.setZIndex(5); // cards created later should render earlier (on bottom)
			cardList2.add(card);
		}
		Collections.shuffle(cardList2); 
		for (int i = 0; i < cardList2.size(); i++) {
			cardList2.get(i).setPosition(100, 10);
		}
		 

	}

	public void update(float dt) 
	{
		//Shows the top two cards in both the players list 
		mainStage.addActor(cardList1.get(0));
		mainStage.addActor(cardList2.get(0));

		for (int i = 0; i < AIPlayed.size(); i++) {
			mainStage.addActor(AIPlayed.get(i));
		}

		if (turn) {

		}
		else {
			AITurn(); 
		}

	}

	public void AIattack (Card off, Card def) {
		//Used for animating attack 
		//These variables are so the program knows where to return the card
		float returnX = off.getX(); 
		float returnY = off.getY();
		//Variables of the card being attacked 
		float targetCardX = def.getX(); 
		float targetCardY= def.getY(); 
		//Attacks in animation and then returns. It's a sequence. 
		off.addAction(Actions.sequence(Actions.moveTo(targetCardX,targetCardY, 0.5f),Actions.moveTo(returnX,returnY, 0.5f)));

		//Reduces health accordingly 
		def.setHealth(def.getHealth() - off.getAttack());

		//If health is below 0 
		if (def.getHealth() <=0 ) {
			//Gets rid of the card that got attacked
			def.addAction(Actions.fadeOut(0.1f));
			//Removes from playlist
			playedCards.remove(def);
			//Removes from screen
			def.remove();
			//Shifts all cards to the right
			for (int i = 0; i < playedCards.size(); i++) {
				float targetX = 311+97*i;
				playedCards.get(i).addAction( Actions.moveTo( targetX, playedCards.get(i).getY(), 0.5f ) );
			}
		}
	}

	public void playerAttack (Card off, Card def) {
		def.setHealth(def.getHealth() - off.getAttack());

		if (def.getHealth() <= 0) {
			//Gets rid of the card that got attacked
			def.addAction(Actions.fadeOut(0.1f));
			//Removes from list
			AIPlayed.remove(def);
			//Removes from screen
			def.remove();
			for (int i = 0; i < AIPlayed.size(); i++) {
				float targetX = 311+97*i;
				AIPlayed.get(i).addAction( Actions.moveTo( targetX, AIPlayed.get(i).getY(), 0.5f ) );
			}
		}
	}

//AI turn, Ai adding a card, Ai attack player 1's card	
	//if its the first turn ai places a card
	//if not then it attack randomly 
	public void AITurn () {
		if (AIPlayed.size() == 0) {
			AIList.get(0).setPosition(311 + 97*AIPlayed.size(), 376);
			AIPlayed.add(AIList.get(0));
			AIList.remove(0);
		}
		else {
			int attackOrPlay = MathUtils.random(1); 
			if (attackOrPlay == 0) {
				if (playedCards.size()>0) {
					int attackingCard = MathUtils.random(AIList.size()-1); 
					int defendingCard = MathUtils.random(playedCards.size()-1); 
					System.out.println("Card that got Attacked: " + playedCards.get(defendingCard).getName());
					AIattack (AIList.get(attackingCard), playedCards.get(defendingCard)); 
				}
			}
			else {
				AIList.get(0).setPosition(311 + 97*AIPlayed.size(), 376);
				AIPlayed.add(AIList.get(0));
				battleCry (AIList.get(0), 1);
				AIList.remove(0);

			}
		}
		for (int i = 0; i < playedCards.size(); i++) {
			playedCards.get(i).dragable = true; 
		}
		cardList1.get(0).dragable = true; 
		cardList2.get(0).dragable = true;
		for (int i = 0; i < playedCards.size(); i++) {
			if (playedCards.get(i).getName().equals("Viserion")) {
				playedCards.get(i).setAttack(playedCards.get(i).getAttack()-1);
				playedCards.get(i).setHealth(playedCards.get(i).getHealth()+2);
			}
			if (playedCards.get(i).getName().equals("Raptor")) {
				playedCards.get(i).setAttack(playedCards.get(i).getAttack()+2);
			}
		}
		turn = true;
	}
	public boolean keyDown(int keycode)
	{
		if (keycode == Keys.E) {    
			if (turn) {
				turn = false; 
				for (int i = 0; i < AIPlayed.size(); i++) {
					if (AIPlayed.get(i).getName().equals("Viserion")) {
						AIPlayed.get(i).setAttack(AIPlayed.get(i).getAttack()-1);
						AIPlayed.get(i).setHealth(AIPlayed.get(i).getHealth()+2);
					}
					if (AIPlayed.get(i).getName().equals("Raptor")) {
						AIPlayed.get(i).setAttack(AIPlayed.get(i).getAttack()+2);
					}
				}
			}
		}
		return false;
	}
	
	//The spiecal ablity for each card, occurs when its played

	public void battleCry (Card card, int k) {
		String name = card.getName();
		if (k == 0) {
			if (name.equals("Firestorm")) {
				for (int i = 0; i < AIPlayed.size(); i++) {
					AIPlayed.get(i).setHealth(AIPlayed.get(i).getHealth()-2);
				}
			}
			else if (name.equals("Food")) {
				for (int i = 0; i < playedCards.size(); i++) {
					if (playedCards.get(i).getName().equals("Ogre")) {
						playedCards.get(i).setHealth(playedCards.get(i).getHealth() + 2);
						playedCards.get(i).setAttack(playedCards.get(i).getAttack() + 2);
					}
				}
			}
			else if (name.equals("Godzilla")) {
				card.setAttack(card.getAttack()+2);
			}
			else if (name.equals("Karken")) {

			}
			else if (name.equals("Ogre")) {

			}
			else if (name.equals("Raptor")){
				AIhp-=2;
			}
			else if (name.equals("Viserion")) {
				card.setAttack(card.getAttack()-1);
				card.setHealth(card.getHealth()+2);
			}
			else if (name.equals("Werepire")) {
				for (int i = 0; i < AIPlayed.size(); i++) {
					if (AIPlayed.get(i).getName().equals("Vampire")) {
						AIPlayed.get(i).setHealth(-1);
					}
				}
				for (int i = 0; i < AIPlayed.size(); i++) {

					if (AIPlayed.get(i).getName().equals("Werewolf")) {
						AIPlayed.get(i).setHealth(-1);
					}
				}
			}
			else if (name.equals("Werewolf")) {
				hp-=2;
			}
			else if (name.equals("Vampire")) {
				hp-=2; 
			}
		}
		else {
			if (name.equals("Firestorm")) {
				for (int i = 0; i < playedCards.size(); i++) {
					playedCards.get(i).setHealth(playedCards.get(i).getHealth()-2);
				}
			}
			else if (name.equals("Food")) {
				for (int i = 0; i < AIPlayed.size(); i++) {
					if (AIPlayed.get(i).getName().equals("Ogre")) {
						AIPlayed.get(i).setHealth(AIPlayed.get(i).getHealth() + 2);
						AIPlayed.get(i).setAttack(AIPlayed.get(i).getAttack() + 2);
					}
				}
			}
			else if (name.equals("Godzilla")) {
				card.setAttack(card.getAttack()+2);
			}
			else if (name.equals("Karken")) {

			}
			else if (name.equals("Ogre")) {

			}
			else if (name.equals("Raptor")){
				hp-=2; 
			}
			else if (name.equals("Viserion")) {
				card.setAttack(card.getAttack()-1);
				card.setHealth(card.getHealth()+2);
			}
			else if (name.equals("Werepire")) {
				for (int i = 0; i < playedCards.size(); i++) {
					if (playedCards.get(i).getName().equals("Vampire")) {
						playedCards.get(i).setHealth(-1);
					}
				}
				for (int i = 0; i < playedCards	.size(); i++) {

					if (playedCards.get(i).getName().equals("Werewolf")) {
						playedCards.get(i).setHealth(-1);
					}
				}
			}
			else if (name.equals("Werewolf")) {
				AIhp-=2;
			}
			else if (name.equals("Vampire")) {
				AIhp-=2; 
			}
		}
		int size = AIPlayed.size(); 
		ArrayList <Card> AIRemovedCards = new ArrayList <Card> (); 
		ArrayList <Card> playerRemovedCards = new ArrayList <Card> (); 
		for (int i = 0; i < size; i++) {
			if (AIPlayed.get(i).getHealth() <= 0) {
				AIRemovedCards.add(AIPlayed.get(i));
				//AIPlayed.get(i).remove();
				//AIPlayed.remove(i);
			}
		}
		size = playedCards.size(); 
		for (int i = 0; i < size; i++) {
			if (playedCards.get(i).getHealth() <= 0) {
				playerRemovedCards.add(playedCards.get(i));
				//playedCards.get(i).remove();
				//playedCards.remove(i);
			}
		}
		for (int i = 0; i < AIRemovedCards.size(); i++) {
			AIRemovedCards.get(i).remove();
			AIPlayed.remove(AIRemovedCards.get(i));
		}

		for (int i = 0; i < playerRemovedCards.size(); i++) {
			playerRemovedCards.get(i).remove();
			playedCards.remove(playerRemovedCards.get(i));
		}
		for (int i  = 0; i < AIPlayed.size(); i++) {
			float targetX = 311+97*i;
			AIPlayed.get(i).addAction( Actions.moveTo( targetX, 376, 0.5f ) );
		}
		for (int i  = 0; i < playedCards.size(); i++) {
			float targetX = 311+97*i;
			playedCards.get(i).addAction( Actions.moveTo( targetX, 231, 0.5f ) );
		}


	}
	
	public void gameOver() {
		if((cardList1.size() == 0 && cardList2.size() == 0) || AIList.size() == 0) {
			System.out.println("Game Over");
		}
	}
	
}