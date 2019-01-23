package org.openpaas.paasta.ondemand;


import org.cloudfoundry.client.lib.CloudFoundryException;
import org.openpaas.paasta.bosh.BoshDirector;
import org.openpaas.paasta.ondemand.util.SSLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;


@SpringBootApplication
public class OnDemandApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(OnDemandApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OnDemandApplication.class, args);
        try {
            BoshDirector boshDirector = new BoshDirector("admin", "idg3k30h72zq61yvg7nz", "https://localhost:25555","https://localhost:8443");
            System.out.println(boshDirector.getDeployments("paasta-mysql-service"));
            System.out.println(boshDirector.postCreateAndUpdateDeployment("{\"manifest\":\"name: paasta-mysql-service                              \\r\\n\\r\\nreleases:\\r\\n- name: paasta-mysql                                    \\r\\n  version: \\\"2.0\\\"                                        \\r\\n\\r\\nstemcells:\\r\\n- alias: default\\r\\n  os: ubuntu-trusty\\r\\n  version: \\\"3309\\\"\\r\\n\\r\\nupdate:\\r\\n  canaries: 1                            \\r\\n  canary_watch_time: 30000-600000        \\r\\n  max_in_flight: 1                       \\r\\n  update_watch_time: 30000-600000        \\r\\n\\r\\ninstance_groups:\\r\\n- name: mysql\\r\\n  azs:\\r\\n  - z5\\r\\n  instances: 1\\r\\n  vm_type: minimal\\r\\n  stemcell: default\\r\\n  persistent_disk_type: 8GB\\r\\n  networks:\\r\\n  - name: service_private\\r\\n    ip:\\r\\n    - 10.30.50.32\\r\\n  properties:\\r\\n    admin_password: admin                \\r\\n    cluster_ips:                         \\r\\n    - 10.30.50.32\\r\\n    network_name: service_private\\r\\n    seeded_databases: null\\r\\n    syslog_aggregator: null\\r\\n    collation_server: utf8_unicode_ci    \\r\\n    character_set_server: utf8\\r\\n  release: paasta-mysql\\r\\n  template: mysql\\r\\n\\r\\n- name: proxy\\r\\n  azs:\\r\\n  - z5\\r\\n  instances: 0\\r\\n  vm_type: minimal\\r\\n  stemcell: default\\r\\n  networks:\\r\\n  - name: service_private\\r\\n    ip:\\r\\n    - 10.30.50.33\\r\\n  properties:\\r\\n    cluster_ips:\\r\\n    - 10.30.50.32\\r\\n    external_host: 115.68.46.188.xip.io       \\r\\n    nats:                                    \\r\\n      machines:\\r\\n      - 10.30.50.34 \\r\\n      password: \\\"fxaqRErYZ1TD8296u9HdMg8ol8dJ0G\\\"\\r\\n      port: 4222\\r\\n      user: nats\\r\\n    network_name: service_private\\r\\n    proxy:                                   \\r\\n      api_password: admin\\r\\n      api_username: api\\r\\n      api_force_https: false\\r\\n    syslog_aggregator: null\\r\\n  release: paasta-mysql\\r\\n  template: proxy\\r\\n\\r\\n- name: paasta-mysql-java-broker\\r\\n  azs:\\r\\n  - z5\\r\\n  instances: 1\\r\\n  vm_type: minimal\\r\\n  stemcell: default\\r\\n  networks:\\r\\n  - name: service_private\\r\\n    ip:\\r\\n    - 10.30.50.35\\r\\n  properties:                                \\r\\n    jdbc_ip: 10.30.50.32\\r\\n    jdbc_pwd: admin\\r\\n    jdbc_port: 3306\\r\\n    log_dir: paasta-mysql-java-broker\\r\\n    log_file: paasta-mysql-java-broker\\r\\n    log_level: INFO\\r\\n  release: paasta-mysql\\r\\n  template: op-mysql-java-broker\\r\\n\\r\\n- name: broker-registrar\\r\\n  lifecycle: errand                          \\r\\n  azs:\\r\\n  - z5\\r\\n  instances: 1\\r\\n  vm_type: minimal\\r\\n  stemcell: default\\r\\n  networks:\\r\\n  - name: service_private\\r\\n  properties:\\r\\n    broker:\\r\\n      host: 10.30.50.35\\r\\n      name: mysql-service-broker\\r\\n      password: cloudfoundry\\r\\n      username: admin\\r\\n      protocol: http\\r\\n      port: 8080\\r\\n    cf:\\r\\n      admin_password: admin\\r\\n      admin_username: admin_test\\r\\n      api_url: https://api.115.68.46.188.xip.io\\r\\n      skip_ssl_validation: true\\r\\n  release: paasta-mysql\\r\\n  template: broker-registrar\\r\\n\\r\\n- name: broker-deregistrar\\r\\n  lifecycle: errand                          \\r\\n  azs:\\r\\n  - z5\\r\\n  instances: 1\\r\\n  vm_type: minimal\\r\\n  stemcell: default\\r\\n  networks:\\r\\n  - name: service_private\\r\\n  properties:\\r\\n    broker:\\r\\n      name: mysql-service-broker\\r\\n    cf:\\r\\n      admin_password: admin\\r\\n      admin_username: admin_test\\r\\n      api_url: https://api.115.68.46.188.xip.io\\r\\n      skip_ssl_validation: true\\r\\n  release: paasta-mysql\\r\\n  template: broker-deregistrar\\r\\n\\r\\n\\r\\nmeta:\\r\\n  apps_domain: 115.68.46.188.xip.io\\r\\n  environment: null\\r\\n  external_domain: 115.68.46.188.xip.io\\r\\n  nats:\\r\\n    machines:\\r\\n    - 10.30.50.34 \\r\\n    password: \\\"fxaqRErYZ1TD8296u9HdMg8ol8dJ0G\\\"\\r\\n    port: 4222\\r\\n    user: nats\\r\\n  syslog_aggregator: null\\r\\n\"}"));
            //System.out.println(boshDirector.deleteDeployment("ondemand"));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        System.exit(1);
    }
}

