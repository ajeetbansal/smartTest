package oracle.idaas.smartTest.persist.impl;

import static oracle.idaas.smartTest.config.dao.DataSourceConfigDAO.DataSourceType;

import oracle.idaas.smartTest.object.dependency.ModuleDependency;
import oracle.idaas.smartTest.persist.ModuleDependencyAccessor;
import oracle.idaas.smartTest.persist.ModuleDependencyAccessorFactory;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HybridAccessor implements ModuleDependencyAccessor {

    private static Logger logger = Logger.getLogger(HybridAccessor.class.getName());
    private List<ModuleDependencyAccessor> moduleDependencyAccessors;


    public HybridAccessor(String version) {
        moduleDependencyAccessors = Arrays.asList(DataSourceType.values()).stream()
                .filter(dataSourceType -> !dataSourceType.equals(DataSourceType.HYBRID))
                .map(dataSourceType -> ModuleDependencyAccessorFactory.resolveAccessor(dataSourceType, version))
                .collect(Collectors.toList());
    }
    @Override
    public void writeModuleDependency(ModuleDependency moduleDependency) {
        moduleDependencyAccessors.stream()
                .forEach(moduleDependencyAccessor -> moduleDependencyAccessor.writeModuleDependency(moduleDependency));
    }

    @Override
    public ModuleDependency readModuleDependency() {
        ModuleDependency retModuleDependency;
        retModuleDependency = moduleDependencyAccessors.stream()
                .map(moduleDependencyAccessor -> moduleDependencyAccessor.readModuleDependency())
                .filter(moduleDependency -> moduleDependency.getModules() != null)
                .findFirst().orElse(new ModuleDependency());

        return retModuleDependency;
    }

    @Override
    public void rollbackModuleDependency() {

    }
}
