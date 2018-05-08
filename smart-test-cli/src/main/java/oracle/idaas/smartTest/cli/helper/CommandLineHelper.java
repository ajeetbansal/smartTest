package oracle.idaas.smartTest.cli.helper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.*;

public class CommandLineHelper {

    private static Set<String> coveredExtensions = Stream.of(".java", ".groovy").collect(Collectors.toSet());

    private static CommandLineHelper INSTANCE = new CommandLineHelper();

    public static CommandLineHelper getInstance() {
        return INSTANCE;
    }

    public Map<String,String> parseCommandLine(String args[]) {
        Options options = new Options();

        Option command = new Option("c", "command", true, "level of granularity");
        command.setRequired(true);
        options.addOption(command);

        Option granularity = new Option("g", "granularity", true, "level of granularity");
        granularity.setRequired(true);
        options.addOption(granularity);

        Option dep = new Option("d", "dependentModule", true, "dependent module name");
        dep.setRequired(false);
        options.addOption(dep);

        Option input = new Option("i", "input", true, "input file/directory");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output file");
        output.setRequired(false);
        options.addOption(output);

        Option version = new Option("v", "version", true, "version");
        version.setRequired(true);
        options.addOption(version);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return null;
        }

        Map<String, String> argsMap = new HashMap<String, String>();
        argsMap.put("command", cmd.getOptionValue("command"));
        argsMap.put("granularity", cmd.getOptionValue("granularity"));
        argsMap.put("dependentModule", cmd.getOptionValue("dependentModule"));
        argsMap.put("input", cmd.getOptionValue("input"));
        argsMap.put("output", cmd.getOptionValue("output"));
        argsMap.put("version", cmd.getOptionValue("version"));
        return argsMap;
    }
}
