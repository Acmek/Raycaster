import java.awt.Color;
import java.awt.Graphics;

public class Player {

    private int[] mapW, mapF, mapC;
    private int mapX, mapY, mapS, mapT, hitbox, mapSX, mapSY;
    private double ws, fs, cs, mapSc;

    public static int width, height;

    public static int pw, ph, fov;
    public static double d;

    public static double px, py, pdx, pdy, pa, ps, prs, pr;
    private double pxo, pyo, ipx, ipy, ipx_add, ipx_sub, ipy_add, ipy_sub;

    private double dxo, dyo, idx_add, idy_add;

    private boolean mapDisplay;
    private int mx, my, mp, dof;
    private double rx, ry, ra, xo, yo, disT;

    private int[] textures, sky, ground;
    private int tres, wres, hres;

    private boolean depthShader;
    private int sd, sx, sy;

    public static int[] depth;

    public Player(int[] pmapW, int[]pmapF, int[] pmapC, int pmapX, int pmapY, int pmapS, int[] ptextures, int[] psky, int[] pground, int ptres, int pwres, int phres, double pmapSc, int pmapSX, int pmapSY) {
        //===REMINDERS===
        //Radians and Degrees Go ClockWise
        //Sometimes mapX and mapY are Switched
        //You Can Walk Closer To Wall Corners if Approached Diagonally
        //Looking Perfectly into Uppper Map Corner Shades Corner Incorrectly

        //---Map Info---
        mapW = pmapW; //Wall Map
        mapF = pmapF; //Floor Map
        mapC = pmapC;
        mapX = pmapX; //Width
        mapY = pmapY; //Height
        mapSX = pmapSX; //Map Starting X
        mapSY = pmapSY; //Map Starting Y
        mapS = pmapS; //Size
        mapT = 255; //Adjust if Map is Automatically Transparent
        ws = 0.5; //Wall Shade (Higher = Brighter, Cannot == 1 idk why :/) Left and Right Walls Darker
        fs = 0.7; //Floor Shade (Same as ws)
        cs = 0.7; //Ceiling Shade (Same as ws)
        mapSc = pmapSc;

        //---Screen Settings---
        //Adjust Screen Width and Height to Match Values in GraphicsRunner
        width = 640;
        height = 480;
        tres = ptres; //Texture Resolution
        wres = pwres; //Sky and Ground Width Resolution
        hres = phres; //Sky and Ground Height Resolution
        depthShader = true;
        sd = 1; //Shade Brightness
        sx = mapS * mapX; //Min Shade X Distance
        sy = mapS * mapY; //Min Shade Y Distance

        //---Player Settings---
        px = mapS + (mapS / 2); //Start X
        py = mapS + (mapS / 2); //Start Y
        pw = (int)((mapS / 8) * mapSc); //Width
        ph = (int)((mapS / 8) * mapSc); //Height
        ps = (mapS / 64) * 1.5; //Speed (Adjust Value at StutterFixer)
        prs = (mapS / 64); //Rotation Speed (Adjust Value at StutterFixer)  DOESNT WORK???-----------------------------------

        pa = Math.PI / 2; //Starting Angle

        pdx = Math.cos(pa); //DeltaX
        pdy = Math.sin(pa); //DeltaY

        hitbox = 20; //Distance Between Player and Wall
        pr = 5; //Reach

        //smallest pixel resolution = 1
        //res = 1  W:240 H:160 d:0.25, W:480 H:320 d:0.125, W:640 H:480 d:0.09375, W:960 H:640 d:0.0625, W:1080 H:720 d:0.05555555555555555
        //res = 4  W:240 H:160 d:1,    W:480 H:320 d:0.5,   W:640 H:480 d:0.375,   W:960 H:640 d:0.25,   W:1080 H:720 d:0.2222222222222222
        //res = 8  W:240 H:160 d:2,    W:480 H:320 d:1,     W:640 H:480 d:0.75,    W:960 H:640 d:0.5,    W:1080 H:720 d:0.4444444444444444
        //resolution = width / (fov/d)
        //d = (resolution * fov) / width   *avoid d that is irrational. rounding or using the actual equation might not work
        fov = 60; //Field of View
        d = 0.75; //Degree Between Each Ray

        //---Textures---
        textures = ptextures;
        sky = psky;
        ground = pground;

        //
        depth = new int[(int)(fov / d)];
    }

    public void setMap(boolean b) { //Turn Map On / Off
        mapDisplay = b;
    }
    
    public void setShade(boolean b) { //Turn Depth Shader On / Off
        depthShader = b;
    }

    public void setMapT(int n) { //Turn Map Transparency On / Off
        mapT = n;
    }

    public void setStutterFixer(double num) {
        ps = ((mapS / 64) * 1.5) * (num + 1); //Adjust if You Change ps
        prs = (mapS / 64) * (num + 1);
    }

    public void draw(Graphics window) { //Draws Players New Position
        window.setColor(new Color(255, 255, 0, mapT));
        window.drawLine((int)((px + pdx * hitbox) * mapSc) + mapSX, (int)((py + pdy * hitbox) * mapSc) + mapSY, (int)((px + pdx * (hitbox + pr)) * mapSc) + mapSX, (int)((py + pdy * (hitbox + pr)) * mapSc) + mapSY);
        window.setColor(new Color(255, 0, 0, mapT));
        window.fillRect((int)((px - (pw / 2)) * mapSc) + mapSX, (int)((py - (ph / 2)) * mapSc) + mapSY, pw, ph);
        window.drawLine((int)(px * mapSc) + mapSX, (int)(py * mapSc) + mapSY, (int)((px + pdx * hitbox) * mapSc) + mapSX, (int)((py + pdy * hitbox) * mapSc) + mapSY);
    }

    public void move(Graphics window, int key) {
        if(pdx < 0)
            pxo = -hitbox;
        else
            pxo = hitbox;
        if(pdy < 0)
            pyo = -hitbox;
        else
            pyo = hitbox;
        ipx = (int)(px / mapS);
        ipx_add = (int)((px + pxo) / mapS);
        ipx_sub = (int)((px - pxo) / mapS);
        ipy = (int)(py / mapS);
        ipy_add = (int)((py + pyo) / mapS);
        ipy_sub = (int)((py - pyo) / mapS);
        
        if(key == 0) { //Foward
            if(mapW[(int)(ipy * mapX + ipx_add)] == 0 || mapW[(int)(ipy * mapX + ipx_add)] < -1)
                px += pdx * ps;
            if(mapW[(int)(ipy_add * mapX + ipx)] == 0 || mapW[(int)(ipy_add * mapX + ipx)] < -1)
                py += pdy * ps;
            if(mapDisplay)
                draw(window);
        }
        if(key == 1) { //Rotate Left
            pa -= 0.025 * prs;
            if(pa < 0)
                pa += 2 * Math.PI;
            pdx = Math.cos(pa);
            pdy = Math.sin(pa);
            if(mapDisplay)
                draw(window);
        }
        if(key == 2) { //Backward
            if(mapW[(int)(ipy * mapX + ipx_sub)] == 0 || mapW[(int)(ipy * mapX + ipx_sub)] < -1)
                px -= pdx * ps;
            if(mapW[(int)(ipy_sub * mapX + ipx)] == 0 || mapW[(int)(ipy * mapX + ipx_sub)] < -1)
                py -= pdy * ps;
            if(mapDisplay)
                draw(window);
        }
        if(key == 3) { //Rotate Right
            pa += 0.025 * prs;
            if(pa > 2 * Math.PI)
                pa -= 2 * Math.PI;
            pdx = Math.cos(pa);
            pdy = Math.sin(pa);
            if(mapDisplay)
                draw(window);
        }
    }

    public void interact() { //Interactions With Objects
        if(pdx < 0)
            dxo = -hitbox - pr;
        else
            dxo = hitbox + pr;
        if(pdy < 0)
            dyo = -hitbox - pr;
        else
            dyo = hitbox + pr;
        idx_add = (int)((px + dxo) / mapS);
        idy_add = (int)((py + dyo) / mapS);

        if(mapW[(int)(idy_add * mapX + idx_add)] == 4) //If Door Close = Open
            mapW[(int)(idy_add * mapX + idx_add)] = -2; //-----------------------TURN KEY6 FALSE AFTER PRESSED
        //else if(mapW[(int)(idy_add * mapX + idx_add)] == -2 && mapW[mp] != -2)  //If Door Open = Close
            //mapW[(int)(idy_add * mapX + idx_add)] = 4;
    }

    public double dist(double ax, double ay, double bx, double by, double ang) { //Find Distance Between Two Points
        return (Math.sqrt(((bx-ax) * (bx - ax)) + ((by-ay) * (by - ay))));
    }

    public void drawRays2D(Graphics window) {
        ra = pa - Math.toRadians(fov / 2);
        if(ra < 0) {
            ra += 2 * Math.PI;
        }
        else if(ra > 2 * Math.PI) {
            ra -= 2 * Math.PI;
        }

        //---Sky/Ground---
        for(int y = 0; y < hres / 2; y++) { //Sky
            for(int x = 0; x < wres; x++) {
                int xo = (int)(-Math.toDegrees(pa) * (wres / 60) * prs) - x;
                if(-xo < 0)
                    xo += wres;
                xo = xo % wres;
                int pixel = ((y * wres) - xo) * 3;
                int red = sky[pixel + 0];
                int green = sky[pixel + 1];
                int blue = sky[pixel + 2];
                window.setColor(new Color(red, green, blue));
                window.fillRect(x * (int)Math.ceil((double)width / wres), y * (int)Math.ceil((double)height / hres), (int)Math.ceil((double)width / wres), (int)Math.ceil((double)height / hres));
            }
        }
        for(int y = 0; y < hres / 2; y++) { //Ground
            for(int x = 0; x < wres; x++) {
                int xo = (int)(-Math.toDegrees(pa) * (wres / 60) * prs) - x;
                if(-xo < 0)
                    xo += wres;
                xo = xo % wres;
                int pixel = ((y * wres) - xo) * 3;
                int red = ground[pixel + 0];
                int green = ground[pixel + 1];
                int blue = ground[pixel + 2];
                window.setColor(new Color(red, green, blue));
                window.fillRect(x * (int)Math.ceil((double)width / wres), (y * (int)Math.ceil((double)height / hres)) + (height / 2), (int)Math.ceil((double)width / wres), (int)Math.ceil((double)height / hres));
            }
        }


        for(int r = 0; r < fov / d; r++) { //Casts Rays
            int vmt = 0, hmt = 0;

            //---Check Horizontal Lines---
            dof = 0;
            double disH = Integer.MAX_VALUE, hx = px, hy = py, idisH = Integer.MAX_VALUE, ihx = px, ihy = py;
            double aTan = -1/Math.tan(ra);
            if(ra > Math.PI) { //looking up
                ry = (((int) py / mapS) * mapS) - 0.0001;
                rx = (py - ry) * aTan + px;
                yo = -mapS;
                xo = -yo * aTan;
            }
            else if(ra > 0 && ra < Math.PI) { //looking down
                ry = (((int) py / mapS) * mapS) + mapS;
                rx = (py - ry) * aTan + px;
                yo = mapS;
                xo = -yo * aTan;
            }
            else if(ra == 0 || ra == Math.PI) { //looking 0 or 180
                rx = px;
                ry = py;
                dof = mapY;
            }
            while (dof < mapY) {
                mx = (int)(rx) / mapS;
                my = (int)(ry) / mapS;
                mp = my * mapX + mx;
                if(ihx == px) {
                    if(mp >= 0 && mp < mapX * mapY && mapW[mp] == -1) {
                        ihx = rx;
                        ihy = ry;
                        idisH = dist(px, py, ihx, ihy, ra);
                    }
                }
                if(mp >= 0 && mp < mapX * mapY && mapW[mp] > 0) {
                    vmt = mapW[mp] - 1;
                    hx = rx;
                    hy = ry;
                    disH = dist(px, py, hx, hy, ra);
                    dof = mapY;
                }
                else {
                    rx += xo;
                    ry += yo;
                    dof += 1;
                }
            }
            
            //---Check Vertical Lines---
            dof = 0;
            double disV = Integer.MAX_VALUE, vx = px, vy = py, idisV = Integer.MAX_VALUE, ivx = px, ivy = py;
            double nTan = -Math.tan(ra);
            if(ra > Math.PI / 2 && ra < (Math.PI * 3) / 2) { //looking left
                rx = (((int) px / mapS) * mapS) - 0.0001;
                ry = (px - rx) * nTan + py;
                xo = -mapS;
                yo = -xo * nTan;
            }
            else if(ra < Math.PI / 2 || ra > (Math.PI * 3) / 2) { //looking right
                rx = (((int) px / mapS) * mapS) + mapS;
                ry = (px - rx) * nTan + py;
                xo = mapS;
                yo = -xo * nTan;
            }
            else if (ra == Math.PI / 2 || ra == 3 * Math.PI / 2) { //looking 90 or 270
                rx = px;
                ry = py;
                dof = mapX;
            }
            while (dof < mapX) {
                mx = (int)(rx) / mapS;
                my = (int)(ry) / mapS;
                mp = my * mapX + mx;
                if(ivx == px) {
                    if(mp >= 0 && mp < mapX * mapY && mapW[mp] == -1) {
                        ivx = rx;
                        ivy = ry;
                        idisV = dist(px, py, ivx, ivy, ra);
                    }
                }
                if(mp >= 0 && mp < mapX * mapY && mapW[mp] > 0) {
                    hmt = mapW[mp] - 1;
                    vx = rx;
                    vy = ry;
                    disV = dist(px, py, vx, vy, ra);
                    dof = mapX;
                }
                else {
                    rx += xo;
                    ry += yo;
                    dof += 1;
                }
            }

            //---Draw 3D Scene---
            double shade = 1;
            double shade2 = 1;
            double shade3 = 1;
            double irx = px;
            double iry = py;
            if(disV < disH) { //Chooses Shortest Ray to Draw and Shades Wall
                vmt = hmt;
                shade = ws;
                rx = vx;
                ry = vy;
                disT = disV;
                if(depthShader) {
                    shade2 = 1 - (disV / ((sd * sx) + hitbox));
                    if(shade2 < 0)
                        shade2 = 0;
                    else if(shade2 > 1)
                        shade2 = 1;
                }
                window.setColor(new Color(0, 0, 220));
            }
            else {
                rx = hx;
                ry = hy;
                disT = disH;
                if(depthShader) {
                    shade3 = 1 - (disH / ((sd * sy) + hitbox));
                    if(shade3 < 0)
                        shade3 = 0;
                    else if(shade3 > 1)
                        shade3 = 1;
                }
                window.setColor(new Color(0, 0, 255));
            }

            if(idisV < idisH && idisV < disT) { //Invisible Wall Rays
                irx = ivx;
                iry = ivy;
            }
            else if(idisH <= idisV && idisH <= disT) {
                irx = ihx;
                iry = ihy;
            }

            if(mapDisplay) { //Draws Rays
                window.drawLine((int)(irx * mapSc) + mapSX, (int)(iry * mapSc) + mapSY, (int)(rx * mapSc) + mapSX, (int)(ry * mapSc) + mapSY);
                window.setColor(new Color(255, 0, 0));
                window.drawLine((int)(px * mapSc) + mapSX, (int)(py * mapSc) + mapSY, (int)(irx * mapSc) + mapSX, (int)(iry * mapSc) + mapSY);
            }
            
            double ca = pa - ra; //Fixes Fish-Eye Effect
            if(ca < 0) {
                ca += 2 * Math.PI;
            }
            else if(ca > 2 * Math.PI) {
                ca -= 2 * Math.PI;
            }
            disT = disT * Math.cos(ca);
            
            double lineH = (mapS * height) / disT;
            double ty_step = tres / (double)lineH;
            double ty_off = 0;
            if (lineH > height) {
                ty_off = (lineH - height) / 2;
                lineH = height;
            }
            double lineO = (height / 2) - lineH / 2;

            double ty = ty_off * ty_step;
            
            double tx;
            if(shade == 1) { //Finds Texture
                tx = (int)(rx / (mapS / tres)) % tres;
                if(ra < Math.PI) { //Flips So Texture is Facing Right Way
                    tx = (tres - 1) - tx;
                }
            }
            else {
                tx = (int)(ry / (mapS / tres)) % tres;
                if(ra > Math.PI / 2 && ra < (3 * Math.PI) / 2) {
                    tx = (tres - 1) - tx;
                }
            }

            depth[r] = (int)disT;

            //---Draw Walls---
            for(int y = 0; y < lineH; y++) {
                int pixel = ((vmt * tres * tres) + (int)ty * tres + (int)tx) * 3;
                int red = (int)(textures[pixel + 0] * shade * shade2 * shade3);
                int green = (int)(textures[pixel + 1] * shade * shade2 * shade3);
                int blue = (int)(textures[pixel + 2] * shade * shade2 * shade3);
                window.setColor(new Color(red, green, blue));
                window.fillRect((int)(r * (int)(width / (fov / d))), (int)lineO + y, (int)(width / (fov / d)), (int)(height / lineH));
                ty += ty_step;
            }
            
            //---Draw Floor & Ceiling---
            for(int y = (int)(lineO + lineH); y < height; y++) {
                double dy = y - (height / 2), raFix = Math.cos(ca);
                tx = px / 2 + Math.cos(ra) * ((height / 2) - (height / 160)) * tres / dy / raFix;
                ty = -py / 2 - Math.sin(ra) * ((height / 2) - (height / 160)) * tres / dy / raFix;
                
                //Floor
                int fmp = (mapF[(int)(-ty / tres) * mapX + (int)(tx / tres)] - 1) * tres * tres;
                if(fmp >= 0 && fmp < textures.length) {
                    int pixel = (fmp + ((int)(-ty) & (tres - 1)) * tres + ((int)(tx) & (tres - 1))) * 3;
                    int red = (int)(textures[pixel + 0] * fs);
                    int green = (int)(textures[pixel + 1] * fs);
                    int blue = (int)(textures[pixel + 2] * fs);
                    window.setColor(new Color(red, green, blue));
                    window.fillRect((int)(r * (int)(width / (fov / d))), y, (int)(width / (fov / d)), (int)(height / lineH));
                }

                //Ceiling
                int cmp = (mapC[(int)(-ty / tres) * mapX + (int)(tx / tres)] - 1) * tres * tres;
                if(cmp >= 0 && cmp < textures.length) {
                    int pixel = (cmp + ((int)(-ty) & (tres - 1)) * tres + ((int)(tx) & (tres - 1))) * 3;
                    int red = (int)(textures[pixel + 0] * cs);
                    int green = (int)(textures[pixel + 1] * cs);
                    int blue = (int)(textures[pixel + 2] * cs);
                    window.setColor(new Color(red, green, blue));
                    window.fillRect((int)(r * (int)(width / (fov / d))), (height - y) - 1, (int)(width / (fov / d)), (int)(height / lineH));
                }
            }
            
            ra += Math.toRadians(d); //Sets Next Ray to Be Casted
            if(ra < 0) {
                ra += 2 * Math.PI;
            }
            else if(ra > 2 * Math.PI) {
                ra -= 2 * Math.PI;
            }
        }
    }
}