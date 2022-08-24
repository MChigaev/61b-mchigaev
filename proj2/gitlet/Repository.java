package gitlet;

import java.io.File;
import static gitlet.Utils.*;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/** Represents a gitlet repository.
 *  Repository
 *  does at a high level.
 *
 *  @author Michael
 */
public class Repository {
    /**
     *
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        Commit initial = new Commit("initial commit", null, null);
        String commitID = initial.toFile();
        File heaD = join(GITLET_DIR, "HEAD");
        writeContents(heaD, commitID);
        File master = join(GITLET_DIR, "master");
        writeContents(master, commitID); //FIX ISSUES WITH BRANCHES
        File activebranch = join(GITLET_DIR, "ACTIVEBRANCH");
        writeContents(activebranch, "master");
        File branchesFile = join(GITLET_DIR, "BRANCHES");
        ArrayList<String> branches = new ArrayList<String>();
        branches.add("master");
        writeObject(branchesFile, branches);
        File immediateSplitPoints = join(GITLET_DIR, "IMSPLITPOINTS");
        TreeMap<String, String> isP = new TreeMap<String, String>();
        writeObject(immediateSplitPoints, isP);
    }
    public static void add(String filename) {
        File blobnum = join(GITLET_DIR, "blobnum");
        int num;
        if (blobnum.exists()) {
            num = Integer.parseInt(readContentsAsString(blobnum));
        } else {
            num = 0;
        }
        File f = join(CWD, filename);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        File staginGareA = join(GITLET_DIR, "STAGINGAREA");
        File removaLareA = join(GITLET_DIR, "REMOVALAREA");
        File heaD = join(GITLET_DIR, "HEAD");
        Commit currcom = Commit.fromFile(join(GITLET_DIR, readContentsAsString(heaD)));
        TreeMap<String, String> map = currcom.getMap() != null
                ? currcom.getMap() : new TreeMap<String, String>();
        TreeMap<String, String> removalArea = removaLareA.exists()
                ? readObject(removaLareA, TreeMap.class) : new TreeMap<String, String>();
        TreeMap<String, String> stagingArea;
        if (staginGareA.exists()) {
            stagingArea = readObject(staginGareA, TreeMap.class);
        } else {
            stagingArea = new TreeMap<String, String>();
        }
        String contents = readContentsAsString(f);
        if (stagingArea.containsKey(filename)) {
            File blob = join(GITLET_DIR, stagingArea.get(filename));
            writeContents(blob, contents);
            return;
        }
        if (map.containsKey(filename)) {
            String trackedfstr = map.get(filename);
            File trackedf = join(GITLET_DIR, trackedfstr);
            String contents2 = readContentsAsString(trackedf);
            if (contents.equals(contents2)) {
                stagingArea.remove(filename);
                writeObject(staginGareA, stagingArea);
                if (removalArea.containsKey(filename)) {
                    removalArea.remove(filename);
                }
                writeObject(removaLareA, removalArea);
                return;
            }
        }
        File blob = join(GITLET_DIR, "blob" + Integer.toString(num));
        writeContents(blob, contents);
        stagingArea.put(filename, "blob" + Integer.toString(num));
        writeObject(staginGareA, stagingArea);
        num++;
        writeContents(blobnum, Integer.toString(num));
    }
    public static void rm(String filename) {
        File removaLareA = join(GITLET_DIR, "REMOVALAREA");
        File staginGareA = join(GITLET_DIR, "STAGINGAREA");
        File heaD = join(GITLET_DIR, "HEAD");
        Commit currcom = Commit.fromFile(join(GITLET_DIR, readContentsAsString(heaD)));
        TreeMap<String, String> currcomfiles = currcom.getMap();
        TreeMap<String, String> stagingArea = new TreeMap<String, String>();
        TreeMap<String, String> removalArea = new TreeMap<String, String>();
        boolean staged = false;
        if (removaLareA.exists()) {
            removalArea = readObject(removaLareA, TreeMap.class);
        }
        if (staginGareA.exists()) {
            stagingArea = readObject(staginGareA, TreeMap.class);
        }
        if (currcomfiles == null) {
            currcomfiles = new TreeMap<String, String>();
        }
        File fd = join(CWD, filename);
        if (stagingArea.containsKey(filename)) {
            stagingArea.remove(filename);
            staged = true;
        }
        if (currcomfiles.containsKey(filename)) {
            removalArea.put(filename, null);
            fd.delete();
        } else if (!staged) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        writeObject(staginGareA, stagingArea);
        writeObject(removaLareA, removalArea);
    }
    public static void status() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        File branchesFile = join(GITLET_DIR, "BRANCHES");
        ArrayList<String> branches = readObject(branchesFile, ArrayList.class);
        String active = readContentsAsString(join(GITLET_DIR, "ACTIVEBRANCH"));
        System.out.println("=== Branches ===");
        System.out.println("*" + active);
        branches.remove(active);
        Collections.sort(branches);
        for (String b : branches) {
            System.out.println(b);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        File staginGareA = join(GITLET_DIR, "STAGINGAREA");
        if (staginGareA.exists()) {
            TreeMap<String, String> stagingArea = readObject(staginGareA, TreeMap.class);
            Set<String> set = stagingArea.keySet();
            ArrayList<String> keys = new ArrayList<String>();
            keys.addAll(set);
            Collections.sort(keys);
            for (String key : keys) {
                System.out.println(key);
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        File removaLareA = join(GITLET_DIR, "REMOVALAREA");
        if (removaLareA.exists()) {
            TreeMap<String, String> removalArea = readObject(removaLareA, TreeMap.class);
            Set<String> set = removalArea.keySet();
            ArrayList<String> keys = new ArrayList<String>();
            keys.addAll(set);
            Collections.sort(keys);
            for (String key : keys) {
                System.out.println(key);
            }
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }
    public static void commit(String message) {
        File staginGareA = join(GITLET_DIR, "STAGINGAREA");
        File removaLareA = join(GITLET_DIR, "REMOVALAREA");
        if (!staginGareA.exists() && !removaLareA.exists()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        TreeMap<String, String> stagingArea = staginGareA.exists()
                ? readObject(staginGareA, TreeMap.class) : new TreeMap<String, String>();
        TreeMap<String, String> removalArea = removaLareA.exists()
                ? readObject(removaLareA, TreeMap.class) : new TreeMap<String, String>();
        if (stagingArea.size() == 0 && removalArea.size() == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        File fileParentCommit = join(GITLET_DIR, readContentsAsString(join(GITLET_DIR, "HEAD")));
        Commit parentcommit = Commit.fromFile(fileParentCommit);
        Commit latestcommit = parentcommit.clone();
        latestcommit.setParent1(parentcommit);
        latestcommit.setParent2(null);
        latestcommit.setNewMessage(message);
        latestcommit.setNewTime();
        TreeMap<String, String> previousmap = parentcommit.getMap();
        if (previousmap == null) {
            latestcommit.setMap(stagingArea);
        } else {
            for (String key : stagingArea.keySet()) {
                if (previousmap.containsKey(key)) {
                    previousmap.replace(key, stagingArea.get(key));
                } else {
                    previousmap.put(key, stagingArea.get(key));
                }
            }
            latestcommit.setMap(previousmap);
        }
        TreeMap<String, String> currmap = latestcommit.getMap();
        for (String key : removalArea.keySet()) {
            currmap.remove(key);
        }
        latestcommit.setMap(currmap);
        latestcommit.toFile();
        staginGareA.delete();
        removaLareA .delete();
        File heaD = join(GITLET_DIR, "HEAD");
        writeContents(heaD, latestcommit.getHash());
        File activebranch = join(GITLET_DIR, "ACTIVEBRANCH");
        File active = join(GITLET_DIR, readContentsAsString(activebranch));
        writeContents(active, latestcommit.getHash());
    }
    public static void commit(String message, Commit p2) { // For second parents
        File staginGareA = join(GITLET_DIR, "STAGINGAREA");
        File removaLareA = join(GITLET_DIR, "REMOVALAREA");
        TreeMap<String, String> stagingArea = staginGareA.exists()
                ? readObject(staginGareA, TreeMap.class) : new TreeMap<String, String>();
        TreeMap<String, String> removalArea = removaLareA.exists()
                ? readObject(removaLareA, TreeMap.class) : new TreeMap<String, String>();
        File fileParentCommit = join(GITLET_DIR, readContentsAsString(join(GITLET_DIR, "HEAD")));
        Commit parentcommit = Commit.fromFile(fileParentCommit);
        Commit latestcommit = parentcommit.clone();
        latestcommit.setParent1(parentcommit);
        latestcommit.setParent2(p2);
        latestcommit.setNewMessage(message);
        latestcommit.setNewTime();
        TreeMap<String, String> previousmap = parentcommit.getMap();
        if (previousmap == null) {
            latestcommit.setMap(stagingArea);
        } else {
            for (String key : stagingArea.keySet()) {
                if (previousmap.containsKey(key)) {
                    previousmap.replace(key, stagingArea.get(key));
                } else {
                    previousmap.put(key, stagingArea.get(key));
                }
            }
            latestcommit.setMap(previousmap);
        }
        TreeMap<String, String> currmap = latestcommit.getMap();
        for (String key : removalArea.keySet()) {
            currmap.remove(key);
        }
        latestcommit.setMap(currmap);
        latestcommit.toFile();
        staginGareA.delete();
        removaLareA.delete();
        File heaD = join(GITLET_DIR, "HEAD");
        writeContents(heaD, latestcommit.getHash());
        File activebranch = join(GITLET_DIR, "ACTIVEBRANCH");
        File active = join(GITLET_DIR, readContentsAsString(activebranch));
        writeContents(active, latestcommit.getHash());
    }
    public static void checkout(String filename) {
        File heaD = join(GITLET_DIR, "HEAD");
        File fileCom = join(GITLET_DIR, readContentsAsString(heaD));
        Commit com = Commit.fromFile(fileCom);
        TreeMap<String, String> map = com.getMap();
        if (!map.containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File fd = join(CWD, filename);
        File blob = join(GITLET_DIR, map.get(filename));
        writeContents(fd, readContentsAsString(blob));
    }
    public static void checkout(String commitID, String filename) {
        if (commitID.length() < 10) {
            List<String> filesarr = plainFilenamesIn(GITLET_DIR);
            ArrayList<String> files = new ArrayList<String>(filesarr);
            files.remove("HEAD");
            files.remove("STAGINGAREA");
            files.remove("REMOVALAREA");
            files.remove("ACTIVEBRANCH");
            files.remove("BRANCHES");
            files.remove("blobnum");
            files.remove("IMSPLITPOINTS");
            File branchesFile = join(GITLET_DIR, "BRANCHES");
            ArrayList<String> branches = readObject(branchesFile, ArrayList.class);
            for (String br : branches) {
                files.remove(br);
            }
            File blobNum = join(GITLET_DIR, "blobnum");
            int blob = blobNum.exists() ? Integer.parseInt(readContentsAsString(blobNum)) : 0;
            for (int i = 0; i < blob; i++) {
                files.remove("blob" + i);
            }
            for (String s : files) {
                String comps = " " + s;
                if (comps.contains(" " + commitID)) {
                    commitID = s;
                    break;
                }
            }
        }
        File fileCom = join(GITLET_DIR, commitID);
        if (!fileCom.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit com = Commit.fromFile(fileCom);
        TreeMap<String, String> map = com.getMap();
        if (!map.containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File fd = join(CWD, filename);
        File blob = join(GITLET_DIR, map.get(filename));
        writeContents(fd, readContentsAsString(blob));
    }
    public static void checkoutBranch(String branch) {
        File br = join(GITLET_DIR, branch);
        if (!br.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        File activebranch = join(GITLET_DIR, "ACTIVEBRANCH");
        String active = readContentsAsString(activebranch);
        if (branch.equals(active)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        Commit currcommit = Commit.fromFile(join(GITLET_DIR,
                readContentsAsString(join(GITLET_DIR, active))));
        Commit newcommit = Commit.fromFile(join(GITLET_DIR,
                readContentsAsString(br)));
        List<String> files = plainFilenamesIn(CWD);
        TreeMap<String, String> currfiles = currcommit.getMap();
        TreeMap<String, String> newfiles = newcommit.getMap();
        currfiles = currfiles == null ? new TreeMap<String, String>() : currfiles;
        newfiles = newfiles == null ? new TreeMap<String, String>() : newfiles;
        for (String f : files) {
            if (newfiles.containsKey(f) && !currfiles.containsKey(f)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                if (branch.equals("-")) {
                    br.delete();
                }
                System.exit(0);
            }
        }
        for (String key : newfiles.keySet()) {
            File temp = join(CWD, key);
            String contents;
            File blob = join(GITLET_DIR, newfiles.get(key));
            contents = readContentsAsString(blob);
            writeContents(temp, contents);
        }
        for (String key : currfiles.keySet()) {
            File temp = join(CWD, key);
            if (temp.exists() && !newfiles.containsKey(key)) {
                temp.delete();
            }
        }
        File stagingArea = join(GITLET_DIR, "STAGINGAREA");
        if (stagingArea.exists()) {
            stagingArea.delete();
        }
        writeContents(activebranch, branch);
        File heaD = join(GITLET_DIR, "HEAD");
        writeContents(heaD, newcommit.getHash());
    }
    public static void globallog() {
        List<String> filesarr = plainFilenamesIn(GITLET_DIR);
        ArrayList<String> files = new ArrayList<String>(filesarr);
        files.remove("HEAD");
        files.remove("STAGINGAREA");
        files.remove("REMOVALAREA");
        files.remove("ACTIVEBRANCH");
        files.remove("BRANCHES");
        files.remove("blobnum");
        files.remove("IMSPLITPOINTS");
        File branchesFile = join(GITLET_DIR, "BRANCHES");
        ArrayList<String> branches = readObject(branchesFile, ArrayList.class);
        for (String br : branches) {
            files.remove(br);
        }
        File blobNum = join(GITLET_DIR, "blobnum");
        int blob = blobNum.exists() ? Integer.parseInt(readContentsAsString(blobNum)) : 0;
        for (int i = 0; i < blob; i++) {
            files.remove("blob" + i);
        }
        for (String com : files) {
            Commit c = Commit.fromFile(join(GITLET_DIR, com));
            System.out.println(c);
        }
    }
    public static void find(String message) {
        List<String> filesarr = plainFilenamesIn(GITLET_DIR);
        ArrayList<String> files = new ArrayList<String>(filesarr);
        files.remove("HEAD");
        files.remove("STAGINGAREA");
        files.remove("REMOVALAREA");
        files.remove("ACTIVEBRANCH");
        files.remove("BRANCHES");
        files.remove("blobnum");
        files.remove("IMSPLITPOINTS");
        File branchesFile = join(GITLET_DIR, "BRANCHES");
        ArrayList<String> branches = readObject(branchesFile, ArrayList.class);
        for (String br : branches) {
            files.remove(br);
        }
        File blobNum = join(GITLET_DIR, "blobnum");
        int blob = blobNum.exists() ? Integer.parseInt(readContentsAsString(blobNum)) : 0;
        for (int i = 0; i < blob; i++) {
            files.remove("blob" + i);
        }
        boolean found = false;
        for (String com : files) {
            Commit c = Commit.fromFile(join(GITLET_DIR, com));
            if (c.getMessage().equals(message)) {
                System.out.println(com);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }
    public static void branch(String b) {
        if (b.equals("-")) {
            System.out.println("Error. This is a non-usable branch name");
            System.exit(0);
        }
        File br = join(GITLET_DIR, b);
        if (br.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        String activebranchID = readContentsAsString(join(GITLET_DIR, "ACTIVEBRANCH"));
        File heaD = join(GITLET_DIR, "HEAD");
        writeContents(br, readContentsAsString(heaD));
        File immediateSplitPoints = join(GITLET_DIR, "IMSPLITPOINTS");
        TreeMap<String, String> isP = readObject(immediateSplitPoints, TreeMap.class);
        isP.put(activebranchID + b, readContentsAsString(heaD));
        writeObject(immediateSplitPoints, isP);
        File branchesFile = join(GITLET_DIR, "BRANCHES");
        ArrayList<String> branches = readObject(branchesFile, ArrayList.class);
        branches.add(b);
        writeObject(branchesFile, branches);
    }
    public static void rmbranch(String b) {
        String active = readContentsAsString(join(GITLET_DIR, "ACTIVEBRANCH"));
        if (b.equals(active)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        File branch = join(GITLET_DIR, b);
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        branch.delete();
        File branchesFile = join(GITLET_DIR, "BRANCHES");
        ArrayList<String> branches = readObject(branchesFile, ArrayList.class);
        branches.remove(b);
        writeObject(branchesFile, branches);
    }
    public static void reset(String com) {
        File c = join(GITLET_DIR, com);
        String activebranchID = readContentsAsString(join(GITLET_DIR, "ACTIVEBRANCH"));
        if (!c.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        customBranch(com);
        checkoutBranch("-");
        File active = join(GITLET_DIR, "ACTIVEBRANCH");
        writeContents(active, activebranchID);
        rmbranch("-");
        File currbranch = join(GITLET_DIR, activebranchID);
        writeContents(currbranch, com);
    }
    private static void customBranch(String com) {
        File tempBranch = join(GITLET_DIR, "-");
        writeContents(tempBranch, com);
    }
    public static void log(Commit com) {
        if (com == null) {
            File heaD = join(GITLET_DIR, "HEAD");
            File fileCom = join(GITLET_DIR, readContentsAsString(heaD));
            com = Commit.fromFile(fileCom);
        }
        System.out.println(com);
        String parent = com.getParent1();
        if (parent == null) {
            return;
        }
        Commit p = Commit.fromFile(join(GITLET_DIR, parent));
        log(p);
    }
    public static void merge(String b) {
        File givenbranch = join(GITLET_DIR, b);
        File staginGareA = join(GITLET_DIR, "STAGINGAREA");
        if (staginGareA.exists()) {
            TreeMap<String, String> stagingArea = readObject(staginGareA, TreeMap.class);
            if (!stagingArea.isEmpty()) {
                System.out.println("You have uncommitted changes.");
                System.exit(0);
            }
        }
        File removaLareA = join(GITLET_DIR, "REMOVALAREA");
        if (removaLareA.exists()) {
            TreeMap<String, String> removalArea = readObject(removaLareA, TreeMap.class);
            if (!removalArea.isEmpty()) {
                System.out.println("You have uncommitted changes.");
                System.exit(0);
            }
        }
        if (!givenbranch.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        String currname = readContentsAsString(join(GITLET_DIR, "ACTIVEBRANCH"));
        File currbranch = join(GITLET_DIR, readContentsAsString(join(GITLET_DIR, "ACTIVEBRANCH")));
        if (currname.equals(b)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Commit givencommit = Commit.fromFile(join(GITLET_DIR, readContentsAsString(givenbranch)));
        Commit currcommit = Commit.fromFile(join(GITLET_DIR, readContentsAsString(currbranch)));
        Commit splitpoint = Commit.findSplitPoint(currcommit, givencommit, currname, b);
        List<String> files = plainFilenamesIn(CWD);
        TreeMap<String, String> currfiles = currcommit.getMap();
        TreeMap<String, String> newfiles = givencommit.getMap();
        currfiles = currfiles == null ? new TreeMap<String, String>() : currfiles;
        newfiles = newfiles == null ? new TreeMap<String, String>() : newfiles;
        for (String f : files) {
            if (newfiles.containsKey(f) && !currfiles.containsKey(f)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        if (splitpoint.getHash().equals(givencommit.getHash())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (splitpoint.getHash().equals(currcommit.getHash())) {
            System.out.println("Current branch fast-forwarded.");
            checkoutBranch(b);
            System.exit(0);
        }
        merge2(b, givenbranch, currname,
                currbranch, givencommit, currcommit, splitpoint);
    }
    public static void addSpecial(String key, String g) {
        File staginGareA = join(GITLET_DIR, "STAGINGAREA");
        TreeMap<String, String> stagingArea;
        if (staginGareA.exists()) {
            stagingArea = readObject(staginGareA, TreeMap.class);
        } else {
            stagingArea = new TreeMap<String, String>();
        }
        stagingArea.put(key, g);
        writeObject(staginGareA, stagingArea);
    }
    public static void merge2(String b, File givenbranch,
                              String currname, File currbranch,
                              Commit givencommit, Commit currcommit, Commit splitpoint) {
        TreeMap<String, String> givenmap = givencommit.getMap() != null
                ? givencommit.getMap() : new TreeMap<String, String>();
        TreeMap<String, String> currmap = currcommit.getMap() != null
                ? currcommit.getMap() : new TreeMap<String, String>();
        TreeMap<String, String> splitmap = splitpoint.getMap() != null
                ? splitpoint.getMap() : new TreeMap<String, String>();
        //System.out.println("\n" + splitpoint.getHash());
        //System.out.println(givencommit.getHash());
        //System.out.println(currcommit.getHash());
        boolean conflict = false;
        for (String key : splitmap.keySet()) {
            String c = currmap.get(key) != null ? currmap.get(key) : "";
            String g = givenmap.get(key) != null ? givenmap.get(key) : "";
            String s = splitmap.get(key) != null ? splitmap.get(key) : "";
            boolean cexists = !c.equals("");
            boolean gexists = !g.equals("");
            boolean se = !s.equals("");
            boolean allthree = cexists && gexists && se;
            if (g.equals("") && c.equals("")) {
                //System.out.println("1");
                continue;
            } else if (!g.equals(s) && s.equals(c) && allthree) {
                //System.out.println("2");
                checkout(givencommit.getHash(), key);
                addSpecial(key, g);
            } else if (!c.equals(s) && s.equals(g) && allthree) {
                //System.out.println("3");
                continue;
            } else if (c.equals(g)) {
                //System.out.println("4");
                continue;
            } else if (c.equals(s) && g.equals("") && cexists && se) {
                //System.out.println("yes");
                rm(key);
            } else if (g.equals(s) && c.equals("") && gexists && se) {
                //System.out.println("6");
                continue;
            }
        }
        for (String key : currmap.keySet()) {
            String c = currmap.get(key) != null ? currmap.get(key) : "";
            String g = givenmap.get(key) != null ? givenmap.get(key) : "";
            String s = splitmap.get(key) != null ? splitmap.get(key) : "";
            boolean cexists = !c.equals("");
            boolean gexists = !g.equals("");
            boolean se = !s.equals("");
            if (g.equals("") && s.equals("")) {
                //System.out.println("7");
                continue;
            } else if (!g.equals(c) && !s.equals(c) && !s.equals(g)) {
                File blob1 = currmap.get(key) != null ? join(GITLET_DIR, currmap.get(key)) : null;
                File blob2 = givenmap.get(key) != null ? join(GITLET_DIR, givenmap.get(key)) : null;
                String contentscurr = blob1 != null ? readContentsAsString(blob1) : "";
                String contentsgiv = blob2 != null ? readContentsAsString(blob2) : "";
                String newcontents = "<<<<<<< HEAD\n"
                        + contentscurr + "=======\n" + contentsgiv + ">>>>>>>\n";
                File fd = join(CWD, key);
                writeContents(fd, newcontents);
                add(key);
                conflict = true;
            }
        }
        for (String key : givenmap.keySet()) {
            String c = currmap.get(key) != null ? currmap.get(key) : "";
            String g = givenmap.get(key) != null ? givenmap.get(key) : "";
            String s = splitmap.get(key) != null ? splitmap.get(key) : "";
            if (c.equals("") && s.equals("")) {
                checkout(givencommit.getHash(), key);
                addSpecial(key, g);
            }
        }
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
        commit("Merged " + b + " into " + currname + ".", givencommit);
    }
}

