package com.antigenomics.ilrfilter.cli;

import picocli.CommandLine;

@CommandLine.Command(name = "examples",
        description = "Runs one of the examples, see the list below",
        mixinStandardHelpOptions = true)
public class MainCli implements Runnable {
    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new MainCli())
                .addSubcommand("hash", new HashFilterCli())
                .addSubcommand("tree", new TreeFilterCli());
        cmd.execute(args);
    }

    @Override
    public void run() {
    }
}
