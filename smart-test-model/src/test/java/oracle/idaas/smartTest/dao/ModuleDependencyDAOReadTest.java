package oracle.idaas.smartTest.dao;

import org.junit.Before;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModuleDependencyDAOReadTest {

    private ModuleDependencyDAO moduleDependencyDAO;

    @Before
    public void setup() {
//        moduleDependencyDAO =
//                ModuleDependencyDAO.getInstance(
//                        FileSystemHelper.getInstance().getFile("moduleDependencies.json"));
    }

    //@Test
    public void getModuleDependencies() {
        List<String> moduleDependencies = moduleDependencyDAO.getModuleDependencies("socialidp-common");
        System.out.println("moduleDependencies = " + moduleDependencies);
        assert moduleDependencies.size() == 2;
        assert moduleDependencies.containsAll(Stream.of("socialidp", "oauth2-srv").collect(Collectors.toList()));
    }
}