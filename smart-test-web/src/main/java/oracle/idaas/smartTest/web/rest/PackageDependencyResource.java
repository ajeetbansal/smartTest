package oracle.idaas.smartTest.web.rest;

import oracle.idaas.smartTest.dao.PackageDependencyDAO;
import oracle.idaas.smartTest.object.dependency.Package;
import oracle.idaas.smartTest.object.dependency.PackageDependency;
import oracle.idaas.smartTest.config.dao.DataSourceConfigDAO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.nio.file.Files;

@Path("{version}/Packages")
public class PackageDependencyResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{packageName}")
    public Package getPackageDependecy(@PathParam("version") String version, @PathParam("packageName") String pkgName) {
        PackageDependencyDAO packageDependencyDAO =
                getPackageDependencyDAO(DataSourceConfigDAO.getInstance().getPackageDependenciesFile(version));
        Package pkg = packageDependencyDAO.getPackage(pkgName);
        return pkg;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PackageDependency getAllPackageDependecies(@PathParam("version") String version) {
        PackageDependencyDAO packageDependencyDAO =
                getPackageDependencyDAO(DataSourceConfigDAO.getInstance().getPackageDependenciesFile(version));
        PackageDependency packageDependency = packageDependencyDAO.getAllPackageDependencies();
        return packageDependency;
    }

    private static PackageDependencyDAO getPackageDependencyDAO(String packageDependencyDataFile) {
        File file = new File(packageDependencyDataFile);
        if(Files.notExists(file.toPath())) {
                throw new RuntimeException("Data source file does not exist: " + packageDependencyDataFile);
        }
        return PackageDependencyDAO.getInstance(file);
    }

}
