/*
 *  Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 *
 */

package org.ballerinalang.stdlib.task.objects;

import org.ballerinalang.bre.Context;
import org.ballerinalang.stdlib.task.SchedulingException;
import org.ballerinalang.stdlib.task.utils.TaskIdGenerator;
import org.quartz.JobDataMap;

import java.util.HashMap;

import static org.ballerinalang.stdlib.task.utils.TaskConstants.TASK_CONTEXT;
import static org.ballerinalang.stdlib.task.utils.TaskConstants.TASK_SERVICE_WITH_PARAMETER;

/**
 * Abstract class which represents a ballerina task.
 */
public abstract class AbstractTask implements Task {

    protected String id = TaskIdGenerator.generate();
    HashMap<String, ServiceWithParameters> serviceMap;
    long maxRuns;

    /**
     * Constructor to create a task without a limited (maximum) number of runs.
     */
    AbstractTask() {
        this.serviceMap = new HashMap<>();
        this.maxRuns = -1;
    }

    /**
     * Constructor to create a task with limited (maximum) number of runs.
     *
     * @param maxRuns Maximum number of runs allowed.
     */
    AbstractTask(long maxRuns) throws SchedulingException {
        validateMaxRuns(maxRuns);
        this.serviceMap = new HashMap<>();
        this.maxRuns = maxRuns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addService(ServiceWithParameters service) {
        this.serviceMap.put(service.getName(), service);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeService(String serviceName) {
        this.serviceMap.remove(serviceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashMap<String, ServiceWithParameters> getServicesMap() {
        return this.serviceMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceWithParameters getService(String serviceName) {
        return this.serviceMap.get(serviceName);
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return this.id;
    }

    /**
     * Create a job data map using the context and the service.
     *
     * @param context Ballerina context of the Task.
     * @param serviceWithParameters <code>ServiceWithParameter</code> object related to the task.
     * @return JobDataMap consists of context and the <code>ServiceWithParameter</code> object.
     */
    JobDataMap getJobDataMapFromService(Context context, ServiceWithParameters serviceWithParameters) {
        JobDataMap jobData = new JobDataMap();
        jobData.put(TASK_CONTEXT, context);
        jobData.put(TASK_SERVICE_WITH_PARAMETER, serviceWithParameters);
        return jobData;
    }

    private void validateMaxRuns(long maxRuns) throws SchedulingException {
        if (maxRuns < 1) {
            throw new SchedulingException("Task noOfOccurrences should be a positive integer.");
        }
    }
}
