package oracle.idaas.smartTest.analyze;

import oracle.idaas.smartTest.common.helper.FileSystemHelper;
import oracle.idaas.smartTest.dao.ModuleDependencyDAO;

import java.io.*;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DependentModulesSelector {

    private static Set<String> coveredExtensions = Stream.of(".java").collect(Collectors.toSet());
    private static Set<String> excludedModules = Stream.of("smart-test").collect(Collectors.toSet());

    Pattern unitTestPattern = Pattern.compile("[^/]+/src/test/.+");
    Pattern integTestPattern = Pattern.compile("[^/]+/src/integTest/.+");

    private String granularity;
    private String inputFileName;
    private String outputFileName;
    private String version;

    public static DependentModulesSelector getInstance(String granularity, String inputFileName, String outputFileName, String version) {
        DependentModulesSelector dependentModulesSelector = new DependentModulesSelector();
        dependentModulesSelector.granularity = granularity;
        dependentModulesSelector.inputFileName = inputFileName;
        dependentModulesSelector.outputFileName = outputFileName;
        dependentModulesSelector.version = version;
        return dependentModulesSelector;
    }

    public void getDependentModules() {
        ModuleDependencyDAO moduleDependencyDAO = ModuleDependencyDAO.getInstance(version);
        String output = null;
        switch (granularity) {
            case "module":
                if(isChangeSetOutOfCoverage(inputFileName)) {
                    output = "integTest";
                    break;
                }
                File file = new File(inputFileName);
                Set<String> moduleNamesWithTestChanges = new TreeSet<String>();
                Set<String> moduleNamesWithSourceChanges = new TreeSet<String>();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String modifiedFilePath = br.readLine();
                    while(modifiedFilePath != null) {
                        String moduleName = extractModuleNameFromFilePath(modifiedFilePath);
                        if(isUnitTestArtifact(modifiedFilePath) || isExcludedModule(moduleName)) {
                            // Does not impact integ test
                        } else if (isIntegTestArtifact(modifiedFilePath)) {
                            moduleNamesWithTestChanges.add(moduleName);
                        } else {
                            moduleNamesWithSourceChanges.add(moduleName);
                        }
                        modifiedFilePath = br.readLine();
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("File does not exist: "+ inputFileName);
                    System.exit(1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Set<String> depModuleNames = new TreeSet<String>();
                depModuleNames.addAll(moduleNamesWithTestChanges);
                depModuleNames.addAll(moduleNamesWithSourceChanges);
                moduleNamesWithSourceChanges.stream().forEach(changedModuleName ->
                        depModuleNames.addAll(moduleDependencyDAO.getModuleDependencies(changedModuleName)));
                output = depModuleNames.stream().map(depModuleName -> String.format(":%s:integTest ", depModuleName)).collect(Collectors.joining());
                break;
        }
        FileSystemHelper.getInstance().writeToFile(outputFileName, output);
    }

    public boolean isChangeSetOutOfCoverage(String fileName) {
        boolean outOfCoverage = false;
        File file = new File(fileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String modifiedFilePath = br.readLine();
            while(modifiedFilePath != null) {
                final String constModifiedFilePath = modifiedFilePath;
                if(!isTestArtifact(modifiedFilePath) &&
                        coveredExtensions.stream().noneMatch(coveredExtension -> constModifiedFilePath.endsWith(coveredExtension))) {
                    outOfCoverage = true;
                    break;
                }
                modifiedFilePath = br.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist: "+ fileName);
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outOfCoverage;
    }

    public String extractModuleNameFromFilePath(String line) {
        return line.split("/")[0];
    }

    public boolean isTestArtifact(String filePath) {
        return isIntegTestArtifact(filePath) || isUnitTestArtifact(filePath);
    }

    public boolean isUnitTestArtifact(String filePath) {
        return unitTestPattern.matcher(filePath).matches();
    }

    public boolean isExcludedModule(String moduleName) {
        return excludedModules.contains(moduleName);
    }

    public boolean isIntegTestArtifact(String filePath) {
        return integTestPattern.matcher(filePath).matches();
    }

}
