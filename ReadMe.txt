This project is a part of Natural Language Processing coursework.
It is an implementation if CYK Parser in Java. It is a parsing algorithm for context-free grammars.

Input:
	- inputGrammar.txt
		- Line 1: Number of productions rules
		- Line 2: The starting variable
		- Line 3: The string to be parsed
		- Line 4 to end: The production rules for the grammar.

Output:
	- The CYK table
	- Result of the parsing

Instructions for entering the production rules:
- The production rules should strictly be entered in the Chomsky Normal Form (CNF)
- Validation of each production rule happens before proceeding the actual algorithm
- The rules are of the form "LHS->RHS"
- LHS should strictly be a non-terminal symbol
- RHS can be of two non-terminal symbols(seperated by a space) or a single terminal symbol
- No space is allowed in the production rules except between the two non-terminal symbols in the RHS