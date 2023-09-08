/*Things to complete
 * Close Door (is glitchy)
 * Transparent Textures (If Texture is Transparent Cast Another Ray Unit Hits Wall) and be able to set darkness or transparency
 * Animation
 * Diagonal Wall Movement Glitch
 * Levels
 * Have Rays Show With Map and Turn into Debug Settings (dont forget to make transparent)
 * Check If Stutter Fixer By Exporting To Better PC
 * Seperate Code Into Classes Using Public Variables (Raycaster.MapS)
 * Skip drawing other tiles in map if they are not used
 * Draw Floor and Ceiling Map Icons
 * Invisible Walls Definitely Possible
 * Shade Floor and Ceiling by Shading Horizontally
 * Sprite hitbox and Add Shader, place Sprites into array and make for loop to print
 * combine code into one java file
 * stutter fixer for background and sprites
*/

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Canvas;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import static java.lang.Character.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.text.CollationElementIterator;

import java.util.*;
import java.io.*;

public class Raycaster extends Canvas implements KeyListener, Runnable {

    private double time, milliseconds, fps;
    private int maxFps;

    private BufferedImage back;

    private boolean[] keys;

    private Player player;
    private Sprite sprite, sprite2, sprite3, sprite4;

    public static int mapX, mapY, mapS, mapTrans, mapT, mapSX, mapSY;
    public static double mapSc;
    public static int[] mapW, mapF, mapC;

    private int[] textures, sky, ground, sprites;
    private List<Integer> text;
    public static int tres, wres, hres;

    public Raycaster() {
        //===REMINDERS===
        //Debug Menu is F3
        //tres Must Be Adjusted to Match Texture Resolution
        //All Vars That Share Multiple Classes Are Public

        maxFps = 125; //Maximum FPS
        keys = new boolean[11];

        //---Debug Settings---
        //Can Only Change When Menu Displayed
        keys[4] = false; //Display Debug Menu
        keys[5] = false; //"Stutter Fixer"
        keys[7] = false; //Display Map Icons (Can Change Only When Map On)
        keys[8] = true; //Depth Shader
        keys[9] = false; //Display Map
        keys[10] = false; //Transparent Map (Can Change Only When Map On)

        //testing------------------------------------------
        keys[4] = true;
        //keys[9] = true;

        //---Map Settings---
        mapX = 8; //Map X Length
        mapY = 8; //Map Y Length
        mapS = 64; //Map Size Per Square
        mapSc = 0.5; //Map Scaler = (height / (mapY * mapS)), Map Icons (F6) Only Display Properly With Increments of tres / mapS
        mapSX = 50; //Map Starting X
        mapSY = 50; //480 - (int)(mapSc * mapY * mapS); //Map Starting Y
        mapTrans = 128; //Transparency 0 - 255 (Higher = More Opaque)
        mapW = new int[] { //Wall Map   -1:Invisible 1:Checkerboard 2:Brick 3:Block 4:Door
            1,1,1,3,3,3,3,1,
            1,0,1,0,0,0,0,1,
            1,0,4,0,0,2,0,1,
            1,0,1,0,0,0,0,1,
            1,0,0,0,0,0,0,1,
            1,0,-1,0,0,3,0,1,
            1,0,0,0,0,0,0,1,
            1,1,2,2,2,2,2,1,
        };
        mapF = new int[] { //Floor Map
            0,0,0,0,0,0,0,0,
            0,1,1,1,1,2,1,0,
            0,1,4,0,2,0,2,0,
            0,1,0,0,0,2,1,0,
            0,1,0,0,0,3,1,0,
            0,1,0,0,3,0,3,0,
            0,1,1,1,1,3,1,0,
            0,0,0,0,0,0,0,0,
        };
        mapC = new int[] { //Ceiling Map
            0,0,0,0,0,0,0,0,
            0,1,1,1,1,3,1,0,
            0,1,4,0,3,0,3,0,
            0,1,0,0,0,3,1,0,
            0,1,0,0,0,2,1,0,
            0,1,0,0,2,0,2,0,
            0,1,1,1,1,2,1,0,
            0,0,0,0,0,0,0,0,
        };

        //---Textures---
        tres = 32; //Texture Resolution
        //3:2 = 120 80, 4:3 = 180 120
        wres = 120; //Sky and Ground Width Resolution (Should Be in Increments of 60)
        hres = 80; //Sky and Ground Height Resolution (Should Be Height / 4)
        text = new ArrayList<Integer>();
        try {
            Scanner file = new Scanner(new File("Textures.ppm Directory")); //Import Texture File
            while(file.hasNext()) { //Grabs Texture RGB
                text.add(file.nextInt());
            }
            textures = new int[text.size()];
            for(int i = 0; i < text.size(); i++) {
                textures[i] = text.get(i);
            }

            text.clear();
            file = new Scanner(new File("3.2_Sky.ppm Directory"));
            while(file.hasNext()) { //Grabs Sky RGB
                text.add(file.nextInt());
            }
            sky = new int[text.size()];
            for(int i = 0; i < text.size(); i++) {
                sky[i] = text.get(i);
            }

            text.clear();
            file = new Scanner(new File("3.2_Ground.ppm Directory"));
            while(file.hasNext()) { //Grabs Ground RGB
                text.add(file.nextInt());
            }
            ground = new int[text.size()];
            for(int i = 0; i < text.size(); i++) {
                ground[i] = text.get(i);
            }

            text.clear();
            file = new Scanner(new File("Sprites.ppm Directory"));
            while(file.hasNext()) { //Grabs Sprites RGB
                text.add(file.nextInt());
            }
            sprites = new int[text.size()];
            for(int i = 0; i < text.size(); i++) {
                sprites[i] = text.get(i);
            }
        }
        catch(Exception e) {
            System.out.println(e);
            System.exit(0);
        }

        
        player = new Player(mapW, mapF, mapC, mapX, mapY, mapS, textures, sky, ground, tres, wres, hres, mapSc, mapSX, mapSY);
        sprite = new Sprite(sprites);
        sprite2 = new Sprite(sprites, 1, true, mapS * 6 + (mapS / 2), mapS * 6 + (mapS / 2), 22);
        sprite3 = new Sprite(sprites, 2, true, mapS * 6 + (mapS / 2), mapS + (mapS / 2), 10);
        sprite4 = new Sprite(sprites, 3, true, mapS * 2 + (mapS / 2), mapS * 2 + (mapS / 2), -2);

        setBackground(Color.GRAY);
        setVisible(true);

        new Thread(this).start();
        addKeyListener(this);
    }

    public void update(Graphics window) {
        paint(window);
    }

    public void paint(Graphics window) {
        time = System.nanoTime();
        
        Graphics2D twoDGraph = (Graphics2D)window;

        if(back == null)
            back = (BufferedImage)(createImage(getWidth(), getHeight()));

        Graphics graphToBack = back.createGraphics();

        move(graphToBack);
        
        player.drawRays2D(graphToBack);
        sprite.drawSprite(graphToBack);
        sprite2.drawSprite(graphToBack);
        sprite3.drawSprite(graphToBack);
        sprite4.drawSprite(graphToBack);
        if(keys[9]) {
            drawMap2D(graphToBack);
            player.draw(graphToBack);
            sprite.drawSpriteMap(graphToBack);
            sprite2.drawSpriteMap(graphToBack);
            sprite3.drawSpriteMap(graphToBack);
            sprite4.drawSprite(graphToBack);
        }
        
        if(keys[4])
            debug(graphToBack);
        
        twoDGraph.drawImage(back, null, 0, 0);


        milliseconds = (System.nanoTime() - time) / 1000000;
        fps = 1000 / (milliseconds + (1000 / maxFps));
        if(keys[5])
            player.setStutterFixer(milliseconds);
        else
            player.setStutterFixer(0);
    }

    public void drawMap2D(Graphics graphToBack) {
        graphToBack.setColor(new Color(128, 128, 128, mapT));
        graphToBack.fillRect(0 + mapSX, 0 + mapSY, (int)(mapX * mapS * mapSc), (int)(mapY * mapS * mapSc));

        for(int x = 0; x < mapX; x++) //Drawing Map
            for(int y = 0; y < mapY; y++) {
                if(mapW[y * mapX + x] > 0)
                    graphToBack.setColor(new Color(255, 255, 255, mapT));
                else
                    graphToBack.setColor(new Color(0, 0, 0, mapT));
                graphToBack.fillRect((int)(x * mapS * mapSc) + 1 + mapSX, (int)(y * mapS * mapSc) + 1 + mapSY, (int)(mapS * mapSc) - 1, (int)(mapS * mapSc) - 1);

                if(keys[7]) { //Displays Textures On Map
                    if(mapW[y * mapX + x] == -1) { //Draw Transparent
                        graphToBack.setColor(new Color(0, 255, 255, mapT));
                    }
                    else if(mapW[y * mapX + x] == -2) { //Draw Open Door
                        graphToBack.setColor(new Color(220, 220, 220, mapT));
                    }
                    graphToBack.fillRect((int)(x * mapS * mapSc) + 1 + mapSX, (int)(y * mapS * mapSc) + 1 + mapSY, (int)(mapS * mapSc) - 1, (int)(mapS * mapSc) - 1);
                    
                    if (mapW[y * mapX + x] > 0) {
                        for(int tx = 0; tx < tres; tx++) { //Draw Wall Textures
                            for(int ty = 0; ty < tres; ty++) {
                                int pixel = (((mapW[y * mapX + x] - 1) * tres * tres) + (int)ty * tres + (int)tx) * 3;
                                int red = (int)(textures[pixel + 0]);
                                int green = (int)(textures[pixel + 1]);
                                int blue = (int)(textures[pixel + 2]);
                                graphToBack.setColor(new Color(red, green, blue, mapT));
                                graphToBack.fillRect((int)((x * mapS * mapSc) + (tx * ((mapS * mapSc) / tres))) + mapSX, (int)((y * mapS * mapSc) + (ty * ((mapS * mapSc) / tres))) + mapSY, (int)((mapS * mapSc) / tres), (int)((mapS * mapSc) / tres));
                            }
                        }
                    }
                }
            }
    }

    public void debug(Graphics window) {
        //Font font = new Font(Font.SANS_SERIF, Font.BOLD, 42);
        window.setColor(Color.RED);
        window.drawString("FPS: " + Math.round(fps), 20, 20);
        window.drawString("Extra Milliseconds Per Frame: " + Math.round(milliseconds), 20, 40);
        window.drawString("Stutter Fixer (F4): " + keys[5], 20, 60);
        window.drawString("Display Map (F5): " + keys[9], 20, 80);
        if(keys[9]) {
            window.drawString("Display Map Icons (F6): " + keys[7] + "   Transparent Map (F7): " + keys[10], 150, 80);
        }
        window.drawString("Depth Shader (F8): " + keys[8], 20, 100);
    }

    public void move(Graphics graphToBack) {
        if(keys[0]) { //Move Forward
            player.move(graphToBack, 0);
        }
        if(keys[1]) { //Rotate Left
            player.move(graphToBack, 1);
        }
        if(keys[2]) { //Move Backward
            player.move(graphToBack, 2);
        }
        if(keys[3]) { //Rotate Right
            player.move(graphToBack, 3);
        }
        if(keys[6]) {
            player.interact();
            keys[6] = false;
        }
        if(keys[8]) {
            player.setShade(true);
        }
        else {
            player.setShade(false);
        }
        if(keys[9]) {
            player.setMap(true);
        }
        else {
            player.setMap(false);
        }
        if(keys[10]) {
            mapT = mapTrans;
            player.setMapT(mapT);
        }
        else {
            mapT = 255;
            player.setMapT(255);
        }
    }

    public void keyPressed(KeyEvent e) {
        //System.out.println(e.getKeyCode());
        switch(e.getKeyCode()) {
			case 87 : 
            case 38 : keys[0] = true; break; //W
			case 65 : 
            case 37 : keys[1] = true; break; //A
			case 83 : 
            case 40 : keys[2] = true; break; //S
			case 68 : 
            case 39 : keys[3] = true; break; //D
            case 114 :  if(keys[4]) //F3
                            keys[4] = false;
                        else
                            keys[4] = true;
                        break;
            case 115 :  if(keys[4]) { // F4
                            if(keys[5])
                                keys[5] = false;
                            else
                                keys[5] = true;
                        } break;
            case 116 :  if(keys[4]) { // F5
                            if(keys[9])
                                keys[9] = false;
                            else
                                keys[9] = true;
                        } break;
            case 117 :  if(keys[4]) // F6
                            if(keys[9]) {
                                if(keys[7])
                                    keys[7] = false;
                                else
                                    keys[7] = true;
                            } break;
            case 118 :  if(keys[4]) // F7
                            if(keys[9]) {
                                if(keys[10])
                                    keys[10] = false;
                                else
                                    keys[10] = true;
                            } break;
            case 119 :  if(keys[4]) { // F8
                            if(keys[8])
                                keys[8] = false;
                            else
                                keys[8] = true;
                        } break;
		}
    }

    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
			case 87 : 
            case 38 : keys[0] = false; break;
			case 65 : 
            case 37 : keys[1] = false; break;
			case 83 : 
            case 40 : keys[2] = false; break;
			case 68 : 
            case 39 : keys[3] = false; break;
		}
    }

    public void keyTyped(KeyEvent e) {
        switch(toUpperCase(e.getKeyChar())) {
            case 'E' : keys[6] = true; break;
        }
    }

    public void run() {
		try {
			while(true) {
				Thread.currentThread().sleep((long)(1000 / maxFps));
				repaint();
			}
		}
        catch(Exception e) {
            e.printStackTrace();
        }
	}
}
