package boardGame.objects.score;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;


/**
 * The object that gets stored in Scoreboard.ser. It is a list of ScoreEntry objects which sorts itself by descending order of score each time an entry is added or updated.
 */
public class Scoreboard implements Serializable {
    /**
     * The list which contains each of the {@link ScoreEntry} objects.
     */
    public ArrayList<ScoreEntry> sortedScores;

    /**
     * Constructor sets {@link #sortedScores} to be an empty ArrayList.
     */
    public Scoreboard() {
        sortedScores = new ArrayList<>();
    }


    /**
     * If no argument is given for score, one point is added to this entry.
     *
     * @param name Name of the player whose score is being incremented.
     */
    public void addEntry(String name) {
        addEntry(name,1);
    }

    public void removeEntry(String name) {
        sortedScores.removeIf(scoreEntry -> scoreEntry.name.equals(name));
    }


    /**
     * Scans the list of scores for one whose name matches the name given and if none exist, a new {@link ScoreEntry} with score of n.
     * Otherwise, n is added to the existing entry with this name.
     *
     * @param name Name of the player whose score is being incremented.
     * @param n Amount of points to add.
     */
    public void addEntry(String name,int n) {
        if (sortedScores.stream().noneMatch(scoreEntry -> scoreEntry.name.equals(name))) {
            ScoreEntry entry = new ScoreEntry(name,n);
            sortedScores.add(entry);
        } else {
            sortedScores.stream().filter(
                    scoreEntry ->
                            scoreEntry.name.equals(name)).findFirst().orElse(new ScoreEntry("broken score",0)).score += n;
        }

        Collections.sort(sortedScores);
    }
}
