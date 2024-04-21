import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Authentication {

    private static final String SALT = "my_salt_string"; // Add a salt for additional security

    public static boolean authenticateUser(String email, String password) {
        String storedHash = getStoredHash(email);

        if (storedHash == null) {
            System.out.println("User not found for email: " + email);
            return false; // User not found
        }

        String inputEmailHash;
        String inputPasswordHash;
        try {
            inputEmailHash = hashEmail(email);
            inputPasswordHash = hashPassword(password);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error: Hashing failed.");
            return false;
        }

        // Debug: Print the computed hashes for verification
        System.out.println("Computed Email Hash: " + inputEmailHash);
        System.out.println("Stored Email Hash: " + storedHash.split("/")[0]);
        System.out.println("Computed Password Hash: " + inputPasswordHash);
        System.out.println("Stored Password Hash: " + storedHash.split("/")[1]);

        if (inputEmailHash.equals(storedHash.split("/")[0]) && inputPasswordHash.equals(storedHash.split("/")[1])) {
            System.out.println("Login successful for email: " + email);
            return true;
        } else {
            System.out.println("Login failed for email: " + email);
            return false;
        }
    }

    private static String getStoredHash(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("/");
                if (parts.length == 3 && parts[1].equals(email)) {
                    return parts[0] + "/" + parts[2]; // Return the stored email hash and password hash
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

        String emailHash;
        String passwordHash;
        try {
            emailHash = hashEmail(email);
            passwordHash = hashPassword(password);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error: Hashing failed.");
            e.printStackTrace();
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("Data.txt", true))) {
            writer.println(emailHash + "/" + email + "/" + passwordHash);
            System.out.println("User registered successfully!");
        } catch (IOException e) {
            System.out.println("Error: Failed to write user data.");
            e.printStackTrace();
        }
    }

    public static boolean isUserRegistered(String email) {
        try (Scanner scanner = new Scanner(new File("Data.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("/");
                if (parts.length == 3 && parts[1].equals(email)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: User database file not found.");
            e.printStackTrace();
        }
        return false;
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

    private static String hashEmail(String email) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(email.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
