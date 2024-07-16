package application;

import javafx.scene.image.Image;

public class Tile {
    private Image image;
    private boolean isEmpty;
    private int x, y;

    public Tile(Image image, boolean isEmpty, int x, int y) {
        this.image = image;
        this.isEmpty = isEmpty;
        this.x = x;
        this.y = y;
    }

    public Image getImage() {
        return image;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tile tile = (Tile) obj;
        return isEmpty == tile.isEmpty &&
               x == tile.x &&
               y == tile.y &&
               (image == null ? tile.image == null : image.equals(tile.image));
    }
}
