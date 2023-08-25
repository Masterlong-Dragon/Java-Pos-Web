package pos.entities.individuals;

public class Individual {
    private int ID;
    private String name;
    private String contact;
    private String address;
    public static final Individual DEFAULT_CUSTOMER = new Individual(1, "Walk-in POS.Individual.Customer", "", "");

    public Individual() {
        ID = 0;
        name = "";
        contact = "";
        address = "";
    }

    public Individual(int ID, String name, String contact, String address) {
        this.ID = ID;
        this.name = name;
        this.contact = contact;
        this.address = address;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
