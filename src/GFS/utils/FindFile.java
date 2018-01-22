package GFS.utils;

import java.io.File;

/**
 * This class is used to find file in the given location
 */
public class FindFile {
    private File path = new File("./");
    private boolean flag;
    private File P;

    public FindFile() {
        this.flag = false;
        this.P = null;
    }


    public void fileLookup(String name, File file) {
        File[] list = file.listFiles();
        if (list != null){
            for (File fil : list) {
                if (!fil.isDirectory() && name.equalsIgnoreCase(fil.getName())) {
//                    fileLookup(name, fil);
                    P = fil;
                    path = fil.getAbsoluteFile();
//                    System.out.println("Found..");
//                    System.out.println(P.getName());
                    System.out.println(P.getAbsoluteFile());
                    this.flag = true;

                }
            }
        }
    }

//    public void fileLookup(String name, File file) {
//        File[] list = file.listFiles();
//        if (list != null)
//            for (File fil : list) {
//                if (fil.isDirectory()) {
//                    fileLookup(name, fil);
//                } else if (name.equalsIgnoreCase(fil.getName())) {
//                    P = fil;
//                    path = fil.getAbsoluteFile();
////                    System.out.println("Found..");
//                    System.out.println(P.getName());
//                    System.out.println(P.getAbsoluteFile());
//                    if (P.canRead()) {
////                        System.out.println("Can read");
//                    }
//                    this.flag = true;
//                }
//            }
//    }


    public void setPath(File path) {
        this.path = path;
    }

    public File getPath() {
        return this.path;
    }

    public boolean isPresent() {
        return flag;
    }
}