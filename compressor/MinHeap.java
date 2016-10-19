/*
   MinHeap
      Is a minimum heap, with generic data type E extending Comparable<E>. It has an ACBT array ArrayList<E> and an int size
      representing the number of Nodes in the heap array. The client may add and poll items from heap, and the min heap
      nature will be preserved.
      
      William Fleetwood
*/

import java.lang.Math;
import java.util.ArrayList;

public class MinHeap<E extends Comparable<E>> {
   private ArrayList<E> heap; //ACBT array in respect to Node.freq, nodes might have children that don't exist w/in heap
   private int size;   //amount of nodes stored in heap
   
   
   //Constructors
   
   /*
      MinHeap
      Preconditions:
         -indices > -1
      Postconditions:
         -size = 0
         -heap is instantiated
   */
   public MinHeap() {
      heap = new ArrayList<E>();
      size = 0;
   }
   
   //Accessors
   
   /*
      size
      Preconditions:
         -none
      Postconditions:
         -none
   */
   public int size() {
      return size;
   }
   
   /*
      extract_min
      Preconditions:
         -none
      Postconditions:
         -root has been removed from heap, and min heap conditions still met
   */
   public E poll() {
      if (size > 1) {
         E x = heap.get(0);
         heap.set(0, heap.get(--size));
         heap.set(size, null);
         min_heapify(0);
         return x;
      } 
      else if (size == 1) {
         E x = heap.get(--size);
         heap.set(0, null);
         return x;
      }
      
      return null;
   }
   
   /*
      build_min_heap()
      Preconditions:
         -heap != null
      Postconditions:
         -heap now satisfies min heap conditions
   */
   private void build_min_heap() {
      int x = (int) ((size % 2 == 0) ? Math.floor(size/2.0) - 1 : Math.floor(size/2.0));
      for (int i = x; i >= 0; i--) {
         min_heapify(i);
      }
   }
   
   /*
      insert
      Preconditions:
         -n is not null, has data and freq
      Postconditions:
         -heap still satisfies min heap conditions
   */
   public void add(E n) {
      heap.add(size, n);
      size++;
      shift_up(size - 1);
   }
   
   /*
      shift_up
      Preconditions:
         -i >= 0
      Postconditions:
         -i and parent(i) are switched if i > parent
            -or-
         -nothing, bottom out
   */
   public void shift_up(int i) {
      int parent = parent(i);
      if (heap.get(parent).compareTo(heap.get(i)) <= 0) { //parent <= i
         return;  //base case
      } 
      else {   //recursive case
         swap(i, parent);
         shift_up(parent);
      }
   }
   
   /*
      parent
      Preconditions:
         -i >= 0
      Postconditions:
         -return parent index of index i
   */
   private int parent(int i) {
      if (i == 0) 
         return i;
      return (int) ((i%2 == 0) ? Math.floor(i/2.0) - 1 : Math.floor(i/2.0));
   }
   
   /*
      swap
      Preconditions:
         -0 <= j <= i < heap.size() 
      Postconditions:
         -data in indexes i and j are switched in heap
   */
   private void swap(int i, int j) {
      E x = heap.get(j);
      heap.set(j, heap.get(i));
      heap.set(i, x);
   }
   
   /*
      min_heapify
      Preconditions:
         -0 <= i
      Postconditions:
         -i is swapped with its smallest child if said child is smaller than i
   */
   private void min_heapify(int i) {
      int left = 2*i + 1;
      int right = 2*i + 2;
      int smallest = i;
      if (left < size && heap.get(left).compareTo(heap.get(smallest)) < 0) {
         smallest = left;
      }
      if (right < size && heap.get(right).compareTo(heap.get(smallest)) < 0) {
         smallest = right;
      }
      if (smallest != i) {
         swap(i, smallest);
         min_heapify(smallest);
      }
   }
      
}