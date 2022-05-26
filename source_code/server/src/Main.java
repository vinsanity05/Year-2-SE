import file_path.FilePath;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        System.out.println( "Start server on port: 1000");
        System.out.println(FilePath.filePath);

        try {
            Files.createDirectories(Paths.get(FilePath.filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        SocketServer server = new SocketServer(1000);
        server.start();

        // Automatically shutdown in 1 minute
        try
        {
            Thread.sleep( 600000 );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        //server.stopServer();
    }
}
