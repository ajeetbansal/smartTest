package oracle.idaas.smartTest.dao;

import oracle.idaas.smartTest.object.coverage.Counter;
import oracle.idaas.smartTest.object.coverage.Report;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import oracle.idaas.smartTest.object.coverage.Package;
import oracle.idaas.smartTest.object.coverage.Class;

public class CodeCoverageDAO {

    public enum CounterType {
        CLASS, LINE, METHOD;
    }

    public Report getReport() {
        return report;
    }

    private Report report;
    private static Logger logger = Logger.getLogger(CodeCoverageDAO.class.getName());

    public CodeCoverageDAO(Report report) {
        this.report = report;
    }

    public static CodeCoverageDAO getInstance(File codeCoverageReportFile) {
        return new CodeCoverageDAO(getCodeCoverageReport(codeCoverageReportFile));
    }

    private static Report getCodeCoverageReport(File codeCoverageReportFile) {
        Report codeCoverageReport = null;
        try {
            JAXBContext jc = JAXBContext.newInstance("oracle.idaas.smartTest.object.coverage");
            Unmarshaller u = jc.createUnmarshaller();

            XMLInputFactory xif = XMLInputFactory.newFactory();
            xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            XMLStreamReader xsr = xif.createXMLStreamReader(new StreamSource(codeCoverageReportFile));

            codeCoverageReport = (Report) u.unmarshal(xsr);
        } catch (JAXBException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } catch (XMLStreamException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return codeCoverageReport;
    }

    public int getClassCoverage() {
        Counter classCounter = getCounter(CounterType.CLASS);
        if (classCounter == null) {
            return 0;
        }
        return Integer.parseInt(classCounter.getCovered());
    }

    public Counter getCounter(CounterType counterType) {
        if (report == null || report.getCounter().isEmpty()) {
            return null;
        }
        return report.getCounter().stream().filter(counter -> counter.getType().equals(counterType.toString())).findFirst().get();
    }

    public Set<String> getCoveredPackages() {
        Set<String> coveredPackages = new HashSet<String>();
        report.getGroupOrPackage().stream()
                .filter(groupOrPackage -> groupOrPackage instanceof Package)
                .filter(groupOrPackage -> getClassCoverageForPackage((Package) groupOrPackage) > 0)
                .forEach(groupOrPackage -> coveredPackages.add(((Package) groupOrPackage).getName()));
        return coveredPackages;
    }

    public Set<String> getCoveredClasses() {
        Set<String> coveredClasses = new HashSet<String>();
        report.getGroupOrPackage().stream()
                .filter(groupOrPackage -> groupOrPackage instanceof Package)
                .filter(groupOrPackage -> getClassCoverageForPackage((Package) groupOrPackage) > 0)
                .forEach(groupOrPackage -> {
                    ((Package) groupOrPackage).getClazzOrSourcefile().stream()
                            .filter(clazz -> clazz instanceof Class)
                            .filter(clazz -> getClassCoverageForClass((Class) clazz) > 0)
                            .forEach(clazz -> coveredClasses.add(getSourceClassName(((Class) clazz).getName())));
                });
        return coveredClasses;
    }

    private String getSourceClassName(String name) {
        if(name.contains("$")) {
            return name.substring(0, name.indexOf("$"));
        }
        return name;
    }

    public int getClassCoverageForPackage(Package pkg) {
        Counter classCounter = getCounterForPackage(pkg, CounterType.CLASS);
        if (classCounter == null) {
            return 0;
        }
        return Integer.parseInt(classCounter.getCovered());
    }

    public Counter getCounterForPackage(Package pkg, CounterType counterType) {
        if (pkg == null || pkg.getCounter().isEmpty()) {
            return null;
        }
        return pkg.getCounter().stream().filter(counter -> counter.getType().equals(counterType.toString())).findFirst().get();
    }

    public int getClassCoverageForClass(Class clazz) {
        Counter classCounter = getCounterForClass(clazz, CounterType.CLASS);
        if (classCounter == null) {
            return 0;
        }
        return Integer.parseInt(classCounter.getCovered());
    }

    public Counter getCounterForClass(Class clazz, CounterType counterType) {
        if (clazz == null || clazz.getCounter().isEmpty()) {
            return null;
        }
        return clazz.getCounter().stream().filter(counter -> counter.getType().equals(counterType.toString())).findFirst().get();
    }
}
