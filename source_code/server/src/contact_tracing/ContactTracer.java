package contact_tracing;

import com_services.Email;
import com_services.Message;
import lab.LabMember;

public class ContactTracer {
    // Imitates how a contact tracer might be busy, available or unavailable in the real world
    // A contact tracer can only accept instructions if it is waiting
    // In a case where multiple clients may be connected (in the real world), the system would place them in a queue
    // until a contact tracer is available
    public static int UNAVAILABLE = -1;
    public static int WAITING = 0;
    public static int BUSY = 1;

    private int status, id;
    private String name;

    public ContactTracer(int id, String name) {
        status = 0;
        this.id = id;
        this.name = name;
    }

    public void contactLabMember(String name, String address, LabMember labMember) {
        System.out.println("Contact tracer " + this.name + " (id: " + id + " contacted lab member " +
                labMember.getName() + " to prep a test kit.");
        labMember.prepTestKit(name, address);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() { return status; }
    public String getName() { return name; }
}
