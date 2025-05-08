package org.example.plugindev;

public class MyClass {

    public static int calculateSum(int a, int b, int c)
    {
        return a + b;
    }

    /**
     * @param a  the quantity to be eliminated
     */
    public static int calculateDiff(int a, int b, int c) {
        return a-b+c + calc(a, b);
    }

    private static int calc(int a, int b) {
        return 2;
    }
}
