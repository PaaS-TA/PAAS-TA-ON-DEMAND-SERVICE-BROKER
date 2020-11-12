package org.openpaas.paasta.ondemand.service.impl;


import com.google.gson.Gson;
import org.json.JSONArray;
import org.openpaas.paasta.bosh.director.BoshDirector;
import org.openpaas.paasta.ondemand.model.DeploymentInstance;
import org.openpaas.paasta.ondemand.model.DeploymentLock;
import org.openpaas.paasta.ondemand.model.JpaServiceInstance;
import org.openpaas.paasta.ondemand.repo.JpaServiceInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OnDemandDeploymentService {

    private static final Logger logger = LoggerFactory.getLogger(OnDemandDeploymentService.class);

    private BoshDirector boshDirector;

    @Autowired
    JpaServiceInstanceRepository jpaServiceInstanceRepository;

    @Autowired
    public void setBoshDirector(BoshDirector boshDirector) {
        this.boshDirector = boshDirector;
    }


    public List<DeploymentInstance> getVmInstance(String deployment_name, String instance_name) {
        try {
            logger.info("bosh    version : " + boshDirector.getBosh_version());
            String tasks = boshDirector.getListDetailOfInstances(deployment_name);
            List<DeploymentInstance> deploymentInstances = new ArrayList<DeploymentInstance>();
            Thread.sleep(2000);
            //logger.info("before    sleep : S");
            //Thread.sleep(2000);
            //logger.info("after     sleep : E" );
            //logger.info("bosh      tasks : " + tasks);

            //List<Map> results = boshDirector.getResultRetrieveTasksLog(tasks);
            List<Map> results = null;
            if (StringUtils.isEmpty(boshDirector.getBosh_version()) || "2700100".compareTo(boshDirector.getBosh_version()) >= 0) {
                results = boshDirector.getResultRetrieveTasksLog(tasks);
            }else {
                results = boshDirector.getResultRetrieveTasksLogv271(tasks);
            }

            for (Map map : results) {
                if (map.get("job_name").equals(instance_name)) {
                    DeploymentInstance deploymentInstance = new DeploymentInstance(map);
                    deploymentInstances.add(deploymentInstance);
                }
            }
            return deploymentInstances;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean getLock(String deployment_name) {
        try {
            String locks = boshDirector.getListLocks();
                JSONArray jsonArray = new JSONArray(locks);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String json = jsonArray.get(i).toString();
                    DeploymentLock dataJson = new Gson().fromJson(json, DeploymentLock.class);
                    if (dataJson.resuource[0].equals(deployment_name)) {
                        return true;
                    }
                }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
        return false;
    }

    public void updateInstanceState(String deployment_name, String instance_name, String instance_id, String type) {
        try {
                boshDirector.updateInstanceState(deployment_name, instance_name, instance_id, type);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public boolean runningTask(String deployment_name, JpaServiceInstance instance) {
        try {
            List<Map> deployTask = boshDirector.getListRunningTasks();
            List<Map> running_deployTask = deployTask.stream().filter(r -> r.get("deployment").equals(deployment_name)).collect(Collectors.toList());
            if (running_deployTask.isEmpty()) {
                return true;
            } else {
                if (instance.getTaskId() == null) {
                    instance.setTaskId(running_deployTask.get(0).get("id").toString());
                    jpaServiceInstanceRepository.save(instance);
                }
                running_deployTask = running_deployTask.stream().filter(r -> r.get("id").toString().equals(instance.getTaskId())).collect(Collectors.toList());
                if (running_deployTask.isEmpty()) {
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public String getTaskID(String deployment_name) {
        try {
            List<Map> deployTask = boshDirector.getListRunningTasks();
            List<Map> running_deployTask = deployTask.stream().filter(r -> r.get("deployment").equals(deployment_name)).collect(Collectors.toList());
            if (running_deployTask.isEmpty()) {
                return null;
            }
            return running_deployTask.get(0).get("id").toString();
        } catch (Exception e) {
            return null;
        }
    }

    public String getStartInstanceIPS(String taks_id, String instance_name, String instance_id) {
        try {
            return boshDirector.getStartVMIPS(taks_id, instance_name, instance_id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public void createInstance(String deployment_name, String instance_name) throws Exception {
        try {
            boshDirector.deploy(deployment_name, instance_name);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public String getUpdateInstanceIPS(String task_id) {
        try {
            return boshDirector.getUpdateVMIPS(task_id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public String getUpdateVMInstanceID(String task_id, String instance_name) {
        try {
            return boshDirector.getUpdateVMInstance(task_id, instance_name);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

}

