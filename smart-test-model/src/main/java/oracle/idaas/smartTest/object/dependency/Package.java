package oracle.idaas.smartTest.object.dependency;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "depModules"
})
public class Package {

    /**
     * The Name Schema
     * <p>
     *
     *
     */
    @JsonProperty("name")
    private String name = "";
    @JsonProperty("depModules")
    private List<String> depModules = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * The Name Schema
     * <p>
     *
     *
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * The Name Schema
     * <p>
     *
     *
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("depModules")
    public List<String> getDepModules() {
        return depModules;
    }

    @JsonProperty("depModules")
    public void setDepModules(List<String> depModules) {
        this.depModules = depModules;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(additionalProperties).append(depModules).append(name).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Package) == false) {
            return false;
        }
        Package rhs = ((Package) other);
        return new EqualsBuilder().append(additionalProperties, rhs.additionalProperties).append(depModules, rhs.depModules).append(name, rhs.name).isEquals();
    }

}