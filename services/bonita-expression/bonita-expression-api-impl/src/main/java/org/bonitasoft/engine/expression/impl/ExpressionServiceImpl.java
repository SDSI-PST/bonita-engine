/**
 * Copyright (C) 2011, 2014 BonitaSoft S.A.
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
package org.bonitasoft.engine.expression.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.commons.LogUtil;
import org.bonitasoft.engine.expression.ExpressionExecutorStrategy;
import org.bonitasoft.engine.expression.ExpressionExecutorStrategyProvider;
import org.bonitasoft.engine.expression.ExpressionService;
import org.bonitasoft.engine.expression.exception.SExpressionDependencyMissingException;
import org.bonitasoft.engine.expression.exception.SExpressionEvaluationException;
import org.bonitasoft.engine.expression.exception.SExpressionTypeUnknownException;
import org.bonitasoft.engine.expression.exception.SInvalidExpressionException;
import org.bonitasoft.engine.expression.model.ExpressionKind;
import org.bonitasoft.engine.expression.model.SExpression;
import org.bonitasoft.engine.log.technical.TechnicalLogSeverity;
import org.bonitasoft.engine.log.technical.TechnicalLoggerService;

/**
 * @author Zhao na
 * @author Emmanuel Duchastenier
 * @author Baptiste Mesta
 * @author Celine Souchet
 */
public class ExpressionServiceImpl implements ExpressionService {

    private final Map<ExpressionKind, ExpressionExecutorStrategy> expressionExecutorsMap;

    private final TechnicalLoggerService logger;

    private boolean checkExpressionReturnType = false;

    public ExpressionServiceImpl(final ExpressionExecutorStrategyProvider expressionExecutorStrategyProvider, final TechnicalLoggerService logger,
            final boolean checkExpressionReturnType) {
        super();
        final List<ExpressionExecutorStrategy> expressionExecutors = expressionExecutorStrategyProvider.getExpressionExecutors();
        expressionExecutorsMap = new HashMap<ExpressionKind, ExpressionExecutorStrategy>(expressionExecutors.size());
        this.checkExpressionReturnType = checkExpressionReturnType;
        for (final ExpressionExecutorStrategy expressionExecutorStrategy : expressionExecutors) {
            expressionExecutorsMap.put(expressionExecutorStrategy.getExpressionKind(), expressionExecutorStrategy);
        }
        this.logger = logger;
    }

    @Override
    public Object evaluate(final SExpression expression, final Map<Integer, Object> resolvedExpressions) throws SExpressionTypeUnknownException,
            SExpressionEvaluationException, SExpressionDependencyMissingException, SInvalidExpressionException {
        return evaluate(expression, new HashMap<String, Object>(1), resolvedExpressions);
    }

    @Override
    public Object evaluate(final SExpression expression, Map<String, Object> dependencyValues, final Map<Integer, Object> resolvedExpressions)
            throws SExpressionTypeUnknownException, SExpressionEvaluationException, SExpressionDependencyMissingException, SInvalidExpressionException {
        final boolean isTraceEnable = logger.isLoggable(this.getClass(), TechnicalLogSeverity.TRACE);
        if (isTraceEnable) {
            logger.log(this.getClass(), TechnicalLogSeverity.TRACE, LogUtil.getLogBeforeMethod(this.getClass(), "evaluate"));
        }

        final ExpressionExecutorStrategy expressionExecutorStrategy = getStrategy(expression.getExpressionKind());
        validateExpression(expressionExecutorStrategy, expression);
        Object expressionResult = expressionExecutorStrategy.evaluate(expression, dependencyValues, resolvedExpressions);
        checkReturnType(expression, expressionResult);

        if (isTraceEnable) {
            logger.log(this.getClass(), TechnicalLogSeverity.TRACE, LogUtil.getLogAfterMethod(this.getClass(), "evaluate"));
        }
        return expressionResult;
    }

    private void validateExpression(final ExpressionExecutorStrategy expressionExecutorStrategy, final SExpression expression)
            throws SInvalidExpressionException {
        try {
            // this will throw exception if the expression is invalid
            expressionExecutorStrategy.validate(expression);
        } catch (final SInvalidExpressionException e) {
            if (logger.isLoggable(this.getClass(), TechnicalLogSeverity.TRACE)) {
                logger.log(this.getClass(), TechnicalLogSeverity.TRACE,
                        LogUtil.getLogOnExceptionMethod(this.getClass(), "evaluate", "Invalid Expression : " + expression.getContent()));
            }
            throw e;
        }
    }

    private ExpressionExecutorStrategy getStrategy(final ExpressionKind expressionKind) throws SExpressionTypeUnknownException {
        final ExpressionExecutorStrategy expressionExecutorStrategy = expressionExecutorsMap.get(expressionKind);
        if (expressionExecutorStrategy == null) {
            if (logger.isLoggable(this.getClass(), TechnicalLogSeverity.TRACE)) {
                logger.log(this.getClass(), TechnicalLogSeverity.TRACE,
                        LogUtil.getLogOnExceptionMethod(this.getClass(), "evaluate",
                                "Unable to find an executor for expression type " + expressionKind));
            }
            throw new SExpressionTypeUnknownException("Unable to find an executor for expression type " + expressionKind);
        }
        return expressionExecutorStrategy;
    }

    @Override
    public List<Object> evaluate(final ExpressionKind expressionKind, final List<SExpression> expressions, final Map<String, Object> dependencyValues,
            final Map<Integer, Object> resolvedExpressions) throws SExpressionTypeUnknownException, SExpressionEvaluationException,
            SExpressionDependencyMissingException {
        if (logger.isLoggable(this.getClass(), TechnicalLogSeverity.TRACE)) {
            logger.log(this.getClass(), TechnicalLogSeverity.TRACE, LogUtil.getLogBeforeMethod(this.getClass(), "evaluate"));
        }
        final ExpressionExecutorStrategy expressionExecutorStrategy = getStrategy(expressionKind);
        final List<Object> list = expressionExecutorStrategy.evaluate(expressions, dependencyValues, resolvedExpressions);
        if (list == null || list.size() != expressions.size()) {
            final String exceptionMessage = "Result list size " + (list == null ? 0 : list.size()) + " is different from expression list size "
                    + expressions.size();
            if (logger.isLoggable(this.getClass(), TechnicalLogSeverity.TRACE)) {
                logger.log(this.getClass(), TechnicalLogSeverity.TRACE, LogUtil.getLogOnExceptionMethod(this.getClass(), "evaluate", exceptionMessage));
            }
            throw new SExpressionEvaluationException(exceptionMessage, null);
        }
        for (int i = 0; i < list.size(); i++) {
            checkReturnType(expressions.get(i), list.get(i));
        }
        if (logger.isLoggable(this.getClass(), TechnicalLogSeverity.TRACE)) {
            logger.log(this.getClass(), TechnicalLogSeverity.TRACE, LogUtil.getLogAfterMethod(this.getClass(), "evaluate"));
        }
        return list;
    }

    /**
     * Check if the declared return type is compatible with the real Expression evaluation return type. If the result of the Expression evaluation is null, then
     * this method returns true.
     * 
     * @param expression
     *            the evaluated expression
     * @param result
     *            the expression result to check
     * @throws SInvalidExpressionException
     *             if the condition is not fulfilled, does nothing otherwise
     */
    private void checkReturnType(final SExpression expression, final Object result) throws SExpressionEvaluationException {
        if (mustCheckExpressionReturnType() && result != null && !result.getClass().getName().equals(expression.getReturnType())) {
            try {
                final Class<?> declaredReturnedType = Thread.currentThread().getContextClassLoader().loadClass(expression.getReturnType());
                final Class<?> evaluatedReturnedType = result.getClass();
                if (!(declaredReturnedType.isAssignableFrom(evaluatedReturnedType))) {
                    throw new SExpressionEvaluationException("Declared return type " + declaredReturnedType + " is not compatible with evaluated type "
                            + evaluatedReturnedType + " for expression " + expression.getName(), expression.getName());
                }
            } catch (final ClassNotFoundException e) {
                throw new SExpressionEvaluationException(
                        "Declared return type unknown : " + expression.getReturnType() + " for expression " + expression.getName(), e, expression.getName());
            }
        }
    }

    @Override
    public boolean mustCheckExpressionReturnType() {
        return checkExpressionReturnType;
    }

    @Override
    public boolean mustPutEvaluatedExpressionInContext(final ExpressionKind expressionKind) {
        return expressionExecutorsMap.get(expressionKind).mustPutEvaluatedExpressionInContext();
    }

}
