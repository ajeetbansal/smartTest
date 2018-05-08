package oracle.idaas.smartTest.dao;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import oracle.idaas.smartTest.object.dependency.Package;
import oracle.idaas.smartTest.object.dependency.PackageDependency;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PackageDependencyDAO {

    private static Logger logger = Logger.getLogger(PackageDependencyDAO.class.getName());

    private PackageDependency packageDependency;
    private File packageDependencyFile;

    public static PackageDependencyDAO getInstance(File packageDependencyFile) {
        PackageDependencyDAO packageDependencyDAO = new PackageDependencyDAO();
        packageDependencyDAO.packageDependencyFile = packageDependencyFile;
        ObjectMapper mapper = new ObjectMapper();
        try {
            packageDependencyDAO.packageDependency = mapper.readValue(new FileInputStream(packageDependencyFile), PackageDependency.class);
        } catch (JsonMappingException jme) {
            packageDependencyDAO.packageDependency = new PackageDependency();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return packageDependencyDAO;
    }

    public void commit() {
        JsonFactory jsonFactory = new JsonFactory();
        try {
            FileOutputStream file = new FileOutputStream(packageDependencyFile);
            JsonGenerator jsonGen = jsonFactory.createJsonGenerator(file, JsonEncoding.UTF8);
            jsonGen.setCodec(new ObjectMapper());
            jsonGen.writeObject(packageDependency);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PackageDependency getAllPackageDependencies() {
        return packageDependency;
    }

    public List<String> getPackageDependencies(String packageName) {
        Package pkg = getPackage(packageName);
        if (pkg == null) {
            return null;
        }
        return pkg.getDepModules();
    }

    public void addPackageDependency(String packageName, String depModuleName) {
        Package pkg = getOrAddModule(packageName);
        synchronized (this) {
            if (pkg.getDepModules() == null) {
                pkg.setDepModules(new ArrayList<String>());
            }
        }
        if (!pkg.getDepModules().contains(depModuleName)) {
            pkg.getDepModules().add(depModuleName);
        }
    }

    public Package getOrAddModule(String packageName) {
        Package pkg = getPackage(packageName);
        if (pkg == null) {
            pkg = addPackage(packageName);
        }
        return pkg;
    }

    public Package getPackage(String packageName) {
        if (packageDependency.getPackages() == null || packageDependency.getPackages().isEmpty()) {
            return null;
        }
        Package matchedPackage = packageDependency.getPackages().stream()
                .filter(pkg -> pkg.getName().equals(packageName))
                .findFirst().orElse(null);
        return matchedPackage;
    }

    private Package addPackage(String packageName) {
        synchronized (this) {
            if (packageDependency.getPackages() == null) {
                packageDependency.setPackages(new ArrayList<Package>());
            }
        }

        Package newPackage = new Package();
        newPackage.setName(packageName);
        packageDependency.getPackages().add(newPackage);

        return newPackage;
    }

}
