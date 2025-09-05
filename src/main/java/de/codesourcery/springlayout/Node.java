package de.codesourcery.springlayout;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class Node
{
    public final int id;
    public final String label;
    public final Vec2 position;
    public final Set<Node> dependencies = new HashSet<>();

    public Node(int id, String label, Vec2 position)
    {
        this.id = id;
        this.label = label;
        this.position = position;
    }

    public void addDependency(Node node)
    {
        dependencies.add( node );
        node.dependencies.add( this );
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Node n && id == n.id;
    }

    @Override
    public int hashCode()
    {
        return Integer.hashCode( id );
    }

    @Override
    public String toString()
    {
        return "Node[" +
               "label=" + label + ", " +
               "position=" + position + "," +
               "dependencies=" + dependencies.stream().map(x->"'"+x.label+"'").collect( Collectors.joining(", ")) + "]";
    }

    public Node createCopyWithoutDeps()
    {
        return new  Node( id, label, position.cpy() );
    }
}
