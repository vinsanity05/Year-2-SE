package contact_tracing;

import com_services.CommunicationService;
import com_services.Email;
import file_path.FilePath;
import lab.LabMember;
import lab.Laboratory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ContactTracingService {
    // Imitates how an NHS staff member would use the system
    // The job of the staff members is to pass information to and from different users in the system
    private List<ContactTracer> contactTracers;
    private List<Laboratory> laboratories;
    public CommunicationService communicationService;

    private final String labPath = FilePath.filePath + "laboratories.csv"; // Labs are stored in a separate file
    private final String contactPath = FilePath.filePath + "contact_tracers.csv"; // Contact tracers are stored in a separate file

    public ContactTracingService() {
        contactTracers = new ArrayList<>();
        laboratories = new ArrayList<>();
        communicationService = new CommunicationService();
        loadContactTracers();
        loadLabs();
        System.out.println("Loaded contact tracers");
        System.out.println("Loaded labs");
    }

    public void contactLabMemberForTestKitPrep(String name, String address) {
        ContactTracer thisTracer = getAvailableContactTracer();
        Laboratory thisLab = getAvailableLaboratory();
        LabMember thisMember = thisLab.getAvailableLabMember();
        // NAME | ADDRESS
        thisTracer.contactLabMember(name, address, thisMember);
        //thisMember.prepTestKit(name, address);
    }

    public ContactTracer getAvailableContactTracer() {
        for (ContactTracer contactTracer : contactTracers) {
            if (contactTracer.getStatus() == ContactTracer.WAITING) {
                return contactTracer;
            }
        }
        return null;
    }

    public void contactPersonInContact(String contact) {
        ContactTracer thisTracer = getAvailableContactTracer();
        thisTracer.setStatus(ContactTracer.BUSY);
        System.out.println(contact);
        String[] _contact = contact.split("\\|");
        String name = _contact[0];
        String email = _contact[1];
        String phone_number = _contact[2];
        communicationService.sendEmail(thisTracer.getName() + "@nhstts.com",
                email,
                "Warning of Covid Contact",
                "Hello " + name + ". This message is to warn you that you have been in contact with" +
                " someone who has contracted COVID-19. Please self-isolate and sign up to the NHS TTS app to get a test " +
                "kit sent to your address. Thank you.", null);
        communicationService.sendSms(thisTracer.getName() + "@nhstts.com",
                phone_number,
                "Hello " + name + ". This message is to warn you that you have been in contact with" +
                        " someone who has contracted COVID-19. Please self-isolate and sign up to the NHS TTS app to get a test " +
                        "kit sent to your address. Thank you.");
        thisTracer.setStatus(ContactTracer.WAITING);
    }

    public Laboratory getAvailableLaboratory() {
        for (Laboratory lab : laboratories) {
            if (lab.getStatus() == Laboratory.WAITING) {
                return lab;
            }
        }
        return null;
    }

    private void loadContactTracers() {
        List<String[]> contactTracerFile = getCsvData(contactPath);
        for (String[] s : contactTracerFile) {
            ContactTracer contactTracer = new ContactTracer(Integer.parseInt(s[0]), s[1]);
            contactTracers.add(contactTracer);
        }
    }

    private void loadLabs() {
        List<String[]> labFile = getCsvData(labPath);
        for (String[] line : labFile) {
            // Each string[] represents a name followed by a list of members
            String[] members = line[1].split("\\|"); // split by a single pipe
            Laboratory newLab = new Laboratory(line[0], members);
            laboratories.add(newLab);
        }
    }

    private List<String[]> getCsvData(String csv_path) {
        List<String[]> fileData = new ArrayList<>();
        System.out.println("loading " + csv_path);
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
                if (csv_path.equalsIgnoreCase(labPath)) {
                    File csvFile = new File(csv_path);
                    csvFile.createNewFile();
                    // Write the headers to the file
                    FileWriter csvWriter = new FileWriter(csvFile);
                    csvWriter.append("Name,Members\n");
                    // Default lab
                    csvWriter.append("BRISTOL LAB,STEVE|JEFF|MARY|LISA");
                    String[] members = new String[]{"STEVE", "JEFF", "MARY", "LISA"};
                    laboratories.add(new Laboratory("BRISTOL LAB", members));
                    csvWriter.flush();
                    csvWriter.close();
                } else {
                    File csvFile = new File(csv_path);
                    csvFile.createNewFile();
                    // Write the headers to the file
                    FileWriter csvWriter = new FileWriter(csvFile);
                    csvWriter.append("Id,Name\n");
                    // Default tracers
                    csvWriter.append("0,Oliver\n");
                    csvWriter.append("1,Rosie\n");
                    csvWriter.append("2,Poppy\n");
                    csvWriter.append("3,Mike\n");
                    contactTracers.add(new ContactTracer(0, "Oliver"));
                    contactTracers.add(new ContactTracer(1, "Rosie"));
                    contactTracers.add(new ContactTracer(2, "Poppy"));
                    contactTracers.add(new ContactTracer(3, "Mike"));
                    csvWriter.flush();
                    csvWriter.close();
                }
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
