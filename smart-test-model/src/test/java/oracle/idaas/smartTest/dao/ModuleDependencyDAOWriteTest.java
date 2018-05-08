package oracle.idaas.smartTest.dao;

import oracle.idaas.smartTest.common.helper.FileSystemHelper;
import oracle.idaas.smartTest.config.dao.DataSourceConfigDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModuleDependencyDAOWriteTest {

    private ModuleDependencyDAO moduleDependencyDAO;
    File file;

    @Before
    public void setup() throws IOException {
        file = new File("/tmp/testModuleDependencies.json");
        file.createNewFile();
    }

    @After
    public void tearDown() {
        file.delete();
    }
    @Test
    public void ModuleDependenciesCRUD() {
        File configFile = FileSystemHelper.getInstance().getFile("data-source.config.test.json");
        System.setProperty(DataSourceConfigDAO.DATA_SOURCE_CONFIG, configFile.getAbsolutePath());
        String version = UUID.randomUUID().toString();
        System.out.println("version = " + version);
        moduleDependencyDAO =
                ModuleDependencyDAO.getInstance(version);
        moduleDependencyDAO.addModuleDependency("socialidp-common", "oauth2-srv");
        moduleDependencyDAO.addModuleDependency("socialidp-common", "socialidp");

        moduleDependencyDAO.commit();

        ModuleDependencyDAO moduleDependencyDAO1 = ModuleDependencyDAO.getInstance(version);
        List<String> moduleDependencies = moduleDependencyDAO.getModuleDependencies("socialidp-common");
        System.out.println("moduleDependencies = " + moduleDependencies);
        assert moduleDependencies.size() == 2;
        assert moduleDependencies.containsAll(Stream.of("socialidp", "oauth2-srv").collect(Collectors.toList()));
    }
}