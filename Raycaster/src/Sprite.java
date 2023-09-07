import java.awt.Color;
import java.awt.Graphics;

public class Sprite {
    private int[] sprites;

    private int type; //0:Test 1:Goomba 2:Eyeball 3:Lantern
    private boolean state;
    private double x, y, z;

    public Sprite(int[] psprites) {
        sprites = psprites;
        
        type = 0;
        state = true;
        x = Raycaster.mapS + (Raycaster.mapS / 2);
        y = Raycaster.mapS * 6 + (Raycaster.mapS / 2);
        //Floor =   W:480 H:320 z:19, W:640 H:480 z:22, W:960 H:640 z:20
        //Ceiling = W:480 H:320 z:1,  W:640 H:480 z:-2, W:960 H:640 z:0  //check values------------
        //Flying =  W:480 H:320 z:10, W:640 H:480 z:10, W:960 H:640 z:10
        z = 22;
    }

    public Sprite(int[] psprites, int ptype, boolean pstate, int psx, int psy, int psz) {
        sprites = psprites;
        
        type = ptype;
        state = pstate;
        x = psx;
        y = psy;
        z = psz;
    }

    public void drawSprite(Graphics window) {
        if(type == 1) {
            int spx = (int)(x / Raycaster.mapS);
            int spy = (int)(y / Raycaster.mapS);
            int spx_add = ((int)x + 15) / Raycaster.mapS;
            int spy_add = ((int)y + 15) / Raycaster.mapS;
            int spx_sub = ((int)x - 15) / Raycaster.mapS;
            int spy_sub = ((int)y - 15) / Raycaster.mapS;

            if(x > Player.px && (Raycaster.mapW[spy * 8 + spx_sub] == 0 ||  Raycaster.mapW[spy * 8 + spx_sub] < -1))
                x -= 0.5;
            if(x < Player.px && (Raycaster.mapW[spy * 8 + spx_add] == 0 ||  Raycaster.mapW[spy * 8 + spx_add] < -1))
                x += 0.5;
            if(y > Player.py && (Raycaster.mapW[spy_sub * 8 + spx] == 0 ||  Raycaster.mapW[spy_sub * 8 + spx] < -1))
                y -= 0.5;
            if(y < Player.py && (Raycaster.mapW[spy_add * 8 + spx] == 0 ||  Raycaster.mapW[spy_add * 8 + spx] < -1))
                y += 0.5;
        }
        if(type == 2) { //only moves when you move or not looking at it
            int spx = (int)(x / Raycaster.mapS);
            int spy = (int)(y / Raycaster.mapS);
            double speed = 0.75;
            if(Raycaster.mapW[spy * 8 + spx] != 0 || Raycaster.mapW[spy * 8 + spx] < -1)
                speed = 0.25;
            if(x > Player.px)
                x -= speed;
            if(x < Player.px)
                x += speed;
            if(y > Player.py)
                y -= speed;
            if(y < Player.py)
                y += speed;
        }
        
        double sx = x - Player.px;
        double sy = y - Player.py;
        double sz = z;

        double CS = Math.cos(Player.pa), SN = Math.sin(Player.pa);
        double a = sy * CS - sx * SN;
        double b = sx * CS + sy * SN;
        sx = a;
        sy = b;

        sx = (sx * (int)(Player.width * (108.0 / 960)) / sy) + ((Player.width / 8) / 2);
        sy = (sz * (int)(Player.width * (108.0 / 960)) / sy) + ((Player.height / 8) / 2);

        int scale = (int)(Raycaster.tres * (Player.height / 8) / b);
        if(scale < 0) {
            scale = 0;
        }
        if(scale > Player.fov / Player.d) {
            scale = (int)(Player.fov / Player.d);
        }

        double t_x = 0, t_y = Raycaster.tres - 1, t_x_step = (double)Raycaster.tres / scale, t_y_step = (double)Raycaster.tres / scale;

        for(int i = (int)(sx - (scale / 2)); i < (int)(sx + (scale / 2)); i++) {
            t_y = Raycaster.tres - 1;
            for(int j = 0; j < scale; j++) {
                if(i > -1 && i < Player.fov / Player.d && b < Player.depth[i]) {
                    int pixel = ((type * Raycaster.tres * Raycaster.tres) + (int)t_y * Raycaster.tres + (int)t_x) * 3;
                    int red = (int)(sprites[pixel + 0]);
                    int green = (int)(sprites[pixel + 1]);
                    int blue = (int)(sprites[pixel + 2]);
                    if(!(red == 255 && green == 0 && blue == 255) && (int)(sy * 8) - (j * 8) < Player.height) {
                        window.setColor(new Color(red, green, blue));
                        window.fillRect((int)(i * 8), (int)(sy * 8) - (j * 8), 8, 8);
                    }
                    t_y -= t_y_step;
                    if(t_y < 0) {
                        t_y = 0;
                    }
                }
            }
            t_x += t_x_step;
        }
    }

    public void drawSpriteMap(Graphics window) {
        window.setColor(new Color(255, 255, 0, Raycaster.mapT));
        window.fillRect((int)((x - (Player.pw / 2)) * Raycaster.mapSc) + Raycaster.mapSX, (int)((y - (Player.ph / 2)) * Raycaster.mapSc) + Raycaster.mapSY, Player.pw, Player.ph);
    }
}