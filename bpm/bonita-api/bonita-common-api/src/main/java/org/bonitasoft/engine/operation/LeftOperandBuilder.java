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
package org.bonitasoft.engine.operation;

import org.bonitasoft.engine.operation.impl.LeftOperandImpl;

/**
 * Builder for <code>LeftOperand</code>. <code>LeftOperand</code> is part of the <code>Operation</code> framework.
 * A <code>LeftOperand</code> have a name, which is a reference to a data / variable, or a document.
 * The value of the reference is evaluated at runtime, according to the Operation context.
 * 
 * @author Zhang Bole
 * @author Emmanuel Duchastenier
 * @author Baptiste Mesta
 * @see Operation
 * @see OperationBuilder
 */
public class LeftOperandBuilder {

    private LeftOperandImpl leftOperand;

    /**
     * Initiate the building of a new <code>LeftOperand</code>. The <code>LeftOperand</code> building will be complete when calling the {@link #done()} method.
     * 
     * @return this builder itself, so that calls the various exposed methods can be chained.
     */
    public LeftOperandBuilder createNewInstance() {
        leftOperand = new LeftOperandImpl();
        return this;
    }

    public LeftOperandBuilder createNewInstance(final String name) {
        leftOperand = new LeftOperandImpl();
        leftOperand.setName(name);
        return this;
    }

    /**
     * Sets the name of this <code>LeftOperand</code>. Can be a reference to a data / variable, or to a document.
     * 
     * @param name
     *            the name of the reference that will be set by this operation execution
     * @return this builder itself, so that calls the various exposed methods can be chained.
     */
    public LeftOperandBuilder setName(final String name) {
        leftOperand.setName(name);
        return this;
    }

    /**
     * @param external
     * @return this builder itself, so that calls the various exposed methods can be chained.
     */
    public LeftOperandBuilder setExternal(final boolean external) {
        leftOperand.setExternal(external);
        return this;
    }

    public LeftOperand done() {
        return leftOperand;
    }

    public LeftOperand createSearchIndexLeftOperand(final int index) {
        return new LeftOperandBuilder().createNewInstance(String.valueOf(index)).done();
    }

    public LeftOperand createDataLeftOperand(final String dataName, final boolean external) {
        return new LeftOperandBuilder().createNewInstance(dataName).setExternal(external).done();
    }

    /**
     * creates a <code>LeftOperand</code> object to set a data, with a default value of external to false (data will be updated in Bonita system)
     * 
     * @param dataName
     *            the name of the data to set
     * @return the newly created <code>LeftOperand</code> object
     */
    public LeftOperand createDataLeftOperand(final String dataName) {
        return new LeftOperandBuilder().createNewInstance(dataName).setExternal(false).done();
    }

}
