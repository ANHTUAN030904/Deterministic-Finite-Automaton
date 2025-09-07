# Deterministic Finite Automaton

## Rules
-	The valid inputs are digits $(0-9)$, operators $(+, -, *, /)$, decimal point and white space 
-	The decimal number must start with $0$ (smaller than $1$)
-	If the input is $0$, the number can only be $0$ a decimal number starting with $0$
-	A number can only be followed by an operator, an operator can only be followed by a number 
-	The white spaces can only be in between the expression
-	The program should throw two errors: NumberException and ExpressionException 

Let $L$ represent the set of all valid expressions formed by the following rules:
$L =${ $w | w$ is a string of valid digits, operators, decimal points, and white spaces such that:
-	Digits $(0-9)$: Every digit in the string is a valid decimal digit.
- Operators $(+, -, *, /)$: Only the arithmetic operators $+, -, *, /$ are allowed.
- Decimal point $(.)$: A decimal point is used to denote a decimal number.
- White space $(‘ ’)$: Spaces are allowed between numbers and operators.
- Constraints:
1.	Decimal numbers must start with $0$: Any decimal number must begin with 0. (e.g., $0.5$), representing a number smaller than $1$.
2.	The number 0 can only be either $0$ or a decimal number starting with $0$: The number $0$ is valid only by itself or as part of a decimal number (e.g., $0.3$), but $00$ or $01$ are invalid.
3.	A number can only be followed by an operator, and an operator can only be followed by a number: No two numbers or two operators can appear consecutively without the other in between (e.g., $3 + 4$ is valid, but $3 4$ or $++$ are not).
4.	White spaces are allowed only between numbers and operators: Spaces must not appear at the beginning or end of the string, nor between digits in a number or between an operator and a number.}

## Notation for the DFA
There are 14 total possible inputs, which can be categorized into 6 types, that can determine the transition of one state to another, here are how they are represent in the DFA:
1.	Zero $(0)$
2.	Ten digits $(1-9)$
3.	Operator $(op)$
4.	Whitespace $(ws)$
5.	Decimal point $(dec)$
6.	Other character $(let)$
As previously mentioned, there are nine states in this DFA. Here are how they are represented and how each state moves onto another:
1.	Start state $(start)$
2.	Number state $(num)$
3.	Zero state $(zero)$
4.	Decimal $(dec)$
5.	Whitespace after number $(wsn)$
6.	Whitespace after operator $(wso)$
7.	Operator $(op)$
8.	Number exception $(ne)$
9.	Expression exception $(ee)$
There are only two acceptance states: Number and Zero. This is because if the valid possibilities of the input expression can only end with $0-9$.


![DFA](https://github.com/user-attachments/assets/8c12821e-6dcf-4a79-ad87-d954efc1a7f4)


## Transition Table
| States | 0 | 1-9 | Operators | Decimal | Whitespace | Other char |
|--------|---|-----|-----------|---------|------------|-------------|
| $start$  | $zero$ | $num$ | $ee$ | $ne$ | $ee$ | $ee$ |
| $num$    | $num$ | $num$ | $op$ | $ne$ | $wsn$ | $ne$ |
| $zero$   | $ne$ | $ne$ | $op$ | $dec$ | $wsn$ | $ne$ |
| $dec$    | $num$ | $num$ | $ee$ | $ee$ | $ee$ | $ee$ |
| $op$     | $zero$ | $num$ | $ee$ | $ne$ | $ee$ | $ee$ |
| $wsn$    | $ee$ | $ee$ | $op$ | $ee$ | $wso$ | $ee$ |
| $wso$    | $zero$ | $num$ | $ee$ | $ee$ | $wso$ | $ee$ |
| $ne$     | $ne$ | $ne$ | $ne$ | $ne$ | $ne$ | $ne$ |
| $ee$     | $ee$ | $ee$ | $ee$ | $ee$ | $ee$ | $ee$ |

## Techniques used to enhance the Automaton
### Efficiency 
Efficiency is a key goal when it comes to programming. This case is no different. 
To make my program as efficient but not sacrificing accuracy, I tried my best to use as little state as I can to avoid the input being over assessed. The ERROR state is extra; however, it should not affect the program efficiency as it would never be reached. The switch case also goes in a straight workflow thanks to the “break;” statement. This helps the program to save time and memory from checking every single case again and avoid unnecessary errors. 
Diving deeper into each case, for the number case, if a decimal point is detected, an error will be thrown immediately rather than having to check what the previous char was. This is because the ZERO state already handles the valid decimal case, meaning the NUMBER case only have to handle the invalid decimal cases. Plus, the NUMBER case is returned from the DECIMAL, the ZERO and NUMBER is returned from the OPERATOR. I could have easily hard coded for each of these cases, but in the name of DRY (Don’t Repeat Yourself) and efficiency, I decided to lead each cases back to each other and reuse the already existed function there. This helps my program to stay true to the DFA nature of not looking back. 
### Accuracy
I always follow the motto: “Rather be more than less”. Therefore, to ensure maximum accuracy, an extra statement is added to provide the tailored output to each char of the input. 
To do this, instead of doing a simple “else” statement, I utilized many other “else if” statements to catch every single case. Which in turn would return a more specific error message. This helps with debugging tremendously. Also, the extra “else” statement (the default case and the very last “else” statement) is used to catch any undetected error. 
Challenges and Limitations in Implementing a Theoretical Construct in Code
Deciding between Number Exception and Expression Exception
While drawing the DFA, the hardest obstacle I encountered was the dilemma of Number or Expression. 
In the case of “1+.8” for example, looking from the perspective of the DFA, while checking char from left to right, it would likely throw an error “No dot after an operator”, which is an Expression Exception. However, looking from the user aspect, as human we would see the entire input right away and we are more likely to say, “A decimal number should start with a 0”, which is a Number Exception since we are say this format of number is not valid. 
In the end, I decided to take the more humane approach as the DFA was made to serve humans. Another deciding factor for this decision is the Number Expression of “0.” given by the test cases, which can also be argued to be an Expression Exception. 
### Limitation
What bugs me the most is that as of my level of programming right now, it seems impossible to fully implement a DFA in coding.
For this DFA in particular: 
1.	DFA has no memory of the previous char, however, in this case it is impossible to be done with the buffer string container. 
2.	This might be a good thing but theoretically a program could handle an infinite input while a DFA can’t. Or at least the automated process in programming would be faster than a DFA. 
For other DFAs:
1.	DFA can handle all possible character such as Greek, or even Chinese and Sanskrit. However, in programming, due to the restriction of ASCII convention, the code might not be able to do that.
2.	DFA can throw unique errors, even for very specific cases. In programming, it seems impossible to create such a thing given that we don’t know what the input is going to be.
## Trials and Errors in coding
Implementing a theoretical DFA into programming was something I never came across. Therefore, I am bound to be bombarded with error. The provided test cases did not in fact cover all the potential errors. In my opinion, this is a feature not a bug as it provided me a great chance to study further into the subject. 
Take the case “1+ 8” for example, all of the test case would be passed even though my program did not return [1.0, +, 8.0]. To address this, I tried my best to stay grounded and start from the bottom up. I decided to create a transition table to give me a more straightforward vision of the program. 
In conclusion, implementing a DFA into code was both challenging and rewarding to me. I learned a lot throughout this project. Not only it teaches me more about this subject material, but it also gave me a little insight into how a research process would be: a bunch of trials and errors. 
## Reference
The DFA drawer I used: [Finite State Machine Designer - by Evan Wallace (unc.edu)](https://personal.utdallas.edu/~gxa120930/fsm/)
