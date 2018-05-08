package oracle.idaas.smartTest.config.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import oracle.idaas.smartTest.common.helper.FileSystemHelper;
import oracle.idaas.smartTest.config.object.DataSource;
import oracle.idaas.smartTest.config.object.DataSourceConfig;
import oracle.idaas.smartTest.config.object.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataSourceConfigDAO {
    public static String DATA_SOURCE_CONFIG = "data-source.config";
    public static String DATA_SOURCE_FILE_LOCATION = "data-source.file.location";
    public static String DEFAULT_DATA_SOURCE_CONFIG = DATA_SOURCE_CONFIG + ".json";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static DataSourceConfigDAO INSTANCE = null;
    private static Logger logger = Logger.getLogger(DataSourceConfigDAO.class.getName());

    private DataSourceConfig dataSourceConfig;

    public static DataSourceConfigDAO getInstance(){
        if( INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (DataSourceConfigDAO.class) {
            if(INSTANCE == null) {
                INSTANCE = new DataSourceConfigDAO();
            }
        }
        DataSourceConfig dataSourceConfig = null;
        dataSourceConfig = loadShortHandConfig();
        if( dataSourceConfig == null) {
            dataSourceConfig = loadFromConfigFile();
        }
        INSTANCE.dataSourceConfig = dataSourceConfig;
        return INSTANCE;
    }

    private static DataSourceConfig loadShortHandConfig() {
        String dataSourceFileLocation = System.getProperty(DATA_SOURCE_FILE_LOCATION);
        if (dataSourceFileLocation == null) {
            return null;
        }
        logger.log(Level.INFO, String.format("System property %s has been specified. Hence over-riding the file data source location with %s",
                    DATA_SOURCE_FILE_LOCATION, dataSourceFileLocation));
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDataSourceType(DataSourceType.FILE.toString());
        Properties properties = new Properties(); properties.setLocation(dataSourceFileLocation);
        DataSource dataSource = new DataSource(); dataSource.setProperties(properties); dataSource.setType(DataSourceType.FILE.toString());
        dataSourceConfig.setDataSources(Stream.of(dataSource).collect(Collectors.toList()));
        return dataSourceConfig;
    }

    private static DataSourceConfig loadFromConfigFile() {
        DataSourceConfig dataSourceConfig = null;
        File bootConfigFile;
        String bootConfigFileName = System.getProperty(DATA_SOURCE_CONFIG);
        if(bootConfigFileName == null || bootConfigFileName.isEmpty()) {
            bootConfigFile = FileSystemHelper.getInstance().getFile(DEFAULT_DATA_SOURCE_CONFIG);
            if (bootConfigFile != null && bootConfigFile.exists()) {
                logger.log(Level.INFO, String.format("System property %s not specified. Defaulting to %s",
                        DATA_SOURCE_CONFIG, bootConfigFile.getAbsolutePath()));
            }
        } else {
            bootConfigFile = new File(bootConfigFileName);
            logger.log(Level.INFO, String.format("System property %s has been specified. Hence using %s for data source config",
                    DATA_SOURCE_CONFIG, bootConfigFileName));
        }
        try {
            if (bootConfigFile != null && bootConfigFile.exists()) {
                dataSourceConfig = mapper.readValue(new FileInputStream(bootConfigFile), DataSourceConfig.class);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while trying to read data source config: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return dataSourceConfig;
    }


        public enum DataSourceType {FILE, COUCH_DB, HYBRID}

    public DataSourceType getDataSourceType() {
        return DataSourceType.valueOf(dataSourceConfig.getDataSourceType());
    }

    public String getModuleDependenciesFile(String version) {
        String fileRepoPath = getLocationProperty(DataSourceType.FILE);
        return String.format(moduleDependenciesFilePattern,fileRepoPath, version);
    }

    public String getPackageDependenciesFile(String version) {
        return String.format(packageDependenciesFilePattern, getLocationProperty(DataSourceType.FILE),version);
    }

    public String getModuleDependenciesUrl(String version) {
        return String.format(moduleDependenciesUrlPattern, getLocationProperty(DataSourceType.COUCH_DB),version);
    }

    private String getLocationProperty(DataSourceType dataSourceType) {
        String locationProperty = null;
        if (dataSourceConfig != null) {
            locationProperty = dataSourceConfig.getDataSources().stream()
                    .filter(dataSource -> dataSource.getType().equals(dataSourceType.toString()))
                    .findFirst().get().getProperties().getLocation();
        }
        if(locationProperty == null || locationProperty.isEmpty()) {
            throw new RuntimeException("Data Source Location is not specified, hence can't proceed further");
        }
        if (!locationProperty.endsWith(File.separator)) {
            locationProperty = locationProperty + File.separator;
        }
        return locationProperty;
    }

    private static String moduleDependenciesFilePattern = "%sIntegTestModuleDep_%s.json";
    private static String packageDependenciesFilePattern = "%sIntegTestPackageDep_%s.json";

    private static String moduleDependenciesUrlPattern = "%s%s";

}
