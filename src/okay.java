import java.util.Arrays;

public class okay {
    public static void main(String[] args) {
        char[] n = "Hallos".toCharArray();
        System.out.println(Arrays.hashCode(n));
        int y = 1;
        for (int x: n){
            y = 31 * y + x;
        }
        System.out.println(y);
    }
}
