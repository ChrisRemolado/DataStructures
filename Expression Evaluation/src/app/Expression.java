package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
	
	public static Stack varStack = new Stack();
	public static Stack opStack = new Stack();
	public static String currToken;
	public static StringTokenizer st1;
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	    	
    	StringTokenizer st2 = new StringTokenizer(expr, delims, true);
    	String currToken1, currToken2, nextStr;
    	boolean isDelim = true;
    	/*
    	while (st2.hasMoreTokens()) {
    		currToken1 = st2.nextToken();
    		isDelim = (currToken.contains("*")) || (currToken.contains("/")) ||
			(currToken.contains("+")) || (currToken.contains("-")) ||
			(currToken.contains("(")) || (currToken.contains(")")) ||
			(currToken.contains("[")) || (currToken.contains("]"));
    		    		
    		if (isDelim == false) {
    			//check if next has [
    			expr.contains(currToken1);
    			nextStr = ;
    			
    			
    			if () {
    				Array tempVar = new Array(currToken1);
    	            arrays.add(tempVar); 
    			}
    			else {
    	    		Variable tempVar = new Variable(currToken1);
    	            vars.add(tempVar); 
    	    		}
    			isDelim = true;
    		}
    		
    	
        //Problem: What about arrays?
            
    	}
    	*/
    	
    	int init = 0;
    	int i = 0;
    	String subStr;
    	//boolean flag = false;
    	
    	//Remove spaces
    	String withoutspaces = "";
        for (int m = 0; m < expr.length(); m++) {
            if (expr.charAt(m) != ' ')
                withoutspaces += expr.charAt(m);
        }
        expr = withoutspaces;
    	
    	//Check if start of expr is op
		while (expr.charAt(init+i) == '(' || expr.charAt(init+i) == '[') {
			init += 1;
		}
    	
		//Base case - not last term
    	while (init+i < expr.length()) {
    		//This is a var
    		if (expr.charAt(init+i) == '*' || expr.charAt(init+i) == '/' || 
    				expr.charAt(init+i) == '+' || expr.charAt(init+i) == '-' || 
    				expr.charAt(init+i) == '(' || expr.charAt(init+i) == ')' || 
    				expr.charAt(init+i) == ']') {
    			subStr = expr.substring(init, init+i);
    			Variable tempVar = new Variable(subStr);
    			vars.add(tempVar);
    			    			
    			//Place at first instance of non op
    			init += i+1;
    			i = 0;
    			while (init < expr.length() && (expr.charAt(init+i) == '*' || expr.charAt(init+i) == '/' || 
        				expr.charAt(init+i) == '+' || expr.charAt(init+i) == '-' || 
        				expr.charAt(init+i) == '(' || expr.charAt(init+i) == ')' || 
        				expr.charAt(init+i) == ']')) {
    				init += 1;
    			}
    			
    		}
    		//This is an array
    		else if (expr.charAt(init+i) == '['){ 
    			subStr = expr.substring(init, init+i);
    			Array tempArr = new Array(subStr);
    			arrays.add(tempArr);
    			
    			//Place at first instance of non op
    			init += i+1;
    			i = 0;
    			while (init < expr.length() && (expr.charAt(init+i) == '*' || expr.charAt(init+i) == '/' || 
        				expr.charAt(init+i) == '+' || expr.charAt(init+i) == '-' || 
        				expr.charAt(init+i) == '(' || expr.charAt(init+i) == ')' || 
        				expr.charAt(init+i) == ']')) {
    				init += 1;
    			}
    			
    			/*if (init != expr.length()) {
	    			while (flag == false && (expr.charAt(init+i) == '*' || expr.charAt(init+i) == '/' || 
	        				expr.charAt(init+i) == '+' || expr.charAt(init+i) == '-' || 
	        				expr.charAt(init+i) == '(' || expr.charAt(init+i) == ')' || 
	        				expr.charAt(init+i) == '[' || expr.charAt(init+i) == ']')) {
	    			init += 1;
	    			if (init == expr.length())
	    				flag = true;
	    			}
    			}*/
    		}
    		
    		i++;
    	}
    	
    	//Last poss var
    	int k = 0;
		if (init < expr.length()) {
			while ((init+k < expr.length()) &&
					(expr.charAt(init+k) != ')' && expr.charAt(init+k) != ']')) {				
				k++;
			}
			subStr = expr.substring(init, init+k);
			Variable tempVar = new Variable(subStr);
			vars.add(tempVar);			
		}
    			
		//Check for numerical vars
		for (int n = 0; n < vars.size(); n++) {
			String currVar = vars.get(n).name;
			Character ch = currVar.charAt(0);
			if (Character.isDigit(ch))
				vars.remove(n);
		}
		
		//Good tests: //abc+B[(xyz)], (varx + vary*varz[(vara+varb[(a+b)*33])])/55
    	
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    public static float getVariableValue(ArrayList<Variable> vars1, String currToken1) {
    	float temp1 = 0;
    	Character ch;
    	boolean isNum = true;
    	
    	//CHECK IF NON-STRING VAR
    	int i = 0;

    	while (isNum && i < currToken1.length()) {
    		ch = currToken1.charAt(i);
    		if (Character.isDigit(ch) == false && ch.equals(".") == false) {
    			isNum = false;
    		}
    		i++;
    	}
    	if (isNum) {
    		temp1 = Float.parseFloat(currToken1);
    		return temp1;
    	}
    	
    	//IS A STRING VAR
    	for (int j = 0; j < vars1.size(); j++) {
			if (currToken1.equals(vars1.get(j).name)) {
				temp1 = vars1.get(j).value;
				return temp1;
				}
			}
    	return temp1;
    }
    
    public static float getArrayValue(ArrayList<Array> arrs1, String currToken1, int index1) {
    	float temp1 = 0;
    	Array correctArr;
    	    	
    	//IS A STRING VAR
    	for (int j = 0; j < arrs1.size(); j++) {
			if (currToken1.equals(arrs1.get(j).name)) {
				correctArr = arrs1.get(j);
				int primArr[] = correctArr.values;
				
				temp1 = primArr[index1];
				return temp1;
				}
			}
    	return temp1;
    }
    

    public static void multOrDiv(String op1, ArrayList<Variable> vars1) {
    	float varX, varY, temp;
    	
    	switch (op1) {
		case "*":
			currToken = st1.nextToken();
			varStack.push(getVariableValue(vars1, currToken));
			varY = Float.parseFloat(varStack.pop().toString());
			varX = Float.parseFloat(varStack.pop().toString());
			temp = varX * varY;
			varStack.push(temp);
			opStack.pop();
			break;
		case "/":
			currToken = st1.nextToken();
			varStack.push(getVariableValue(vars1, currToken));
			varY = Float.parseFloat(varStack.pop().toString());
			varX = Float.parseFloat(varStack.pop().toString());
			temp = varX / varY;
			varStack.push(temp);
			opStack.pop();
			break;
		default:
			//System.out.println("Error");
			break;
			}
    }
    
    public static void addOrSub(String op1, ArrayList<Variable> vars1) {
    	float varX, varY, temp;
    	
    	switch (op1) {
		case "+":
			currToken = st1.nextToken();
			varStack.push(getVariableValue(vars1, currToken));
			varY = Float.parseFloat(varStack.pop().toString());
			varX = Float.parseFloat(varStack.pop().toString());
			temp = varX + varY;
			varStack.push(temp);
			opStack.pop();
			break;
		case "-":
			currToken = st1.nextToken();
			varStack.push(getVariableValue(vars1, currToken));
			varY = Float.parseFloat(varStack.pop().toString());
			varX = Float.parseFloat(varStack.pop().toString());
			temp = varX - varY;
			varStack.push(temp);
			opStack.pop();
			break;
		default:
			System.out.println("Error");
			break;
		}
    }
    
    public static void addOrSubAfter(ArrayList<Variable> vars1) {
    	float varX, varY, temp;
    	String op;
    	
    	op = (String) opStack.pop(); //know for sure to pop
		switch (op) {
		case "+":
			varY = Float.parseFloat(varStack.pop().toString());
			varX = Float.parseFloat(varStack.pop().toString());
			temp = varX + varY;
			varStack.push(temp);
			break;
		case "-":
			varY = Float.parseFloat(varStack.pop().toString());
			varX = Float.parseFloat(varStack.pop().toString());
			temp = varX - varY;
			varStack.push(temp);
			break;
		default:
			System.out.println("Error");
			break;
		}
    }
    
    public static float doOp(String op, String var1, String var2) {
    	float varY = Float.parseFloat(var1);
		float varX = Float.parseFloat(var2);
		float result = 0;
		
		switch (op){
			case "+":
				result = varX + varY;
				break;
			case "-":
				result = varX - varY;
				break;			
			case "*":
				result = varX * varY;
				break;
			case "/":
				result = varX / varY;
				break;
			default:
				System.out.println("Error");
				break;					
		}
		
		return result;
    }
    
    public static boolean hasPrecedence(String op1, String op2) {
    	if (op2.equals("(") || op2.equals(")")) {
    		return false;
    	}
    	if ((op1.equals("*") || op1.equals("/")) && 
    			(op2.equals("+") || op2.equals("-"))) {
    		return false;
    	}
    	else {
    		return true;
    	}
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	
    	//Stack varStack = new Stack();
    	//Stack opStack = new Stack();
    	//String currToken;
    	boolean hasMultDiv = false;
    	boolean isArray = false;
    	
    	String op;
    	float varX, varY, temp;
    	int tempInd;
    	String tempTok;
    	String arrName = "";
    	
    	//Detect Mult or Div
    	st1 = new StringTokenizer(expr, delims, true);
    	while (st1.hasMoreTokens() && hasMultDiv == false) {
    		currToken = st1.nextToken();
    		if (currToken.equals("*") || currToken.equals("/")) {
    			hasMultDiv = true;
    		}
    	}
    	
    	//Remove spaces
    	String withoutspaces = "";
        for (int m = 0; m < expr.length(); m++) {
            if (expr.charAt(m) != ' ')
                withoutspaces += expr.charAt(m);
        }
        expr = withoutspaces;
    	
    	//1 VAR CASE (no arrays)
    	st1 = new StringTokenizer(expr, delims, true);
    	currToken = st1.nextToken();
    	if  (st1.hasMoreTokens() == false) {
    		return getVariableValue(vars, currToken);
    	}
    	
    	//BASE CASE
    	st1 = new StringTokenizer(expr, delims, true);
    	while (st1.hasMoreTokens()) {
    		currToken = st1.nextToken();
    		isArray = false;
    		
    		//CHECK IF ARRAY NAME
    		for (int a = 0; a < arrays.size(); a++) {
    			if (currToken.equals(arrays.get(a).name)) {
        			isArray = true;
        		}
    		}
    		
    		//ADDING TO STACK
    		if (currToken.equals("(")) { //parenthesis open
    			opStack.push(currToken);
    		}
    		else if (currToken.equals(")")){ //parenthesis close
    			  while (!opStack.peek().equals("(")) { 
                      temp = doOp(opStack.pop().toString(), varStack.pop().toString(), varStack.pop().toString()); 
                      varStack.push(temp);
    			  }   
                  opStack.pop(); 
    		}
    		else if (currToken.equals("*") || currToken.equals("/") 
    				|| currToken.equals("+") || currToken.equals("-") ) { //operation
    			
    			while (!opStack.isEmpty() && hasPrecedence(currToken, opStack.peek().toString())) { 
                    temp = doOp(opStack.pop().toString(), varStack.pop().toString(), varStack.pop().toString()); 
    				varStack.push(temp);
    			}
                  opStack.push(currToken);
    		}    		
    		else if (currToken.equals("[")) { //THIS IS ARRAY
    			opStack.push(currToken);
    			isArray = false;
    			
    			//Store all tokens
    			while(!currToken.equals("]")) {/*!opStack.peek().equals("[")*/
    				currToken = st1.nextToken();
    				
    				/*if (currToken.equals("[")) { //bracket open
    	    			opStack.push(currToken);
    	    		}
    				else if (currToken.equals("]")){ //bracket close
  	    			  while (!opStack.peek().equals("[")) { 
  	                      temp = doOp(opStack.pop().toString(), varStack.pop().toString(), varStack.pop().toString()); 
  	                      varStack.push(temp);
  	    			  }   
  	                  opStack.pop();
    				}*/
    				if (currToken.equals("(")) { //parenthesis open
    	    			opStack.push(currToken);
    	    		}
    	    		else if (currToken.equals(")")){ //parenthesis close
    	    			  while (!opStack.peek().equals("(")) { 
    	                      temp = doOp(opStack.pop().toString(), varStack.pop().toString(), varStack.pop().toString()); 
    	                      varStack.push(temp);
    	    			  }   
    	                  opStack.pop(); 
    	    		}
    				else if (currToken.equals("*") || currToken.equals("/") 
	        				|| currToken.equals("+") || currToken.equals("-")) { //operation
	        			
	        			while (/*!opStack.isEmpty() &&*/ !opStack.peek().equals("[") && hasPrecedence(currToken, opStack.peek().toString())) { 
	        				Stack varT = varStack;
    	                    Stack opsT = opStack;
	        				temp = doOp(opStack.pop().toString(), varStack.pop().toString(), varStack.pop().toString()); 
	        				varStack.push(temp);
	        			}
	                      opStack.push(currToken);
	        		}    		
	    			else /*if (!isArray)*/ { //variable or number
	    				if (!currToken.equals("]"))
	        			 varStack.push(getVariableValue(vars, currToken));
	        		}
    			}
    			
    			/*while (!currToken.equals("]")) {
    				currToken = st1.nextToken();
    				
    				if (currToken.contentEquals("(") || currToken.contentEquals(")") || 
    						currToken.contentEquals("+") || currToken.contentEquals("-") || 
    						currToken.contentEquals("*") || currToken.contentEquals("/")) {
    					opStack.push(currToken);
    				}
    				else {
    					varStack.push(getVariableValue(vars, currToken));
    				}
    			}
    			varStack.pop(); //pop ]
    			
    			//Calculate index
    			while (!opStack.isEmpty() && !opStack.peek().equals("[")) { //do ops in []
    				tempTok = opStack.pop().toString();
    				
    				if (tempTok.equals("*") || tempTok.equals("/") 
    	    				|| tempTok.equals("+") || tempTok.equals("-") ) { //operation
    	    			
    	    			while (!opStack.peek().equals("[") && !opStack.isEmpty() && hasPrecedence(tempTok, opStack.peek().toString())) { 
    	                    Stack varT = varStack;
    	                    Stack opsT = opStack;
    	    				temp = doOp(tempTok, varStack.pop().toString(), varStack.pop().toString()); 
    	    				varStack.push(temp);
    	    			}
    	    			//opStack.push(tempTok);
    	    		}
    	    		else { //variable or number
    	    			 varStack.push(getVariableValue(vars, tempTok));
    	    		}
    			}
    			*/
    			
    			//opStack.pop(); //pop ]
    			
    			//Do the remainder of stacks within [
    	    	while (!opStack.peek().equals("[")) {
    					varStack.push(doOp(opStack.pop().toString(), varStack.pop().toString(), varStack.pop().toString())); 		  
    			 }
    			
    			//CALCULATE REAL value from index
    			Stack varT = varStack;
                Stack opsT = opStack;
    			float tempFl = Float.parseFloat(varStack.pop().toString());
    			tempInd = (int) Math.floor(tempFl);
    			temp = getArrayValue(arrays, arrName, tempInd);
    			varStack.push(temp);
    			
    			opStack.pop(); //pop [
    		}
    		else if (isArray) { //store Array name
    			arrName = currToken;
    		}
    		else { //variable or number
    			if (!isArray) {
    			 varStack.push(getVariableValue(vars, currToken));
    			}
    		}
    	
    	}
    	
    	//Do the remainder of stacks
    	while (opStack.isEmpty() == false) {
				varStack.push(doOp(opStack.pop().toString(), varStack.pop().toString(), varStack.pop().toString())); 		  
		 }
    	
    	float result = Float.parseFloat(varStack.pop().toString());
    	return result;
    }
}