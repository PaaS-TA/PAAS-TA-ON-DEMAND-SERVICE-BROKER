package org.openpaas.paasta.ondemand.service.impl;


import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.openpaas.paasta.bosh.director.BoshDirector;
import org.openpaas.paasta.ondemand.model.DeploymentInstance;
import org.openpaas.paasta.ondemand.model.DeploymentLock;
import org.openpaas.paasta.ondemand.model.JpaServiceInstance;
import org.openpaas.paasta.ondemand.repo.JpaServiceInstanceRepository;
import org.openpaas.servicebroker.model.CreateServiceInstanceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OnDemandDeploymentService {

    private static final Logger logger = LoggerFactory.getLogger(OnDemandDeploymentService.class);

    private static boolean deployment_job = true;

    private BoshDirector boshDirector;

    @Autowired
    JpaServiceInstanceRepository jpaServiceInstanceRepository;

    @Autowired
    public void setBoshDirector(BoshDirector boshDirector) {
        this.boshDirector = boshDirector;
    }


    public List<DeploymentInstance> getVmInstance(String deployment_name, String instance_name) {
        try {
            String tasks = boshDirector.getListDetailOfInstances(deployment_name);
            List<DeploymentInstance> deploymentInstances = new ArrayList<DeploymentInstance>();
                Thread.sleep(2000);
                List<Map> results = boshDirector.getResultRetrieveTasksLog(tasks);
                logger.info(results.toString());
                for (Map map : results) {
                    logger.info(map.toString());
                    if (map.get("job_name").equals(instance_name)) {
                        DeploymentInstance deploymentInstance = new DeploymentInstance(map);
                        deploymentInstances.add(deploymentInstance);
                    }
                }
            return deploymentInstances;
        }catch (Exception e){
           logger.info(e.getMessage());
            return null;
        }
    }

    public boolean getLock(String deployment_name){
        try{
            String locks = boshDirector.getListLocks();
            try {
                JSONArray jsonArray = new JSONArray(locks);
                for(int i = 0; i< jsonArray.length(); i++){
                    String json = jsonArray.get(i).toString();
                    DeploymentLock dataJson= new Gson().fromJson(json, DeploymentLock.class);
                    if(dataJson.resuource[0].equals(deployment_name)){
                        return true;
                    }
                }
            } catch (JSONException e) {
                return false;
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return false;
    }

    public void updateInstanceState(String deployment_name, String instance_name, String instance_id, String type){
        try {
            if(deployment_job) {
                boshDirector.updateInstanceState(deployment_name, instance_name, instance_id, type);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    public boolean runningTask(String deployment_name){
        try {
                List<Map> deployTask = boshDirector.getListRunningTasks();
                List<Map> running_deployTask = deployTask.stream().filter(r -> r.get("deployment").equals(deployment_name)).collect(Collectors.toList());
                if(running_deployTask.isEmpty()){
                    logger.info("IsEmpty");
                    return true;
                }
                   return false;
        }catch (Exception e){
            logger.error(e.getMessage());
            return false;
        }
    }

    public String getTaskID(String deployment_name){
        try {
            List<Map> deployTask = boshDirector.getListRunningTasks();
            List<Map> running_deployTask = deployTask.stream().filter(r -> r.get("deployment").equals(deployment_name)).collect(Collectors.toList());
            if(running_deployTask.isEmpty()) {
                return null;
            }
            logger.info("비어있지 않아요");
            return running_deployTask.get(0).get("id").toString();
        }catch (Exception e){
            logger.error(e.getMessage());
            return null;
        }
    }

    public String getStartInstanceIPS(String taks_id, String instance_name, String instance_id){
        try{
            return boshDirector.getStartVMIPS(taks_id, instance_name, instance_id);
        }
        catch (Exception e){

        }
        return null;
    }

    public void createInstance(String deployment_name, String instance_name) throws Exception {
       boshDirector.deploy(deployment_name, instance_name);
    }

    public String getUpdateInstanceIPS(String task_id) {
        try {
            return boshDirector.getUpdateVMIPS(task_id);
        }catch (Exception e){
            return null;
        }
    }

}

