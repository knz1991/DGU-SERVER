package com.dgu.server;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.Arrays;

/*
* Non paramterized Bash Script execution: Done.
* Parameterized Bash script execution: Work in Progress.
* Non parameterized PL/SQL Script Execution: Haven't started.
* Parameterized PL/SQL Script Execution: Haven't started.
* Execution Historization: Haven't started.
 */




public class TestSSH3 {
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        String username = "moetezwelhazi";
        String password = "moetez";
        String host = "192.168.56.101";
        String script = "src/main/resources/Scripts/testLOCAL.sh";
        int port = 22;
        String auth = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtLm0xQG5lb3hhbS5jb20iLCJpYXQiOjE2NTA4ODY1MzMsImV4cCI6MTY1MDk3MjkzM30.oup9Cs23-nYsdDEtakWOfAeIffmP3gBxcvDFR-pbzYAJ-V6WZwwwXHdRGaBjdm86m1rZ_g_KkdX0vw8K8aGf0Q";
        System.out.println(auth.substring(7));
        /*System.out.println("File upload: "+uploadSftpFromPath(username,password,host,port,script));
        try{
            System.out.println("Script Execution:\n"+ executeCommand(username, password,host,port,"bash remoteScript.sh"));
            System.out.println("Script Deleted!"+executeCommand(username, password, host, port, "rm remoteScript.sh"));
        }
        catch(Exception e){
            e.printStackTrace();
        }*/

    }

    public static String executeCommand(String username, String password,
                                       String host, int port, String command) throws Exception {
        ChannelExec channel = null;
        String responseString = "";
        try {
            channel = (ChannelExec) setupJsch(username,password,host,port, "exec");
            //System.out.println("test 1: Connection to session Established!");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            InputStream inputStream = channel.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            channel.setOutputStream(responseStream);
            channel.setErrStream(responseStream);
            channel.connect();
            System.out.println("Connected to Channel!");

            //System.out.println("test 2: Connection to channel Established!");
            //int cpt = 3;
            String response;
            PrintStream out = new PrintStream(responseStream);
            byte[] buf=responseStream.toByteArray();
            int off=0;
            int len = 0;
            while (channel.isConnected()) {
                Thread.sleep(100);
                //responseStream.write(responseStream.toByteArray(), off, len - off );
                //responseStream.writeBytes(responseStream.toByteArray());
                //responseStream.writeTo(System.out);
                //System.out.println("test "+(cpt++)+": inside while loop");
            }

            responseString = responseStream.toString();
            System.out.println("Exit Status: "+channel.getExitStatus());
        }
        finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
        return responseString;
        //return "done!";
    }

    private static Channel setupJsch(String username, String password, String host, int port, String type) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(password);
        session.connect();
        return session.openChannel(type);
    }

    public static boolean uploadSftpFromPath(String username, String password, String host, int port, String script) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = (ChannelSftp) setupJsch(username, password, host, port, "sftp");
            channelSftp.connect();

            try{
                channelSftp.put(script, "remoteScript.sh");
                System.out.println("Upload Complete");
            } catch (SftpException e) {
                // throw the exception
                System.out.println("Upload Failed!");
                e.printStackTrace();
            }
        } catch (JSchException e) {
            // throw the exception
            System.out.println("Session/Channel connection Failed!");
            e.printStackTrace();
        } finally {
            if (channelSftp != null)
            channelSftp.disconnect();
        }
        return true;
    }


}
