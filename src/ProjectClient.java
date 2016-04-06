import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

public class ProjectClient {

	@SuppressWarnings({ "resource" })
	public static void main(String[] args) throws Exception {
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
		FileInputStream fis = new FileInputStream("./src/inputGrammer.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        int num_prod = Integer.parseInt(reader.readLine());
        System.out.println("Number of productions: " + num_prod);
		
		String start = reader.readLine();
		System.out.println("Start variable: " + start);
		String str = reader.readLine();
		System.out.println("String to be searched: " + str);
		
		/*
		 * In this loop, we ask the user to enter the production rules
		 * 		- Once the rule is entered, we check if it is in CNF or no
		 * 		- If the rule is in CNF, we proceed further and save it,
		 * 		- If the rule is not in CNF, we terminate the execution
		 */
		for(int i = 0; i < num_prod; ++i)
		{
			lhsFlag = false;
			rhsFlag = false;
			pointer = -1;
			temp_prod = reader.readLine();
			System.out.println("Production # " + (i+1) + ": " + temp_prod);
			pointer = temp_prod.indexOf("->");
			if(pointer != -1)
			{
				lhs = temp_prod.substring(0, pointer);
				rhs = temp_prod.substring(pointer + 2, temp_prod.length());
				rhs = rhs.replaceAll("\\|", ",");
				lhsFlag = checkLHS(lhs);
				if(lhsFlag == false)
				{
					System.out.println("Grammar not in CNF\nExiting");
					System.exit(0);
				}
				if(rhs.indexOf(",") == -1)
				{
					rhsFlag = checkRHS(rhs);
					if(rhsFlag == false)
					{
						System.out.println("Grammar not in CNF\nExiting");
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
							//System.out.println("Test: " + newRHS[j]);
							rhsFlag = checkRHS(newRHS[j]);
							if(rhsFlag == false)
							{
								System.out.println("Grammar not in CNF\nExiting");
								System.exit(0);
							}
						}
					}
				}
			}
			else if(pointer == -1)
			{
				System.out.println("Grammar not in CNF\nExiting");
				System.exit(0);
			}
			
			if(grammar.containsKey(lhs))
			{
				String old = grammar.get(lhs);
				grammar.put(lhs, old +"," + rhs);
			}
			else
			{
				grammar.put(lhs, rhs);
		
			}
		}
		matrix = new String[str.length()][str.length()];
		
		/*
		 * The below for loop will fill the diagonal elements in the matrix
		 */
		for(int i = 0; i < str.length(); ++i)
		{
			matrix[i][i] = search_prod(str.charAt(i) + "", grammar);
		}
		
		/*
		 * This loop is used to populate all the entries above the diagonal
		 * The top most cell to the right will tell us if the string is generated or no
		 */
		for(int k = 1; k < str.length(); ++k)
		{
			for(int j = k; j < str.length(); ++j)
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
		System.out.println("\nFinal Matrix: ");
		for(int i = 0; i < str.length(); ++i)
		{
			for(int j = 0; j < str.length(); ++j)
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
		if(matrix[0][str.length()-1].indexOf(start) == -1)
		{
			System.out.println(str + " cannot be generated with the given grammar");
		}
		else
		{
			System.out.println(str + " can be generated with the given grammar");
		}
	}

	
	/*
	 * this method is used to generate all possible combinations for the given symbols on x and y
	 * it produces the value to be saved in the matrix
	 */
	private static String generate_combinations(String x, String y, HashMap<String, String> grammar) 
	{
		// TODO Auto-generated method stub
		String pri = x;
		String re = "";
		for(int i = 0; i < x.length(); ++i)
		{
			for(int j = 0; j < y.length(); ++j)
			{
				pri = "";
				pri = pri + x.charAt(i) + y.charAt(j);
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
		String newString = x;
		for(int i = 0; i < y.length(); ++i)
		{
			if(newString.indexOf(y.charAt(i)) == -1)
			{
				newString += y.charAt(i);
			}
		}
		return newString;
	}

	
	
	/*
	 * To check if the LHS of the rule is in CNF or no
	 * i.e if the length of LHS is 1, it should be a single character (non-terminal symbol)
	 * if the length is other than length 1, it is not in CNF 
	 */
	private static boolean checkLHS(String substring) 
	{
		// TODO Auto-generated method stub
		if(substring.length() == 1 && substring.charAt(0) >= 'A' && substring.charAt(0) <= 'Z')
			return true;
		return false;	
	}
	
	
	
	/*
	 * To check if the RHS of the rule is in CNF or no
	 * i.e if the length of the RHS is 1, it is a terminal symbol
	 * if the length of the RHS is 2, it is two non-terminal symbol
	 * if the length is other than 1 or 2, it is not in CNF 
	 */
	private static boolean checkRHS(String substring) 
	{
		// TODO Auto-generated method stub
		if((substring.length() == 1 && substring.charAt(0) >= 'a' && substring.charAt(0) <= 'z') ||
				(substring.length() == 1 && substring.charAt(0) >= '0' && substring.charAt(0) <= '9'))
			return true;
		if(substring.length() == 2 && substring.charAt(0) >= 'A' && substring.charAt(0) <= 'Z' && 
				substring.charAt(1) >= 'A' && substring.charAt(1) <= 'Z')
			return true;
		return false;
	}

}