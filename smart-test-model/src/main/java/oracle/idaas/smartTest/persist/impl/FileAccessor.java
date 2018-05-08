package oracle.idaas.smartTest.persist.impl;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import oracle.idaas.smartTest.config.dao.DataSourceConfigDAO;
import oracle.idaas.smartTest.object.dependency.ModuleDependency;
import oracle.idaas.smartTest.persist.ModuleDependencyAccessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileAccessor implements ModuleDependencyAccessor {

    private static Logger logger = Logger.getLogger(ModuleDependencyAccessor.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper();
    private File moduleDependencyFile;

    public FileAccessor(String version) {
        moduleDependencyFile = new File(DataSourceConfigDAO.getInstance().getModuleDependenciesFile(version));
        if(!moduleDependencyFile.getParentFile().exists()) {
            if(!moduleDependencyFile.getParentFile().mkdirs()) {
                throw new RuntimeException(String.format("Directory %s does not exist and attempt to create it also failed: ", moduleDependencyFile.getParent()));
            }
        }
        if(Files.notExists(moduleDependencyFile.toPath())) {
            try {
                moduleDependencyFile.createNewFile();
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void writeModuleDependency(ModuleDependency moduleDependency) {
        JsonFactory jsonFactory = new JsonFactory();
        try {
            FileOutputStream file = new FileOutputStream(moduleDependencyFile);
            JsonGenerator jsonGen = jsonFactory.createJsonGenerator(file, JsonEncoding.UTF8);
            jsonGen.setCodec(new ObjectMapper());
            jsonGen.writeObject(moduleDependency);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while serializing: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @Override
    public ModuleDependency readModuleDependency() {
        ModuleDependency moduleDependency;
        try {
            moduleDependency = mapper.readValue(new FileInputStream(moduleDependencyFile), ModuleDependency.class);
        } catch (JsonMappingException jme) {
            moduleDependency = new ModuleDependency();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return moduleDependency;
    }

    @Override
    public void rollbackModuleDependency() {
     if(moduleDependencyFile != null && moduleDependencyFile.exists()) {
         moduleDependencyFile.delete();
         logger.log(Level.INFO, "Deleted module dependency data file: " + moduleDependencyFile.getAbsolutePath());
     }
    }
}
