package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.omg.CORBA.portable.UnknownException;

import java.net.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class FTP_backend {

    private boolean is_connected = false;
    private boolean cont = true;
    private boolean at_the_end_of_the_disk = false;
    private String file_transfer_mode = "binary";
    private ObservableList<String> status_list = FXCollections.observableArrayList();
    private List<String> local_file_status = new LinkedList<>();
    private List<String> server_file_status = new LinkedList<>();
    private String local_path = System.getProperty("user.dir");
    private String server_path = "";
    private Socket ctrlSocket;
    public PrintWriter ctrlOutput;
    public BufferedReader ctrlInput;
    final int CTRLPORT = 21;

    public void openConnection(String host) throws IOException, UnknownException
    {
        ctrlSocket = new Socket(host, CTRLPORT);
        ctrlOutput = new PrintWriter(ctrlSocket.getOutputStream());
        ctrlInput = new BufferedReader(new InputStreamReader(ctrlSocket.getInputStream()));
    }

    public void closeConnection() throws IOException
    {
        cont = false;
        ctrlSocket.close();
    }

    public void showMenu()
    {
        System.out.println(">Command?");
        System.out.print(" 1 login");
        System.out.print(" 2 ls");
        System.out.print(" 3 cd");
        System.out.print(" 4 get");
        System.out.print(" 5 put");
        System.out.print(" 6 ascii");
        System.out.print(" 7 binary");
        System.out.print(" 8 delete file");
        System.out.print(" 9 make directory");
        System.out.print(" 10 remove directory");
        System.out.print(" 11 PWD");
        System.out.println(" 12 quit");
    }

    public void local_make_directory(String dirName)
    {
        File theDir = new File(local_path + "\\" + dirName);
        if (!theDir.exists())
        {
            boolean result = false;

            try
            {
                result = theDir.mkdir();
            }
            catch (SecurityException se)
            {
                se.printStackTrace();
            }

            if (result)
            {
                MessageWindow.display("DIR created successfully");
            }
            else
            {
                AlertWindow.display("Create failed");
            }
        }
    }

    public void local_delete_file_or_directory(String fileName)
    {
        File theFile = new File(local_path + "\\" + fileName);
        if (theFile.exists())
        {
            boolean result = false;

            try
            {
                result = theFile.delete();
            }
            catch (SecurityException se)
            {
                se.printStackTrace();
            }

            if (result)
            {
                MessageWindow.display("File or Dir deleted successfully");
            }
            else
            {
                AlertWindow.display("Delete failed");
            }
        }
    }

    public void doLogin(String loginName, String password)
    {
        ctrlOutput.println("USER " + loginName);
        ctrlOutput.flush();
        ctrlOutput.println("PASS " + password);
        ctrlOutput.flush();
    }

    public void doQuit()
    {
        ctrlOutput.println("QUIT ");
        ctrlOutput.flush();
    }

    public void doAscii()
    {
        file_transfer_mode = "ascii";
        ctrlOutput.println("TYPE A");
        ctrlOutput.flush();
    }

    public String getFile_transfer_mode()
    {
        return file_transfer_mode;
    }

    public void doBinary()
    {
        file_transfer_mode = "binary";
        ctrlOutput.println("TYPE I");
        ctrlOutput.flush();
    }

    public void doDeleteFile(String fileName)
    {
        ctrlOutput.println("DELE " + fileName);
    }

    public void makeDirectory(String dirName)
    {
        ctrlOutput.println("MKD " + dirName);
        ctrlOutput.flush();
    }

    public void removeDirectory(String dirName)
    {
        ctrlOutput.println("RMD " + dirName);
        ctrlOutput.flush();
    }

    public String getLocal_path()
    {
        return local_path;
    }

    public String getServer_path() {
        return server_path;
    }

    public boolean IsConnected()
    {
        return is_connected;
    }

    public void PWD()
    {
        ctrlOutput.println("PWD");
        ctrlOutput.flush();
    }

    public void doCd(String dirName)
    {

        ctrlOutput.println("CWD " + dirName);
        ctrlOutput.flush();
    }

    public ObservableList<String> doLs()
    {
        ObservableList<String> files = FXCollections.observableArrayList();

            try
            {
                int n;
                byte[] buff = new byte[4096];
                server_file_status.clear();
                files.add("..");
                server_file_status.add("d");
                Socket dataSocket = dataConnection("LIST");
                BufferedInputStream dataInput = new BufferedInputStream(dataSocket.getInputStream());
                while ((n = dataInput.read(buff)) > 0)
                {
                    String lines[] = new String(buff, 0, n).split("\\r?\\n");
                    for (String line: lines) {
                        if (line.charAt(0) == 'd') {
                            server_file_status.add("d");
                        } else {
                            server_file_status.add("f");
                        }
                        files.add(line.substring(49));
                    }
                }

            int len = server_file_status.size();
            for (int i = 1; i < len; i++)
            {
                if (server_file_status.get(i).equals("d"))
                {
                    String s = files.get(i);
                    server_file_status.remove(i);
                    files.remove(i);
                    server_file_status.add(1, "d");
                    files.add(1, s);
                }
            }

            dataSocket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        return files;
    }

    public Socket dataConnection(String ctrlcmd)
    {
        StringBuilder cmd = new StringBuilder("PORT ");
        int i;
        Socket dataSocket = null;
        try
        {
            byte[] address = InetAddress.getLocalHost().getAddress();
            ServerSocket serverDataSocket = new ServerSocket(0, 1);
            for (i = 0; i < 4; i++)
                cmd.append(address[i] & 0xff).append(",");
            cmd.append(((serverDataSocket.getLocalPort()) / 256) & 0xff).append(",").append(serverDataSocket.getLocalPort() & 0xff);
            ctrlOutput.println(cmd);
            ctrlOutput.flush();
            ctrlOutput.println(ctrlcmd);
            ctrlOutput.flush();
            dataSocket = serverDataSocket.accept();
            serverDataSocket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        return dataSocket;
    }

    public void doGet(String fileName)
    {
        try
        {
            int n;
            byte[] buff = new byte[1024];
            FileOutputStream outfile = new FileOutputStream(local_path + "\\"  + fileName);
            Socket dataSocket = dataConnection("RETR " + fileName);
            BufferedInputStream dataInput = new BufferedInputStream(dataSocket.getInputStream());
            while((n = dataInput.read(buff)) > 0)
            {
                outfile.write(buff, 0, n);
            }
            dataSocket.close();
            outfile.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void doPut(String fileName)
    {
        try
        {
            int n;
            byte[] buff = new byte[1024];
            FileInputStream sendfile = new FileInputStream(local_path + "\\" + fileName);
            Socket dataSocket = dataConnection("STOR " + fileName);
            OutputStream outstr = dataSocket.getOutputStream();
            while((n = sendfile.read(buff)) > 0)
            {
                outstr.write(buff, 0, n);
            }
            dataSocket.close();
            sendfile.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void local_check_file_type(String fileName)
    {
        File file = new File(local_path + "\\" + fileName);
        if (file.isDirectory())
        {
            doLocalCd(fileName);
        }
        else
        {
            doPut(fileName);
        }
    }

    public void server_check_file_type(int index, String fileName)
    {
        if (server_file_status.get(index).equals("d"))
        {
            doCd(fileName);
        }
        else
        {
            doGet(fileName);
        }
    }

    public ObservableList<String> doLocalLs()
    {
        local_file_status.clear();
        final File folder = new File(local_path);
        ObservableList<String> localfiles = FXCollections.observableArrayList();

        if (!at_the_end_of_the_disk)
        {
            localfiles.add("..");
            local_file_status.add("d");
        }

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                localfiles.add(fileEntry.getName());
                local_file_status.add("d");
            } else {
                localfiles.add(fileEntry.getName());
                local_file_status.add("f");
            }
        }

        int len = local_file_status.size();
        for (int i = 1; i < len; i++)
        {
            if (local_file_status.get(i).equals("d"))
            {
                String s = localfiles.get(i);
                local_file_status.remove(i);
                localfiles.remove(i);
                local_file_status.add(1, "d");
                localfiles.add(1, s);
            }
        }

        return localfiles;
    }

    public List<String> getLocal_file_status()
    {
        return local_file_status;
    }

    public List<String> getServer_file_status()
    {
        return server_file_status;
    }

    public void doLocalCd(String dirName)
    {
        if (dirName.equals(".."))
        {
            int i = local_path.length() - 1;
            while(local_path.charAt(i) != '\\')
            {
                local_path = local_path.substring(0, i);
                i--;
            }

            if (local_path.charAt(i - 1) == ':')
            {
                at_the_end_of_the_disk = true;
                local_path = local_path.substring(0, i + 1);
            }
            else
            {
                local_path = local_path.substring(0, i);
            }
        }
        else
        {
            local_path += "\\" + dirName;
            at_the_end_of_the_disk = false;
        }
    }

    public String get_local_file_status(int index) { return local_file_status.get(index); }

    public String get_server_file_status(int index)
    {
        return server_file_status.get(index);
    }

    public void getMsgs()
    {
        try {
            CtrlListen listener = new CtrlListen(ctrlInput);
            Thread listenerthread = new Thread(listener);
            listenerthread.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public ObservableList<String> getStatus_list()
    {
        return status_list;
    }

    class CtrlListen implements Runnable
    {
        BufferedReader ctrlInput = null;
        public CtrlListen(BufferedReader in) {
            ctrlInput = in;
        }

        public void run() {
            while (cont) {
                try {
                    String s = ctrlInput.readLine();
                    check_message(s);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            status_list.add("status: " + s);
                        }
                    });
                } catch (Exception e) {
                    //e.printStackTrace();
                    //System.exit(1);
                }
            }
        }
    }

    private void check_message(String msg)
    {
        switch (msg.substring(0, 3))
        {
            case "230":
                is_connected = true;
                break;

            case "421":
                is_connected = false;
                break;

            case "257":
                int end = 0;
                for (int i = 5; i < msg.length(); i++)
                {
                    if (msg.charAt(i) == '\"')
                    {
                        end = i;
                        break;
                    }
                }
                server_path = msg.substring(5, end);
                break;

            case "226":
                int start = 0;
                for (int i = msg.length() - 2; i >= 0; i--)
                {
                    if (msg.charAt(i) == '\"')
                    {
                        start = i;
                        break;
                    }
                }
                server_path = (msg.substring(start + 1, msg.length() - 1));
                break;
        }
    }
}