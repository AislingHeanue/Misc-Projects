import javafx.scene.transform.Rotate;

import java.util.ArrayList;

public class Animator {
    Controller controller;
    Cubie[][][] cubies;
    Cube cube;
    int n;
    ArrayList<Cubie> cubiesToMove;

    public Animator(Controller c) {
        controller = c;
        n = controller.n;
        cubies = controller.cubies;
        cube = controller.cube;
    }

    public void Up(Cube.Turn turn,boolean wide) {
        cubiesToMove = new ArrayList<>();
        cube.setNextMoveIsWide(wide);
        cube.Up(turn);
        for (int x = 0; x < n; x ++) {
            for (int z = 0; z < n; z++) {
                cubiesToMove.add(cubies[x][0][z]);
                if (wide) cubiesToMove.add(cubies[x][1][z]);
            }
        }
        controller.animateSpin(cubiesToMove,turn, Rotate.Y_AXIS);
    }

    public void Left(Cube.Turn turn, boolean wideSelected) {
        cubiesToMove = new ArrayList<>();
        cube.setNextMoveIsWide(wideSelected);
        cube.Left(turn);
        for (int y = 0; y < n; y ++) {
            for (int z = 0; z < n; z++) {
                cubiesToMove.add(cubies[0][y][z]);
                if (wideSelected) cubiesToMove.add(cubies[1][y][z]);
            }
        }
        controller.animateSpin(cubiesToMove,turn, Rotate.X_AXIS);
    }

    public void Right(Cube.Turn turn, boolean wideSelected) {
        cubiesToMove = new ArrayList<>();
        cube.setNextMoveIsWide(wideSelected);
        cube.Right(turn);
        for (int y = 0; y < n; y ++) {
            for (int z = 0; z < n; z++) {
                cubiesToMove.add(cubies[2][y][z]);
                if (wideSelected) cubiesToMove.add(cubies[1][y][z]);
            }
        }
        controller.animateSpin(cubiesToMove,cube.inverseTurn(turn), Rotate.X_AXIS);
    }

    public void Front(Cube.Turn turn, boolean wideSelected) {
        cubiesToMove = new ArrayList<>();
        cube.setNextMoveIsWide(wideSelected);
        cube.Front(turn);
        for (int x = 0; x < n; x ++) {
            for (int y = 0; y < n; y++) {
                cubiesToMove.add(cubies[x][y][0]);
                if (wideSelected) cubiesToMove.add(cubies[x][y][1]);
            }
        }
        controller.animateSpin(cubiesToMove,turn, Rotate.Z_AXIS);
    }


    public void Back(Cube.Turn turn, boolean wideSelected) {
        cubiesToMove = new ArrayList<>();
        cube.setNextMoveIsWide(wideSelected);
        cube.Back(turn);
        for (int x = 0; x < n; x ++) {
            for (int y = 0; y < n; y++) {
                cubiesToMove.add(cubies[x][y][2]);
                if (wideSelected) cubiesToMove.add(cubies[x][y][1]);
            }
        }
        controller.animateSpin(cubiesToMove,cube.inverseTurn(turn), Rotate.Z_AXIS);
    }

    public void Down(Cube.Turn turn, boolean wideSelected) {
        cubiesToMove = new ArrayList<>();
        cube.setNextMoveIsWide(wideSelected);
        cube.Down(turn);
        for (int x = 0; x < n; x ++) {
            for (int z = 0; z < n; z++) {
                cubiesToMove.add(cubies[x][2][z]);
                if (wideSelected) cubiesToMove.add(cubies[x][1][z]);
            }
        }
        controller.animateSpin(cubiesToMove,cube.inverseTurn(turn),Rotate.Y_AXIS);
    }

    public void x(Cube.Turn turn) {
        cubiesToMove = new ArrayList<>();
        cube.x(turn);
        addAll();
        controller.animateSpin(cubiesToMove,cube.inverseTurn(turn), Rotate.X_AXIS);
    }

    public void y(Cube.Turn turn) {
        cubiesToMove = new ArrayList<>();
        cube.y(turn);
        addAll();
        controller.animateSpin(cubiesToMove,turn, Rotate.Y_AXIS);
    }

    public void z(Cube.Turn turn) {
        cubiesToMove = new ArrayList<>();
        cube.z(turn);
        addAll();
        controller.animateSpin(cubiesToMove,turn, Rotate.Z_AXIS);
    }

    public void M(Cube.Turn turn) {
        cubiesToMove = new ArrayList<>();
        cube.M(turn);
        for (int y = 0; y < n; y ++) {
            for (int z = 0; z < n; z++) {
                cubiesToMove.add(cubies[1][y][z]);
            }
        }
        controller.animateSpin(cubiesToMove,turn, Rotate.X_AXIS);
    }

    public void E(Cube.Turn turn) {
        cubiesToMove = new ArrayList<>();
        cube.E(turn);
        for (int x = 0; x < n; x ++) {
            for (int z = 0; z < n; z++) {
                cubiesToMove.add(cubies[x][1][z]);
            }
        }
        controller.animateSpin(cubiesToMove,cube.inverseTurn(turn), Rotate.Y_AXIS);
    }

    public void S(Cube.Turn turn) {
        cubiesToMove = new ArrayList<>();
        cube.S(turn);
        for (int x = 0; x < n; x ++) {
            for (int y = 0; y < n; y++) {
                cubiesToMove.add(cubies[x][y][1]);
            }
        }
        controller.animateSpin(cubiesToMove,turn, Rotate.Z_AXIS);
    }

    private void addAll() {
        for (int x = 0; x < n; x ++) {
            for (int y = 0; y < n; y++) {
                for (int z = 0; z < n; z++) {
                    cubiesToMove.add(cubies[x][y][z]);
                }
            }
        }
    }
}
