import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.transform.Translate;
import org.fxyz3d.shapes.primitives.CuboidMesh;

public class Cubie {
    CuboidMesh cuboid;
    int x,y,z,n;
    static double BOX_SIZE = 100;

    double xPos, yPos, zPos;

    Cube cube;


    public Cubie(int x, int y, int z, Group group,int n,Cube cube) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.n = n;
        xPos = (x-1)*102;
        yPos = (y-1)*102;
        zPos = (z-1)*102;
        this.cube = cube;
        //this line makes noise in the console I guess
        cuboid = new CuboidMesh(BOX_SIZE,BOX_SIZE,BOX_SIZE);
        cuboid.getTransforms().add(new Translate(xPos,yPos,zPos));
        cuboid.setCullFace(CullFace.BACK);
        group.getChildren().add(cuboid);

        updateColours();


    }

    public void updateColours() {
        Cube.Colour[] colours = new Cube.Colour[6];
        for (int i = 0; i < 6; i ++) {
            if (isFaceVisible(x, y, z, i)) {
                colours[i] = cube.getFaceColour(x, y, z, i);
            } else {
                colours[i] = Cube.Colour.BLACK;
            }
        }
        Image newMeshImage = Net.makeNet(colours,BOX_SIZE);
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseMap(newMeshImage);
        cuboid.setMaterial(mat);
    }

    private boolean isFaceVisible(int x,int y,int z,int face) {
        switch (face){
            case 0:
                return y == 0;
            case 1:
                return x == 0;
            case 2:
                return z == 0;
            case 3:
                return x == n-1;
            case 4:
                return z == n-1;
            case 5:
                return y == n-1;
            default:
                return false;
        }
    }

}
