package org.openpaas.paasta.ondemand.common;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.openpaas.paasta.ondemand.config.TokenGrantTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class Common {

    @Value("${cloudfoundry.cc.api.url}")
    public String apiTarget;

    @Value("${cloudfoundry.cc.api.uaaUrl}")
    public String uaaTarget;

    @Value("${cloudfoundry.cc.api.sslSkipValidation}")
    public boolean cfskipSSLValidation;

    @Value("${cloudfoundry.user.admin.username}")
    public String adminUserName;

    @Value("${cloudfoundry.user.admin.password}")
    public String adminPassword;

    @Autowired
    DefaultConnectionContext connectionContext;

    @Autowired
    PasswordGrantTokenProvider tokenProvider;

    /**
     * ReactorCloudFoundryClient 생성하여, 반환한다.
     *
     * @param connectionContext
     * @param tokenProvider
     * @return DefaultCloudFoundryOperations
     */
    public ReactorCloudFoundryClient cloudFoundryClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorCloudFoundryClient.builder().connectionContext(connectionContext).tokenProvider(tokenProvider).build();
    }

    public ReactorCloudFoundryClient cloudFoundryClient(TokenProvider tokenProvider) {
        return ReactorCloudFoundryClient.builder().connectionContext(connectionContext()).tokenProvider(tokenProvider).build();
    }

    public ReactorCloudFoundryClient cloudFoundryClient() {
        return ReactorCloudFoundryClient.builder().connectionContext(connectionContext()).tokenProvider(tokenProvider()).build();
    }


    /**
     * DefaultConnectionContext 가져온다.
     *
     * @return DefaultConnectionContext
     */
    public DefaultConnectionContext connectionContext() {
        return connectionContext;
    }


    /**
     * TokenGrantTokenProvider 생성하여, 반환한다.
     *
     * @param token
     * @return DefaultConnectionContext
     * @throws Exception
     */
    public TokenProvider tokenProvider(String token) {

        if (token.indexOf("bearer") < 0) {
            token = "bearer " + token;
        }
        TokenGrantTokenProvider tokenProvider = new TokenGrantTokenProvider(token);
        return tokenProvider;

    }

    public PasswordGrantTokenProvider tokenProvider() {
        if (tokenProvider == null) {
            tokenProvider = PasswordGrantTokenProvider.builder().password(adminPassword).username(adminUserName).build();
        }
        return tokenProvider;
    }


}
