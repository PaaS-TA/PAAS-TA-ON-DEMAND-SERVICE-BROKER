package org.openpaas.paasta.ondemand.repo;

import org.openpaas.paasta.ondemand.model.JpaServiceInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Web-Ide 서비스 관련 에러 Exception 클래스
 *
 * @author sjchoi
 * @since 2018.08.14
 */

@Repository
public interface JpaServiceInstanceRepository extends JpaRepository<JpaServiceInstance, String>{
    JpaServiceInstance findByOrganizationGuid(String organizationId);

    JpaServiceInstance findByVmInstanceIdAndOrganizationGuid(String vmInstanceId,String organizationId);

    boolean existsByOrganizationGuid(String organizationId);

    boolean existsByServiceInstanceId(String instanceID);

    JpaServiceInstance findByServiceInstanceId(String serviceInstanceId);

    List<JpaServiceInstance> findByUseYn(String use_yn);

    JpaServiceInstance findByDashboardUrl(String dashboardurl);

    JpaServiceInstance findByVmInstanceId(String vmInstanceId);

    boolean existsAllByVmInstanceId(String vmInstanceId);

    JpaServiceInstance findByServiceInstanceIdAndDashboardUrl(String serviceInstanceId, String dashboardUrl);

    void deleteByServiceInstanceId(String id);


}
