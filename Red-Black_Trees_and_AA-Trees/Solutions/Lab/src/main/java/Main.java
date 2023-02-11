public class Main {
    public static void main(String[] args) {

        RedBlackTree<Integer> rbt = new RedBlackTree<>();
        int[] elements = {5, 12, 18, 37, 48, 60, 80};

        for (int element : elements) {
            rbt.insert(element);
            rbt.printPreOrder();
            System.out.println("<========================>");
        }
    }
}