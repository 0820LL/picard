package picard.sam.util;

/**
 * Little struct-like class to hold read name information, record index, and duplicate set size information.
 */

public class RepresentativeReadName {

    public int readIndexInFile = -1;

    public int setSize = -1;

    public String readname = null;

    public String getReadName() {return readname;};

    public void setReadName(final String name) { this.readname = name; }

}
