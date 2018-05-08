package oracle.idaas.smartTest.web.rest;

import oracle.idaas.smartTest.dao.ModuleDependencyDAO;
import oracle.idaas.smartTest.object.dependency.Module;
import oracle.idaas.smartTest.object.dependency.ModuleDependency;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

@Path("{version}/Modules")
public class ModuleDependencyResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{moduleName}/DepModules")
    public List<String> getDepModules(@PathParam("version") String version, @PathParam("moduleName") String moduleName) {
        ModuleDependencyDAO moduleDependencyDAO =  getModuleDependencyDAO(version);
        Module module = moduleDependencyDAO.getModule(moduleName);
        return module.getDepModules();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ModuleDependency getAllModuleDependecies(@PathParam("version") String version) {
        ModuleDependencyDAO moduleDependencyDAO =  getModuleDependencyDAO(version);
        ModuleDependency moduleDependency =moduleDependencyDAO.getAllModuleDependencies();
        return moduleDependency;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{moduleName}/ReqModules")
    public List<String> getReqDependecy(@PathParam("version") String version, @PathParam("moduleName") String moduleName) {
        ModuleDependencyDAO moduleDependencyDAO =  getModuleDependencyDAO(version);
        List<String> reqModules;
        reqModules = moduleDependencyDAO.getReqModules(moduleName);
        Collections.sort(reqModules);
        return reqModules;
    }

    private static ModuleDependencyDAO getModuleDependencyDAO(String version) {
//        File file = new File(moduleDependencyDataFile);
//        if(Files.notExists(file.toPath())) {
//                throw new RuntimeException("Data source file does not exist: " + moduleDependencyDataFile);
//        }
        return ModuleDependencyDAO.getInstance(version);
    }

}
