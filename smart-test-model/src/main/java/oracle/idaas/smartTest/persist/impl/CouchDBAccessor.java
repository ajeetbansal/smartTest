package oracle.idaas.smartTest.persist.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import oracle.idaas.smartTest.config.dao.DataSourceConfigDAO;
import oracle.idaas.smartTest.dao.helper.RESTClientFactory;
import oracle.idaas.smartTest.object.dependency.ModuleDependency;
import oracle.idaas.smartTest.persist.ModuleDependencyAccessor;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CouchDBAccessor implements ModuleDependencyAccessor {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String version;
    private static Logger logger = Logger.getLogger(CouchDBAccessor.class.getName());
    private String couchDBUrl;


    public CouchDBAccessor(String version) {
        this.version = version;
        couchDBUrl = DataSourceConfigDAO.getInstance().getModuleDependenciesUrl(version);
    }
    @Override
    public void writeModuleDependency(ModuleDependency moduleDependency) {
        String moduleDependecyData = null;
        try {
            moduleDependecyData = mapper.writeValueAsString(moduleDependency);
        } catch (JsonProcessingException e) {
            logger.log(Level.INFO, "Error while serializing moduleDependency: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            RESTClientFactory.getRESTClient().target(couchDBUrl).request().put(Entity.json(moduleDependecyData));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception while trying to write module dependency data: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public ModuleDependency readModuleDependency() {
        ModuleDependency moduleDependency;
        String moduleDependecyData = null;
        try {
            moduleDependecyData = RESTClientFactory.getRESTClient().target(couchDBUrl).request().get(String.class);
        } catch (NotFoundException e) {
            logger.log(Level.INFO, "No existing module dependency data for version: " + version);
        }
        try {
            moduleDependency = moduleDependecyData != null ?
                                    mapper.readValue(moduleDependecyData, ModuleDependency.class): new ModuleDependency();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return moduleDependency;
    }

    @Override
    public void rollbackModuleDependency() {

    }
}
