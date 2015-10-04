package doop.persistent.elements

/**
 * Created by anantoni on 2/10/2015.
 */
class Field extends Symbol {
    String signature;
    String type;
    Class enclosingClass;
    boolean isStatic;

    public Field() {}

    public Field(Position position, String compilationUnit, String signature, String type, Class enclosingClass, boolean isStatic) {
        super(position, compilationUnit);
        this.signature = signature;
        this.type = type;
        this.enclosingClass = enclosingClass;
        this.isStatic = isStatic;
    }

}
