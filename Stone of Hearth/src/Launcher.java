import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
public class Launcher {

	public static void main(String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		// change configuration settings
		config.width = 1000;
		config.height = 750;
		
		config.title = "Stone of Heart";
		
		StoneOfHearth myProgram = new StoneOfHearth();
		LwjglApplication launcher = new LwjglApplication(myProgram, config);
		
		//Function f = new Function();
		//f.turn();

	}

}
