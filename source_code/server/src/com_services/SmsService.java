package com_services;

import file_path.FilePath;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SmsService {
    // Email service
    // Outputs the email into a .csv file to emulate the "sending".
    private final String csv_path = FilePath.filePath + "sms.csv";
    private int nextId = -1;
    private List<String[]> fileData;

    public SmsService() {
        // Get the ID for the next email
        getLineCountOfCsv();
        fileData = getCsvData();
    }

    public void sendSms(String sender, String recipient, String content) {
        Message toSend = new Message(nextId, sender, recipient, content);
        nextId++;
        saveSmsToCsv(toSend);
    }

    private void saveSmsToCsv(Message sms) {
        File csvFile = new File(csv_path);
        // Write the account to the file
        try {
            FileWriter csvWriter = new FileWriter(csvFile, true); // Make sure it doesn't overwrite
            StringBuilder thisSmsLine = new StringBuilder(sms.getId() + "," + sms.getSender() + "," + sms.getRecipient()
                    + "," + sms.getContent() + ",");
            thisSmsLine.append("\n");
            csvWriter.append(thisSmsLine);
            csvWriter.flush();
            csvWriter.close();
            System.out.println("SMS was sent successfully! (Saved to sms.csv)");
        } catch (IOException e) {
            System.out.println("Unable to access file " + csv_path + "!");
        }
    }

    private void getLineCountOfCsv() {
        int lineCount = -1;
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(csv_path));
            while (csvReader.readLine() != null) {
                lineCount++;
            }
            nextId = lineCount;
        } catch (FileNotFoundException e) {
            // Create the file
            try {
                File csvFile = new File(csv_path);
                csvFile.createNewFile();
                // Write the headers to the file
                FileWriter csvWriter = new FileWriter(csvFile);
                csvWriter.append("Id,Sender,Recipient,Content\n");
                csvWriter.flush();
                csvWriter.close();
                nextId = 0;
            }
            catch (IOException ex) {
                System.out.println("Unable to create a new .csv file.");
                nextId = -1;
            }
        } catch (IOException e) {
            System.out.println("Unable to read the existing .csv file.");
            nextId = -1;
        }
    }

    public String getSms(int id) {
        for (String[] line : fileData) {
            if (line[0].equalsIgnoreCase("" + id)) {
                StringBuilder sms = new StringBuilder();
                int counter = 0;
                for (String entry : line) {
                    if (counter != 0) {
                        sms.append(entry);
                        sms.append(",");
                    }
                    counter++;
                }
                return sms.toString();
            }
        }
        return "null";
    }

    private List<String[]> getCsvData() {
        // Call when an admin wants to manage already sent emails
        List<String[]> fileData = new ArrayList<>();
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(csv_path));
            int counter = 0;
            String line = csvReader.readLine(); // Read in the initial line
            while (line != null) { // While the lines aren't null, read in all of them
                if (counter != 0) { // Ignore the headers
                    String[] row_data = line.split(",");
                    fileData.add(row_data);
                }
                counter++;
                line = csvReader.readLine(); // Read in the next line - done here or else lines are skipped
            }
            csvReader.close(); // Close after reading the file
        } catch (FileNotFoundException e) {
            // Create the file
            try {
                File csvFile = new File(csv_path);
                csvFile.createNewFile();
                // Write the headers to the file
                FileWriter csvWriter = new FileWriter(csvFile);
                csvWriter.append("Id,Sender,Recipient,Content\n");
                csvWriter.flush();
                csvWriter.close();
            }
            catch (IOException ex) {
                System.out.println("Unable to create a new .csv file.");
            }
        } catch (IOException e) {
            System.out.println("Unable to read the existing .csv file.");
            fileData = null;
        }
        return fileData;
    }
}

