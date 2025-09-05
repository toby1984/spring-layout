package de.codesourcery.springlayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpringLayout
{
    private final double k = 150f;
    private final double springRestLength = 250d;

    private Vec2 calculateRepulsiveForce(Node a, Node b)
    {
        final Vec2 forceVector = b.position.cpy().sub( a.position );
        final double distance = forceVector.len();
        if ( distance > 0.000001d )
        {
            // -k²/d
            double scl = -Math.pow( k, 2 ) / distance;
            return forceVector.norm().scale( scl );
        }
        return forceVector.set(0,0);
    }

    private Vec2 calculateAttractiveForce(Node a, Node b)
    {
        final Vec2 forceVector = b.position.cpy().sub( a.position );
        final double distance = Math.abs( forceVector.len() - springRestLength );
        // d² / k
        double scl = Math.pow(distance,2) / k;
        return forceVector.norm().scale( scl );
    }

    public double iteration(List<Node> nodes, double elapsedSeconds, double forceScalingFactor) {

        // copy positions as we must not change node positions
        // before all calculations have been performed.
        final Map<Node, Vec2> newPositionsByNode = new HashMap<>( nodes.size() );
        for ( final Node node : nodes )
        {
            newPositionsByNode.put( node, node.position.cpy() );
        }

        // perform calculation
        for ( final Node node : nodes )
        {
            // sum repulsive forces
            final Vec2 f = new Vec2();

            for ( final Node otherNode : nodes ) {
                if ( otherNode != node ) {
                    final Vec2 repulsion = calculateRepulsiveForce( node, otherNode );
                    f.add( repulsion );
                }
            }

            // sum attractive forces
            for ( final Node dependency : node.dependencies )
            {
                f.add( calculateAttractiveForce( node, dependency ) );
            }
            f.scale( elapsedSeconds * forceScalingFactor);
            newPositionsByNode.get( node ).add( f );
        }

        // find center of mass to translate
        // graph so that it's always around the origin
        // of the coordinate system.
        final Vec2 centerOfMass = new Vec2();
        newPositionsByNode.values().forEach(  centerOfMass::add );
        if ( !nodes.isEmpty() ) {
            centerOfMass.scale( 1/(double)nodes.size() );
        }

        // now update node positions
        double totalDelta = 0;
        for ( final Node node : nodes )
        {
            final Vec2 newPosition = newPositionsByNode.get( node );
            newPosition.sub( centerOfMass );
            totalDelta += node.position.dist( newPosition );
            node.position.set( newPosition );
        }
        return totalDelta;
    }
}
