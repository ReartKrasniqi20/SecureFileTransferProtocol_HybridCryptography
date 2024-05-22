package ft.filetransfer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.SecretKey;

public class Server {

    private static final int PORT = 12345;
    private static KeyPair keyPair;
    private static final String SERVER_FOLDER = "C://Users//Admin//Desktop//Server/";

    public static void main(String[] args) {
        try {
            keyPair = RSAUtils.generateKeyPair();
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Secure File Transfer Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected, exchanging keys...");
                handleClient(clientSocket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (DataInputStream in = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {

            // Send server's public key to client
            String publicKeyString = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            out.writeUTF(publicKeyString);

            // Receive encrypted AES key from client
            String encryptedAESKey = in.readUTF();
            String aesKeyString = RSAUtils.decrypt(keyPair.getPrivate(), encryptedAESKey);
            SecretKey aesKey = AESUtils.decryptAESKey(aesKeyString);

            String command = in.readUTF();
            if ("LIST_FILES".equals(command)) {
                listFiles(out);
            } else if ("REQUEST_FILE".equals(command)) {
                String fileName = in.readUTF();
                sendFile(out, fileName, aesKey);
            } else {
                // Receive file
                receiveFile(in, command, aesKey);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void listFiles(DataOutputStream out) throws IOException {
        File folder = new File(SERVER_FOLDER);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            String fileNames = Stream.of(listOfFiles)
                    .map(File::getName)
                    .collect(Collectors.joining(";"));
            out.writeUTF(fileNames);
        } else {
            out.writeUTF("");
        }
    }

    private static void receiveFile(DataInputStream in, String fileName, SecretKey aesKey) throws Exception {
        File encryptedFile = new File(SERVER_FOLDER, "received_" + fileName);
        try (FileOutputStream fileOut = new FileOutputStream(encryptedFile)) {
            int bytes;
            byte[] buffer = new byte[4096];
            while ((bytes = in.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytes);
            }
        }

        // Decrypt file
        File decryptedFile = new File(SERVER_FOLDER, "decrypted_" + fileName);
        AESUtils.decryptFile(aesKey, encryptedFile, decryptedFile);

        System.out.println("File '" + fileName + "' received and decrypted successfully.");
    }

    private static void sendFile(DataOutputStream out, String fileName, SecretKey aesKey) throws Exception {
        File fileToSend = new File(SERVER_FOLDER, fileName);
        if (!fileToSend.exists()) {
            out.writeUTF("FILE_NOT_FOUND");
            System.out.println("File not found on server: " + fileName);
            return;
        }

        File encryptedFile = new File(SERVER_FOLDER, "encrypted_" + fileToSend.getName());
        AESUtils.encryptFile(aesKey, fileToSend, encryptedFile);

        out.writeUTF("FILE_FOUND");

        try (FileInputStream fileIn = new FileInputStream(encryptedFile)) {
            byte[] buffer = new byte[4096];
            int bytes;
            while ((bytes = fileIn.read(buffer)) != -1) {
                out.write(buffer, 0, bytes);
            }
        }

        System.out.println("File '" + fileName + "' encrypted and sent successfully.");
    }
}
