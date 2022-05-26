package lab;

import java.util.ArrayList;
import java.util.List;

public class Laboratory {
    // Represents a lab
    // Pretty much just a collection of Lab Members that each prep test kits
    // The lab members also confirm or deny if a test kit is being sent
    private String name;
    private List<LabMember> labMembers;

    public static int WAITING = 0;
    public static int BUSY = 1;
    public static int UNAVAILABLE = -1;

    private int status;

    public Laboratory(String name, String[] members) {
        this.name = name;
        status = WAITING;
        labMembers = new ArrayList<>();
        for (String member : members) {
            LabMember newMember = new LabMember(member);
            labMembers.add(newMember);
        }
    }

    public LabMember getAvailableLabMember() {
        for (LabMember member : labMembers) {
            if (member.getStatus() == LabMember.WAITING) {
                return member;
            }
        }
        return null;
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

}
