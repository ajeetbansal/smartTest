package oracle.idaas.smartTest.dao;

import oracle.idaas.smartTest.object.dependency.Module;
import oracle.idaas.smartTest.object.dependency.ModuleDependency;
import oracle.idaas.smartTest.persist.ModuleDependencyAccessor;
import oracle.idaas.smartTest.persist.ModuleDependencyAccessorFactory;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ModuleDependencyDAO {

    private static Logger logger = Logger.getLogger(ModuleDependencyDAO.class.getName());

    private ModuleDependency moduleDependency;
    private ModuleDependencyAccessor moduleDependencyAccessor;
    private static Map<String, ModuleDependencyDAO> instances = new HashMap<String, ModuleDependencyDAO>();

    public static ModuleDependencyDAO getInstance(String version) {
        ModuleDependencyDAO moduleDependencyDAO = instances.get(version);
        if(moduleDependencyDAO == null) {
            synchronized (ModuleDependencyDAO.class) {
                moduleDependencyDAO = new ModuleDependencyDAO();
                moduleDependencyDAO.moduleDependencyAccessor = ModuleDependencyAccessorFactory.getAccessor(version);
                moduleDependencyDAO.moduleDependency = moduleDependencyDAO.moduleDependencyAccessor.readModuleDependency();
            }
        }
        return moduleDependencyDAO;
    }

    public void commit() {
        moduleDependencyAccessor.writeModuleDependency(moduleDependency);
    }

    public void rollback() {
        moduleDependencyAccessor.rollbackModuleDependency();
    }

    public ModuleDependency getAllModuleDependencies() {
        return moduleDependency;
    }

    public List<String> getModuleDependencies(String moduleName) {
        Module module = getModule(moduleName);
        if (module == null) {
            return Collections.EMPTY_LIST;
        }
        return module.getDepModules();
    }

    public void addModuleDependency(String moduleName, String depModuleName) {
        Module module = getOrAddModule(moduleName);
        synchronized (this) {
            if (module.getDepModules() == null) {
                module.setDepModules(new ArrayList<String>());
            }
        }
        if (!module.getDepModules().contains(depModuleName)) {
            module.getDepModules().add(depModuleName);
        }
    }

    public Module getOrAddModule(String moduleName) {
        Module module = getModule(moduleName);
        if (module == null) {
            module = addModule(moduleName);
        }
        return module;
    }

    public Module getModule(String moduleName) {
        if (moduleDependency.getModules() == null || moduleDependency.getModules().isEmpty()) {
            return null;
        }
        Module matchedModule = moduleDependency.getModules().stream()
                .filter(module -> module.getName().equals(moduleName))
                .findFirst().orElse(null);
        return matchedModule;
    }

    private Module addModule(String moduleName) {
        synchronized (this) {
            if (moduleDependency.getModules() == null) {
                moduleDependency.setModules(new ArrayList<Module>());
            }
        }

        Module newModule = new Module();
        newModule.setName(moduleName);
        moduleDependency.getModules().add(newModule);

        return newModule;
    }

    public List<String> getReqModules(String moduleName) {
        return moduleDependency.getModules().stream()
                .filter(module -> module.getDepModules().contains(moduleName))
                .map(module -> module.getName())
                .collect(Collectors.toList());
    }
}
