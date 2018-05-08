package oracle.idaas.smartTest.dao;

import oracle.idaas.smartTest.common.helper.FileSystemHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public class CodeCoverageDAOTest {

    private CodeCoverageDAO codeCoverageReport;

    @Before
    public void setup() {
        codeCoverageReport =
                CodeCoverageDAO.getInstance(
                        FileSystemHelper.getInstance().getFile("sampleJacocoReport.xml"));
    }
    @Test
    public void getClassCoverage() {
        assert codeCoverageReport.getClassCoverage() == 10;
    }

    @Test
    public void getCoveredPackages() {
        Set<String> coveredPackages = codeCoverageReport.getCoveredPackages();
        System.out.println("coveredPackages = " + coveredPackages);
        assert coveredPackages.size() > 0;
    }

    @Test
    public void getCoveredClasses() {
        Set<String> coveredClasses = codeCoverageReport.getCoveredClasses();
        System.out.println("coveredClasses = " + coveredClasses);
        assert coveredClasses.size() > 0;
    }

}