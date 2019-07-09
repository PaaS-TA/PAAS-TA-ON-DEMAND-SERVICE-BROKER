package org.openpaas.paasta.ondemand.repo;

import org.openpaas.paasta.ondemand.model.JpaServiceInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaServiceInstanceRepository extends JpaRepository<JpaServiceInstance, String> {

    List<JpaServiceInstance> findAllByOrganizationGuid(String organizationId);

    List<JpaServiceInstance> findAllBySpaceGuid(String spaceId);

    JpaServiceInstance findByVmInstanceIdAndOrganizationGuid(String vmInstanceId, String organizationId);

    boolean existsByOrganizationGuid(String organizationId);

    boolean existsByServiceInstanceId(String instanceID);

    JpaServiceInstance findByServiceInstanceId(String serviceInstanceId);

    JpaServiceInstance findByDashboardUrl(String dashboardurl);

    JpaServiceInstance findByVmInstanceId(String vmInstanceId);

    boolean existsAllByVmInstanceId(String vmInstanceId);

    boolean existsAllByDashboardUrl(String dashboardUrl);

    JpaServiceInstance findByServiceInstanceIdAndDashboardUrl(String serviceInstanceId, String dashboardUrl);

    void deleteByServiceInstanceId(String id);


}
