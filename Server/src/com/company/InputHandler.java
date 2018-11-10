//
//  Author: Zhengyu Chen
//  Date: 29 Sep 2018
//

package com.company;

import java.util.regex.Pattern;

public class InputHandler {
    private String[] args;

    public InputHandler(String[] args) {
        this.args = args;
    }

    public int ParameterHandler() {
        int size = args.length;
        if (size == 1) {
            if (args[0].equals("-h")) {
                System.out.println("> java -jar ScrabbleGameServer.jar <port>");
                System.out.println("> <port> should be an integer from \"49152\" to \"65535\"");
                return 0;
            } else {
                int portHandler = PortHandler(args[0]);
                if (portHandler == 0)
                    return 0;
                return portHandler;
            }
        }
        System.out.println("> Wrong parameters, input -h to get help information.");
        return 0;
    }

    public int PortHandler(String portString) {
        Pattern pattern = Pattern.compile("[\\d]{5}");
        boolean match = pattern.matcher(portString).matches();
        if (!match) {
            System.out.println("> Port number is wrong.");
            System.out.println("> Please input an integer of 5 digits.");
            return 0;
        }
        int port = Integer.parseInt(portString);
        if (port < 49152 || port > 65535) {
            System.out.println("> Port number is wrong.");
            System.out.println("> Please input an integer from 49152 to 65535");
            return 0;
        }
        return port;
    }
}
