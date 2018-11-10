package com;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ErrorHandling {
    private static ArrayList<String> userList;

    private static ErrorHandling instance = new ErrorHandling();

    private ErrorHandling() {
        userList = new ArrayList<String>();
    }

    public static ErrorHandling getInstance() {
        return instance;
    }

    public boolean HandleName(String name) {
        int nameLength = name.length();
        if (nameLength == 0 || nameLength > 10) {
            System.out.println("> The length of name should be between 1 to 10 digits.");
            return false;
        }
        for (int i = 0; i < name.length(); ++i) {
            char currentCharacter = name.charAt(i);
            if (!((currentCharacter >= '0' && currentCharacter <= '9') ||
                    (currentCharacter >= 'a' && currentCharacter <= 'z'))) {
                System.out.println("> The name can only contain of 0-9 and a-z.");
                return false;
            }
        }
        return true;
    }

    public boolean HandlePort(String port) {
        Pattern pattern = Pattern.compile("[\\d]{5}");
        boolean match = pattern.matcher(port).matches();
        if (!match) {
            System.out.println("> Port number is wrong.");
            System.out.println("> Please input an integer of 5 digits.");
            return false;
        } else {
            int portNumber = Integer.parseInt(port);
            if (portNumber >= 49152 && portNumber <= 65535) {
                return true;
            } else {
                System.out.println("> Port number is wrong.");
                System.out.println("> Please input an integer from 49152 to 65535");
                return false;
            }
        }
    }

    public boolean HandleIP(String ip) {
        if (ip.equals("localhost"))
            return true;
        Pattern pattern = Pattern.compile("(\\d{1,3}.){3}\\d{1,3}");
        boolean match = pattern.matcher(ip).matches();
        if (!match) {
            System.out.println("> IP is wrong.");
            System.out.println("> Please input an IP between 0.0.0.0 and 255.255.255.255.");
            return false;
        } else {
            boolean ipParser = this.IPParser(ip);
            if (!ipParser) {
                System.out.println("> IP is wrong.");
                System.out.println("> Please ensure each integer is between 0 and 255.");
                return false;
            } else {
                return true;
            }
        }

    }

    private boolean IPParser(String ip) {
        String[] ipSplit = ip.split("\\.");

        for (int i = 0; i < ipSplit.length; ++i) {
            int ipNumber = Integer.parseInt(ipSplit[i]);
            if (ipNumber < 0 || ipNumber > 255) {
                return false;
            }
        }

        return true;
    }
}
