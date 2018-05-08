
package oracle.idaas.smartTest.config.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "dataSourceType",
    "dataSources"
})
public class DataSourceConfig {

    @JsonProperty("dataSourceType")
    private String dataSourceType;
    @JsonProperty("dataSources")
    private List<DataSource> dataSources = new ArrayList<DataSource>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("dataSourceType")
    public String getDataSourceType() {
        return dataSourceType;
    }

    @JsonProperty("dataSourceType")
    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    @JsonProperty("dataSources")
    public List<DataSource> getDataSources() {
        return dataSources;
    }

    @JsonProperty("dataSources")
    public void setDataSources(List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
