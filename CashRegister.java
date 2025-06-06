import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CashRegister {
    private static Scanner sc = new Scanner(System.in);
    private static ArrayList<String> usernames = new ArrayList<>();
    private static ArrayList<String> passwords = new ArrayList<>();
    private static String currentUser = ""; // New: Track logged-in user

    public static void main(String[] args) {
        System.out.println("=== Welcome to Starbucks System ===");

        while (true) {
            System.out.print("Do you have an account? (yes/no): ");
            String hasAccount = sc.nextLine().toLowerCase();
            if (hasAccount.equals("yes")) {
                login();
                break;
            } else if (hasAccount.equals("no")) {
                signup();
                System.out.println("Signup successful! Please login.\n");
                login();
                break;
            } else {
                System.out.println("Please enter 'yes' or 'no'.");
            }
        }

        cashRegister();
    }

    public static void signup() {
        System.out.println("\n=== USER SIGNUP ===");
        String username, password;

        while (true) {
            System.out.print("Enter a username (5–15 alphanumeric characters): ");
            username = sc.nextLine();
            if (!username.matches("^[a-zA-Z0-9]{5,15}$")) {
                System.out.println("Invalid username format. Try again.");
                continue;
            }

            System.out.print("Enter a password (8–20 chars, 1 uppercase, 1 number): ");
            password = sc.nextLine();
            if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{8,20}$")) {
                System.out.println("Invalid password format. Try again.");
                continue;
            }

            usernames.add(username);
            passwords.add(password);
            break;
        }
    }

    public static void login() {
        System.out.println("\n=== USER LOGIN ===");
        while (true) {
            System.out.print("Username: ");
            String inputUser = sc.nextLine();
            System.out.print("Password: ");
            String inputPass = sc.nextLine();

            for (int i = 0; i < usernames.size(); i++) {
                if (usernames.get(i).equals(inputUser) && passwords.get(i).equals(inputPass)) {
                    System.out.println("Login successful!\n");
                    currentUser = inputUser;
                    return;
                }
            }
            System.out.println("Incorrect credentials. Try again.\n");
        }
    }

    public static void cashRegister() {
        ArrayList<String> productNames = new ArrayList<>(Arrays.asList(
            "Brewed Coffee", "Iced Latte", "Caramel Macchiato", "Mocha Frappuccino", "Espresso Shot"));
        ArrayList<Double> productPrices = new ArrayList<>(Arrays.asList(
            120.00, 150.00, 175.00, 190.00, 95.00));

        boolean anotherTransaction = true;

        while (anotherTransaction) {
            ArrayList<Integer> orderQuantities = new ArrayList<>();
            ArrayList<String> orderItems = new ArrayList<>();
            ArrayList<Double> orderPrices = new ArrayList<>();

            System.out.println("\n==== STARBUCKS MENU ====");
            for (int i = 0; i < productNames.size(); i++) {
                System.out.printf("%d. %s - %.2f\n", i + 1, productNames.get(i), productPrices.get(i));
            }

            boolean ordering = true;
            while (ordering) {
                try {
                    System.out.print("\nEnter item number to order: ");
                    int itemNum = Integer.parseInt(sc.nextLine()) - 1;

                    if (itemNum >= 0 && itemNum < productNames.size()) {
                        System.out.print("Enter quantity: ");
                        int qty = Integer.parseInt(sc.nextLine());

                        orderItems.add(productNames.get(itemNum));
                        orderQuantities.add(qty);
                        orderPrices.add(productPrices.get(itemNum));
                    } else {
                        System.out.println("Invalid item number.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please try again.");
                    continue;
                }

                System.out.print("Add another item? (yes/no): ");
                String more = sc.nextLine().toLowerCase();
                if (!more.equals("yes")) {
                    ordering = false;
                }
            }

            System.out.println("\n==== ORDER SUMMARY ====");
            double total = 0;
            for (int i = 0; i < orderItems.size(); i++) {
                double subtotal = orderQuantities.get(i) * orderPrices.get(i);
                System.out.printf("%d x %s @ %.2f = %.2f\n", orderQuantities.get(i), orderItems.get(i), orderPrices.get(i), subtotal);
                total += subtotal;
            }
            System.out.printf("Total: %.2f\n", total);

            System.out.print("Remove any item? (yes/no): ");
            String remove = sc.nextLine().toLowerCase();
            while (remove.equals("yes")) {
                System.out.print("Enter name of item to remove: ");
                String removeName = sc.nextLine();

                int index = orderItems.indexOf(removeName);
                if (index != -1) {
                    orderItems.remove(index);
                    orderQuantities.remove(index);
                    orderPrices.remove(index);
                    System.out.println(removeName + " removed.");
                } else {
                    System.out.println("Item not found in order.");
                }

                System.out.print("Remove another? (yes/no): ");
                remove = sc.nextLine().toLowerCase();
            }

            total = 0;
            System.out.println("\n==== FINAL RECEIPT ====");
            for (int i = 0; i < orderItems.size(); i++) {
                double subtotal = orderQuantities.get(i) * orderPrices.get(i);
                System.out.printf("%d x %s @ %.2f = %.2f\n", orderQuantities.get(i), orderItems.get(i), orderPrices.get(i), subtotal);
                total += subtotal;
            }
            System.out.printf("Final Total: %.2f\n", total);

            double payment = 0;
            while (true) {
                try {
                    System.out.print("Enter payment amount: ");
                    payment = Double.parseDouble(sc.nextLine());
                    if (payment < total) {
                        System.out.println("Insufficient payment. Try again.");
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }

            double change = payment - total;
            System.out.printf("Change: %.2f\n", change);
            System.out.println("Transaction completed!");

            // Save to File
            saveTransaction(orderItems, orderQuantities, orderPrices, total);

            System.out.print("Do another transaction? (yes/no): ");
            String again = sc.nextLine().toLowerCase();
            if (!again.equals("yes")) {
                anotherTransaction = false;
                System.out.println("Thank you for visiting Starbucks!");
            }
        }
    }

    // File Handling Method
    public static void saveTransaction(ArrayList<String> items, ArrayList<Integer> qtys, ArrayList<Double> prices, double total) {
        try {
            FileWriter writer = new FileWriter("transactions.txt", true);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = LocalDateTime.now().format(formatter);

            writer.write("=== Transaction ===\n");
            writer.write("Date & Time: " + timestamp + "\n");
            writer.write("Cashier: " + currentUser + "\n");
            for (int i = 0; i < items.size(); i++) {
                writer.write(qtys.get(i) + " x " + items.get(i) + " @ " + prices.get(i) + " = " + (qtys.get(i) * prices.get(i)) + "\n");
            }
            writer.write("Total: " + total + "\n");
            writer.write("------------------------\n");
            writer.close();

            System.out.println("Transaction logged to transactions.txt");
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }
}
