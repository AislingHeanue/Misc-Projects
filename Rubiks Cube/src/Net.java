import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Net {
    public static Image makeNet(Cube.Colour[] colours,double boxSize) {
        if (colours.length != 6) return null;
        int pad = 4;
        GridPane g = new GridPane();
        g.setAlignment(Pos.CENTER);
        Rectangle back0 = new Rectangle(boxSize,boxSize,Color.BLACK);
        Rectangle back1 = new Rectangle(boxSize,boxSize,Color.BLACK);
        Rectangle back2 = new Rectangle(boxSize,boxSize,Color.BLACK);
        Rectangle back3 = new Rectangle(boxSize,boxSize,Color.BLACK);
        Rectangle back4 = new Rectangle(boxSize,boxSize,Color.BLACK);
        Rectangle back5 = new Rectangle(boxSize,boxSize,Color.BLACK);
        Rectangle r0 = new Rectangle(boxSize-2*pad,boxSize-2*pad,getColour(colours[0]));
        Rectangle r1 = new Rectangle(boxSize-2*pad,boxSize-2*pad,getColour(colours[1]));
        Rectangle r2 = new Rectangle(boxSize-2*pad,boxSize-2*pad,getColour(colours[2]));
        Rectangle r3 = new Rectangle(boxSize-2*pad,boxSize-2*pad,getColour(colours[3]));
        Rectangle r4 = new Rectangle(boxSize-2*pad,boxSize-2*pad,getColour(colours[4]));
        Rectangle r5 = new Rectangle(boxSize-2*pad,boxSize-2*pad,getColour(colours[5]));

        g.add(new StackPane(back0,r0),1,0);
        g.add(new StackPane(back1,r1),2,1);
        g.add(new StackPane(back2,r2),3,1);
        g.add(new StackPane(back3,r3),0,1);
        g.add(new StackPane(back4,r4),1,1);
        g.add(new StackPane(back5,r5),1,2);

        return g.snapshot(null,null);
    }

    private static Color getColour(Cube.Colour c) {
        switch (c) {
            case RED: return Color.RED;
            case GREEN: return Color.GREEN;
            case BLUE: return Color.BLUE;
            case YELLOW: return Color.YELLOW;
            case ORANGE: return Color.ORANGE;
            case WHITE: return Color.WHITE;
            default: return Color.BLACK;
        }
    }


}
