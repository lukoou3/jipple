package com.jipple.util;

import java.util.Random;

/**
 * Utility class for class loading operations.
 */
public class JippleClassUtils {
    
    private static final Random random = new Random();

    /**
     * Gets the class loader for this class.
     * 
     * @return the class loader
     */
    public static ClassLoader getJippleClassLoader() {
        return JippleClassUtils.class.getClassLoader();
    }
    
    /**
     * Gets the context class loader or falls back to Jipple class loader.
     * 
     * @return the context class loader or Jipple class loader
     */
    public static ClassLoader getContextOrJippleClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return contextClassLoader != null ? contextClassLoader : getJippleClassLoader();
    }
    
    /**
     * Preferred alternative to Class.forName(className), as well as
     * Class.forName(className, initialize, loader) with current thread's ContextClassLoader.
     * 
     * @param className the fully qualified name of the desired class
     * @param <C> the type of the class
     * @return the Class object for the class with the specified name
     * @throws ClassNotFoundException if the class cannot be located
     */
    public static <C> Class<C> classForName(String className) throws ClassNotFoundException {
        return classForName(className, true, false);
    }
    
    /**
     * Preferred alternative to Class.forName(className), as well as
     * Class.forName(className, initialize, loader) with current thread's ContextClassLoader.
     * 
     * @param className the fully qualified name of the desired class
     * @param initialize if true the class will be initialized
     * @param <C> the type of the class
     * @return the Class object for the class with the specified name
     * @throws ClassNotFoundException if the class cannot be located
     */
    public static <C> Class<C> classForName(String className, boolean initialize) throws ClassNotFoundException {
        return classForName(className, initialize, false);
    }
    
    /**
     * Preferred alternative to Class.forName(className), as well as
     * Class.forName(className, initialize, loader) with current thread's ContextClassLoader.
     * 
     * @param className the fully qualified name of the desired class
     * @param initialize if true the class will be initialized
     * @param noJippleClassLoader if true, use thread's context class loader instead of Jipple class loader
     * @param <C> the type of the class
     * @return the Class object for the class with the specified name
     * @throws ClassNotFoundException if the class cannot be located
     */
    @SuppressWarnings("unchecked")
    public static <C> Class<C> classForName(String className, boolean initialize, boolean noJippleClassLoader)
            throws ClassNotFoundException {
        if (!noJippleClassLoader) {
            return (Class<C>) Class.forName(className, initialize, getContextOrJippleClassLoader());
        } else {
            return (Class<C>) Class.forName(className, initialize, 
                    Thread.currentThread().getContextClassLoader());
        }
    }
    
    /**
     * Determines whether the provided class is loadable in the current thread.
     * 
     * @param clazz the fully qualified name of the class
     * @return true if the class is loadable, false otherwise
     */
    public static boolean classIsLoadable(String clazz) {
        try {
            classForName(clazz, false);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Returns true if and only if the underlying class is a member class.
     *
     * Note: jdk8u throws a "Malformed class name" error if a given class is a deeply-nested
     * inner class (See SPARK-34607 for details). This issue has already been fixed in jdk9+, so
     * we can remove this helper method safely if we drop the support of jdk8u.
     * 
     * @param cls the class to check
     * @return true if the class is a member class, false otherwise
     */
    public static boolean isMemberClass(Class<?> cls) {
        try {
            return cls.isMemberClass();
        } catch (InternalError e) {
            // We emulate jdk8u `Class.isMemberClass` below:
            //   public boolean isMemberClass() {
            //     return getSimpleBinaryName() != null && !isLocalOrAnonymousClass();
            //   }
            // `getSimpleBinaryName()` returns null if a given class is a top-level class,
            // so we replace it with `cls.getEnclosingClass != null`. The second condition checks
            // if a given class is not a local or an anonymous class, so we replace it with
            // `cls.getEnclosingMethod == null` because `cls.getEnclosingMethod()` return a value
            // only in either case (JVM Spec 4.8.6).
            //
            // Note: The newer jdk evaluates `!isLocalOrAnonymousClass()` first,
            // we reorder the conditions to follow it.
            return cls.getEnclosingMethod() == null && cls.getEnclosingClass() != null;
        }
    }
}

