package lab;

public class LabMember {
    // Can prep test kits for a user
    // Can also use the email service to reply to a contact tracer
    public static int UNAVAILABLE = -1;
    public static int WAITING = 0;
    public static int BUSY = 1;

    private String name;
    private int status;

    public LabMember(String name) {
        this.name = name;
        status = WAITING;
    }

    public void prepTestKit(String name, String address) {
        System.out.println("Lab member " + getName() + " has prepped a test kit for " + name + " living at " + address);
    }

    public void setStatus(int status) { this.status = status; }

    public int getStatus() {
        return status;
    }
    public String getName() { return name; }

}
