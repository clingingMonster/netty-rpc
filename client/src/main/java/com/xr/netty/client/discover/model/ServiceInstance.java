package com.xr.netty.client.discover.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author XR
 * Created  on 2020/12/4.
 */
public class ServiceInstance {

    private String id;

    private String serviceName;

    private String host;

    private Integer port;

    private boolean enabled;

    private boolean healthy;

    private Map<String, String> metadata = new HashMap<>();

    private transient String address;


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public ServiceInstance() {

    }

    public ServiceInstance(String id, String serviceName, String host, Integer port) {
        if (port != null && port.intValue() < 1) {
            throw new IllegalArgumentException("The port must be greater than zero!");
        }
        this.id = id;
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
        this.enabled = true;
        this.healthy = true;
    }

    public ServiceInstance(String serviceName, String host, Integer port) {
        this(host + ":" + port, serviceName, host, port);
    }


    private final String REVISION_KEY = "version";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceInstance)) return false;
        ServiceInstance that = (ServiceInstance) o;
        boolean equals = Objects.equals(getServiceName(), that.getServiceName()) &&
                Objects.equals(getHost(), that.getHost()) &&
                Objects.equals(getPort(), that.getPort());
        for (Map.Entry<String, String> entry : this.getMetadata().entrySet()) {
            if (entry.getKey().equals(REVISION_KEY)) {
                continue;
            }
            equals = equals && entry.getValue().equals(that.getMetadata().get(entry.getKey()));
        }

        return equals;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getServiceName(), getHost(), getPort());
        for (Map.Entry<String, String> entry : this.getMetadata().entrySet()) {
            if (entry.getKey().equals(REVISION_KEY)) {
                continue;
            }
            result = 31 * result + (entry.getValue() == null ? 0 : entry.getValue().hashCode());
        }
        return result;
    }


    @Override
    public String toString() {
        return "DefaultServiceInstance{" +
                "id='" + id + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", enabled=" + enabled +
                ", healthy=" + healthy +
                ", metadata=" + metadata +
                '}';
    }
}
