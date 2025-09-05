package de.codesourcery.springlayout;

public class Vec2
{
    double x,y;

    public Vec2(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Vec2()
    {
    }

    public Vec2 flip() {
        this.x = -x;
        this.y = -y;
        return this;
    }

    public double dist(Vec2 other) {
        return Math.sqrt( dist2(other));
    }

    public double dist2(Vec2 other)
    {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        return dx * dx + dy * dy;
    }

    public Vec2(Vec2 other)
    {
        this.x = other.x;
        this.y = other.y;
    }

    public Vec2 cpy() {
        return new Vec2(x, y);
    }

    public Vec2 add(Vec2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vec2 norm()
    {
        double l = len2();
        if ( l > 0.000000001d) {
            double len = Math.sqrt(l);
            this.x /= len;
            this.y /= len;
        }
        return this;
    }

    public Vec2 scale(double s)
    {
        this.x *= s;
        this.y *= s;
        return this;
    }

    public double len() {
        return Math.sqrt( len2() );
    }

    public double len2() {
        return x*x+y*y;
    }

    public Vec2 set(Vec2 v) {
        return set(v.x, v.y);
    }

    public Vec2 set(double x, double y)
    {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vec2 sub(Vec2 position)
    {
        this.x = this.x - position.x;
        this.y = this.y - position.y;
        return this;
    }

    @Override
    public String toString()
    {
        return "(" + x + "," + y + ")";
    }
}
