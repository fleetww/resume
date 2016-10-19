   /*
   Node
      Represents a huffman tree node, storing a character, frequency of character, a left child and a right child.
      this.data can be the equivalant of a null char, meaning this is a branch node.
      
      William Fleetwood
*/

public class Node implements Comparable<Node> {
   public char data;
   public int freq;
   public Node left;
   public Node right;
   
   /*
      Node
      Preconditions:
         -none
      Postconditions:
         -class fields are set to passed in variables 
   */
   public Node (char data, int freq, Node left, Node right) {
      this.data = data;
      this.freq = freq;
      this.left = left;
      this.right = right;
   }
   
   /*
      compareTo
      Preconditions:
         -x != null
      Postconditions:
         -none
   */
   public int compareTo(Node x) {
      return (this.freq != x.freq) ? this.freq - x.freq : this.data - x.data;
   }
}