package ft.filetransfer;

import java.io.*;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.SecretKey;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Client extends Application {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;
    private KeyPair keyPair;
    private PublicKey serverPublicKey;
    private static final String CLIENT_FOLDER = "C://Users//Admin//Desktop//Client/";

    private static final String SERVER_FOLDER = "C://Users//Admin//Desktop//Server/";

    public Client() {
        try {
            this.keyPair = RSAUtils.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectAndTransferFile(String filePath) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            System.out.println("Successful connection to Secure File Transfer Server...");


            receiveServerPublicKey(in);


            SecretKey aesKey = AESUtils.generateAESKey();


            sendEncryptedAESKey(out, aesKey);


            transferFile(out, filePath, aesKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectAndDownloadFile(String fileName, String savePath) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            System.out.println("Successful connection to Secure File Transfer Server...");


            receiveServerPublicKey(in);


            SecretKey aesKey = AESUtils.generateAESKey();


            sendEncryptedAESKey(out, aesKey);


            requestFile(out, in, fileName, savePath, aesKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveServerPublicKey(DataInputStream in) throws Exception {
        String serverPublicKeyString = in.readUTF();
        serverPublicKey = RSAUtils.decodePublicKey(serverPublicKeyString);
        System.out.println("Server's public key received...");
    }

    private void sendEncryptedAESKey(DataOutputStream out, SecretKey aesKey) throws Exception {
        String encryptedAESKey = RSAUtils.encrypt(serverPublicKey, Base64.getEncoder().encodeToString(aesKey.getEncoded()));
        out.writeUTF(encryptedAESKey);
        System.out.println("AES key encrypted and sent...");
    }

    private void transferFile(DataOutputStream out, String filePath, SecretKey aesKey) throws Exception {
        File inputFile = new File(filePath);
        if (!inputFile.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }

        File encryptedFile = new File("encrypted_" + inputFile.getName());
        AESUtils.encryptFile(aesKey, inputFile, encryptedFile);

        try (FileInputStream fileIn = new FileInputStream(encryptedFile)) {
            out.writeUTF(inputFile.getName());
            byte[] buffer = new byte[4096];
            int bytes;
            while ((bytes = fileIn.read(buffer)) != -1) {
                out.write(buffer, 0, bytes);
            }
        }

        System.out.println("File '" + inputFile.getName() + "' encrypted and sent successfully.");
    }

    private void requestFile(DataOutputStream out, DataInputStream in, String fileName, String savePath, SecretKey aesKey) throws Exception {
        out.writeUTF("REQUEST_FILE");
        out.writeUTF(fileName);

        String response = in.readUTF();
        if ("FILE_NOT_FOUND".equals(response)) {
            System.out.println("File not found on server: " + fileName);
            return;
        }

        File encryptedFile = new File(savePath + ".enc");
        try (FileOutputStream fileOut = new FileOutputStream(encryptedFile)) {
            int bytes;
            byte[] buffer = new byte[4096];
            while ((bytes = in.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytes);
            }
        }

        File decryptedFile = new File(savePath);
        AESUtils.decryptFile(aesKey, encryptedFile, decryptedFile);
        encryptedFile.delete();

        System.out.println("File '" + fileName + "' downloaded and decrypted successfully to " + savePath);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to upload or download a file? (u/d): ");
        String choice = scanner.nextLine();

        if ("u".equalsIgnoreCase(choice)) {
            uploadFile();
        } else if ("d".equalsIgnoreCase(choice)) {
            downloadFile();
        } else {
            System.out.println("Invalid choice. Please enter 'u' for upload or 'd' for download.");
        }
    }

    private void uploadFile() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Upload");
        fileChooser.setInitialDirectory(new File(CLIENT_FOLDER));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            connectAndTransferFile(selectedFile.getAbsolutePath());
        }
    }

    private void downloadFile() {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            System.out.println("Successful connection to Secure File Transfer Server...");


            receiveServerPublicKey(in);


            SecretKey aesKey = AESUtils.generateAESKey();


            sendEncryptedAESKey(out, aesKey);


            out.writeUTF("LIST_FILES");


            String filesList = in.readUTF();
            List<String> files = Stream.of(filesList.split(";")).collect(Collectors.toList());


            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select File to Download");
            fileChooser.setInitialDirectory(new File(SERVER_FOLDER));  // Adjust initial directory if needed
            for (String fileName : files) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileName, "*." + getFileExtension(fileName)));
            }
            Stage stage = new Stage();
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                String selectedFileName = selectedFile.getName();
                Stage saveStage = new Stage();
                FileChooser saveFileChooser = new FileChooser();
                saveFileChooser.setTitle("Save Downloaded File");
                saveFileChooser.setInitialDirectory(new File(CLIENT_FOLDER));
                File saveFile = saveFileChooser.showSaveDialog(saveStage);
                if (saveFile != null) {
                    connectAndDownloadFile(selectedFileName, saveFile.getAbsolutePath());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    @Override
    public void start(Stage primaryStage) {
        run();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
