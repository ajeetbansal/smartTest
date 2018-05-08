package oracle.idaas.smartTest.persist;

import oracle.idaas.smartTest.config.dao.DataSourceConfigDAO;
import oracle.idaas.smartTest.persist.impl.CouchDBAccessor;
import oracle.idaas.smartTest.persist.impl.FileAccessor;
import oracle.idaas.smartTest.persist.impl.HybridAccessor;

import java.util.HashMap;
import java.util.Map;

import static oracle.idaas.smartTest.config.dao.DataSourceConfigDAO.DataSourceType;


public class ModuleDependencyAccessorFactory {
    private static Map<String, ModuleDependencyAccessor> accessorInstances =
            new HashMap<String, ModuleDependencyAccessor>();
    public static ModuleDependencyAccessor getAccessor(String version) {
        ModuleDependencyAccessor moduleDependencyAccessor;
        moduleDependencyAccessor = accessorInstances.get(version);
        if(moduleDependencyAccessor == null) {
            synchronized (ModuleDependencyAccessorFactory.class) {
                if(moduleDependencyAccessor == null) {
                    moduleDependencyAccessor = resolveAccessor(DataSourceConfigDAO.getInstance().getDataSourceType(), version);
                    accessorInstances.put(version, moduleDependencyAccessor);
                }
            }
        }
        return moduleDependencyAccessor;
    }

    public static ModuleDependencyAccessor resolveAccessor(DataSourceType dataSourceType, String version){
        ModuleDependencyAccessor moduleDependencyAccessor = null;
        switch(dataSourceType) {
            case COUCH_DB:
                    moduleDependencyAccessor = new CouchDBAccessor(version);
                break;
            case FILE:
                moduleDependencyAccessor = new FileAccessor(version);
                break;
            case HYBRID:
                moduleDependencyAccessor = new HybridAccessor(version);
                break;
        }
        return moduleDependencyAccessor;
    }
}
