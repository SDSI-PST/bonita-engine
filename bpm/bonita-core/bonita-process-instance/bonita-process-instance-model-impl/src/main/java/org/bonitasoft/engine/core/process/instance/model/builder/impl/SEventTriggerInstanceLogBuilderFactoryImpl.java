/**
 * Copyright (C) 2012-2013 BonitaSoft S.A.
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
package org.bonitasoft.engine.core.process.instance.model.builder.impl;

import org.bonitasoft.engine.core.process.instance.model.builder.event.trigger.SEventTriggerInstanceLogBuilderFactory;
import org.bonitasoft.engine.queriablelogger.model.builder.impl.CRUDELogBuilderFactory;

/**
 * @author Elias Ricken de Medeiros
 * @author Matthieu Chaffotte
 */
public class SEventTriggerInstanceLogBuilderFactoryImpl extends CRUDELogBuilderFactory implements SEventTriggerInstanceLogBuilderFactory {

    public static final int EVENT_INSTANCE_INDEX = 0;

    public static final int EVENT_TRIGGER_INSTANCE_INDEX = 1;

    public static final String EVENT_INSTANCE_NAME = "numericIndex1";

    public static final String EVENT_TRIGGER_INSTANCE_NAME = "numericIndex2";

    @Override
    public String getObjectIdKey() {
        return EVENT_TRIGGER_INSTANCE_NAME;
    }

    @Override
    public String getEventInstanceIdKey() {
        return EVENT_INSTANCE_NAME;
    }

}
