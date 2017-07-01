package android.csulb.edu.gesturerecognition;

    
public class ContactModel {
    // ID: primary key
    // name: Name of the contact in content provider.
    String id, name;

    public ContactModel() {
        id = "";
        name = "";
    }

    public ContactModel(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    // this method returns id
    public String getId() {
        return id;
    }

    // this method sets id
    public void setId(String id) {
        this.id = id;
    }

    // this method returns name of the contact
    public String getName() {
        return name;
    }

    // this method sets name of the contact
    public void setName(String name) {
        this.name = name;
    }
}