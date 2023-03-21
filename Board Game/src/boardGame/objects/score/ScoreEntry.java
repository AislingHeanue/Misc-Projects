package boardGame.objects.score;

import java.io.Serializable;

/**
 * Simple class which contains an entry for a scoreboard.
 */
public class ScoreEntry implements Comparable<ScoreEntry>, Serializable {

    /**
     * The player's score.
     */
    public int score;
    /**
     * Name of the player.
     */
    public String name;


    public ScoreEntry(String n,int s){
        name = n;
        score = s;
    }

    /**
     * Compares two score entries by their scores and returns the opposite answer to if they were compared normally. This is
     * done to make Collections.sort() sort in descending order.
     *
     * @param o the object to be compared.
     * @return 1 if score is less than the score of the entry it is comparing to, 0 if they are equal, -1 if score is larger
     */
    @Override
    public int compareTo(ScoreEntry o) {
        return -Integer.compare(score,o.score);
    }
}
