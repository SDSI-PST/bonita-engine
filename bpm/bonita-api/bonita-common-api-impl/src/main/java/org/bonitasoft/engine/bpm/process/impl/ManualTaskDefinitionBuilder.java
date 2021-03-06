/**
 * Copyright (C) 2011-2013 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.bpm.process.impl;

import org.bonitasoft.engine.bpm.flownode.impl.FlowElementContainerDefinitionImpl;
import org.bonitasoft.engine.bpm.flownode.impl.HumanTaskDefinitionImpl;
import org.bonitasoft.engine.bpm.flownode.impl.ManualTaskDefinitionImpl;


/**
 * @author Baptiste Mesta
 * @author Matthieu Chaffotte
 * @author Feng Hui
 */
public class ManualTaskDefinitionBuilder extends ActivityDefinitionBuilder {

    public ManualTaskDefinitionBuilder(final ProcessDefinitionBuilder processDefinitionBuilder, final FlowElementContainerDefinitionImpl container,
            final String name, final String actorName) {
        super(container, processDefinitionBuilder, getManualTask(name, actorName, container));
    }

    private static ManualTaskDefinitionImpl getManualTask(final String name, final String actorName, final FlowElementContainerDefinitionImpl container) {
        return new ManualTaskDefinitionImpl(name, actorName);
    }

    public UserFilterDefinitionBuilder addUserFilter(final String name, final String userFilterId, final String version) {
        return new UserFilterDefinitionBuilder(getProcessBuilder(), getContainer(), name, userFilterId, version, (HumanTaskDefinitionImpl) getActivity());
    }

    public ManualTaskDefinitionBuilder addExpectedDuration(final long time) {
        ((ManualTaskDefinitionImpl) getActivity()).setExpectedDuration(time);
        return this;
    }

    public ManualTaskDefinitionBuilder addPriority(final String priority) {
        ((ManualTaskDefinitionImpl) getActivity()).setPriority(priority);
        return this;
    }

}
