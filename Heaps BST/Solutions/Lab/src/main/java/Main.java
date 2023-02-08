import implementations.BinarySearchTree;
import interfaces.AbstractBinarySearchTree;

public class Main {
    public static void main(String[] args) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();

        bst.insert(15);
        bst.insert(11);
        bst.insert(20);
        bst.insert(8);
        bst.insert(22);
        bst.insert(16);
        bst.insert(1);
        bst.print();

        System.out.println(bst.contains(22));

        System.out.println();

        AbstractBinarySearchTree<Integer> search = bst.search(20);
        search.print();
    }
}