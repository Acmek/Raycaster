import javax.swing.JFrame;
import java.awt.Component;

public class GraphicsRunner extends JFrame {

    //1080 : 720 = 1094 : 757
    //960 : 640 = 974 : 677
    //640 : 480 = 654 : 517
    //480 : 320 = 494 : 357
    //240 : 160 = 254 : 197
    private static final int WIDTH = 654; //+14 if the Window Bar is Enabled
    private static final int HEIGHT = 517; //+37 if the Window Bar is Enabled

    public GraphicsRunner() {
        super("Raycaster");
        setSize(WIDTH, HEIGHT);

        Raycaster game = new Raycaster();

        ((Component)game).setFocusable(true);
        getContentPane().add(game);
        
        setLocationRelativeTo(null); //Open in Middle of Screen

        setUndecorated(false); //Remove Window Bar

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false); //Cannot Change Window Size
    }

    public static void main(String[] args) throws Exception {
        new GraphicsRunner();
    }
}