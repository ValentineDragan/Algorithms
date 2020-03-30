import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;



/**
* Description of the Algorithm (primitive solution = O(K^2); improved solution = O(K + N))
*
* In essence, this problem requires us to implement a LRU Cache. The cabinets can be considered 'blocks' in the cache, with
* different space sizes. A primitive LRU Cache implementation would use a LinkedList to represent the cache queue, and a 
* HashMap/Set to keep track of what elements are in the cache. Using this implementation, the algorithm would iterate through
* all the inputs in O(K) and for each input item X, it would search where X is located in the LinkedList cache, remove it and
* insert it at the beginning. Removal and Insertion can be done in O(1), but searching for X is done in O(K). Hence, the overall * complexity of this implementation would be O(K^2).
*
* Improved Solution: We iterate through all the inputs in O(K) and insert each input X at the beginning of a Deque. 
* We do this even if there are duplicate items and we don't remove any items from the Deque. (e.g. for the Sample Input #01, 
* the Deque will hold 2,6,5,4,3,2,1). Essentially, this is the same as storing the input items in reverse order.
*
* If we now look at the Deque, the 'position in the cache' of each item is equal to (1 + the number of *unique items*) 
* before it in the Deque. For example, in the Sample Input #01 the position of 5 is 3 (because there are 2 unique items before
* it) and the position of 1 is 6 (because there are 5 unique items before it).
*
* Therefore, all we have to do is: 1) pop the first element from the Deque - this is the element we'll "search for".
* 2) Count the number of unique items in the Deque that appear before the sought item - this can be done with an iteration 
* through the Deque elements in O(K) and a HashSet to mark the unique items. 3) We compute in which Cabinet the sought item 
* would be located based on their 'position in the cache' and the sizes of the cabinets. For example, if the position is 3
* and the cabinet sizes are 2,2,4 , the item would be located in the second cabinet.
*
* Therefore the complexity of this solution is: O(K) for reading the input + O(K) for finding the item's position in the cahce
* + O(N) for computing the Cabinet in which the item would be located. In total: O(K + N)
*/
public class Solution {

    private static Deque<Long> queue = new ArrayDeque<Long>();   
    private static List<Integer> cabinetSizes = new ArrayList<Integer>();

    // these constants are used for checking that the input is correct
    static final int MAX_CABINET_SIZE = 1024;
    static final int MAX_NUMBER_OF_CABINETS = 64;
    static final long MAX_K = 4294967296L;
    static final long MAX_ITEM_KEY = 4294967296L;


    /**
    * This method reads the entire Input from STDIN, adds all the K items to the Deque (including duplicates of the same item)
    * and adds all the Cabinet Sizes to the respective cabinetSizes List in their order.
    * The method also performs typechecking on the input and throws an error if the input is incorrect.
    * After this method is executed successfully, the queue and cabinetSizes datastructures will be completely filled.
    */
    private static void readInput() throws Exception {
        Scanner in = new Scanner(System.in);

        // Read cabinet sizes
        String rawInputCabinets = in.nextLine();
        String[] cabinets = rawInputCabinets.split(" ");
        if (cabinets.length >= MAX_NUMBER_OF_CABINETS)
            throw new Exception("Invalid number of cabinets!");

        for (int i=0; i<cabinets.length; i++) {
            try {
                int x = Integer.parseInt(cabinets[i]);
                if (x>0 && x<MAX_CABINET_SIZE)
                    cabinetSizes.add(x);
                else throw new Exception("Invalid Cabinet Input!"); 
            } catch (Exception e) {
                throw new Exception("Invalid Cabinet Input!");
            }
        }

        // Read K and the next K lines representing the items
        try {
            long K = Long.parseLong(in.nextLine());
            if (K<=0 || K>=MAX_K)
                throw new Exception("Invalid K Input!");

            for (long i=0; i<K; i++) {
                try {
                    long x = Long.parseLong(in.nextLine());
                    if (x>0 && x<MAX_ITEM_KEY)
                        queue.addFirst(x);
                    else throw new Exception("Invalid key input!");
                } catch (Exception e) {
                    throw new Exception("Invalid key input!");
                }
            }
        } catch (Exception e) {
            throw new Exception("Invalid K Input!");
        }
    }

    /**
    * This method calculates the 'position in the cache' index of a sought element in the Deque, by counting how many
    * unique items there are before the sought element. If the item is not found in the Deque, it means it's a NEW item.
    * The result of this method is used to compute in which cabinet the item would've been placed.
    * 
    * @param itemSought - the item which we're looking for in the Deque
    * @param queue - the Deque containing all the items read from input
    * @return the position in the cache of the sought item, which is equal to how many unique items appear before it +1
    */
    private static int calculatePositionIndex(long itemSought, Deque<Long> queue) {
        HashSet<Long> itemSet = new HashSet<Long>();
        int positionIndex = 0;
        boolean itemFound = false;

        //queue.removeFirst();
        for (long item: queue) {
            //System.out.print(elem + " ");
            if (!itemSet.contains(item)) {
                positionIndex++;
                itemSet.add(item);
            }
            if (item == itemSought) {
                itemFound = true;
                break;
            }
        }

        return (itemFound)? positionIndex : 0;
    }

    /**
    * This method computes the index of which cabinet the item would be located, based on its position in the cache and the
    * sizes of the cabinets. The method returns the result as a string, representing the index of the cabinet; or "NEW" if 
    * the item was not found in the cache; or "OUTSIDE" if the item was taken outside because all the cabinets were full.
    *
    * @param positionIndex - the position in the cache of the item
    * @param cabinetSizes - a List containing the sizes of the cabinets, in order
    * @return the index of the cabinet as a String, or "NEW" if the item was newly created, or "OUTSIDE" if the item was outside.
    */
    private static String getCorrespondingCabinet(int positionIndex, List<Integer> cabinetSizes)
    {
        if (positionIndex == 0)
            return "NEW";
        
        int index;
        for (index=0; index<cabinetSizes.size() && positionIndex>0; index++)
            positionIndex -= cabinetSizes.get(index);
        return (positionIndex>0)? "OUTSIDE" : Integer.toString(index);
    }

    public static void main(String args[] ) throws Exception {
        // Read the Input in O(N+K)
        try {
            readInput();
        } catch (Exception e) {
            System.out.println("INPUT_ERROR");
            System.exit(0);
        }

        // Calculate the position in the cache of the latest item in O(K)
        int positionIndex = calculatePositionIndex(queue.pollFirst(), queue);

        // Compute in which cabinet the item would be placed and print, in O(N)
        System.out.println(getCorrespondingCabinet(positionIndex, cabinetSizes));
    }
}
