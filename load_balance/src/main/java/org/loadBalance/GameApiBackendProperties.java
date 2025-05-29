package org.loadBalance;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "game-api")

public class GameApiBackendProperties {
    private List<String> backends;

    public  List<String> getBackends()
    {
        return backends;
    }

    public void setBackends(List<String> backends)
    {
        this.backends = backends;
    }
}

