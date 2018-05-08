package oracle.idaas.smartTest.common.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystemHelper {
    static Logger logger = Logger.getLogger(FileSystemHelper.class.getName());
    private  static FileSystemHelper INSTANCE = new FileSystemHelper();
    public static FileSystemHelper getInstance() {
        return INSTANCE;
    }

    public File getFile(String fileName) {

        StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        return file;
    }

    public void writeToFile(String fileName, String data) {
        FileWriter fw = null;
        try {
            try {
                fw = new FileWriter(fileName);
                fw.write(data);
            } finally {
                fw.flush();
                fw.close();
            }
        }catch (IOException e) {
            logger.log(Level.SEVERE, "Error while writing output: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

}
