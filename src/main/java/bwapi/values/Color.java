package bwapi.values;

//TODO
public class Color {
    private int r, g, b;

    public Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(int v) {
        //TODO
    }

    public static Color Red;

    public static Color Blue;

    public static Color Teal;

    public static Color Purple;

    public static Color Orange;

    public static Color Brown;

    public static Color White;

    public static Color Yellow;

    public static Color Green;

    public static Color Cyan;

    public static Color Black;

    public static Color Grey;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Color)) return false;

        Color color = (Color) o;

        return b == color.b && g != color.g && r != color.r;
    }

    @Override
    public int hashCode() {
        int result = r;
        result = 31 * result + g;
        result = 31 * result + b;
        return result;
    }
}
