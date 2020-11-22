package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	//POLYNOMIAL LINKED LIST CONSTRUCTOR
	private Node headOfPolynomial;
	public Polynomial(Node initNode) 
	{
		headOfPolynomial = initNode;
	}
	
	public void setHead(Node headNode) {
		headOfPolynomial = headNode;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	
	public static void store(Node result, Node nextTerm) {
		Node resultPoly1 = result;
		Node tempPoly1 = nextTerm;
		
		while (resultPoly1.next != null){
			resultPoly1 = resultPoly1.next;
		}
		
		resultPoly1.next = tempPoly1;
	}
	
	
	public static void removeZeros(Node poly1) {
		//Node headOfPoly = poly1;
		Node ptrPoly = poly1;
		
		while (ptrPoly != null) {
				//Base case
				if (ptrPoly.next != null && ptrPoly.next.term.coeff == 0) {
					ptrPoly.next = ptrPoly.next.next;
				}
				else {
				ptrPoly = ptrPoly.next;
				}
		}
	}
	
	public static Node add(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		
		Node resultPoly = null; 
		Node tempPoly = null;
		
		Node ptr1 = poly1;
		Node ptr2 = poly2;

		//IF BOTH 0:
		if (poly1 == null && poly2 == null) {
			return null;
		}
		
		while (ptr1 != null || ptr2 != null){
			//PROBLEM: how do you compare degrees if a ptr is null?
			if (ptr1 != null && ptr2 != null) {
				if (ptr1.term.degree == ptr2.term.degree) {
					float coeffSum = ptr1.term.coeff + ptr2.term.coeff;			
					tempPoly = new Node(coeffSum, ptr1.term.degree, null); //Insert right before last term
					ptr1 = ptr1.next;
					ptr2 = ptr2.next;
				}
				
				else if (ptr1.term.degree <= ptr2.term.degree) {
					tempPoly = new Node(ptr1.term.coeff, ptr1.term.degree, null); //Insert right before last term
					ptr1 = ptr1.next;
				}
				else if (ptr1.term.degree >= ptr2.term.degree) {
					tempPoly = new Node(ptr2.term.coeff, ptr2.term.degree, null); //Insert right before last term
					ptr2 = ptr2.next;
				}
			}
			//IF PTR1 IS EMPTY, GO THRU REST OF PTR2
			else if (ptr1 == null) {
				tempPoly = new Node(ptr2.term.coeff, ptr2.term.degree, null); //Insert right before last term
				ptr2 = ptr2.next;
			}
			//IF PTR2 IS EMPTY, GO THRU REST OF PTR1
			else if (ptr2 == null) {
				tempPoly = new Node(ptr1.term.coeff, ptr1.term.degree, null); //Insert right before last term
				ptr1 = ptr1.next;
			}
				
				//CHECK IF 1ST POLY
				if (resultPoly == null) {
					resultPoly = tempPoly;
				}
				else {
					store(resultPoly, tempPoly);
				}	

		}
		
		//REMOVE ZEROS
		removeZeros(resultPoly);
		
		//IF FIRST TERM = 0x^n --> removeZeros():
		if (resultPoly.term.coeff == 0) {
			resultPoly = resultPoly.next;
		}
		return resultPoly;
	}
	
	public static Node findNodeBefore(Node polynomial, Node pointer) {
		Node headofPolynomial = polynomial;
		Node target = pointer;
		Node ptrPolynomial = polynomial;
		
		while (ptrPolynomial.next != target) {
			ptrPolynomial = ptrPolynomial.next;
		}
		
		return ptrPolynomial;
	}
	
	public static void removeSameDegrees(Node poly1) {
		Node thisPoly = poly1;
		Node ptrPoly = poly1;
		
		while (thisPoly != null) {
			while (ptrPoly.next != null) {
				if (thisPoly.term.degree == ptrPoly.next.term.degree) {
					thisPoly.term.coeff *= ptrPoly.next.term.coeff;
					ptrPoly.next = ptrPoly.next.next;
				}
				
				ptrPoly = ptrPoly.next;
			}
			thisPoly = thisPoly.next;
			ptrPoly = thisPoly;
		}
	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		
		Node resultPoly = null; 
		Node tempPoly = null;
		
		Node ptr1 = poly1;
		Node ptr2 = poly2;
		
		//Case if multiply by 0
		if (poly1 == null || poly2 == null) {
			return resultPoly;
		}
		
		//Base case
		while (ptr1 != null) {
			while (ptr2 != null) {
				tempPoly = new Node ((ptr1.term.coeff * ptr2.term.coeff), 
						(ptr1.term.degree + ptr2.term.degree), null);
				
				//CHECK IF 1ST POLY
				if (resultPoly == null) {
					resultPoly = tempPoly;
				}
				else {
					store(resultPoly, tempPoly);
				}
							
				ptr2 = ptr2.next;
			}			
			ptr1 = ptr1.next;
			ptr2 = poly2;
		}
		
		removeSameDegrees(resultPoly);
		removeZeros(resultPoly);
		
		//IF FIRST TERM = 0x^n --> removeZeros():
		if (resultPoly.term.coeff == 0) {
			resultPoly = resultPoly.next;
		}
		
		return resultPoly;
	}
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		/** COMPLETE THIS METHOD **/
		float result = 0;
		
		while (poly != null) //for each polynomial
		{
			double temp = 0;
			temp = Math.pow(x, poly.term.degree);
			result += poly.term.coeff * temp;
			
			poly = poly.next;
		}
		return result;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}