/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.bonitasoft.engine.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.bpm.process.ProcessActivationException;
import org.bonitasoft.engine.bpm.process.ProcessDefinitionNotFoundException;
import org.bonitasoft.engine.bpm.process.ProcessExecutionException;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.operation.Operation;

/**
 * Created by Vincent Elcrin
 * Date: 10/12/13
 * Time: 14:48
 */
public interface RepairAPI {

    public ProcessInstance startProcess(long startedBy,
                             long processDefinitionId,
                             List<String> activityNames,
                             final List<Operation> operations, 
                             final Map<String, Serializable> context) 
                                     throws ProcessDefinitionNotFoundException, ProcessActivationException, ProcessExecutionException;
}