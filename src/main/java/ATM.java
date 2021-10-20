import java.util.*;
import java.util.stream.Collectors;

public class ATM {

    private TreeMap<Integer, Integer> totalMoney = new TreeMap<>();
    private Integer totalAmount = 0;

    public static Set<Integer> validDenominations;
    private static final String DEPOSIT = "Deposit";
    private static final String WITHDRAW = "Withdraw";

//    Errors
    private static final String INSUFFICIENT_FUNDS_ERROR = "Incorrect or Insufficient funds";
    private static final String INCORRECT_DEPOSIT_AMOUNT = "Incorrect Deposit amount.";
    private static final String DEPOSIT_AMOUNT_CANNOT_BE_ZERO = "Deposit amount cannot be zero.";
    private static final String NOT_A_VALID_DENOMINATION = " is not a valid denomination !";
    private static final String NOT_DISPENSABLE = "Requested withdraw amount NOT dispensable";
    private static final String NO_VALID_DENOMINATIONS_FOUND = "No valid denominations found !!";
    private static final String AS_PROGRAM_ARGS_SEPARATED_BY_SPACES = "Restart application passing valid denominations as program args. ( separated by spaces )";

    public static void main(String[] args) {

        ATM atm = new ATM();

        if (atm.noDenominations(args)) return;

        while(true) {
            Scanner read = new Scanner(System.in);
            String line = read.nextLine();

            String transactionType = line.substring(0, line.indexOf(":"));
            String amount = line.substring(line.indexOf(":") + 2);
            atm.transact(transactionType, amount);
        }

    }

    private boolean noDenominations(String[] args) {

        validDenominations = Arrays.stream(args).map(Integer::valueOf).collect(Collectors.toSet());
        if (validDenominations.isEmpty()) {
            System.out.println(NO_VALID_DENOMINATIONS_FOUND);
            System.out.println(AS_PROGRAM_ARGS_SEPARATED_BY_SPACES);
            return true;
        }

        return false;
    }

    public static boolean addNewDenomination(int denomination){
        return validDenominations.add(denomination);
    }

    private void transact(String type, String amount){

        String transaction = type.split(" ")[0];

        switch (transaction) {

            case DEPOSIT:
                String[] amounts = amount.split(",");
                deposit(amounts);
                break;

            case WITHDRAW:
                withdraw(Integer.parseInt(amount));
                break;

            default:
                System.out.println("Unknown transaction type :: " + transaction);

        }
    }

    public boolean deposit(String[] amounts){

        Map<Integer, Integer> moneyCount = mapAmounts(amounts);

        if (! depositValidationsSuccess(moneyCount)) { return false; }

        for (Map.Entry<Integer, Integer> entry : moneyCount.entrySet()) {

            totalMoney.merge(entry.getKey(), entry.getValue(), (v1, v2) -> v1 + v2);
            totalAmount = totalAmount + (entry.getKey() * entry.getValue());
        }

        System.out.println(totalMoney + ". Total = " + totalAmount);
        return true;
    }

    private Map<Integer, Integer> mapAmounts(String[] amounts) {

        Map<Integer, Integer> moneyCount = new HashMap<>();

        for (String amount: amounts) {

            String[] split = amount.trim().split(":");
            int denomination = Integer.parseInt(split[0].replace("s", ""));
            int count = Integer.parseInt(split[1].trim());

            moneyCount.put(denomination, count);
        }
        return moneyCount;
    }

    public boolean withdraw(int amount) {

        if (! withdrawValidationsSuccess(amount)) { return false; }

        Map<Integer, Integer> withdrawBills = new HashMap<>();

        Integer highestDenomination = totalMoney.lastKey();

        while (highestDenomination > amount){

            highestDenomination = totalMoney.lowerKey(highestDenomination);
            if (highestDenomination == null) {
                System.out.println(NOT_DISPENSABLE);
                return false;
            }
        }

        while (amount > 0) {

            int numberofBillsNeeded = amount / highestDenomination;
            int numberOfBillsAvailable = totalMoney.get(highestDenomination);

            if (numberOfBillsAvailable < numberofBillsNeeded) {
                withdrawBills.put(highestDenomination, numberOfBillsAvailable);
                amount = amount - (highestDenomination * numberOfBillsAvailable);
            } else {
                withdrawBills.put(highestDenomination, numberofBillsNeeded);
                amount = amount - (highestDenomination * numberofBillsNeeded);
            }

            if (amount == 0) {
                break;
            }

            highestDenomination = totalMoney.lowerKey(highestDenomination);
            if (highestDenomination == null) {
                System.out.println(NOT_DISPENSABLE);
                return false;
            }
        }

        Map<Integer, Integer> filteredWithdrawBills = withdrawBills.entrySet()
                .stream()
                .filter(entry -> entry.getValue() != 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (Map.Entry<Integer, Integer> entry : filteredWithdrawBills.entrySet()) {

            totalMoney.merge(entry.getKey(), entry.getValue(), (v1, v2) -> v1 - v2);
            totalAmount = totalAmount - (entry.getKey() * entry.getValue());
        }

        System.out.println("Dispensed : " + filteredWithdrawBills);
        System.out.println("Balance : " + totalMoney + ". Total = " + totalAmount);
        return true;
    }

    private boolean depositValidationsSuccess(Map<Integer, Integer> moneyCount) {

        for (Map.Entry<Integer, Integer> entry: moneyCount.entrySet()){

            if (entry.getValue() < 0) {
                System.out.println(INCORRECT_DEPOSIT_AMOUNT);
                return false;
            }
            if (entry.getValue() == 0) {
                System.out.println(DEPOSIT_AMOUNT_CANNOT_BE_ZERO);
                return false;
            }
            if (! validDenominations.contains(entry.getKey())) {
                System.out.println(entry.getKey() + NOT_A_VALID_DENOMINATION);
                return false;
            }
        }
        return true;
    }

    private boolean withdrawValidationsSuccess(Integer amount) {

        if (amount < 0 || amount == 0 || amount > totalAmount) {
            System.out.println(INSUFFICIENT_FUNDS_ERROR);
            return false;
        }

        return true;
    }

    private boolean validationAmount(String input) {
        return false;
    }

    public Map<Integer, Integer> getTotalCash(){
        return totalMoney;
    }

    public Integer getTotalAmount(){
        return totalAmount;
    }
}
