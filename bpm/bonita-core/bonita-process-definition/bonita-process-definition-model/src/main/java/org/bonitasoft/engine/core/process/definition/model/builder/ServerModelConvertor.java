/**
 * Copyright (C) 2012 BonitaSoft S.A.
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
package org.bonitasoft.engine.core.process.definition.model.builder;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.engine.bpm.businessdata.BusinessDataDefinition;
import org.bonitasoft.engine.bpm.data.DataDefinition;
import org.bonitasoft.engine.bpm.data.TextDataDefinition;
import org.bonitasoft.engine.bpm.data.XMLDataDefinition;
import org.bonitasoft.engine.builder.BuilderFactory;
import org.bonitasoft.engine.core.operation.model.SOperation;
import org.bonitasoft.engine.core.operation.model.SOperatorType;
import org.bonitasoft.engine.core.operation.model.builder.SLeftOperandBuilderFactory;
import org.bonitasoft.engine.core.operation.model.builder.SOperationBuilderFactory;
import org.bonitasoft.engine.core.process.definition.model.SBusinessDataDefinition;
import org.bonitasoft.engine.data.definition.model.SDataDefinition;
import org.bonitasoft.engine.data.definition.model.builder.SDataDefinitionBuilder;
import org.bonitasoft.engine.data.definition.model.builder.SDataDefinitionBuilderFactory;
import org.bonitasoft.engine.data.definition.model.builder.SXMLDataDefinitionBuilder;
import org.bonitasoft.engine.data.definition.model.builder.SXMLDataDefinitionBuilderFactory;
import org.bonitasoft.engine.expression.Expression;
import org.bonitasoft.engine.expression.exception.SInvalidExpressionException;
import org.bonitasoft.engine.expression.model.SExpression;
import org.bonitasoft.engine.expression.model.builder.SExpressionBuilderFactory;
import org.bonitasoft.engine.operation.Operation;

/**
 * @author Baptiste Mesta
 */
public class ServerModelConvertor {

    public static SExpression convertExpression(final Expression value) {
        if (value == null) {
            return null;
        } else {
            final ArrayList<SExpression> dependencies = new ArrayList<SExpression>();
            for (final Expression expression : value.getDependencies()) {
                dependencies.add(convertExpression(expression));
            }
            try {
                return BuilderFactory.get(SExpressionBuilderFactory.class).createNewInstance().setName(value.getName()).setContent(value.getContent())
                        .setExpressionType(value.getExpressionType()).setInterpreter(value.getInterpreter()).setReturnType(value.getReturnType())
                        .setDependencies(dependencies).done();
            } catch (final SInvalidExpressionException e) {
                throw new IllegalArgumentException("Error building SExpression", e);
            }
        }
    }

    public static SOperation convertOperation(final Operation operation) {
        return BuilderFactory.get(SOperationBuilderFactory.class).createNewInstance().setOperator(operation.getOperator())
                .setType(SOperatorType.valueOf(operation.getType().name()))
                .setRightOperand(ServerModelConvertor.convertExpression(operation.getRightOperand()))
                .setLeftOperand(BuilderFactory.get(SLeftOperandBuilderFactory.class).createNewInstance().setName(operation.getLeftOperand().getName()).done())
                .done();
    }

    public static List<SOperation> convertOperations(final List<Operation> operations) {
        final List<SOperation> sOperations = new ArrayList<SOperation>(operations.size());
        for (final Operation operation : operations) {
            sOperations.add(convertOperation(operation));
        }
        return sOperations;
    }

    public static SDataDefinition convertDataDefinition(final DataDefinition dataDefinition) {
        if (dataDefinition instanceof XMLDataDefinition) {
            final XMLDataDefinition xmlDataDef = (XMLDataDefinition) dataDefinition;
            final SXMLDataDefinitionBuilderFactory fact = BuilderFactory.get(SXMLDataDefinitionBuilderFactory.class);
            final SXMLDataDefinitionBuilder builder = fact.createNewXMLData(dataDefinition.getName()).setElement(xmlDataDef.getElement())
                    .setNamespace(xmlDataDef.getNamespace());
            builder.setDefaultValue(ServerModelConvertor.convertExpression(dataDefinition.getDefaultValueExpression()));
            builder.setDescription(dataDefinition.getDescription());
            builder.setTransient(dataDefinition.isTransientData());
            return builder.done();
        } else {
            final SDataDefinitionBuilderFactory fact = BuilderFactory.get(SDataDefinitionBuilderFactory.class);
            SDataDefinitionBuilder builder = null;
            if (dataDefinition instanceof TextDataDefinition) {
                final TextDataDefinition textDataDefinition = (TextDataDefinition) dataDefinition;
                builder = fact.createNewTextData(dataDefinition.getName()).setAsLongText(textDataDefinition.isLongText());
            } else {
                builder = fact.createNewInstance(dataDefinition.getName(), dataDefinition.getClassName());
            }
            builder.setDefaultValue(ServerModelConvertor.convertExpression(dataDefinition.getDefaultValueExpression()));
            builder.setDescription(dataDefinition.getDescription());
            builder.setTransient(dataDefinition.isTransientData());
            return builder.done();
        }
    }

    public static SBusinessDataDefinition convertBusinessDataDefinition(final BusinessDataDefinition businessDataDefinition) {
        final SBusinessDataDefinitionBuilder builder = getSBusinessDataDefinitionBuilder(businessDataDefinition);
        builder.setDefaultValue(ServerModelConvertor.convertExpression(businessDataDefinition.getDefaultValueExpression()));
        builder.setDescription(businessDataDefinition.getDescription());
        return builder.done();
    }

    protected static SBusinessDataDefinitionBuilder getSBusinessDataDefinitionBuilder(final BusinessDataDefinition businessDataDefinition) {
        final SBusinessDataDefinitionBuilderFactory fact = BuilderFactory.get(SBusinessDataDefinitionBuilderFactory.class);
        final SBusinessDataDefinitionBuilder builder = fact.createNewInstance(businessDataDefinition.getName(), businessDataDefinition.getClassName());
        return builder;
    }
}
