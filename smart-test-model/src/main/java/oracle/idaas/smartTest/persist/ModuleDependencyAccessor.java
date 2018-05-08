package oracle.idaas.smartTest.persist;

import oracle.idaas.smartTest.object.dependency.ModuleDependency;

public interface ModuleDependencyAccessor {
    void writeModuleDependency(ModuleDependency moduleDependency);
    ModuleDependency readModuleDependency();
    void rollbackModuleDependency();
}
