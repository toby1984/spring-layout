package de.codesourcery.springlayout;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends JFrame
{
    private static final int NODE_COUNT = 20;

    private static final double VIEWPORT_WIDTH = 1500;
    private static final double VIEWPORT_HEIGHT = 1500;

    private static final double NODE_WIDTH = 10; // model space
    private static final double NODE_HEIGHT = 10; // model space

    private final List<Node> nodes = new ArrayList<>();

    private final JPanel panel = new JPanel() {

        private double scaleX, scaleY;
        private final Vec2 tmp =  new Vec2();
        private final Vec2 tmp2 =  new Vec2();
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent( g );

            scaleX = getWidth() / VIEWPORT_WIDTH;
            scaleY = getHeight() / VIEWPORT_HEIGHT;

            final int boxWidth = modelToPixelsX( NODE_WIDTH );
            final int boxHeight = modelToPixelsY( NODE_HEIGHT);

            for ( final Node node : nodes )
            {
                modelToView( node.position, tmp );

                final int topLeftX = round( tmp.x - boxWidth/2d );
                final int topLeftY = round( tmp.y - boxHeight/2d );

                g.drawRect( topLeftX, topLeftY, boxWidth, boxHeight );
                g.drawString( node.label, topLeftX, topLeftY );

                // draw node connections
                for ( final Node dep : node.dependencies )
                {
                    modelToView( dep.position, tmp2 );
                    g.drawLine( round(tmp.x), round(tmp.y), round(tmp2.x), round(tmp2.y) );
                }
            }
            Toolkit.getDefaultToolkit().sync();
        }

        private int modelToPixelsX(double widthInModelSpace) {
            return round( widthInModelSpace * scaleX );
        }

        private static int round(double v) {
            return (int) Math.round(v);
        }

        private int modelToPixelsY(double heightInModelSpace) {
            return (int) Math.round( heightInModelSpace * scaleY );
        }

        private void modelToView(Vec2 modelPosition, Vec2 viewPosition) {

            // assumed view port is around the model space origin (0,0)
            // and covers (-VIEWPORT_WIDTH/2,-VIEWPORT_HEIGHT/2) to
            // (VIEWPORT_WIDTH/2,VIEWPORT_HEIGHT/2)

            double dx = modelPosition.x + VIEWPORT_WIDTH/2d;
            double dy = modelPosition.y + VIEWPORT_HEIGHT/2d;
            final double x = scaleX * dx;
            final double y = scaleY * dy;
            viewPosition.set( x, y );
        }
    };

    public Main() {
        super( "SpringLayout" );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        getContentPane().add( panel );
        panel.setPreferredSize( new Dimension( 400, 400 ) );
        pack();
        setLocationRelativeTo( null );
        setVisible( true );

        for ( int i = 0; i < NODE_COUNT; i++ ) {
            nodes.add( new Node( i, "Node #" + (i + 1), new Vec2() ) );
        }

        final Random rnd = new Random( 0xdeadbeefL );

        // initialize node positions inside around the origin
        final double radius = 100d;
        for ( final Node node : nodes ) {
            final double angle = rnd.nextDouble() * 2 * Math.PI;
            final double x = radius * Math.cos(angle);
            final double y = radius * Math.sin(angle);
            node.position.set( x, y );
        }

        // one node in the center

        final int centerIdx = rnd.nextInt( nodes.size() );
        final Node center = nodes.get( centerIdx );
        for ( int i = 0, nodesSize = nodes.size(); i < nodesSize; i++ )
        {
            final Node node = nodes.get( i );
            if ( node != center ) {
                node.addDependency(  center );
            }
        }

//        for ( final Node n : nodes )
//        {
//            int depCount = 1+rnd.nextInt(4 );
//            for ( ; depCount > 0 ; depCount-- ) {
//                final Node dep = nodes.get( rnd.nextInt( nodes.size() ) );
//                n.addDependency(dep);
//            }
//        }

        final Timer t = new Timer( 16, new ActionListener()
        {
            private long lastTimestamp;

            @Override
            public void actionPerformed(ActionEvent ev)
            {
                final long now = System.currentTimeMillis();
                if ( lastTimestamp != 0 ) {
                    double elapsedSeconds = (now - lastTimestamp) / 1000.0;
                    Main.this.tick(elapsedSeconds);
                }
                lastTimestamp = now;
            }
        } );
        t.start();
    }

    private final SpringLayout layout = new SpringLayout();

    private boolean converged;

    private void tick(double elapsedSeconds) {
        if ( ! converged )
        {
            final double delta = layout.iteration( nodes, elapsedSeconds, 1.5 );
            if ( delta < 4 )
            {
                converged = true;
            }
            System.out.println( delta );
            panel.repaint();
        }
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait( Main::new );
    }
}