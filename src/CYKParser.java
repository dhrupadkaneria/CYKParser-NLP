import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

public class CYKParser {

	@SuppressWarnings({ "resource" })
	public static void main(String[] args) throws Exception 
	{
		// TODO Auto-generated method stub
		String temp_prod = null;
		String r = "";
		String lhs = "";
		String rhs = "";
		boolean lhsFlag = false;
		boolean rhsFlag = false;
		int pointer = -1;
		String[][] matrix = null;
		HashMap<String, String> grammar = new HashMap<String, String>();
		FileInputStream fis = new FileInputStream("./src/inputGrammar4.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        int num_prod = Integer.parseInt(reader.readLine());
        System.out.println("Number of productions: " + num_prod);
		
		String start = reader.readLine();
		System.out.println("Start variable: " + start);
		String str = reader.readLine();
		System.out.println("String to be searched: " + str);
		System.out.println("\nProduction rules:");
		
		/*
		 * In this loop, the production rules are fetched from a file
		 * 		- Once the rule is fetched, we check if it is in CNF or no
		 * 		- If the rule is in CNF, we proceed further and save it,
		 * 		- If the rule is not in CNF, we terminate the execution
		 */
		for(int i = 0; i < num_prod; ++i)
		{
			lhsFlag = false;
			rhsFlag = false;
			pointer = -1;
			temp_prod = reader.readLine();
			System.out.println(temp_prod);
			pointer = temp_prod.indexOf("->");
			if(pointer != -1)
			{
				lhs = temp_prod.substring(0, pointer);
				rhs = temp_prod.substring(pointer + 2, temp_prod.length());
				rhs = rhs.replaceAll("\\|", ",");
				lhsFlag = checkLHS(lhs);
				if(lhsFlag == false)
				{
					System.out.println("\nGrammar not in CNF\nLHS not valid\nExiting");
					System.exit(0);
				}
				if(rhs.indexOf(",") == -1)
				{
					rhsFlag = checkRHS(rhs);
					if(rhsFlag == false)
					{
						System.out.println("\nGrammar not in CNF\nRHS not valid\nExiting");
						System.exit(0);
					}
				}
				else
				{
					String[] newRHS = rhs.split(",");
					for(int j = 0; j < newRHS.length; ++j)
					{
						rhsFlag = false;
						if(!rhsFlag)
						{
							rhsFlag = checkRHS(newRHS[j]);
							if(rhsFlag == false)
							{
								System.out.println("\nGrammar not in CNF\nRHS not valid\nExiting");
								System.exit(0);
							}
						}
					}
				}
			}
			else if(pointer == -1)
			{
				System.out.println("\nGrammar not in CNF\nNo -> present\nExiting");
				System.exit(0);
			}
			
			if(grammar.containsKey(lhs))
			{
				String old = grammar.get(lhs);
				grammar.remove(lhs);
				grammar.put(lhs.trim(), old.trim() +"," + rhs.trim());
			}
			else
			{
				grammar.put(lhs.trim(), rhs.trim());
		
			}
		}
		System.out.println("\nGrammar: " + grammar);
		
		String[] checkString = str.split(" ");
		int strLength = checkString.length;
		matrix = new String[strLength][strLength];
		
		/*
		 * The below for loop will fill the diagonal elements in the matrix
		 */
		for(int i = 0; i < strLength; ++i)
		{
			matrix[i][i] = search_prod( checkString[i], grammar);
		}
		
		
		
		/*
		 * This loop is used to populate all the entries above the diagonal
		 * The top most cell to the right will tell us if the string is generated or no
		 */
		for(int k = 1; k < strLength; ++k)
		{
			for(int j = k; j < strLength; ++j)
			{
				r = "";
				for(int l = j-k; l < j; ++l)
				{
					String pr = generate_combinations(matrix[j-k][l], matrix[l+1][j], grammar);
					r = concat_string(r, pr);
				}
				matrix[j-k][j] = r;
			}
		}

		
		/*
		 * This section is used to display the matrix in the required format
		 */
		System.out.println("\n\nFinal Matrix: ");
		for(int i = 0; i < strLength; ++i)
		{
			for(int j = 0; j < strLength; ++j)
			{
				if(j < i)
				{
					System.out.print("\t");
				}
				else
				{
					if(matrix[i][j] == "")
					{
						System.out.print("null\t");
					}
					else
					{
						System.out.print(matrix[i][j] + "\t");
					}
				}
			}
			System.out.println();
		}
		System.out.println();
		
		
		/*
		 * This snippet below is used to check if the given string is 
		 * generated from the given grammar or no.
		 * Depending on the outcome, corresponding message is displayed.
		 */
		if(matrix[0][strLength-1].indexOf(start) == -1)
		{
			System.out.println("\"" + str + "\" cannot be generated with the given grammar");
		}
		else
		{
			System.out.println("\"" + str + "\" can be generated with the given grammar");
		}
	}

	
	/*
	 * this method is used to generate all possible combinations for the given symbols on x and y
	 * it produces the value to be saved in the matrix
	 */
	private static String generate_combinations(String x, String y, HashMap<String, String> grammar) 
	{
		// TODO Auto-generated method stub
		String pri = "", re = "";
		String[] newX = x.split(",");
		String[] newY = y.split(",");
		for(int i = 0; i < newX.length; ++i)
		{
			for(int j = 0; j < newY.length; ++j)
			{
				pri = newX[i] + " " +newY[j];
				re = re + search_prod(pri, grammar);
			}
		}
		return re;
	}

	
	
	/*
	 * This method is used to search the production rules that 
	 * produces the string'p'
	 * it returns all the symbols which can produce 'p'
	 */
	private static String search_prod(String p, HashMap<String, String> grammar) 
	{
		// TODO Auto-generated method stub
		String r = "";
		Set<String> leftKeys = grammar.keySet();
		for(String leftkey : leftKeys)
		{
			String value = grammar.get(leftkey);
			String[] splitValue = value.split(",");
			for(int j = 0; j < splitValue.length; ++j)
			{
				if(splitValue[j].equals(p))
				{
					r = concat_string(r, leftkey);
				}
			}
		}
		return r;
	}
	
	
	
	/*
	 * This method is written to concatenate the strings present
	 * in the matrix and fill the values for further use.
	 *		- one of the input is the already present value from the matrix
	 *		- another input is the new generated rule that needs to be added to the matrix
	 */
	private static String concat_string(String x, String y) 
	{
		// TODO Auto-generated method stub		
		String[] temp_str = x.split(",");
		boolean doesExist = false;
		for(int i = 0; i < temp_str.length; ++i)
		{
			if(temp_str[i].equalsIgnoreCase(y))
				doesExist = true;
		}
		if(doesExist)
			return x;
		else if(x.equals(""))
			return y;
		else if(y.equals(""))
			return x;
		else
			return x + "," + y;
	}

	
	
	/*
	 * To check if the LHS of the rule is in CNF or no
	 * The condition is that there should not be any whitespace in the LHS
	 * If a whitespace is present, the rule is not validated and the program is terminated
	 */
	private static boolean checkLHS(String substring) 
	{
		// TODO Auto-generated method stub
		int count = 0;
		for(int i = 0; i < substring.length(); ++i)
		{
			if(substring.charAt(i) == ' ')
				count++;
		}
		if(count == 0)
			return true;
	return false;	
	}
	
	
	
	/*
	 * To check if the RHS of the rule is in CNF or no
	 * The condition in RHS is that it should have only or less than 1 whitespace
	 * If there are more or lesser number of whitespace, then the RHS
	 * 	is not validated and the program is terminated
	 */
	private static boolean checkRHS(String substring) 
	{
		// TODO Auto-generated method stub
		int count = 0;
		for(int i = 0; i < substring.length(); ++i)
		{
			if(substring.charAt(i) == ' ')
				count++;
		}
		if(count <= 1)
			return true;
	return false;
	}
}
