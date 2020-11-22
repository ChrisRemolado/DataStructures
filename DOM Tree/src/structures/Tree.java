package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/
		
		//Empty HTML doc
		if (sc == null) {
			return;
		}
		
		//Base case
		Stack<TagNode> tagStack = new Stack<TagNode>();
		String currLine = sc.nextLine();
		root = new TagNode("html", null, null);
		tagStack.push(root);
		
		while (sc.hasNextLine()) {
			currLine = sc.nextLine();
			TagNode toAdd;
			
			if (currLine.contains("<") && currLine.contains(">") && //THIS IS AN OPENED TAG
					!currLine.contains("/")) {
				if  (tagStack.peek().firstChild == null) { //no first child, this is first child
					//Tokenize tag
					String temp = currLine.replace("<", "");
					temp = temp.replace(">", "");
					toAdd = new TagNode(temp, null, null);
					
					//tag of tag goes under
					tagStack.peek().firstChild = toAdd;
					tagStack.push(toAdd);
				}
				else { //has first child
					//go to end of text line
					TagNode tagPtr = tagStack.peek().firstChild;
					while (tagPtr.sibling != null) {
						tagPtr = tagPtr.sibling;
					}
					
					//Tokenize tag
					String temp = currLine.replace("<", "");
					temp = temp.replace(">", "");
					toAdd = new TagNode(temp, null, null);
					
					//tag goes next to end
					tagPtr.sibling = toAdd;
					tagStack.push(toAdd);
				}
			}
			else if (currLine.contains("<") && currLine.contains(">") 
					&& currLine.contains("/")) { //THIS IS A CLOSED TAG
				tagStack.pop(); //get out of current tag
			}
			else { //THIS IS NORMAL TEXT
				if (tagStack.peek().firstChild == null) { //belongs in special tag
					tagStack.peek().firstChild = new TagNode(currLine, null, null);
				}
				else {//belongs by itself
					//go to the end of text line
					TagNode tagPtr = tagStack.peek().firstChild;
					while (tagPtr.sibling != null) {
						tagPtr = tagPtr.sibling;
					}
					
					//tag goes to the end
					tagPtr.sibling = new TagNode(currLine, null, null);
				}
			}
		}
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		
		//Empty case
		if (root == null) {
			return;
		}
		//Base case
		else {
			replaceTagRecurser(oldTag, newTag, root);
		}
	}
	
	public void replaceTagRecurser(String oldTag1, String newTag1, TagNode tagPtr) {
		/** COMPLETE THIS METHOD **/
		if (tagPtr == null) { //end of tree
			return;
		}
		else { //editor
			if (tagPtr.tag.equals(oldTag1)) {
				tagPtr.tag = newTag1;
			}
		}
		
		//Traverser
		replaceTagRecurser(oldTag1, newTag1, tagPtr.firstChild);
		replaceTagRecurser(oldTag1, newTag1, tagPtr.sibling);
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		TagNode tagPtr = root;
		TagNode tableStart = tableFinder(tagPtr);
		
		//No table found
		if (tableStart == null) {
			System.out.println("There is no table in this html document.");
		}
		
		//Table found
		tagPtr = tableStart.firstChild; //Guaranteed tr tag
		
		//Find row
		for (int i = 1; i < row; i++) {
			tagPtr = tagPtr.sibling;
		}
		
		//Convert all denominations to bold
		tagPtr = tagPtr.firstChild; //Guaranteed td tags
		while (tagPtr != null) {
			tagPtr.firstChild = new TagNode ("b", tagPtr.firstChild, null);

			tagPtr = tagPtr.sibling;
		}
	}
	
	public TagNode tableFinder(TagNode tagPtr1) {
		TagNode result = null;
		
		//Empty case
		if (root == null) {
			return result;
		}
		
		//Base case
		String tagStr = tagPtr1.tag;
		if (tagStr.equals("table")) {
			result = tagPtr1;
			return result;
		}
		
		//Traverser
		if (result == null && tagPtr1.firstChild != null) {
			result = tableFinder(tagPtr1.firstChild);
		}
		if (result == null && tagPtr1.sibling != null) {
			result = tableFinder(tagPtr1.sibling);
		}
		
		//End all be all
		return result;
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		removeTagDoer(tag, root, null);
		
	}
	
	public void removeTagDoer(String tagToRem, TagNode tagPtr, TagNode tagPtrPrev) {
		/** COMPLETE THIS METHOD **/
		//Empty case
		if  (root == null) {
			return;
		}
		
		//Category 1: p, em and b removed
		if (tagToRem.equals(tagPtr.tag) && (tagPtr.tag.equals("p") || 
				tagPtr.tag.equals("em") || tagPtr.tag.equals("b"))) {
			//Pointer has child only
			if (tagPtr.firstChild != null && tagPtr.sibling == null) {
				//if prev.sib = curr
				if (tagPtrPrev.sibling == tagPtr)
					tagPtrPrev.sibling = tagPtr.firstChild;
				//if prev.first = curr
				if (tagPtrPrev.firstChild == tagPtr)
					tagPtrPrev.firstChild = tagPtr.firstChild;
			}
			//Pointer has sibling only
			else if (tagPtr.sibling != null && tagPtr.firstChild == null) {
				//Curr tag child has siblings
				if (tagPtr.firstChild.sibling != null) {
					tagPtrPrev.sibling = tagPtr.firstChild;
					
					//find end of curr child's siblings
					TagNode finder = tagPtr.firstChild;
					while (finder.sibling != null) {
						finder = finder.sibling;
					}
					//set end of siblings to next part of text
					finder.sibling = tagPtr.sibling;
				}
				//"Normal" case
				else {
					tagPtrPrev.sibling = tagPtr.firstChild;
					tagPtr.firstChild.sibling = tagPtrPrev.sibling;
				}
			}
			//Pointer has child and sibling
			else if (tagPtr.sibling != null && tagPtr.firstChild != null) {
				//Curr tag child has siblings
				if (tagPtr.firstChild.sibling != null) {
					tagPtrPrev.sibling = tagPtr.firstChild;
					
					//find end of curr child's siblings
					TagNode finder = tagPtr.firstChild;
					while (finder.sibling != null) {
						finder = finder.sibling;
					}
					//set end of siblings to next part of text
					finder.sibling = tagPtr.sibling;
				}
				//"Normal" case
				else {
					//New target is not a child of a node (if prev.first != curr)
					if (tagPtrPrev.firstChild != tagPtr) {
						tagPtr.firstChild.sibling = tagPtr.sibling;
						tagPtrPrev.sibling = tagPtr.firstChild;
					}
					//New target is a child of a node if (prev.first == curr)
					else if (tagPtrPrev.firstChild == tagPtr) {
						tagPtrPrev.firstChild = tagPtr.firstChild;
						tagPtr.firstChild.sibling = tagPtr.sibling;
					}					
				}
			}	
		}
		
		//Category 2: ol, ul removed. li(s) under become p
		if (tagToRem.equals(tagPtr.tag) && (tagPtr.tag.equals("ol") || 
				tagPtr.tag.equals("ul"))) {
			//Prev has child only
			if (tagPtrPrev.firstChild != null && tagPtrPrev.sibling == null) {
				//Normal case
				tagPtrPrev.firstChild = tagPtr.firstChild;
				
				//li to p
				liUpdater(tagPtr.firstChild);
				
				//find end of curr child's siblings
				TagNode finder = tagPtr.firstChild;
				while (finder.sibling != null) {
					finder = finder.sibling;
				}
				//set end of siblings to next part of text
				finder.sibling = tagPtr.sibling;
			}
			//Prev has sibling only
			else if (tagPtrPrev.firstChild == null && tagPtrPrev.sibling != null) {
				//Normal case
				tagPtrPrev.sibling = tagPtr.firstChild;
				
				//li to p
				liUpdater(tagPtr.firstChild);
				
				//find end of curr child's siblings
				TagNode finder = tagPtr.firstChild;
				while (finder.sibling != null) {
					finder = finder.sibling;
				}
				//set end of siblings to next part of text
				finder.sibling = tagPtr.sibling;
			}
			
			//Prev has child and sibling
			else if (tagPtrPrev.firstChild != null && tagPtrPrev != null) {
				//Normal case
				tagPtrPrev.sibling = tagPtr.firstChild;
				
				//li to p
				liUpdater(tagPtr.firstChild);
				
				//find end of curr child's siblings
				TagNode finder = tagPtr.firstChild;
				while (finder.sibling != null) {
					finder = finder.sibling;
				}
				//set end of siblings to next part of text
				finder.sibling = tagPtr.sibling;
			}
			
		}
		
		//Traverser
		if (tagPtr.firstChild != null)
			removeTagDoer(tagToRem, tagPtr.firstChild, tagPtr);
		if (tagPtr.sibling != null)
			removeTagDoer(tagToRem, tagPtr.sibling, tagPtr);
	}
	
	public void liUpdater(TagNode listNode) {
		while (listNode != null) {
			listNode.tag = "p";
			listNode = listNode.sibling;
		}
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
		addTagDoer(word, tag, root, null);
		
	}
	
	public void addTagDoer(String keyWord, String tagToAdd, TagNode tagPtr, TagNode tagPtrPrev) {
		/** COMPLETE THIS METHOD **/
		
		//Global vars
		TagNode tagPtrBefore = null;
		TagNode tagPtrCurr = null;
		TagNode newTagLbl = null;
		TagNode tagPtrAfter = null;
		
		//Empty case
		if (root == null) {
			return;
		}
		
		//Base case
		if (tagPtr.tag.equals("html") || tagPtr.tag.equals("body") || tagPtr.tag.equals("p") || 
				tagPtr.tag.equals("em") || tagPtr.tag.equals("b") || tagPtr.tag.equals("table") || 
				tagPtr.tag.equals("tr") || tagPtr.tag.equals("td") || tagPtr.tag.equals("ol") || tagPtr.tag.equals("ul") || tagPtr.tag.equals("li")) {
			//do nothing
		}
		else { //This is text
			//Is there an embedded word?
			int currSentLength = tagPtr.tag.length();
			String currSentence = tagPtr.tag;
			boolean matchesWord = false;
					
			int i = 0;
			while (i < currSentLength) {
				if (currSentence.charAt(i) == ' ') { //This is a space
					i++;
				}
				else { //This is a word
					String currWord = "", currWordUpdate = "";
					int startWordInd = i;
					
					while (i < currSentLength && currSentence.charAt(i) != ' ' && !isPunctuation(currSentence.charAt(i))) {
						currWord = currWord + currSentence.charAt(i);
						i++;
					}
					//Update with punctuation
					if (i < currSentLength && isPunctuation(currSentence.charAt(i))) {
						currWordUpdate = currWord + currSentence.charAt(i);
						i++;
					}
					else {
						currWordUpdate = currWord;
					}
					
					int endWordInd = i-1;
					
					//IF THE WORDS MATCH
					if (keyWord.toLowerCase().equals(currWord.toLowerCase())) {
						String before = currSentence.substring(0, startWordInd);
						String curr = currWordUpdate;
						
						//CASE: prev.first = curr
						//Account for end of sentence
						if (i >= currSentLength && tagPtrPrev.firstChild == tagPtr
								&& !tagToAdd.equals(tagPtrPrev.tag)) {							
							 tagPtrBefore = new TagNode(before, null, null);
							 tagPtrCurr = new TagNode(curr, null, null);
							 newTagLbl = new TagNode(tagToAdd, null, null);
							
							if (tagPtrBefore.tag.equals("")) {
								tagPtrPrev.firstChild = newTagLbl;
								newTagLbl.firstChild = tagPtrCurr;
							}
							else {
								tagPtrPrev.firstChild = tagPtrBefore;
								tagPtrBefore.sibling = newTagLbl;
								newTagLbl.firstChild = tagPtrCurr;
							 }
							
							//TRAVERSER UPDATE
							tagPtr = tagPtrCurr;
						}
						//CASE: prev.sib = curr
						//Account for end of sentence
						else if (i >= currSentLength && tagPtrPrev.sibling == tagPtr
								&& !tagToAdd.equals(tagPtrPrev.tag)) {							
							 tagPtrBefore = new TagNode(before, null, null);
							 tagPtrCurr = new TagNode(curr, null, null);
							 newTagLbl = new TagNode(tagToAdd, null, null);
							
							if (tagPtrBefore.tag.equals("")) {
								tagPtrPrev.sibling = newTagLbl;
								newTagLbl.firstChild = tagPtrCurr;
								}
							else {
								tagPtrPrev.sibling = tagPtrBefore;
								tagPtrBefore.sibling = newTagLbl;
							
								newTagLbl.firstChild = tagPtrCurr;
							 }
							
							//TRAVERSER UPDATE
							tagPtr = tagPtrCurr;
						}
						//CASE: prev.first = curr
						//Whole node is word to replace
						else if ((currSentence.toLowerCase()).equals(currWordUpdate.toLowerCase()) && tagPtrPrev.firstChild == tagPtr
								&& !tagToAdd.equals(tagPtrPrev.tag)) {
							tagPtrCurr = new TagNode(curr, null, null);
							newTagLbl = new TagNode(tagToAdd, null, null);
							
							tagPtrPrev.firstChild = newTagLbl;
							newTagLbl.firstChild = tagPtrCurr;
							
							//TRAVERSER UPDATE
							tagPtr = tagPtrCurr;
						}
						//CASE: prev.sib = curr
						//Whole node is word to replace
						else if ((currSentence.toLowerCase()).equals(currWordUpdate.toLowerCase()) && tagPtrPrev.sibling == tagPtr
								&& !tagToAdd.equals(tagPtrPrev.tag)) {
							tagPtrCurr = new TagNode(curr, null, null);
							newTagLbl = new TagNode(tagToAdd, null, null);
							
							tagPtrPrev.sibling = newTagLbl;
							newTagLbl.firstChild = tagPtrCurr;		
							
							//TRAVERSER UPDATE
							tagPtr = tagPtrCurr;
						}
						//CASE: prev.first = curr
						//Base case
						else if (tagPtrPrev.firstChild == tagPtr && !tagToAdd.equals(tagPtrPrev.tag)) {
							String after = currSentence.substring(endWordInd+1, currSentence.length());
							
							 tagPtrBefore = new TagNode(before, null, null);
							 tagPtrCurr = new TagNode(curr, null, null);
							 tagPtrAfter = new TagNode(after, null, null);
							 newTagLbl = new TagNode(tagToAdd, null, null);

							tagPtrPrev.firstChild = tagPtrBefore;
							tagPtrBefore.sibling = newTagLbl;
							newTagLbl.sibling = tagPtrAfter;
							newTagLbl.firstChild = tagPtrCurr;
							tagPtrAfter.sibling = tagPtr.sibling;
						
							newTagLbl.firstChild = tagPtrCurr;
							
							//TRAVERSER UPDATE
							tagPtr = tagPtrCurr;
						}
						//CASE: prev.sib = curr
						//Base case
						else if (tagPtrPrev.sibling == tagPtr && !tagToAdd.equals(tagPtrPrev.tag)) {
							String after = currSentence.substring(endWordInd+1, currSentence.length());
							
							 tagPtrBefore = new TagNode(before, null, null);
							 tagPtrCurr = new TagNode(curr, null, null);
							 tagPtrAfter = new TagNode(after, null, null);
							 newTagLbl = new TagNode(tagToAdd, null, null);
							
							
							tagPtrPrev.sibling = tagPtrBefore;
							tagPtrBefore.sibling = newTagLbl;
							newTagLbl.sibling = tagPtrAfter;
							newTagLbl.firstChild = tagPtrCurr;
							tagPtrAfter.sibling = tagPtr.sibling;
							
							newTagLbl.firstChild = tagPtrCurr;
							
							//TRAVERSER UPDATE
							tagPtr = tagPtrCurr;
						}
					}
				}
			}
			
			//If embedded, what are the boundaries? 
			//Where is the embedded word? What are the new tokens?
			//How do we insert embedded word?	
		}
		
		//Traverser
		if (tagPtr.firstChild != null)
			addTagDoer(keyWord, tagToAdd, tagPtr.firstChild, tagPtr);
		if (tagPtr.sibling != null)
			addTagDoer(keyWord, tagToAdd, tagPtr.sibling, tagPtr);
		
		/*
		if (tagPtr.firstChild != null && tagPtrBefore == null)
			addTagDoer(keyWord, tagToAdd, tagPtr.firstChild, tagPtr);
		else
			addTagDoer(keyWord, tagToAdd, tagPtrCurr, newTagLbl);
		
		if (tagPtr.sibling != null && tagPtrBefore == null)
			addTagDoer(keyWord, tagToAdd, tagPtr.sibling, tagPtr);
		else
			addTagDoer(keyWord, tagToAdd, tagPtrAfter, newTagLbl);
		*/
	}
	
	public boolean isPunctuation(char hm) {
		if (hm == '!' || hm == '?' || hm == '.' || hm == ';' || hm == ':' || hm == '"')
			return true;
		else
			return false;
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}