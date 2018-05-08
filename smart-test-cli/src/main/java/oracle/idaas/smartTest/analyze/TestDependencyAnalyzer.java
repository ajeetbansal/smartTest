/*
 * Copyright (c) 2014-2016 Oracle and/or its affiliates. All rights reserved.
 */
package oracle.idaas.smartTest.analyze;

import oracle.idaas.smartTest.common.helper.FileSystemHelper;
import oracle.idaas.smartTest.dao.CodeCoverageDAO;
import oracle.idaas.smartTest.dao.ModuleDependencyDAO;
import oracle.idaas.smartTest.cli.helper.CommandLineHelper;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *  @author ajbansal
 */
public class TestDependencyAnalyzer {
    static Logger logger = Logger.getLogger(TestDependencyAnalyzer.class.getName());
    private static CommandLineHelper commandLineHelper = CommandLineHelper.getInstance();

    public static void main(String[] args) {
        Map<String ,String> argsMap = null;
        String version = null;
        String command = null;
        try {
            argsMap = commandLineHelper.parseCommandLine(args);
            command = argsMap.get("command");
            version = argsMap.get("version");
            switch (command) {
                case "generate":
                    generateDependencyData(argsMap.get("granularity"), argsMap.get("dependentModule"), argsMap.get("input"), argsMap.get("version"));
                    break;
                case "getDepModules":
                    DependentModulesSelector
                            .getInstance(argsMap.get("granularity"), argsMap.get("input"), argsMap.get("output"), version)
                            .getDependentModules();
                    break;
                case "rollback":
                    ModuleDependencyDAO.getInstance(version).rollback();
                    break;
            }

        } catch (Exception e) {
            System.out.println("Error occured while analyzing test dependencies: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

    }

    public static void generateDependencyData(String granularity, String dependentModuleName, String codeCoverageRootDir, String version) {
        List<Path> codeCoverageFiles = getCodeCoverageFiles(codeCoverageRootDir);
        if(granularity.equals("module")) {
            ModuleDependencyDAO moduleDependencyDAO = ModuleDependencyDAO.getInstance(version);
            codeCoverageFiles.stream().forEach(codeCoverageFile -> {
                logger.log(Level.INFO, "Analyzing " + codeCoverageFile);
                CodeCoverageDAO codeCoverageDAO = CodeCoverageDAO.getInstance(codeCoverageFile.toFile());
                String moduleName = codeCoverageDAO.getReport().getName();
                if (codeCoverageDAO.getClassCoverage() > 0) {
                    moduleDependencyDAO.addModuleDependency(moduleName, dependentModuleName);
                }
            });
            moduleDependencyDAO.commit();
//        } else if(granularity.equals("package")){
//            file = DataSourceConfigDAO.getPackageDependenciesFile(version);
//            PackageDependencyDAO packageDependencyDAO = getPackageDependencyDAO(file);
//            codeCoverageFiles.stream().forEach(codeCoverageFile -> {
//                logger.log(Level.INFO, "Analyzing " + codeCoverageFile);
//                CodeCoverageDAO codeCoverageDAO = CodeCoverageDAO.getInstance(codeCoverageFile.toFile());
//                Set<String> coveredPackages = codeCoverageDAO.getCoveredPackages();
//                coveredPackages.forEach(coveredPackage ->
//                        packageDependencyDAO.addPackageDependency(coveredPackage, dependentModuleName));
//
//            });
//            packageDependencyDAO.commit();

        }
        System.out.println("Successfully analyzed test dependencies for version: " + version);
        return;
    }

//    private static PackageDependencyDAO getPackageDependencyDAO(String packageDependencyDataFile) {
//        File file = new File(packageDependencyDataFile);
//        if(!file.getParentFile().exists()) {
//            throw new RuntimeException(String.format("Directory %s does not exist", file.getParent()));
//        }
//        if(Files.notExists(file.toPath())) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                logger.log(Level.SEVERE, e.getMessage(), e);
//                throw new RuntimeException(e);
//            }
//        }
//        return PackageDependencyDAO.getInstance(file);
//    }

    private static List<Path> getCodeCoverageFiles(String rootDirectory) {
        try {
            return Files.find(Paths.get(rootDirectory),
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> filePath.getFileName().endsWith("jacocoReport.xml"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
