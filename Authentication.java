import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Authentication {

    private static final String SALT = "my_salt_string"; // Add a salt for additional security

    public static boolean authenticateUser(String email, String password) {
        String storedHash = getStoredHash(email);

        if (storedHash == null) {
            return false; // User not found
        }

        String inputHash;
        try {
            inputHash = hashPassword(password);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error: Password hashing failed.");
            return false;
        }

        return inputHash.equals(storedHash);
    }

    private static String getStoredHash(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("/");
                if (parts.length == 3 && parts[1].equals(email)) {
                    return parts[2]; // Return the stored hash
                }
            }
        } catch (IOException e) {
            System.out.println("Error: Failed to read user data.");
            e.printStackTrace();
        }
        return null; // User not found or error occurred
    }

    public static void registerUser(String email, String password) {
        if (isUserRegistered(email)) {
            System.out.println("Email already exists. Please choose a different one.");
            return;
        }

        User user = new User(email, password);
        saveUserToDatabase(user);
    }

    public static boolean isUserRegistered(String email) {
        try (Scanner scanner = new Scanner(new File("Data.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("/");
                String storedEmail = parts[1];
                if (email.equals(storedEmail)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: User database file not found.");
            e.printStackTrace();
        }
        return false;
    }

    private static void saveUserToDatabase(User user) {
        try {
            String hashedPassword = hashPassword(user.getPassword());

            try (PrintWriter writer = new PrintWriter(new FileWriter("Data.txt", true))) {
                writer.println( user.getUseremail() + "/" + hashedPassword);
                System.out.println("User registered successfully!");
            } catch (IOException e) {
                System.out.println("Error: Failed to write user data.");
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error: Password hashing failed.");
            e.printStackTrace();
        }
    }

    private static String hashPassword(String password) throws NoSuchAlgorithmException {
        String saltedPassword = password + SALT;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(saltedPassword.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
