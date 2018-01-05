package GFS.Nodes;

import GFS.utils.ConfigurationManager;

public class Client {

    public static void main(String[] args) {
        ConfigurationManager configManager = new ConfigurationManager(0);
        configManager.isValid();
    }
}
