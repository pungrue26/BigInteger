/**
 * @author kimjoonho
 *
 */
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BigInteger {
	private static boolean isResultMinus = false;

	private static String [] extractedSigns = new String [3];
	private static int extractedSignsNum = 0;
	private static int [] extractedSignsStartPos = new int [3];
	private static int caculationResult [] = null;
	
	public static void main(String args[]) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				String input = br.readLine();
				if (input.compareTo("quit") == 0) {
					break;
				}
				calculate(input);
			}
			catch (Exception e) {
				System.out.println("Exception occured! : " + e.toString());
				e.printStackTrace();
			}
		}
	}

	private static void calculate(String input) {
		// remove all white spaces in input string
		input = input.replaceAll("\\s+","");
		// extract all sign characters, and
		// put them into the string array
		Pattern pattern = Pattern.compile("[\\Q+\\E\\Q-\\E\\Q*\\E]");
		Matcher matcher = pattern.matcher(input);
		
		while (matcher.find()) {
        	extractedSigns[extractedSignsNum] = matcher.group();
        	extractedSignsStartPos[extractedSignsNum] =  matcher.start();
        	extractedSignsNum++;
        }
		
		// This logic assumes that signs appear at most 3 times.
		// Because assignment specification allows to do that. :)
		if(extractedSignsNum == 1) {
			// a (+,-,*) b
			handleOneSign(input);
		} else if (extractedSignsNum == 2) {
			// (+,-) a (+,-,*) b
			// a (+,-,*) (+,-) b
			handleTwoSigns(input);
		} else if(extractedSignsNum == 3) {
			// (+,-)a (+,-,*) (+,-)b
			handleThreeSigns(input);
		}
		printResult(caculationResult);
	}
	
	private static void handleThreeSigns(String input) {
		String operationSymbol = extractedSigns[1];
		String signOfFirstOperand = extractedSigns[0];
		String signOfSecondOperand = extractedSigns[2];
		
		int operationSymbolStartPos = extractedSignsStartPos[1];
		// firstOperand's length is symbolStartPos-1, Cause there is 1 sign character.
		byte[] firstOperand = new byte[operationSymbolStartPos-1];
		for(int i = 0, j = 1; j < operationSymbolStartPos; i++, j++){
			firstOperand[i] = Byte.parseByte(String.valueOf(input.charAt(j))); 
		}
		byte[] secondOperand = new byte[input.length() - (operationSymbolStartPos+2)];
		for(int k = 0, l = operationSymbolStartPos + 2; l < input.length(); k++, l++){
			secondOperand[k] = Byte.parseByte(String.valueOf(input.charAt(l))); 
		}
		
		if(operationSymbol.equals("+")) {
			if(signOfFirstOperand.equals("+") && signOfSecondOperand.equals("+")) {
				// (+a) + (+b) = a + b
				caculationResult = addition(firstOperand, secondOperand);
			} else if(signOfFirstOperand.equals("+") && signOfSecondOperand.equals("-")) {
				// (+a) + (-b) = a - b
				caculationResult = subtraction(firstOperand, secondOperand);
			} else if(signOfFirstOperand.equals("-") && signOfSecondOperand.equals("+")) {
				// (-a) + (+b) = b - a
				caculationResult = subtraction(secondOperand, firstOperand);
			} else if(signOfFirstOperand.equals("-") && signOfSecondOperand.equals("-")) {
				// (-a) + (-b) = -(a + b)
				isResultMinus = true;
				caculationResult = addition(secondOperand, firstOperand);					
			}
		} else if(operationSymbol.equals("-")) {
			if(signOfFirstOperand.equals("+") && signOfSecondOperand.equals("+")) {
				// (+a) - (+b) = a - b
				caculationResult = subtraction(firstOperand, secondOperand);
			} else if(signOfFirstOperand.equals("+") && signOfSecondOperand.equals("-")) {
				// (+a) - (-b) = a + b
				caculationResult = addition(firstOperand, secondOperand);
			} else if(signOfFirstOperand.equals("-") && signOfSecondOperand.equals("+")) {
				// (-a) - (+b) = -a - b = -(a + b)
				isResultMinus = true;
				caculationResult = addition(firstOperand, secondOperand);
			} else if(signOfFirstOperand.equals("-") && signOfSecondOperand.equals("-")) {
				// (-a) - (-b) = -a + b = b - a
				caculationResult = subtraction(secondOperand, firstOperand);
			}
		} else if(operationSymbol.equals("*")) {
			if(signOfFirstOperand.equals("+") && signOfSecondOperand.equals("+")) {
				// (+a) * (+b) = a * b
				caculationResult = multiplication(firstOperand, secondOperand);
			} else if(signOfFirstOperand.equals("+") && signOfSecondOperand.equals("-")) {
				// (+a) * (-b) = -(a * b)
				isResultMinus = true;
				caculationResult = multiplication(firstOperand, secondOperand);
			} else if(signOfFirstOperand.equals("-") && signOfSecondOperand.equals("+")) {
				// (-a) * (+b) = -(a * b)
				isResultMinus = true;
				caculationResult = multiplication(firstOperand, secondOperand);
			} else if(signOfFirstOperand.equals("-") && signOfSecondOperand.equals("-")) {
				// (-a) * (-b) = a * b
				caculationResult = multiplication(firstOperand, secondOperand);					
			}
		}		
	}

	private static void handleTwoSigns(String input) {
		// This case means one character is a sign of operand,
		// and the other is the symbol of operation. 
		// We can tell it from checking the first character of input string.
		if(extractedSignsStartPos[0] == 0) {
			// (+,-) a (+,-,*) b
			// The sign appeared first is sign of first operand, and
			// the second sign character is the symbol of operation.
			String signOfFirstOp = extractedSigns[0];
			String operationSymbol = extractedSigns[1];
			int operationSymbolStartPos = extractedSignsStartPos[1];
			// firstOperand's length is symbolStartPos-1, Cause 1 sign character is included in.
			byte[] firstOperand = new byte[operationSymbolStartPos-1];
			for(int i = 0, j = 1; j < operationSymbolStartPos; i++, j++){
				firstOperand[i] = Byte.parseByte(String.valueOf(input.charAt(j))); 
			}
			byte[] secondOperand = new byte[input.length() - (operationSymbolStartPos+1)];
			for(int k = 0, l = operationSymbolStartPos + 1; l < input.length(); k++, l++){
				secondOperand[k] = Byte.parseByte(String.valueOf(input.charAt(l))); 
			}
			if(operationSymbol.equals("+")) {
				if(signOfFirstOp.equals("+")) {
					// +a + b
					caculationResult = addition(firstOperand, secondOperand);	
				} else if(signOfFirstOp.equals("-")) {
					// -a + b = b - a
					caculationResult = subtraction(secondOperand, firstOperand);
				}
			} else if(operationSymbol.equals("-")) {
				if(signOfFirstOp.equals("+")){
					// +a - b = a - b
					caculationResult = subtraction(firstOperand, secondOperand);
				} else if(signOfFirstOp.equals("-")) {
					// -a - b = -(a + b)
					isResultMinus = true;
					caculationResult = addition(firstOperand, secondOperand);
				}
			} else if(operationSymbol.equals("*")) {
				if(signOfFirstOp.equals("+")){
					// +a * b = a * b
					caculationResult = multiplication(firstOperand, secondOperand);	
				} else if(signOfFirstOp.equals("-")){
					// -a * b = -(a * b)
					isResultMinus = true;
					caculationResult = multiplication(firstOperand, secondOperand);
				}
			}
		} else {
			// a (+,-,*) (+,-) b
			String operationSymbol = extractedSigns[0];
			String signOfSecondOperand = extractedSigns[1];
			int operationSymbolStartPos = extractedSignsStartPos[0];
			byte[] firstOperand = new byte[operationSymbolStartPos];
			for(int i = 0; i < operationSymbolStartPos; i++){
				firstOperand[i] = Byte.parseByte(String.valueOf(input.charAt(i))); 
			}
			byte[] secondOperand = new byte[input.length() - (operationSymbolStartPos+2)];
			for(int j = 0, k = operationSymbolStartPos + 2; k < input.length(); j++, k++){
				secondOperand[j] = Byte.parseByte(String.valueOf(input.charAt(k))); 
			}
			if(operationSymbol.equals("+")) {
				if(signOfSecondOperand.equals("+")) {
					// a + (+b) = a + b
					caculationResult = addition(firstOperand, secondOperand);
				} else if(extractedSigns[1].equals("-")) {
					// a + (-b) = a - b
					caculationResult = subtraction(firstOperand, secondOperand);
				}
			} else if(operationSymbol.equals("-")) {
				if(signOfSecondOperand.equals("+")) {
					// a - (+b) = a - b
					caculationResult = subtraction(firstOperand, secondOperand);
				} else if(signOfSecondOperand.equals("-")) {
					// a - (-b) = a + b
					caculationResult = addition(firstOperand, secondOperand);
				}
			} else if(operationSymbol.equals("*")) {
				if(signOfSecondOperand.equals("+")) {
					// a * (+b) = a * b
					caculationResult = multiplication(firstOperand, secondOperand);
				} else if(signOfSecondOperand.equals("-")) {
					// a * (-b) = - (a * b)
					isResultMinus = true;
					caculationResult = multiplication(firstOperand, secondOperand);
				}
			}
		}		
	}

	private static void handleOneSign(String input) {
		String operationSymbol = extractedSigns[0];
		int operationSymbolStartPos = extractedSignsStartPos[0];
		
		if(operationSymbolStartPos == 0) {
			throw new IllegalArgumentException("Input isn't correct.");
		}
		
		byte[] firstOperand = new byte[operationSymbolStartPos];
		for(int i = 0; i < operationSymbolStartPos; i++){
			firstOperand[i] = Byte.parseByte(String.valueOf(input.charAt(i))); 
		}
		byte[] secondOperand = new byte[input.length() - (operationSymbolStartPos+1)];
		for(int j = 0, k = operationSymbolStartPos + 1; k < input.length(); j++, k++){
			secondOperand[j] = Byte.parseByte(String.valueOf(input.charAt(k))); 
		}	
		if(operationSymbol.equals("+")) {
			caculationResult = addition(firstOperand, secondOperand);
		} else if(operationSymbol.equals("-")) {
			caculationResult = subtraction(firstOperand, secondOperand);
		} else if(operationSymbol.equals("*")) {
			caculationResult = multiplication(firstOperand, secondOperand);
		}		
	}

	private static int [] addition(byte[] firstOperand, byte[] secondOperand) {
		int firstOperandLength = firstOperand.length;
		int secondOperandLength = secondOperand.length;
		int longerLength = Math.max(firstOperandLength, secondOperandLength);
		int shorterLength = Math.min(firstOperandLength, secondOperandLength);
		byte [] longerArray = firstOperandLength > secondOperandLength? firstOperand : secondOperand;
		
		int resultLength = longerLength + 1;
		int[] result = new int[resultLength];
		for(int i = 1; i <= shorterLength; i++) {
			result[resultLength - i] = firstOperand[firstOperandLength - i] + secondOperand[secondOperandLength - i];
		}
		for(int i = shorterLength + 1; i <= longerLength; i++) {
			result[resultLength - i] = longerArray[longerLength - i];
		}
		for(int i = 1; i < resultLength; i++) {
			if(result[resultLength - i] >=10) {
				result[resultLength - i] = result[resultLength - i] - 10;
				result[resultLength - i - 1]++;
			}
		}
		return result;
	}
	
	private static int[] subtraction(byte[] firstOperand, byte[] secondOperand) {
		// This method handles "a - b" format, and a, b both are positive value. 
		// Also, a can be smaller than b. So if a < b, we will treat it as -(b - a).
		int longerLength = Math.max(firstOperand.length, secondOperand.length);
		int shorterLength = Math.min(firstOperand.length, secondOperand.length);
		byte[] biggerOperand = new byte[longerLength];
		byte[] smallerOperand = new byte[shorterLength];

		// First, check whether the result is less than 0 or not, so we have to set isResultMinus to true.
		if(firstOperand.length > secondOperand.length) {
			biggerOperand = firstOperand;
			smallerOperand = secondOperand;
		} else if (firstOperand.length < secondOperand.length) {
			isResultMinus = true;
			biggerOperand = secondOperand;
			smallerOperand = firstOperand;
		} else {
			// firstOperand's length and secondOperand's length are same.
			// scan both operand from leftmost to last to which one is larger than the other. 
			for(int i = 0; i < longerLength; i++){
				if(firstOperand[i] > secondOperand[i]) {
					biggerOperand = firstOperand;
					smallerOperand = secondOperand;
					break;
				} else if(firstOperand[i] < secondOperand[i]) {
					isResultMinus = true;
					biggerOperand = secondOperand;
					smallerOperand = firstOperand;
					break;
				} else {
					if(i == longerLength - 1) {
						// firstOperand and secondOperand have exactly same value.
						return new int[1];
					} else {
						continue;
					}
				}
			}
		}
		
		int [] result = new int[longerLength];
		for(int i = 1; i <= shorterLength; i++) {
			if(biggerOperand[longerLength - i] >= smallerOperand[shorterLength - i]) {
				result[longerLength - i] = biggerOperand[longerLength - i] - smallerOperand[shorterLength - i];
			} else {
				result[longerLength - i] = 10 + biggerOperand[longerLength - i] - smallerOperand[shorterLength - i];
				biggerOperand[longerLength - i - 1]--;
			}
		}
		
		for(int i = shorterLength + 1 ; i <= longerLength; i++) {
			if(biggerOperand[longerLength - i] == -1){
				result[longerLength - i] = biggerOperand[longerLength - i] + 10;
				biggerOperand[longerLength - i - 1]--;
			} else {
				result[longerLength - i] = biggerOperand[longerLength - i];
			}
		}
		return result;
	}
	
	private static int[] multiplication(byte[] firstOperand, byte[] secondOperand) {
		byte [] biggerOperand, smallerOperand;
		if(firstOperand.length > secondOperand.length) {
			biggerOperand = firstOperand;
			smallerOperand = secondOperand;
		} else {
			biggerOperand = secondOperand;
			smallerOperand = firstOperand;
		}
		
		int resultLength = firstOperand.length + secondOperand.length;
		int[] result = new int[resultLength];
		for(int i = 1; i <= smallerOperand.length; i++) {
			for(int j = 1; j <= biggerOperand.length; j++) {
				result[resultLength - j - i + 1] += biggerOperand[biggerOperand.length - j] * smallerOperand[smallerOperand.length - i];
			}
		}
		for(int i = 1; i < resultLength; i++) {
			if(result[resultLength - i] >=10) {
				result[resultLength - i - 1] += result[resultLength - i] / 10;
				result[resultLength - i] = result[resultLength - i] % 10;
			}
		}
		return result;
	}
	
	private static void printResult(int[] result) {
		// There can be one or more '0's in front of result.
		// remove all those '0's.
		int numOf0Counter = 0;
		for(int i = 0; i < result.length; i++) {
			if(result[i] == 0) {
				numOf0Counter++;
				continue;
			} 
			break;
		}
		
		StringBuffer sb = new StringBuffer();
		for(int i = numOf0Counter; i < result.length; i++) {
			sb.append(result[i]);
		}
		
		String calculationReslut = sb.toString();
		if(isResultMinus) {
			calculationReslut = "-" + calculationReslut;
			isResultMinus = false;
		}
		System.out.println("result : " + calculationReslut);
	}
}