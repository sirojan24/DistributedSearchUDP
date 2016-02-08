package com.uom.cse.distsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.uom.cse.distsearch.model.NodeInfo;
import com.uom.cse.distsearch.model.Request;
import com.uom.cse.distsearch.util.Constant;
import com.uom.cse.distsearch.util.Constant.Command;

public class BootstrapServer extends Server {
    /**
     * Logger to log the events.
     */
    private static final Logger LOGGER = Logger.getLogger(BootstrapServer.class);

    /**
     * List of neighbours.
     */
    private List<NodeInfo> registeredNodes = new ArrayList<NodeInfo>();

    /**
     * Random number generator.
     */
    private static final Random RANDOM_NUMBER_GENERATOR = new Random();

    /**
     * Start the BootstrapServer.
     *
     * @throws IOException
     */
    public void run() throws IOException {
        start(Constant.BOOTSTRAP_SERVER_PORT);
    }

    /**
     * Register a node.
     *
     * @param host
     * @param incomingPort
     * @param tokenizer
     */
    private void register(String host, int incomingPort, StringTokenizer tokenizer) {
        // Register the node
        String reply = "0114 REGOK ";

        String ip = tokenizer.nextToken();
        int port = Integer.parseInt(tokenizer.nextToken());
        String username = tokenizer.nextToken();
        synchronized (this) {
            if (registeredNodes.size() == 0) {
                reply += "0";
                registeredNodes.add(new NodeInfo(ip, port, username));
            } else {
                boolean isOkay = true;
                for (NodeInfo info : registeredNodes) {
                    if (info.getPort() == port) {
                        if (info.getUsername().equals(username)) {
                            // Already registered
                            reply += "9998";
                        } else {
                            // Some other nodes using this ip and port
                            reply += "9997";
                        }
                        isOkay = false;
                    }
                }
                if (isOkay) {
                    if (registeredNodes.size() == 1) {
                        reply += "1 " + registeredNodes.get(0).getIp() + " " + registeredNodes.get(0).getPort();
                    } else if (registeredNodes.size() == 2) {
                        reply += "2 " + registeredNodes.get(0).getIp() + " " + registeredNodes.get(0).getPort() + " "
                                + registeredNodes.get(1).getIp() + " " + registeredNodes.get(1).getPort();
                    } else {
                        // Upper bound of random number
                        int bound = registeredNodes.size();

                        int randomA = RANDOM_NUMBER_GENERATOR.nextInt(bound);
                        int randomB = RANDOM_NUMBER_GENERATOR.nextInt(bound);
                        //while (randomA == randomB) {
                        //	randomB = RANDOM_NUMBER_GENERATOR.nextInt(bound);
                        //}
                        reply += "2 " + registeredNodes.get(randomA).getIp() + " " + registeredNodes.get(randomA).getPort()
                                + " " + registeredNodes.get(randomB).getIp() + " " + registeredNodes.get(randomB).getPort();
                    }

                    // Add the current node to the list
                    registeredNodes.add(new NodeInfo(ip, port, username));
                }
            }
            send(reply, host, incomingPort);
        }
    }

    private void unregister(String host, int incomingPort, StringTokenizer tokenizer) {
        // Unregister the node
        String ip = tokenizer.nextToken();
        int nodePort = Integer.parseInt(tokenizer.nextToken());
        String username = tokenizer.nextToken();
        synchronized (this) {
            for (int i = 0; i < registeredNodes.size(); i++) {
                if (registeredNodes.get(i).getPort() == nodePort) {
                    registeredNodes.remove(i);
                    String reply = "0012 UNROK 0";

                    send(reply, host, incomingPort);
                }
            }
        }
    }

    @Override
    public void onRequest(Request request) {
        String host = request.getHost();
        int incomingPort = request.getPort();

        LOGGER.debug("Message from: " + host + " : " + incomingPort);
        // Tokenize the message
        StringTokenizer tokenizer = new StringTokenizer(request.getMessage(), " ");
        String length = tokenizer.nextToken();
        String command = tokenizer.nextToken();

        if (Command.REG.equals(command)) {
            register(host, incomingPort, tokenizer);
        } else if (Command.UNREG.equals(command)) {
            unregister(host, incomingPort, tokenizer);
        } else {
            LOGGER.warn("Unknown command: " + command);
        }
    }

    public static void main(String args[]) {
        try {
            BootstrapServer bootstrapServer = new BootstrapServer();
            bootstrapServer.run();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}