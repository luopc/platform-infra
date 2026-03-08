package com.luopc.platform.web.listeners;

import cn.hutool.core.net.NetUtil;
import com.luopc.platform.web.common.core.util.SimpleSystemInfoUtil;
import com.luopc.platform.web.common.core.util.os.OSInfo;
import com.luopc.platform.web.common.core.util.os.OSRuntimeInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.luopc.platform.web.common.core.constant.LoggingConstants.INSTANCE_KEY;
import static com.luopc.platform.web.common.core.util.SimpleSystemInfoUtil.formatData;

/**
 * @author Robin
 */
@Slf4j
@Component
@Order(value = 1000)
public class BootWebApplicationListener implements ApplicationListener<WebServerInitializedEvent> {


    @Value("${jasypt.verify.encrypted-text}")
    private String encryptedText;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        WebServer server = webServerInitializedEvent.getWebServer();
        WebServerApplicationContext context = webServerInitializedEvent.getApplicationContext();
        Environment env = context.getEnvironment();
        printSystemInfo();
        log.info("Jasypt Decryption Test - Expected: [{}], Actual: [{}]", "test_value_549527", encryptedText);

        String ip = NetUtil.getLocalhost().getHostAddress();
        Integer port = server.getPort();
        String contextPath = env.getProperty("server.servlet.context-path");
        String instanceKey = env.getProperty(INSTANCE_KEY);
        if (contextPath == null) {
            contextPath = "";
        }
        String projectName = Optional.ofNullable(env.getProperty("spring.application.name")).orElse(env.getProperty("application.name"));
        String profile = env.getProperty("spring.profiles.active");
        String title = String.format("--------------- System Environment for [%s%s] ---------------", projectName, StringUtils.isNotBlank(profile) ? "(" + profile + ")" : "");
        String localhostUrl = String.format("http://localhost:%d%s", port, contextPath);
        String externalUrl = String.format("http://%s:%d%s", ip, port, contextPath);
        String monitorUrl = String.format("http://localhost:%d%s/actuator", port, contextPath);
        String prometheusUrl = String.format("http://localhost:%d%s/actuator/prometheus", port, contextPath);
        String apiUrl = String.format("http://localhost:%d%s/doc.html", port, contextPath);
           String apiDocsUrl = """
                
                {}
                	API:		{}
                	Local:		{}
                	External:	{}
                	Monitor:	{}
                	Prometheus:	{}
                {}
                """;
        log.info(apiDocsUrl, title, apiUrl, localhostUrl, externalUrl, monitorUrl, prometheusUrl, "-".repeat(title.length()));

    }

    private void printSystemInfo() {
        try {
            OSInfo osInfo = SimpleSystemInfoUtil.getSystemInfo();
            OSRuntimeInfo osRuntimeInfo = SimpleSystemInfoUtil.getOSRuntimeInfo();
            long total = osRuntimeInfo.getTotalMemory();
            long availableMemory = osRuntimeInfo.getAvailableMemory();

            String systemInfo = """
                    
                    --------------- System Information ---------------
                    	OS:		    {} ({})
                    	JVM:		{} ({} {})
                    	CPU:		{} cores, {} physical cores
                    	Memory:		{} total, {} available
                    	Host:		{}
                    	Hostname:	{}
                    --------------------------------------------------
                    """;
            log.info(systemInfo,
                    osInfo.getOs(),
                    osInfo.getOsArch(),
                    osInfo.getJavaRuntimeName(),
                    osInfo.getJavaVersion(),
                    osInfo.getJavaVmVendor(),
                    osInfo.getCpuCount(),
                    osInfo.getPhysicalCoresCount(),
                    formatData(total),
                    formatData(availableMemory),
                    osInfo.getHost(),
                    osInfo.getHostName()
            );
        } catch (Exception e) {
            log.warn("Failed to retrieve system information: {}", e.getMessage());
        }
    }
}
