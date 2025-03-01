import java.util.*;

public class ArrayCube implements Cube{
    Colour[][][] faceColours;
    enum Band{TOP,BOTTOM,LEFT,RIGHT}
    Colour[] colourList;
    Map<Colour,Integer> colourIndex;
    int n;

    boolean nextMoveIsWide;

    public ArrayCube(int n) {
        this.n = n;
        nextMoveIsWide = false;
        faceColours = new Colour[6][n][n]; //change this line if we want to extend to larger cubes.
        //net view order is white,orange,green,red,blue,yellow
        colourList = new Colour[]{Colour.WHITE, Colour.ORANGE, Colour.GREEN, Colour.RED, Colour.BLUE, Colour.YELLOW};
        colourIndex = new HashMap<>();
        for (int face = 0; face < 6; face++) {
            colourIndex.put(colourList[face],face);
            for (int i = 0; i < n; i ++) {
                for (int j = 0; j < n; j++) {
                    faceColours[face][i][j] = colourList[face];
                }
            }
        }
    }

    public String emoji(Colour colour) {
        String[] emojiList = {"⬜\u200b","\uD83D\uDFE7","\uD83D\uDFE9","\uD83D\uDFE5","\uD83D\uDFE6","\uD83D\uDFE8"};
        return emojiList[colourIndex.get(colour)];
    }

    public String toString(){
        String[] rows = new String[9];
        rows[0] = String.format("%13s",String.format("%s%s%s",emoji(faceColours[0][0][0]),emoji(faceColours[0][0][1]),emoji(faceColours[0][0][2]))).replaceAll("\u200b","");
        rows[1] = String.format("%13s",String.format("%s%s%s",emoji(faceColours[0][1][0]),emoji(faceColours[0][1][1]),emoji(faceColours[0][1][2]))).replaceAll("\u200b","");
        rows[2] = String.format("%13s",String.format("%s%s%s",emoji(faceColours[0][2][0]),emoji(faceColours[0][2][1]),emoji(faceColours[0][2][2]))).replaceAll("\u200b","");
        rows[3] = String.format("%s%s%s %s%s%s %s%s%s %s%s%s",emoji(faceColours[1][0][0]),emoji(faceColours[1][0][1]),emoji(faceColours[1][0][2]),emoji(faceColours[2][0][0]),emoji(faceColours[2][0][1]),emoji(faceColours[2][0][2]),emoji(faceColours[3][0][0]),emoji(faceColours[3][0][1]),emoji(faceColours[3][0][2]),emoji(faceColours[4][0][0]),emoji(faceColours[4][0][1]),emoji(faceColours[4][0][2])).replaceAll("\u200b","");
        rows[4] = String.format("%s%s%s %s%s%s %s%s%s %s%s%s",emoji(faceColours[1][1][0]),emoji(faceColours[1][1][1]),emoji(faceColours[1][1][2]),emoji(faceColours[2][1][0]),emoji(faceColours[2][1][1]),emoji(faceColours[2][1][2]),emoji(faceColours[3][1][0]),emoji(faceColours[3][1][1]),emoji(faceColours[3][1][2]),emoji(faceColours[4][1][0]),emoji(faceColours[4][1][1]),emoji(faceColours[4][1][2])).replaceAll("\u200b","");
        rows[5] = String.format("%s%s%s %s%s%s %s%s%s %s%s%s",emoji(faceColours[1][2][0]),emoji(faceColours[1][2][1]),emoji(faceColours[1][2][2]),emoji(faceColours[2][2][0]),emoji(faceColours[2][2][1]),emoji(faceColours[2][2][2]),emoji(faceColours[3][2][0]),emoji(faceColours[3][2][1]),emoji(faceColours[3][2][2]),emoji(faceColours[4][2][0]),emoji(faceColours[4][2][1]),emoji(faceColours[4][2][2])).replaceAll("\u200b","");
        rows[6] = String.format("%13s",String.format("%s%s%s",emoji(faceColours[5][0][0]),emoji(faceColours[5][0][1]),emoji(faceColours[5][0][2]))).replaceAll("\u200b","");
        rows[7] = String.format("%13s",String.format("%s%s%s",emoji(faceColours[5][1][0]),emoji(faceColours[5][1][1]),emoji(faceColours[5][1][2]))).replaceAll("\u200b","");
        rows[8] = String.format("%14s",String.format("%s%s%s%n",emoji(faceColours[5][2][0]),emoji(faceColours[5][2][1]),emoji(faceColours[5][2][2]))).replaceAll("\u200b","");
        return String.join("\n",rows);
    }



    public static void main(String[] args) {
        Cube cube = new ArrayCube(3);
        System.out.println(cube.shuffle(false,20));
        System.out.println(cube);

    }

    private int getTurns(Turn turn) {
        //i do declare, U' = U3, and nobody can stop me
        switch(turn) {
            case CLOCKWISE:
                return 1;
            case ANTICLOCKWISE:
                return 3;
            case TWO:
                return 2;
        }
        return 0;
    }

    public void setNextMoveIsWide(boolean wide) {
        nextMoveIsWide = wide;
    }

    public Turn inverseTurn(Turn turn) {
        switch (turn) {
            case NONE:
                return Turn.NONE;
            case CLOCKWISE:
                return Turn.ANTICLOCKWISE;
            case ANTICLOCKWISE:
                return Turn.CLOCKWISE;
            case TWO:
                return Turn.TWO;
        }
        return null;
    }

    private void turnFace(Turn turn,int front,int[] sides, Band[] bands) {
        int turns = getTurns(turn);
        Colour buffer;
        int[][] bandCoordinates = new int[4*n][3];
        //turn the colours on the front face first
        turnFaceTiles(turn,front);
        //now turn the edges
        if (bands.length != 4) throw new RuntimeException("bands was the wrong size");
        for (int k = 0; k < turns; k ++) {
            //this block of code is conceptually annoying
            //when you rotate a side of a cube, there are 3 pieces on each of the attached sides which also move in a circuit of length 12, and for this step, I lay those pieces' coordinates out in the order they appear in the circuit.
            for (int j = 0; j < 4; j++) {
                switch (bands[j]) {
                    case TOP:
                        for (int l = 0; l < n; l++) {
                            bandCoordinates[n * j + l][0] = sides[j];
                            bandCoordinates[n * j + l][1] = 0;
                            bandCoordinates[n * j + l][2] = n - l - 1;
                        }
                        break;
                    case BOTTOM:
                        for (int l = 0; l < n; l++) {
                            bandCoordinates[n * j + l][0] = sides[j];
                            bandCoordinates[n * j + l][1] = n-1;
                            bandCoordinates[n * j + l][2] = l;
                        }
                        break;
                    case LEFT:
                        for (int l = 0; l < n; l++) {
                            bandCoordinates[n * j + l][0] = sides[j];
                            bandCoordinates[n * j + l][1] = l;
                            bandCoordinates[n * j + l][2] = 0;
                        }
                        break;
                    case RIGHT:
                        for (int l = 0; l < n; l++) {
                            bandCoordinates[n * j + l][0] = sides[j];
                            bandCoordinates[n * j + l][1] = n-l-1;
                            bandCoordinates[n * j + l][2] = n-1;
                        }
                        break;

                }
                checkWide(turn,front);
            }
            for (int i = 0; i < n; i++) {
                buffer = faceColours[bandCoordinates[i][0]][bandCoordinates[i][1]][bandCoordinates[i][2]];
                faceColours[bandCoordinates[i][0]][bandCoordinates[i][1]][bandCoordinates[i][2]] = faceColours[bandCoordinates[i+3*n][0]][bandCoordinates[i+3*n][1]][bandCoordinates[i+3*n][2]];
                faceColours[bandCoordinates[i+3*n][0]][bandCoordinates[i+3*n][1]][bandCoordinates[i+3*n][2]] = faceColours[bandCoordinates[i+2*n][0]][bandCoordinates[i+2*n][1]][bandCoordinates[i+2*n][2]];
                faceColours[bandCoordinates[i+2*n][0]][bandCoordinates[i+2*n][1]][bandCoordinates[i+2*n][2]] = faceColours[bandCoordinates[i+n][0]][bandCoordinates[i+n][1]][bandCoordinates[i+n][2]];
                faceColours[bandCoordinates[i+n][0]][bandCoordinates[i+n][1]][bandCoordinates[i+n][2]] = buffer;
            }
        }
    }


    public void doAlgorithm(String input) throws IllegalArgumentException {
        StringTokenizer st = new StringTokenizer(input," ");
        String next;
        Turn turn;
        while (st.hasMoreTokens()) {
            next = st.nextToken();
            switch (next.charAt(next.length()-1)) {
                case '2':
                    turn = Turn.TWO; break;
                case '\'':
                    turn = Turn.ANTICLOCKWISE; break;
                default:
                    turn = Turn.CLOCKWISE;
            }

            switch (next.charAt(0)) {
                case 'U': Up(turn); break;
                case 'D': Down(turn); break;
                case 'F': Front(turn); break;
                case 'B': Back(turn); break;
                case 'L': Left(turn); break;
                case 'R': Right(turn); break;
                case 'x': x(turn); break;
                case 'y': y(turn); break;
                case 'z': z(turn); break;
                case 'u': nextMoveIsWide = true; Up(turn); break;
                case 'r': nextMoveIsWide = true; Right(turn); break;
                case 'l': nextMoveIsWide = true; Left(turn); break;
                case 'f': nextMoveIsWide = true; Front(turn); break;
                case 'b': nextMoveIsWide = true; Back(turn); break;
                case 'd': nextMoveIsWide = true; Down(turn); break;
            }
        }
    }

    private void checkWide(Turn turn,int face) {
        if (nextMoveIsWide) {
            nextMoveIsWide = false;
            switch (face) {
                case 0: E(inverseTurn(turn)); break;
                case 1: M(turn); break;
                case 2: S(turn); break;
                case 3: M(inverseTurn(turn)); break;
                case 4: S(inverseTurn(turn)); break;
                case 5: E(turn); break;
            }
        }
    }

    public void Up(Turn turn) {
        int frontFace = 0;
        int[] sideFaces = new int[]{1,4,3,2};
        Band[] bands = new Band[]{Band.TOP,Band.TOP,Band.TOP,Band.TOP};
        turnFace(turn,frontFace,sideFaces,bands);
    }

    public void Down(Turn turn) {
        turnFace(turn,5,new int[]{1,2,3,4},new Band[]{Band.BOTTOM,Band.BOTTOM,Band.BOTTOM,Band.BOTTOM});
    }

    public void Left(Turn turn) {
        turnFace(turn,1,new int[]{0,2,5,4},new Band[]{Band.LEFT,Band.LEFT,Band.LEFT,Band.RIGHT});
    }

    public void Right(Turn turn) {
        turnFace(turn,3,new int[]{5,2,0,4},new Band[]{Band.RIGHT,Band.RIGHT,Band.RIGHT,Band.LEFT});
    }

    public void Front(Turn turn) {
        turnFace(turn,2,new int[]{1,0,3,5},new Band[]{Band.RIGHT,Band.BOTTOM,Band.LEFT,Band.TOP});
    }

    public void Back(Turn turn) {
        turnFace(turn,4,new int[]{5,3,0,1},new Band[]{Band.BOTTOM,Band.RIGHT,Band.TOP,Band.LEFT});
    }

    private void turnFaceTiles(Turn turn,int face) {
        Colour buffer;
        if (turn == Turn.ANTICLOCKWISE) {
            for (int i = 0; i < n - 1; i++) {
                buffer = faceColours[face][i][n-1];
                faceColours[face][i][n-1] = faceColours[face][n - 1][n-i-1];
                faceColours[face][n - 1][n-i-1] = faceColours[face][n - i - 1][0];
                faceColours[face][n - i - 1][0] = faceColours[face][0][i];
                faceColours[face][0][i] = buffer;
            }
        } else if (turn != Turn.NONE) {
            for (int i = 0; i < n - 1; i++) {
                buffer = faceColours[face][0][i];
                faceColours[face][0][i] = faceColours[face][n - i - 1][0];
                faceColours[face][n - i - 1][0] = faceColours[face][n - 1][n - i - 1];
                faceColours[face][n - 1][n - i - 1] = faceColours[face][i][n - 1];
                faceColours[face][i][n - 1] = buffer;
            }
            if (turn == Turn.TWO) turnFaceTiles(Turn.CLOCKWISE,face);
        }
    }

    private void turnCube(Turn turn, int front, int back, int[] sides,Turn[] rotations) {
        int turns = getTurns(turn);
        if (sides.length != 4 || rotations.length != 4) throw new RuntimeException("sides of rotations were the wrong size");
        Colour buffer;
        for (int k = 0; k < turns; k++) {
            turnFaceTiles(Turn.CLOCKWISE, front);
            turnFaceTiles(Turn.ANTICLOCKWISE, back);
            for (int i = 0; i < 4; i++) {
                turnFaceTiles(rotations[i], sides[i]);
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    buffer = faceColours[sides[0]][i][j];
                    faceColours[sides[0]][i][j] = faceColours[sides[3]][i][j];
                    faceColours[sides[3]][i][j] = faceColours[sides[2]][i][j];
                    faceColours[sides[2]][i][j] = faceColours[sides[1]][i][j];
                    faceColours[sides[1]][i][j] = buffer;
                }
            }

        }

    }
    public void x(Turn turn) {
        turnCube(turn,3,1,new int[]{2,0,4,5},new Turn[]{Turn.NONE,Turn.TWO,Turn.TWO,Turn.NONE});
    }
    public void y(Turn turn) {
        turnCube(turn,0,5,new int[]{1,4,3,2},new Turn[]{Turn.NONE,Turn.NONE,Turn.NONE,Turn.NONE});
    }
    public void z(Turn turn) {
        turnCube(turn,2,4,new int[]{1,0,3,5},new Turn[]{Turn.CLOCKWISE,Turn.CLOCKWISE,Turn.CLOCKWISE,Turn.CLOCKWISE});
    }

    public void M(Turn turn) {x(inverseTurn(turn));Left(inverseTurn(turn));Right(turn);}
    public void E(Turn turn) {y(inverseTurn(turn));Up(turn);Down(inverseTurn(turn));}
    public void S(Turn turn) {z(turn);Front(inverseTurn(turn));Back(turn);}


    public String turnByFaceNumber(int face,Turn turn) {
        switch (face) {
            case 0: Up(turn); return "U";
            case 1: Left(turn); return "L";
            case 2: Front(turn); return "F";
            case 3: Right(turn); return "R";
            case 4: Back(turn); return "B";
            case 5: Down(turn); return "D";
            default: return "";
        }
    }

    public void reset() {
        for (int face = 0; face < 6; face++) {
            colourIndex.put(colourList[face],face);
            for (int i = 0; i < n; i ++) {
                for (int j = 0; j < n; j++) {
                    faceColours[face][i][j] = colourList[face];
                }
            }
        }
    }

    public String shuffle(boolean giveSolution,int shuffleLength) {
        reset();
        Random r = new Random();
        ArrayList<String> history = new ArrayList<>();
        ArrayList<String> solution = new ArrayList<>();
        Turn[] turnTypes = new Turn[]{Turn.CLOCKWISE,Turn.ANTICLOCKWISE,Turn.TWO};
        String returnString;
        int lastMoved = -1;
        int num1,num2;
        for (int i = 0; i < shuffleLength; i++) {
            num1 = r.nextInt(6);
            num2 = r.nextInt(3);
            if (num1 == lastMoved) continue;
            lastMoved = num1;
            String str2,inverseStr2;
            switch (turnTypes[num2]) {
                case ANTICLOCKWISE: str2 = "'";inverseStr2=""; break;
                case TWO: str2 = "2"; inverseStr2="2"; break;
                default: str2 = ""; inverseStr2="'"; break;
            }

            returnString = turnByFaceNumber(num1,turnTypes[num2]);
            history.add(returnString + str2);
            solution.add(0,returnString + inverseStr2);
        }
        if (giveSolution) return String.join(" ",history) + "\n" + String.join(" ",solution);
        return String.join(" ",history);
    }

    public Colour getFaceColour(int x, int y, int z, int face) {
        switch (face) {
            case 0:
                return faceColours[face][n-z-1][x];
            case 1:
                return faceColours[face][y][n-z-1];
            case 2:
                return faceColours[face][y][x];
            case 3:
                return faceColours[face][y][z];
            case 4:
                return faceColours[face][y][n-x-1];
            case 5:
                return faceColours[face][z][x];
            default:
                return Colour.ORANGE;
        }
    }
}
