package model;

import org.openpaas.paasta.ondemand.model.DeploymentInstance;

import java.util.HashMap;
import java.util.Map;

public class DeploymentInstanceModel {

    public static DeploymentInstance getDeploymentInstance(){
        Map map = new HashMap<>();
        map.put("id","id");
        map.put("vm_cid","vm_cid");
        map.put("disk_cid","disk_cid");
        map.put("agent_id","agent_id");
        map.put("ips","ips");
        map.put("job_name","job_name");
        map.put("job_state","running");
        map.put("state","started");
        map.put("active","active");
        return new DeploymentInstance(map);
    }

    public static DeploymentInstance getDeploymentDetachedInstance(){
        Map map = new HashMap<>();
        map.put("id","id");
        map.put("vm_cid","vm_cid");
        map.put("disk_cid","disk_cid");
        map.put("agent_id","agent_id");
        map.put("ips","ips");
        map.put("job_name","job_name");
        map.put("job_state","running");
        map.put("state","detached");
        map.put("active","active");
        return new DeploymentInstance(map);
    }

    public static DeploymentInstance getDeploymentEmptyInstance(){
        Map map = new HashMap<>();
        map.put("id","id");
        map.put("vm_cid","vm_cid");
        map.put("disk_cid","disk_cid");
        map.put("agent_id","agent_id");
        map.put("ips","ips");
        map.put("job_name","job_name");
        map.put("job_state","job_state");
        map.put("state","state");
        map.put("active","active");
        return new DeploymentInstance(map);
    }
}
