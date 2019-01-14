public class StoneOfHearth extends BaseGame
{
    public void create() 
    {  
        // initialize resources common to multiple screens
        
        // load game screen
        GameScreen gs = new GameScreen(this);
        setScreen( gs );
    }
}
