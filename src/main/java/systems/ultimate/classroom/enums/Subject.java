package systems.ultimate.classroom.enums;


public enum Subject {
    MATHS("Mathematics"),
    SCIENCE("Science"),
    ART("Art"),
    IT("Computer Science");

    private final String desc;
    Subject(String desc){
        this.desc = desc;
    }

    public String description() {
        return desc;
    }
}
