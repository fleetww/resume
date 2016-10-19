I created the three java files, Program3.java, MinHeap.java, and 
Node.java along with their respective .class files. There is also
the output folder for the keys and new bit representations as well
as the the two .txt writeups. Program3 holds the "main" body file
that needs to be compiled, and its purpose is to read through the
inputted text file, count each character's occurrence, and calculate
the huffman codes for each character. Then it proceeds to print out 
each character's huffman code and at the end how much space was saved.
MinHeap's purpose is to represent a minimum heap, including the 
public functions add() and poll(). Node's purpose is to represent a 
node in the huffman tree, by storing a pointer to a left and right
child as well as an int for the frequency of its char data obviously
for its respective charcter. Each instance of a Node is stored in an
instance of a MinHeap. I came across little problems during this 
assignment. The biggest difficulty would probably be the writing of
MinHeap, as it was the most complex part of the code. This difficulty
stemmed from having to understand a Node could have children Nodes
that didn't exist within MinHeap's ACBT array.