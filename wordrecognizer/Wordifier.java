/* 
 * Wordifier.java
 *
 * Implements methods for iteratively learning words from a 
 * character-segmented text file, and then evaluating how good they are
 *
 * Students may only use functionality provided in the packages
 *     java.lang
 *     java.util 
 *     java.io
 * 
 * Use of any additional Java Class Library components is not permitted 
 * 
 * Will Fleetwood & Brooks Sparling
 * January 2015
 *
 */

import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.io.*;
import java.lang.*;
import java.util.*;

public class Wordifier {

    // loadSentences
    // Preconditions:
    //    - textFilename is the name of a plaintext input file
    // Postconditions:
    //  - A LinkedList<String> object is returned that contains
    //    all of the tokens in the input file, in order
    // Notes:
    //  - If opening any file throws a FileNotFoundException, print to standard error:
    //        "Error: Unable to open file " + textFilename
    //        (where textFilename contains the name of the problem file)
    //      and then exit with value 1 (i.e. System.exit(1))
	public static LinkedList<String> loadSentences( String textFilename ) {
		Scanner reader = null; LinkedList<String> text = new LinkedList<String>();
		try {
			reader = new Scanner(new File(textFilename));
		} catch (FileNotFoundException e) {
			System.err.println("Error: Unable to open file " + textFilename);
			System.exit(1);
		}
		
      
      while (reader.hasNext()) {
         text.add(reader.next());
      }
      
		return text;
	}	
	
    // findNewWords
    // Preconditions:
    //    - bigramCounts maps bigrams to the number of times the bigram appears in the data
    //    - scores maps bigrams to its bigram product score 
    //    - countThreshold is a threshold on the counts
    //    - probabilityThreshold is a threshold on the bigram product score 
    // Postconditions:
    //    - A HashSet is created and returned, containing all bigrams that meet the following criteria
    //        1) the bigram is a key in bigramCounts
    //        2) the count of the bigram is >= countThreshold
    //        3) the score of the bigram is >= probabilityThreshold
    //      Formatting note: the returned HashSet should include a space between bigrams
	public static HashSet<String> findNewWords( HashMap<String,Integer> bigramCounts, HashMap<String,Double> scores, int countThreshold, double probabilityThreshold ) {
		HashSet<String> newWords = new HashSet<String>();
      Set<String> keys = bigramCounts.keySet();
      for (String key : keys) {
         if ((bigramCounts.get(key) >= countThreshold) && (scores.get(key) >= probabilityThreshold))
            newWords.add(key);
      }
      return newWords;
	}

    // resegment
    // Preconditions:
    //    - previousData is the LinkedList representation of the data
    //    - newWords is the HashSet containing the new words (after merging)
    // Postconditions:
    //    - A new LinkedList is returned, which contains the same information as
    //      previousData, but any pairs of words in the newWords set have been merged
    //      to a single entry (merge from left to right)
    //
    //      For example, if the previous linked list contained the following items:
    //         A B C D E F G H I
    //      and the newWords contained the entries "B C" and "G H", then the returned list would have 
    //         A BC D E F GH I
	public static LinkedList<String> resegment( LinkedList<String> previousData, HashSet<String> newWords ) {
      LinkedList<String> newData = new LinkedList<String>();
      ListIterator<String> it = previousData.listIterator();
      
      String word1 = null;
      String word2 = it.next();
      
      while (it.hasNext()) {
         word1 = word2;
         word2 = it.next();
         if (newWords.contains(word1 + " " + word2)) {
            newData.add(word1+word2);
            if (it.hasNext()) {
               word2 = it.next();
            } 
         } else {
            newData.add(word1);
            if (it.nextIndex() == (previousData.size() - 1)) {
               newData.add(it.next());
            }
         }
      }
      
      return newData;
      
	}

    // computeCounts
    // Preconditions:
    //    - data is the LinkedList representation of the data
    //    - bigramCounts is an empty HashMap that has already been created
    // Postconditions:
    //    - bigramCounts maps each bigram appearing in the data to the number of times it appears
	public static void computeCounts(LinkedList<String> data, HashMap<String,Integer> bigramCounts ) {
      ListIterator<String> it = data.listIterator();
      String word1 = null;
      String word2 = it.next();
      while (it.hasNext()) {
         word1 = word2;
         word2 = it.next();
         incrementHashMap(bigramCounts, word1 + " " + word2, 1);
      }
      
	}

    // convertCountsToProbabilities 
    // Preconditions:
    //    - bigramCounts maps each bigram appearing in the data to the number of times it appears
    //    - bigramProbs is an empty HashMap that has already been created
    //    - leftUnigramProbs is an empty HashMap that has already been created
    //    - rightUnigramProbs is an empty HashMap that has already been created
    // Postconditions:
    //    - bigramProbs maps bigrams to their joint probability
    //        (where the joint probability of a bigram is the # times it appears over the total # bigrams)
    //    - leftUnigramProbs maps words in the first position to their "marginal probability"
    //    - rightUnigramProbs maps words in the second position to their "marginal probability"
	public static void convertCountsToProbabilities(HashMap<String,Integer> bigramCounts, HashMap<String,Double> bigramProbs, HashMap<String,Double> leftUnigramProbs, HashMap<String,Double> rightUnigramProbs ) {
		Set<String> bigrams = bigramCounts.keySet();
      HashMap<String, Integer> leftUnigrams = new HashMap<String, Integer>();  // Represents each unique unigram found in bigramCounts
      HashMap<String, Integer> rightUnigrams = new HashMap<String, Integer>();
      
      // Adds each unique unigram to our unigram hashmap
      for (String bigram : bigrams) {
         incrementHashMap(leftUnigrams, bigram.substring(0, bigram.lastIndexOf(' ')), 1);
         incrementHashMap(rightUnigrams, bigram.substring(bigram.lastIndexOf(' ') + 1, bigram.length()), 1);
      }
      Set<String> leftUnigramsSet = leftUnigrams.keySet();
      Set<String> rightUnigramsSet = rightUnigrams.keySet();
      
      // Creates left and right unigram probabilities
      for (String unigram : leftUnigramsSet) {
         leftUnigramProbs.put(unigram, (leftUnigrams.get(unigram) / ((double) bigramCounts.size())) );
         
      }
      for (String unigram : rightUnigramsSet){   
         rightUnigramProbs.put(unigram, (rightUnigrams.get(unigram) / ((double) bigramCounts.size())) );
      }
      
      // Fills bigramProbs with bigrams and their respective joint probabilities
      double jointProb = 0;
      for (String bigram : bigrams) {
         jointProb = (bigramCounts.get(bigram) / ((double) bigramCounts.size()));
         bigramProbs.put(bigram, jointProb);
      }
      
      return;
	}

    // getScores
    // Preconditions:
    //    - bigramProbs maps bigrams to to their joint probability
    //    - leftUnigramProbs maps words in the first position to their probability
    //    - rightUnigramProbs maps words in the first position to their probability
    // Postconditions:
    //    - A new HashMap is created and returned that maps bigrams to
    //      their "bigram product scores", defined to be P(w1|w2)P(w2|w1)
    //      The above product is equal to P(w1,w2)/sqrt(P_L(w1)*P_R(w2)), which 
    //      is the form you will want to use
	public static HashMap<String,Double> getScores( HashMap<String,Double> bigramProbs, HashMap<String,Double> leftUnigramProbs, HashMap<String,Double> rightUnigramProbs ) {
		Set<String> bigrams = bigramProbs.keySet();
      double leftProb = 0, rightProb = 0, bigramScore = 0;
      HashMap<String, Double> bigramScores = new HashMap<String, Double>();
      
      for (String bigram : bigrams) {
      
         leftProb = (leftUnigramProbs.get(bigram.substring(0, bigram.lastIndexOf(' '))));
         rightProb = (rightUnigramProbs.get(bigram.substring(bigram.lastIndexOf(' ') + 1, bigram.length())));
         
         bigramScore = (bigramProbs.get(bigram) / (Math.sqrt(leftProb * rightProb)));
         bigramScores.put(bigram, bigramScore);
      }
      
      return bigramScores;
	}

    // getVocabulary
    // Preconditions:
    //    - data is a LinkedList representation of the data
    // Postconditions:
    //    - A new HashMap is created and returned that maps words
    //      to the number of times they appear in the data
	public static HashMap<String,Integer> getVocabulary( LinkedList<String> data ) {
      ListIterator<String> it = data.listIterator();
      HashMap<String,Integer> vocab = new HashMap<String,Integer>();
      
     while(it.hasNext()) {
         incrementHashMap(vocab, it.next(), 1);
     }
      
		return vocab;
	}

    // loadDictionary
    // Preconditions:
    //    - dictionaryFilename is the name of a dictionary file
    // Postconditions:
    //    - A new HashSet is created and returned that contains
    //      all unique words appearing in the dictionary
	public static HashSet<String> loadDictionary( String dictionaryFilename ) {
      Scanner reader = null; HashSet<String> words = new HashSet<String>();
      try {
			reader = new Scanner(new File(dictionaryFilename));
		} catch (FileNotFoundException e) {
			System.err.println("Error: Unable to open file " + dictionaryFilename);
			System.exit(1);
		}
      
      while (reader.hasNextLine()) {
         words.add(reader.nextLine());
      }
      
		return words;
	}

    // incrementHashMap
    // Preconditions:
    //  - map is a non-null HashMap 
    //  - key is a key that may or may not be in map
    //  - amount is the amount that you would like to increment key's value by
    // Postconditions:
    //  - If key was already in map, map.get(key) returns amount more than it did before
    //  - If key was not in map, map.get(key) returns amount
    // Notes:
    //  - This method has been provided for you 
	private static void incrementHashMap(HashMap<String,Integer> map,String key,int amount) {
		if( map.containsKey(key) ) {
			map.put(key,map.get(key)+amount);
		} else {
			map.put(key,amount);
		}
		return;
	}

    // printNumWordsDiscovered
    // Preconditions:
    //    - vocab maps words to the number of times they appear in the data
    //    - dictionary contains the words in the dictionary
    // Postconditions:
    //    - Prints each word in vocab that is also in dictionary, in sorted order (alphabetical, ascending)
    //        Also prints the counts for how many times each such word occurs
    //    - Prints the number of unique words in vocab that are also in dictionary 
    //    - Prints the total of words in vocab (weighted by their count) that are also in dictionary 
	// Notes:
    //    - See example output for formatting
	public static void printNumWordsDiscovered( HashMap<String,Integer> vocab, HashSet<String> dictionary ) {
      Set<String> words = vocab.keySet(); 
      String[] wordsArray = words.toArray(new String[0]);
      Arrays.sort(wordsArray);
      for (String word : wordsArray) {
         if (dictionary.contains(word)) {
            System.out.println("Discovered " + word + " (count " + vocab.get(word) + ")");
         }
      }
      
            
      int sumWordsFoundInDictionary = 0;
      int wordsFoundInDictionary = 0;
      int totalWords = 0;
      for (String word : words) {
         totalWords += vocab.get(word).intValue();
         if (dictionary.contains(word)) {
            sumWordsFoundInDictionary += vocab.get(word).intValue();
            wordsFoundInDictionary++;
         }
      }
      
      
      String percentDictWordsFound = Double.toString((((double) wordsFoundInDictionary) / dictionary.size()) * 100);
      String percentTotal = Double.toString(((double) sumWordsFoundInDictionary/totalWords) * 100);
      if (percentDictWordsFound.length() > 5) {
         percentDictWordsFound = percentDictWordsFound.substring(0,5);
      }
      if (percentTotal.length() > 5) {
         percentTotal = percentTotal.substring(0,5);
      }
      
      
      System.out.println("Discovered " + wordsFoundInDictionary + " actual (unique) words out of " + dictionary.size() + 
                                       " dictionary words (" + percentDictWordsFound + "%)");
      System.out.println("Discovered " + sumWordsFoundInDictionary + " actual word tokens out of " + totalWords + " total tokens " +
                                       " (" + percentTotal + "%)");
      return;
	}

}
