package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Random;

public class LunarPuzzle {
    private Image fullImage;
    private Tile[][] tiles;
    private int gridSize;
    private int pieceWidth, pieceHeight;
    private static final int PUZZLE_SIZE = 400;
    private static final int BORDER_WIDTH = 2;
    private Tile[][] solvedState;

    public LunarPuzzle(int level) {
        fullImage = new Image(getClass().getResourceAsStream("/application/assets/puzzles/" + level + ".jpg"));
        gridSize = 3; // 3x3 puzzle, you can change this for different difficulties
        double scale = PUZZLE_SIZE / Math.max(fullImage.getWidth(), fullImage.getHeight());
        pieceWidth = (int) (fullImage.getWidth() * scale / gridSize);
        pieceHeight = (int) (fullImage.getHeight() * scale / gridSize);
        createPuzzlePieces();
        do {
            shufflePuzzle();
        } while (checkWin()); // Reshuffle if the puzzle is solved
    }

    private void createPuzzlePieces() {
        tiles = new Tile[gridSize][gridSize];
        solvedState = new Tile[gridSize][gridSize];
        PixelReader reader = fullImage.getPixelReader();
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                WritableImage pieceImage = new WritableImage(reader, 
                    (int)(x * fullImage.getWidth() / gridSize), 
                    (int)(y * fullImage.getHeight() / gridSize), 
                    (int)(fullImage.getWidth() / gridSize), 
                    (int)(fullImage.getHeight() / gridSize));
                
                boolean isEmpty = (x == gridSize - 1 && y == gridSize - 1);
                Tile tile = new Tile(pieceImage, isEmpty, x, y);
                tiles[x][y] = tile;
                solvedState[x][y] = new Tile(pieceImage, isEmpty, x, y);
            }
        }
    }

    private void shufflePuzzle() {
        Random rand = new Random();
        int shuffleCount = 1000; // You can adjust this number
        for (int i = 0; i < shuffleCount; i++) {
            int x = rand.nextInt(gridSize);
            int y = rand.nextInt(gridSize);
            moveTile(tiles[x][y]);
        }
    }
    
    public Image getFullImage() {
        return fullImage;
    }

    public void draw(GraphicsContext gc) {
        double startX = (gc.getCanvas().getWidth() - pieceWidth * gridSize - BORDER_WIDTH * (gridSize - 1)) / 2;
        double startY = (gc.getCanvas().getHeight() - pieceHeight * gridSize - BORDER_WIDTH * (gridSize - 1)) / 2;

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(BORDER_WIDTH);
        gc.strokeRect(startX - BORDER_WIDTH/2, startY - BORDER_WIDTH/2, 
                      pieceWidth * gridSize + BORDER_WIDTH * (gridSize + 1), 
                      pieceHeight * gridSize + BORDER_WIDTH * (gridSize + 1));

        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                if (!tiles[x][y].isEmpty()) {
                    gc.drawImage(tiles[x][y].getImage(), 
                        startX + x * (pieceWidth + BORDER_WIDTH), 
                        startY + y * (pieceHeight + BORDER_WIDTH), 
                        pieceWidth, 
                        pieceHeight);
                }
            }
        }
    }

    public boolean handleClick(GraphicsContext gc, double clickX, double clickY) {
        double startX = (gc.getCanvas().getWidth() - pieceWidth * gridSize - BORDER_WIDTH * (gridSize - 1)) / 2;
        double startY = (gc.getCanvas().getHeight() - pieceHeight * gridSize - BORDER_WIDTH * (gridSize - 1)) / 2;

        int tileX = (int)((clickX - startX) / (pieceWidth + BORDER_WIDTH));
        int tileY = (int)((clickY - startY) / (pieceHeight + BORDER_WIDTH));

        if (tileX >= 0 && tileX < gridSize && tileY >= 0 && tileY < gridSize) {
            return moveTile(tiles[tileX][tileY]);
        }
        return false;
    }

    private boolean moveTile(Tile tile) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int newX = tile.getX() + dir[0];
            int newY = tile.getY() + dir[1];
            if (newX >= 0 && newX < gridSize && newY >= 0 && newY < gridSize) {
                if (tiles[newX][newY].isEmpty()) {
                    // Swap tiles
                    Tile temp = tiles[tile.getX()][tile.getY()];
                    tiles[tile.getX()][tile.getY()] = tiles[newX][newY];
                    tiles[newX][newY] = temp;

                    // Update coordinates
                    tiles[tile.getX()][tile.getY()].setX(tile.getX());
                    tiles[tile.getX()][tile.getY()].setY(tile.getY());
                    tiles[newX][newY].setX(newX);
                    tiles[newX][newY].setY(newY);

                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkWin() {
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                if (tiles[x][y].isEmpty() != solvedState[x][y].isEmpty()) {
                    return false;
                }
                if (!tiles[x][y].isEmpty() && !tiles[x][y].getImage().equals(solvedState[x][y].getImage())) {
                    return false;
                }
            }
        }
        return true;
    }

    public void drawFullImage(GraphicsContext gc) {
        double x = (gc.getCanvas().getWidth() - fullImage.getWidth() * PUZZLE_SIZE / fullImage.getWidth()) / 2;
        double y = (gc.getCanvas().getHeight() - fullImage.getHeight() * PUZZLE_SIZE / fullImage.getWidth()) / 2;
        gc.drawImage(fullImage, x, y, PUZZLE_SIZE, PUZZLE_SIZE * fullImage.getHeight() / fullImage.getWidth());
    }

    public void showHint(GraphicsContext gc) {
        double hintSize = 200;
        double scale = hintSize / Math.max(fullImage.getWidth(), fullImage.getHeight());
        gc.drawImage(fullImage, 
            gc.getCanvas().getWidth() - fullImage.getWidth() * scale - 10, 
            10, 
            fullImage.getWidth() * scale, 
            fullImage.getHeight() * scale);
    }
}