# Programming Challenge Description:
We say a portfolio matches the benchmark when the number of shares and assets in the portfolio matches those in the benchmark. Your challenge is to write a program that determines the transactions necessary to make a portfolio match a benchmark.
# Background<br/>
A portfolio is a collection of assets such as Stocks and Bonds. A portfolio could have 10 shares of Vodafone stock, 15 shares of Google stock and 15 shares of Microsoft bonds.

A benchmark is also just a collection of assets. A benchmark could have 15 shares of Vodafone stock, 10 shares of Google stock and 15 shares of Microsoft bonds.

A transaction is when you “Buy” or “Sell” a particular asset. For instance, you can decide to buy 5 shares of Vodafone stock which, given the portfolio described above, would result in you having 15 shares of Vodafone stock.

# Inputs and Outputs
You will receive a string in the following format Portfolio:Benchmark where Portfolio & Benchmark each are in the same format.
Here is the format: Name,AssetType,Shares where each asset within Portfolio or Benchmark is separated by '|' symbol.
The output for the transactions is TransactionType,Name,Shares
# Assumptions
•	Shares & Price are positive decimals
•	There will always be at least 1 asset present in the Portfolio and Benchmark
•	A particular asset will only be a stock or a bond, but not both
•	The trades should be sorted in lexicographic order based on the names of the assets. If the names of the asset are the same, BONDS should come before STOCKS.

# Input:
Vodafone,STOCK,10|Google,STOCK,15|Microsoft,BOND,15:Vodafone,STOCK,15|Google,STOCK,10|Microsoft,BOND,15
# Output:
BUY,Vodafone,5
SELL,Google,5

•	Test 1
# Test Input 
Download Test Input

Google,STOCK,10|Microsoft,STOCK,15|IBM,BOND,15:IBM,BOND,20|Google,STOCK,15|Microsoft,STOCK,10
# Expected Output 
Download Test Output

BUY,Google,5
BUY,IBM,5
SELL,Microsoft,5
•	Test 2
# Test Input 
Download Test Input

Vodafone,STOCK,10|Google,STOCK,15|Microsoft,BOND,15:Vodafone,STOCK,15|Google,STOCK,10|Microsoft,BOND,15
# Expected Output 
Download Test Output

SELL,Google,5
BUY,Vodafone,5
