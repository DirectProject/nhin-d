package com.gsihealth.auditclient;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SyslogClient {

    private Socket client = null;
    private int loopStart = 1;
    private int loopEnd = 1;
    private int delay = 10;
    private OutputStreamWriter out = null;
    private InputStreamReader in = null;
    private String sendhost = "localhost";
    private String sendport = "514";

    public SyslogClient(String host, String port) {
        sendhost = host;
        sendport = port;
    }

    public void sendMessage(String message) throws Exception {

        try {
            connect();
            System.out.print(message);
            send(message);

            close();

        } catch (Exception e) {
            e.printStackTrace();
        } // catch

    }

    private void connect() {


        try {

          
            client = new Socket(sendhost, new Integer(sendport).intValue());
            out = new OutputStreamWriter(client.getOutputStream(), "Cp1252");

            in = new InputStreamReader(client.getInputStream());

        } catch (Exception x) {
            x.printStackTrace();
        }

    }

    private void close() {


        try {
            out.flush();
            out.close();
            in.close();

            client.close();


        } catch (Exception x) {
            x.printStackTrace();
        }

    }

    private void send(String s) {


        try {

            out.write(s, 0, s.length());
            System.out.println("THE LENGTH IS " + s.length());
            out.flush();


        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}

