import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

public class Main {

  // AssetType represents the type of an asset: Bond or Stock
  enum AssetType {
    BOND("BOND"),
    STOCK("STOCK");
    
    private final String type;
    AssetType(String type) {
      this.type = type;
    }
    
    public String getType() {
      return this.type;
    }
  }
  
  // TransactionType represents the type of a transaction: Buy or Sell
  enum TransactionType {
    BUY("BUY"),
    SELL("SELL");
    
    private final String type;
    TransactionType(String type) {
      this.type = type;
    }
    
    public String getType() {
      return this.type;
    }
  }
  
  // Asset contains information about a financial asset, including: 
  // company, asset type, number of shares
  public static class Asset {
    private String company;
    private AssetType assetType;
    private int shares;
    
    public Asset(String company, AssetType assetType, int shares) {
      this.company = company;
      this.assetType = assetType;
      this.shares = shares;
    }
    
    public String getCompany() {
      return this.company;
    }
    
    public AssetType getAssetType() {
      return this.assetType;
    }
    
    public int getShares() {
      return this.shares;
    }
  }
  
  // Transaction includes information about a transaction, including: 
  // the company, asset type, transaction type, number of shares
  public static class Transaction {
    private String company;
    private AssetType assetType;
    private TransactionType transactionType;
    private int numShares;
    
    public Transaction(String company, AssetType assetType, TransactionType transactionType, int numShares) {
      this.company = company;
      this.assetType = assetType;
      this.transactionType = transactionType;
      this.numShares = numShares;
    }
    
    public String getCompany() {
      return this.company;
    }
    
    public AssetType getAssetType() {
      return this.assetType;
    }
    
    public TransactionType getTransactionType() {
      return this.transactionType;
    }
    
    public int getNumShares() {
      return this.numShares;
    }
  }
  
  // Comparator used in sorting trades lexicographically, and by asset type in case of equality
  static class LexicographicComparator implements Comparator<Transaction> {
    @Override
    public int compare(Transaction a, Transaction b) {
      if (a.getCompany().equals(b.getCompany())) {
        if (a.getAssetType().equals(AssetType.BOND)) {
          return -1;
        }
        return 1;
      }
      return a.getCompany().compareToIgnoreCase(b.getCompany());
    }
  }
  
  // popMatchingAsset finds the asset in the assetsList that matches the soughtAsset, pops it from the list and returns it.
  // In case no matching asset exists, the method returns null. 
  private static Asset popMatchingAsset(ArrayList<Asset> assetsList, Asset soughtAsset) {
    for (Asset asset : assetsList) {
      if (asset.company.equals(soughtAsset.company) && asset.assetType.equals(soughtAsset.assetType)) {
        assetsList.remove(asset);
        return asset;
      }
    }
    return null;
  }
  
  // matchBenchmarkAsset returns the Transaction necessary to match the given portfolio asset with
  // the given benchmark asset. If the portfolio or benchmark assets are null, we BUY/SELL the entire asset.
  // If the number of shares matches perfectly, we return null.
  public static Transaction matchBenchmarkAsset(Asset portfolioAsset, Asset benchmarkAsset) {
    if (portfolioAsset == null) {
      return new Transaction(benchmarkAsset.getCompany(), benchmarkAsset.getAssetType(), TransactionType.BUY, benchmarkAsset.getShares());
    }
    else if (benchmarkAsset == null) {
      return new Transaction(portfolioAsset.getCompany(), portfolioAsset.getAssetType(), TransactionType.SELL, portfolioAsset.getShares());
    }
    else {
      TransactionType transactionType = TransactionType.BUY;
      int sharesDifference = benchmarkAsset.getShares() - portfolioAsset.getShares();
      if (sharesDifference == 0) {
        return null;
      }
      else if (sharesDifference < 0) {
        transactionType = TransactionType.SELL;
        sharesDifference *= -1;
      }
      return new Transaction(benchmarkAsset.getCompany(), benchmarkAsset.getAssetType(), transactionType, sharesDifference);
    }
  }
  
  // printTransactions prints the output transactions.
  private static void printTransactions(ArrayList<Transaction> transactions) {
    for (Transaction transaction: transactions) {
      System.out.printf("%s,%s,%s,%d\n", transaction.getTransactionType(), transaction.getCompany(), transaction.getAssetType(), transaction.getNumShares());
    }
  }
  
  public static void main(String[] args) throws IOException {
    InputStreamReader reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
    BufferedReader in = new BufferedReader(reader);
    String line;
    while ((line = in.readLine()) != null) {
      matchBenchmark(line);
    }
  }
  
  public static void matchBenchmark(String input) {
    // Split the input string into its portfolio and benchmark counterparts
    String[] inputSplit = input.split(":");
    String[] portfolioStrings = inputSplit[0].split("\\|");
    String[] benchmarkStrings = inputSplit[1].split("\\|");
    
    // Initialise array lists containing the Assets in the portfolio and benchmark
    ArrayList<Asset> portfolioAssets = new ArrayList<>();
    ArrayList<Asset> benchmarkAssets = new ArrayList<>();
    
    // Iterate through the tokenized input, serialising it into Asset objects and adding it to the array lists
    for (String portfolioStr : portfolioStrings) {
      String[] data = portfolioStr.split(",");
      portfolioAssets.add(new Asset(data[0], AssetType.valueOf(data[1]), Integer.parseInt(data[2])));
    }
    for (String benchmarkStr : benchmarkStrings) {
      String[] data = benchmarkStr.split(",");
      benchmarkAssets.add(new Asset(data[0], AssetType.valueOf(data[1]), Integer.parseInt(data[2])));
    }

    // Initialise array list containing the Transactions to be performed
    ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    
    // Iterate through each Asset in the benchmark
    for (Asset asset : benchmarkAssets) {
      // Find its matching Asset in the portfolio
      Asset matchingPortfolioAsset = popMatchingAsset(portfolioAssets, asset);
      // Compute the Transaction that would match the portfolio asset against the benchmark
      Transaction transaction = matchBenchmarkAsset(matchingPortfolioAsset, asset);
      // If the Transaction exists, add it to the list
      if (transaction != null) {
        transactions.add(transaction); 
      }  
    }
    
    // If there are remaining Assets in the portfolio that were not contained in the benchmark, sell them all
    for (Asset remainingAsset : portfolioAssets) {
      transactions.add(matchBenchmarkAsset(remainingAsset, null));
    }
    
    // Sort the Transactions lexicographically (and by asset type in case of equality)
    Collections.sort(transactions, new LexicographicComparator());

    // Print the results
    printTransactions(transactions);
  }
}