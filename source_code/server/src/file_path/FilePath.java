package file_path;

import javax.swing.*;

public class FilePath {

    public static String filePath = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\NHSTTS\\";

}
