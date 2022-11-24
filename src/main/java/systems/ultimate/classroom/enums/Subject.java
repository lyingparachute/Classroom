package systems.ultimate.classroom.enums;


import lombok.Getter;

@Getter
public enum Subject {
    MATHS("Mathematics"),
    SCIENCE("Science"),
    ART("Art"),
    IT("Computer Science");

    private final String desc;
    Subject(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
