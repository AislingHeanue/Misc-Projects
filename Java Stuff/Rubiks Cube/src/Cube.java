public interface Cube {
    /**
     * Interface for the rubik's cube, should define all operations possible on it, as well as the return types
     */
    void setNextMoveIsWide(boolean wide);
    enum Turn {CLOCKWISE, ANTICLOCKWISE, TWO, NONE}

    enum Colour {YELLOW, WHITE, ORANGE, RED, GREEN, BLUE,BLACK}

    void Up(Turn turn);

    void Down(Turn turn);

    void Left(Turn turn);

    void Right(Turn turn);

    void Front(Turn turn);

    void Back(Turn turn);

    void y(Turn turn);

    void x(Turn turn);

    void z(Turn turn);

    void M(Turn turn);

    void E(Turn turn);

    void S(Turn turn);
    String shuffle(boolean makeSolution,int shuffleLength);

    void doAlgorithm(String input);

    void reset();

    Turn inverseTurn(Turn turn);
    Colour getFaceColour(int x, int y, int z, int face);


}
