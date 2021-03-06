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
package org.bonitasoft.engine.commons;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.commons.exceptions.SReflectException;

/**
 * @author Baptiste Mesta
 * @author Matthieu Chaffotte
 */
public class ClassReflector {

    private static final Map<String, Method> methods;

    static {
        methods = new HashMap<String, Method>();
    }

    public static Collection<Method> getAccessibleGetters(final Class<?> clazz) {
        final Collection<Method> methods = new HashSet<Method>();
        for (final Method method : clazz.getMethods()) {
            if (!Void.class.equals(method.getReturnType()) && method.getParameterTypes().length == 0 && method.getName().startsWith("get")) {
                methods.add(method);
            }
        }
        return methods;
    }

    public static <T> Class<T> getClass(final Class<T> clazz, final String className) throws SReflectException {
        try {
            return (Class<T>) Class.forName(className);
        } catch (final Exception e) {
            throw new SReflectException(e);
        }
    }

    public static <T> T getObject(final Class<T> clazz, final String className) throws SReflectException {
        try {
            return getClass(clazz, className).newInstance();
        } catch (final Exception e) {
            throw new SReflectException(e);
        }
    }

    public static <T> Constructor<T> getConstructor(final Class<T> clazz, final Class<?>... parameterTypes) throws SReflectException {
        try {
            return clazz.getConstructor(parameterTypes);
        } catch (final Exception e) {
            throw new SReflectException(e);
        }
    }

    public static <T> Constructor<T> getConstructor(final Class<T> clazz, final String className, final Class<?>... parameterTypes) throws SReflectException {
        try {
            return getClass(clazz, className).getConstructor(parameterTypes);
        } catch (final Exception e) {
            throw new SReflectException(e);
        }
    }

    public static <T> T getInstance(final Constructor<T> constructor, final Object... parameters) throws SReflectException {
        try {
            return constructor.newInstance(parameters);
        } catch (final Exception e) {
            throw new SReflectException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeGetter(final Object entity, final String getterName) throws SReflectException {
        try {
            final Method getter = getMethod(entity.getClass(), getterName);
            return (T) getter.invoke(entity, (Object[]) null);
        } catch (final Exception e) {
            throw new SReflectException(e);
        }
    }

    public static void invokeSetter(final Object entity, final String setterName, final Class<?> parameterType, final Object parameterValue)
            throws SReflectException {
        try {
            final Method setter = getMethod(entity.getClass(), setterName, new Class[] { parameterType });
            setter.invoke(entity, new Object[] { parameterValue });
        } catch (final Exception e) {
            throw new SReflectException(e);
        }
    }

    public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) throws NoSuchMethodException {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(clazz.hashCode());
        stringBuilder.append(':');
        stringBuilder.append(clazz.getName());
        stringBuilder.append('.');
        stringBuilder.append(methodName);
        stringBuilder.append('(');
        if (parameterTypes != null) {
            for (final Class<?> class1 : parameterTypes) {
                stringBuilder.append(class1.getName());
                stringBuilder.append(',');
            }
        }
        stringBuilder.append(')');
        final String key = stringBuilder.toString();
        if (!methods.containsKey(key)) {
            methods.put(key, clazz.getMethod(methodName, parameterTypes));
        }
        return methods.get(key);
    }

    public static Method getMethodByName(final Class<?> clazz, final String methodName) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(clazz.getName());
        stringBuilder.append('.');
        stringBuilder.append(methodName);
        final String key = stringBuilder.toString();
        if (!methods.containsKey(key)) {
            for (final Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName)) {
                    methods.put(key, method);
                }
            }
        }
        return methods.get(key);
    }

    public static void invokeMethodByName(final Object entity, final String methodName, final Object... parameterValues) throws SReflectException {
        final Class<?> clazz = entity.getClass();
        // no check on parameters
        final Method methodToInvoke = getMethodByName(clazz, methodName);
        if (methodToInvoke == null) {
            throw new SReflectException("unable to find a method with name '" + methodName + "' within class " + clazz.getName());
        }
        try {
            methodToInvoke.invoke(entity, parameterValues);
        } catch (final Exception e) {
            throw new SReflectException(e);
        }
    }

    public static Object invokeMethod(final Object entity, final String methodName, final Class<?> parameterType, final Object parameterValue)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Method method = getMethod(entity.getClass(), methodName, parameterType);
        return method.invoke(entity, parameterValue);
    }

    public static Object invokeMethod(final Object entity, final String methodName, final Class<?>[] parameterType, final Object[] parameterValue)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Method method = getMethod(entity.getClass(), methodName, parameterType);
        return method.invoke(entity, parameterValue);
    }

    private static boolean isWrapped(final Class<?> a, final Class<?> b) {
        return a.equals(int.class) && b.equals(Integer.class) || a.equals(double.class) && b.equals(Double.class) || a.equals(boolean.class)
                && b.equals(Boolean.class) || a.equals(char.class) && b.equals(Character.class) || a.equals(long.class) && b.equals(Long.class)
                || a.equals(short.class) && b.equals(Short.class) || a.equals(float.class) && b.equals(Float.class) || a.equals(byte.class)
                && b.equals(Byte.class);
    }

    public static Method getCompatibleMethod(final Class<?> clazz, final String methodName, final Class<?>... paramTypes) throws SReflectException {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (final Exception e) {
            if (paramTypes != null) {
                final Method[] methods = clazz.getMethods();
                for (final Method method : methods) {
                    if (methodName.equals(method.getName())) {
                        final Class<?>[] types = method.getParameterTypes();
                        boolean check = true;
                        for (int i = 0; i < types.length; i++) {
                            if (!(types[i].isAssignableFrom(paramTypes[i]) || paramTypes[i].isAssignableFrom(types[i]) || isWrapped(types[i], paramTypes[i]))) {
                                check = false;
                                break;
                            }
                        }
                        if (check) {
                            return method;
                        }
                    }
                }
            }
            throw new SReflectException(e);
        }
    }

    public static Type getGetterReturnType(final Class<?> classConnector, final String getterName) throws SReflectException {
        Method m;
        try {
            m = getMethod(classConnector, getterName, (Class<?>) null);
        } catch (final Exception e) {
            throw new SReflectException(e);
        }
        return m.getGenericReturnType();
    }

    public static Method[] getDeclaredSetters(final Class<?> clazz) {
        final List<Method> setters = new ArrayList<Method>();
        final Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            if (isASetterMethod(method)) {
                setters.add(method);
            }
        }
        return setters.toArray(new Method[setters.size()]);
    }

    public static Method[] getDeclaredGetters(final Class<?> clazz) {
        final List<Method> getters = new ArrayList<Method>();
        final Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            if (isAGetterMethod(method)) {
                getters.add(method);
            }
        }
        return getters.toArray(new Method[getters.size()]);
    }

    public static boolean isAGetterMethod(final Method method) {
        final String methodName = method.getName();
        return (methodName.startsWith("get") || methodName.startsWith("is")) && method.getParameterTypes().length == 0
                && !Void.class.equals(method.getReturnType());
    }

    public static boolean isASetterMethod(final Method method) {
        final String methodName = method.getName();
        return methodName.startsWith("set") && "void".equals(method.getReturnType().toString()) && method.getParameterTypes().length == 1;
    }

    public static String getGetterName(final String fieldName) {
        final StringBuilder builder = new StringBuilder("get");
        builder.append(String.valueOf(fieldName.charAt(0)).toUpperCase());
        builder.append(fieldName.substring(1));
        return builder.toString();
    }

    public static String getFieldName(final String methodName) {
        int cut = 4;
        if (methodName.startsWith("is")) {
            cut = 3;
        }
        if (methodName.length() < cut) {
            return "";
        }
        final String end = methodName.substring(cut);
        final char c = methodName.charAt(cut - 1);
        final String begin = String.valueOf(c).toLowerCase();
        return begin.concat(end);
    }

}
