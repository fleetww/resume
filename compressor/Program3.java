/*
   Program3
      Reads through a text document and records the frequency of occurrence for each unique character.
      Then creates a Huffman tree to calculate and store the encoding for each character in a output text 
      file. Also prints out the number of bits saved by the encoding process.
      
      William Fleetwood
*/

import java.util.HashMap; 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

public class Program3 {
   
   static HashMap<Character, Integer> charFreq;  //mapping chars to freq
   static HashMap<Character, String> encoding;  //maps each character to its encoding bits
   static Scanner reader;                      //reads input file
   static MinHeap<Node> minHeap;              //minimum heap used for Huffman codes
   static PrintStream output;
   static Scanner input;
   
   /*
      main
      Preconditions:
         -none
      Postconditions:
         -Correct exiting if incorrect arguments
         -Otherwise output file created and wrote to including the characters in order and their
            binary encoding
   */
   public static void main(String[] args) throws FileNotFoundException {
      input = new Scanner(System.in);
      System.out.print("Enter the local path of a text file that you wish to encode: ");
      
      String fileName = input.nextLine();
      try {
         reader = new Scanner(new File(fileName));
      } 
      catch (FileNotFoundException e) {
         System.err.println("Incorrect local file path, please try again.");
         System.exit(1);
      }
      
      readFile();             //instantiate and populate frequency hashmap
      populateMinHeap();      //instantiate and populate minimum heap from hashmap
      huffman();              //iterate through min heap and create the huffman code tree
      calculateEncodings();   //create huffman encodings
      printOutput(fileName);  //print saved space to ./output/fileName.huf
   }
   
   /*
      readFile
      Preconditions:
         -reader is a valid, fully attached Scanner to a valid File
      Postconditions:
         -charFreq is a fully populated HashMap, mapping unique characters to their frequency
   */
   private static void readFile() {
      charFreq = new HashMap<Character,Integer>();
      String line;
      while (reader.hasNextLine()) {
         line = reader.nextLine();
         for (int i = 0; i < line.length(); i++) {
            addToHashMap(new Character(line.charAt(i)), charFreq);    //increments integer mapped to
         }                                                           //charAt(i)
      }
      return;
   }
   
   /*
      createMinHeap
      Preconditions:
         -charFreq is not null
      Postconditions:
         -instantiates a MinHeap from charFreq hashmap
   */
   private static void populateMinHeap() {
      minHeap = new MinHeap<Node>();
      for (Character ch : charFreq.keySet()) {
         minHeap.add(new Node(ch.charValue(), charFreq.get(ch).intValue(), null, null));
      }
   }
   
   /*
      addToHashMap
      Preconditions:
         -c and freq are not null
      Postconditions:
         -freq maps c to a new Integer = 0 or,
         -freq maps c to an incremented Integer
   */
   private static void addToHashMap(Character c, HashMap<Character, Integer> freq) {
      if (!freq.containsKey(c)) {
         freq.put(c, new Integer(1));
      } 
      else { //increment existing Integer
         freq.put(c, new Integer(freq.get(c) + 1));
      }
   }
   
   /*
      huffman
      Preconditions:
         -heap is a fully functioning, non-null min heap
      Postconditions:
         -heap contains one node, which is the root of a huffman code tree
   */
   private static void huffman() {
      while (minHeap.size() > 1) {
         char c = 0; //represents an empty node
         Node z = new Node(c, 0, null, null);
         Node a = minHeap.poll();
         Node b = minHeap.poll();
         int cmp = a.compareTo(b);  //Node.compareTo() handles the case where a.freq == b.freq
         z.left =  (cmp <= 0) ? a : b;
         z.right = (cmp <= 0) ? b : a;
         z.freq = z.left.freq + z.right.freq;
         minHeap.add(z);
      }
   }
   
   /*
      calculateEncodings
      Preconditions:
         -root is the root of a fully proper huffman tree
      Postconditions:
         -encoding maps each character to its respective huffman code
   */
   private static void calculateEncodings() {
      Node root = minHeap.poll();
      encoding = new HashMap<Character, String>();
      traverseHuffman(root, new String());  
   }
   
   /*
      traverseHuffman
      Preconditions:
         -n != null, code != null
      Postconditions:
         -encoding maps each character to its respective huffman code
   */
   private static void traverseHuffman(Node n, String code) {
      //base case
      if (n.left == null && n.right == null) {
         encoding.put(new Character(n.data), new String(code));
      } 
      else {
         //recursive case
         if (n.left != null) {
            code += "0";
            traverseHuffman(n.left, code);   //add new bit
            code = code.substring(0, code.length()-1);   //when bubbling up, remove added bit
         }
         if (n.right != null) {
            code += "1";
            traverseHuffman(n.right, code);
            code = code.substring(0, code.length()-1);
         }
      }
      
      return;
   }
   
   /*
      printOutput
      Preconditions:
         -fileName != null
      Postconditions:
         -./output/fileName.huf holds each character and huffman code as well as original space, amount saved, and
          saved space percentage
   */
   private static void printOutput(String fileName) throws FileNotFoundException {
      String outputDir = "./output/" + fileName.substring(0, fileName.length()-4).concat(".huf");
      output = new PrintStream(new File(outputDir));
      String line;
      Character[] chars = encoding.keySet().toArray(new Character[0]);
      java.util.Arrays.sort(chars);
      int origSize = findOldSize();
      int newSize = findNewSize();
      
      for (Character ch : chars) {
         line = ch.charValue() + " : " + encoding.get(ch);
         output.println(line);
      }
      output.println();
      output.println("Original Space: " + origSize + "\tSpace Saved: " + (origSize - newSize)); //saved space
      output.println("Percent Saved: " + ((double) (origSize - newSize)/origSize)*100 + '%');
      return;
   }
   
   /*
      findNewSize
      Preconditions:
         -encoding != null, and none of the mappings != null
      Postconditions:
         -sumOfProducts is the sum of all coding length * respective frequency
   */
   private static int findNewSize() {
      int sumOfProducts = 0;
      for (Character ch : encoding.keySet()) {
         sumOfProducts += encoding.get(ch).length() * charFreq.get(ch).intValue();
      }
      return sumOfProducts;
   }
   
   /*
      findOldSize
      Preconditions:
         -charFreq != null and none of the mappings are either
      Postconditions:
         -sumOfProducts is sum of each character frequency times 16
   */
   private static int findOldSize() {
      int sumOfProducts = 0;
      for (Character ch : encoding.keySet()) {
         sumOfProducts += charFreq.get(ch).intValue() * 16;
      }
      return sumOfProducts;
   }
   
   //testing functions
   
   /*
      findMax
      Preconditions:
         -map != null
      Postconditions:
         -int max stores the maximum Integer mapped in map
   */
   public static int findMax(HashMap<Character, Integer> map) {
      int max = 0;
      for (Character ch : map.keySet()) {
         max = (map.get(ch).intValue() > max) ? map.get(ch).intValue() : max;
      }
      return max;
   }
   
   /*
      findMin
      Preconditions:
         -map != null
      Postconditions:
         -int min stores the minimum Integer mapped in map
   */
   public static int findMin(HashMap<Character, Integer> map) {
      int min = Integer.MAX_VALUE;
      for (Character ch : map.keySet()) {
         min = (map.get(ch).intValue() < min) ? map.get(ch).intValue() : min;
      }
      return min;
   }
   
}