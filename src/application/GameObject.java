package application;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
public class GameObject {
    protected GraphicsContext gc;
    protected double x, y;
    protected String text;
    protected Image image;
    protected double width;
    protected double height;
    protected double fontSize;

    public GameObject(GraphicsContext gc, double x, double y, String text) {
        this.gc = gc;
        this.x = x;
        this.y = y;
        this.text = text;
        this.fontSize = 20;
    }

    public GameObject(GraphicsContext gc, double x, double y, Image image) {
        this.gc = gc;
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public GameObject(GraphicsContext gc, double x, double y, double width, double height, String text) {
        this.gc = gc;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.fontSize = 20;
    }

    public void update() {
        if (image != null) {
            drawImage();
        } else if (text != null) {
            if (width > 0 && height > 0) {
                drawRoundedRectangle();
                drawWrappedText();
            } else {
                drawSimpleText();
            }
        }
    }

    private void drawImage() {
        gc.drawImage(image, x - image.getWidth() / 2, y - image.getHeight() / 2);
    }

    private void drawSimpleText() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Joystix Monospace", fontSize));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(text, x, y);
    }
    
    private void drawRoundedRectangle() {
        double cornerRadius = 20;
        
        // Draw black fill
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x - width / 2, y - height / 2, width, height, cornerRadius, cornerRadius);
        
        // Draw white border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRoundRect(x - width / 2, y - height / 2, width, height, cornerRadius, cornerRadius);
    }

    private void drawWrappedText() {
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        double lineHeight = fontSize * 1.2;
        double yOffset = -height / 2 + lineHeight / 2;

        Text textNode = new Text();
        textNode.setFont(Font.font("Joystix Monospace", fontSize));

        for (String word : words) {
            String testLine = line + (line.length() > 0 ? " " : "") + word;
            textNode.setText(testLine);
            
            if (textNode.getLayoutBounds().getWidth() <= width - 20) {
                line.append((line.length() > 0 ? " " : "") + word);
            } else {
                gc.setFont(Font.font("Joystix Monospace", fontSize));
                gc.fillText(line.toString(), x, y + yOffset);
                yOffset += lineHeight;
                line = new StringBuilder(word);
            }

            if (yOffset + lineHeight > height / 2) {
                fontSize -= 1;
                if (fontSize < 8) {  // Set a minimum font size
                    break;
                }
                yOffset = -height / 2 + lineHeight / 2;
                line = new StringBuilder();
                words = text.split("\\s+");
                textNode.setFont(Font.font("Joystix Monospace", fontSize));
            }
        }

        if (line.length() > 0) {
            gc.setFont(Font.font("Joystix Monospace", fontSize));
            gc.fillText(line.toString(), x, y + yOffset);
        }
    }

    public void setText(String text) {
        this.text = text;
        this.fontSize = 20; // Reset font size when text changes
    }

    public boolean contains(double px, double py) {
        if (image != null) {
            return px >= x - image.getWidth() / 2 && px <= x + image.getWidth() / 2 &&
                   py >= y - image.getHeight() / 2 && py <= y + image.getHeight() / 2;
        } else if (width > 0 && height > 0) {
            return px >= x - width / 2 && px <= x + width / 2 &&
                   py >= y - height / 2 && py <= y + height / 2;
        } else {
            double textWidth = gc.getFont().getSize() * text.length();
            return px >= x - textWidth / 2 && px <= x + textWidth / 2 &&
                   py >= y - fontSize / 2 && py <= y + fontSize / 2;
        }
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }
    public void setImage(Image image) {
        this.image = image;
    }
}