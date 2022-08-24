package gitlet;



import java.util.Date;
import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.Calendar;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  Commit
 *  does at a high level.
 *
 *  @author Michael
 */
public class Commit implements Serializable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date time;
    private String parent1;
    private String parent2;
    private TreeMap<String, String> filepointers = null;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    //@Source: Knew about ternary operators before, googled java syntax
    //https://www.baeldung.com/java-ternary-operator
    public Commit(String m, Commit p1, Commit p2) {
        this.message = m;
        this.time = new Date();
        this.parent1 = p1 == null ? null : p1.getHash();
        this.parent2 = p2 == null ? null : p2.getHash();
        if (p1 == null) {
            this.time = new Date((long) 0.0);
        }
    }
    public Commit(String m, Date t, String p1, String p2, TreeMap<String, String> f) { //for cloning
        this.message = m;
        this.time = t;
        this.parent1 = p1;
        this.parent2 = p2;
        this.filepointers = f;
    }
    public String getHash() {
        String hp1 = parent1 == null ? "0" : Integer.toString(parent1.hashCode());
        String hp2 = parent2 == null ? "0" : Integer.toString(parent2.hashCode());
        String hfp = filepointers == null ? "0" : Integer.toString(filepointers.hashCode());
        return Utils.sha1(Integer.toString(message.hashCode()),
                Integer.toString(time.hashCode()), hp1, hp2, hfp);
    }
    public String toFile() {

        String hash = getHash();
        File f = join(GITLET_DIR, hash);
        Utils.writeObject(f, this);
        return hash;
    }
    public static Commit fromFile(File commit) {
        return Utils.readObject(commit, Commit.class);
    }
    public TreeMap<String, String> getMap() {
        return this.filepointers;
    }
    public void setMap(TreeMap<String, String> map) {
        this.filepointers = map;
    }
    public void setParent1(Commit parent) {
        if (parent == null) {
            this.parent1 = null;
            return;
        }
        this.parent1 = parent.getHash();
    }
    public String getParent1() {
        return this.parent1;
    }
    public void setParent2(Commit parent) {
        if (parent == null) {
            this.parent2 = null;
            return;
        }
        this.parent2 = parent.getHash();
    }
    public String getParent2() {
        return this.parent2;
    }
    public Commit clone() {
        return new Commit(message, time, parent1, parent2, filepointers);
    }
    public void setNewTime() {
        time = new Date();
    }
    public void setNewMessage(String m) {
        message = m;
    }
    public String getMessage() {
        return this.message;
    }
    public Date getDate() {
        return this.time;
    }
    public static Commit findSplitPoint(Commit a, Commit b, String branch1, String branch2) {
        File immediateSplitPoints = join(GITLET_DIR, "IMSPLITPOINTS");
        TreeMap<String, String> isP = readObject(immediateSplitPoints, TreeMap.class);
        if (isP.get(branch1 + branch2) != null
                || isP.get(branch2 + branch1) != null) {
            Commit out = isP.get(branch1 + branch2) != null
                    ? Commit.fromFile(join(GITLET_DIR, isP.get(branch1 + branch2)))
                    : Commit.fromFile(join(GITLET_DIR,
                    isP.get(branch2 + branch1)));
            return out;
        }
        String splitstr = recurSplit(a.getHash(), b.getHash());
        String[] hashes = splitstr.split("\\s+");
        String latest = hashes[0];
        if (hashes.length > 1) {
            for (String s : hashes) {
                File latFile = join(GITLET_DIR, latest);
                Commit lat = Commit.fromFile(latFile);
                Date C = lat.getDate();
                File temp = join(GITLET_DIR, s);
                Commit T = Commit.fromFile(temp);
                Date I = T.getDate();
                if (C.compareTo(I) < 0) {
                    latest = s;
                }
            }
        }
        return Commit.fromFile(join(GITLET_DIR, latest));
    }
    private static String recurSplit(String a, String b) {
        if (a != null && b != null && a.equals(b)) {
            return a;
        }
        if (a == null || b == null) {
            return "";
        }
        Commit acom = Commit.fromFile(join(GITLET_DIR, a));
        Commit bcom = Commit.fromFile(join(GITLET_DIR, b));
        String a1 = acom.getParent1();
        String a2 = acom.getParent2();
        String b1 = bcom.getParent1();
        String b2 = bcom.getParent2();
        String r1 = ""; //recurSplit(a, b1) + " ";
        String r2 = ""; //recurSplit(a, b2) + " ";
        String r3 = recurSplit(a1, b1) + " ";
        String r4 = recurSplit(a1, b2) + " ";
        String r5 = recurSplit(a2, b1) + " ";
        String r6 = recurSplit(a2, a2) + " ";
        String r7 = ""; //recurSplit(a1, b) + " ";
        String r8 = ""; //recurSplit(a2, b);
        return r1 + r2 + r3 + r4 + r6 + r7 + r8;
    }
    //@Source: Used stackoverflow to get a grasp on
    // how to do the regex for whitespace splitting,
    // and for the Calendar class
    //https://stackoverflow.com/questions/7899525/how-to-split-a-string-by-space
    //https://stackoverflow.com/questions/7899525/how-to-split-a-string-by-space
    @Override
    public String toString() {
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int dayInt = cal.get(Calendar.DAY_OF_WEEK);
        String day = days[dayInt];
        String d = time.toGMTString();
        String[] sec = d.split("\\s+");
        String finalDate = day + " " + sec[1] + " " + sec[0] + " " + sec[3] + " " + sec[2];
        if (parent2 != null) {
            String returnStr = "===\n";
            returnStr = returnStr + "commit " + getHash() + "\n";
            returnStr = returnStr + "Merge: " + parent1.substring(0, 7) + " "
                    + parent2.substring(0, 7) + "\n";
            returnStr = returnStr + "Date: " + finalDate + " "
                    + "-0800" + " "
                    + "\n";
            returnStr = returnStr + message + "\n";
            return returnStr;
        } else {
            String returnStr = "===\n";
            returnStr = returnStr + "commit " + getHash() + "\n";
            returnStr = returnStr + "Date: " + finalDate
                    + " " + "-0800"
                    + " " + "\n";
            returnStr = returnStr + message + "\n";
            return returnStr;
        }
    }
}
