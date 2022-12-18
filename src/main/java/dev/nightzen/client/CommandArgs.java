package dev.nightzen.client;

import com.beust.jcommander.Parameter;

public class CommandArgs {
    @Parameter(names = {"-t"}, description = "Type")
    public String type;

    @Parameter(names = {"-k"}, description = "Key")
    public String key;

    @Parameter(names = {"-v"}, description = "Value")
    public String value;

    @Parameter(names = "-in", description = "Command file name")
    public String in;
}
