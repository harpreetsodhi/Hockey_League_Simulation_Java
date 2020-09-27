package g4dhl;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class GameTest {

    @Test
    public void setLeaguesTest(){
        Game game = new Game();
        ArrayList<ILeague> leagues = new ArrayList<>();
        game.setLeagues(leagues);
        Assert.assertEquals(leagues, game.getLeagues());
    }
}