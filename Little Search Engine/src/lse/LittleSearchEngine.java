package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		
		HashMap<String,Occurrence> keyWordsTable = new HashMap<String,Occurrence>();
		
		try {
			Scanner sc = new Scanner(new File(docFile));
			while (sc.hasNext()) {
				String currWord = getKeyword(sc.next());
				
				if (currWord != null) {
					if (!keyWordsTable.containsKey(currWord)) { //NOT in hashmap
						Occurrence occ = new Occurrence(docFile, 1);
						keyWordsTable.put(currWord, occ); //inserts to hashmap
					}
					else //Increase frequency if in hashmap
						keyWordsTable.get(currWord).frequency = keyWordsTable.get(currWord).frequency + 1;
				}
			}
			
			return keyWordsTable;
		}
		catch (FileNotFoundException e) {
			throw new FileNotFoundException("File does not exist.");
		}
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		
		for (String key: kws.keySet()) {
			Occurrence o = kws.get(key);
			
			if (keywordsIndex.containsKey(key)) { //IF MATCH		
				keywordsIndex.get(key).add(kws.get(key));
				insertLastOccurrence(keywordsIndex.get(key));
			}
			else { //IF NOT MATCH
				ArrayList<Occurrence> occs = new ArrayList<Occurrence>();
				occs.add(o);
				keywordsIndex.put(key, occs);
			}	
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		
		String lowercase = word.toLowerCase();
		boolean hasMiddleSymbol = false, isNoiseWord = false;
		boolean isEndPunc = true;
		
		int endOfStrip = lowercase.length();
		
		//Strip the keyword of symbols
		for (int i = lowercase.length() - 1; i >= 0; i--) {
			char currLetter = lowercase.charAt(i);
			
			if (currLetter == '.' || currLetter == ',' || currLetter == '?' 
					|| currLetter == ':' || currLetter == ';' || currLetter == '!') {
				if (isEndPunc)
					endOfStrip = i;
			}
			else
				isEndPunc = false;
		}
		lowercase = lowercase.substring(0, endOfStrip);
		
		//Word stripped of end symbols
		for (int i = 0; i < lowercase.length(); i++) {
			char currLetter = lowercase.charAt(i);
			
			if (!Character.isLetter(currLetter) && i != lowercase.length()-1) {
				hasMiddleSymbol = true;
			}
		}
		
		//Determine if noise word
		if (noiseWords.contains(lowercase))
			isNoiseWord = true;
		
		//Return
		if (!hasMiddleSymbol && !isNoiseWord) {
			return lowercase;
		}
		else
			return null;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		
		ArrayList<Integer> result = new ArrayList<Integer>(); //only for testing
		
		int indL = 0, indR = occs.size()-2, indM = 0;
		int targetFreq = occs.get(occs.size()-1).frequency, midFreq = 0;
		
		//"Empty" case
		if (occs.size() == 1)
			return null;
		
		//Normal case - Find index
		while (indL < indR) {
			indM = (indL + indR)/2;
			result.add(indM);
			midFreq = occs.get(indM).frequency;
			
			if (targetFreq == midFreq) //found
				break;
			else if (targetFreq < midFreq) //increase L
				indL = indM + 1;
			else if (targetFreq > midFreq) //decrease R
				indR = indM - 1;
		}
		
		//Normal case - enter to occs ArrayList
		Occurrence o = occs.get(occs.size()-1);
		
		if (targetFreq == midFreq || targetFreq < midFreq) {
			occs.add(indM + 1, o);
			occs.remove(occs.size()-1);
		}
		else if (targetFreq > midFreq) {
			occs.add(indM, o);
			occs.remove(occs.size()-1);
		}
			
		return result;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
			ArrayList<Occurrence> resultsInit = new ArrayList<Occurrence>();
			ArrayList<Integer> fromList = new ArrayList<Integer>();
			
			//BUBBLE SORT OCCURENCES LISTS
			ArrayList<Occurrence> kw1List = keywordsIndex.get(kw1);
			ArrayList<Occurrence> kw2List = keywordsIndex.get(kw2);
			
			for (int i = 0; i < kw1List.size()-1; i++) {
				for (int j = 0; j < kw1List.size()-i-1; j++) {
					if (kw1List.get(j).frequency < kw1List.get(j+1).frequency) {
						 Collections.swap(kw1List, j, j+1);
					}
				}
			}
			
			for (int i = 0; i < kw2List.size()-1; i++) {
				for (int j = 0; j < kw2List.size()-i-1; j++) {
					if (kw2List.get(j).frequency < kw2List.get(j+1).frequency) {
						 Collections.swap(kw2List, j, j+1);
					}
				}
			}
			
			//ADD TO MASTER LIST
			if (!keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2)) { //not contained anywhere
				return null;
			}
			else if (kw1List == null && kw2List == null) {
				return null;
			}
			else { //has kw1 and/or kw2
				boolean in1 = false, in2 = false;
				String doc1, doc2;
				int frq1, frq2;
				
				//Parse through List 1
				if (kw1List != null && kw2List != null) {
					for (int i = 0; i < kw1List.size(); i++) {
						in1 = true;
						doc1 = kw1List.get(i).document;
						frq1 = kw1List.get(i).frequency;
						
						for (int j = 0; j < kw2List.size(); j++) {
							doc2 = kw2List.get(j).document;
							frq2 = kw2List.get(j).frequency;
						
							if (doc1.equals(doc2)) {
								in2 = true;
								
								if (frq1 >= frq2) {
									resultsInit.add(kw1List.get(i)); 
									fromList.add(1);
								}
								else if (frq1 < frq2) {
									resultsInit.add(kw2List.get(j));
									fromList.add(2);
								}
							}
						}
						
						if (in1 && !in2) {
							resultsInit.add(kw1List.get(i));
							fromList.add(1);
						}
							
						in1 = false;
						in2 = false;
					}
				}
				else if (kw1List != null && kw2List == null) {
					resultsInit = kw1List;
				}
				
				//Parse through List 2
				if (kw1List == null && kw2List != null) {
					resultsInit = kw2List;
				}
				else {
					boolean alreadyExists = false;
					
					for (int i = 0; i < kw2List.size(); i++) {
						for (int j = 0; j < resultsInit.size(); j++) {
							if (kw2List.get(i).document == resultsInit.get(j).document) {
								alreadyExists = true;
							}
						}
						if (!alreadyExists) {
							resultsInit.add(kw2List.get(i));
							fromList.add(2);
						}
						
						alreadyExists = false;
					}
				}
			}
			
			//BUBBLE SORT INIT LIST
			for (int i = 0; i < resultsInit.size()-1; i++) {
				for (int j = 0; j < resultsInit.size()-i-1; j++) {
					if (resultsInit.get(j).frequency < resultsInit.get(j+1).frequency) {
						 Collections.swap(resultsInit, j, j+1);
					}
				}
			}
			
			//PRECEDENCE SORT INIT LIST
			if (fromList != null) {
				for (int i = 0; i < resultsInit.size(); i++) {
					for (int j = i; j < resultsInit.size(); j++) {
						if (fromList.get(i).intValue() != fromList.get(j).intValue()
								&& resultsInit.get(i).frequency == resultsInit.get(j).frequency) {
							 Collections.swap(resultsInit, i, j);
						}
					}
				}
			}
			
			//TRUNCATE MASTER LIST TO TOP 5
			ArrayList<String> resultsFinal = new ArrayList<String>();
			
			int size = resultsInit.size();
			
			if (size <= 5) {
				for (int i = 0; i < size; i++)
					resultsFinal.add(resultsInit.get(i).document);
			}
			else {
				for (int i = 0; i < 5; i++) {
					resultsFinal.add(resultsInit.get(i).document);
				}
			}
			return resultsFinal;
	}
}