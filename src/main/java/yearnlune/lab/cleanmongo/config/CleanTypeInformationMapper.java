package yearnlune.lab.cleanmongo.config;

import org.springframework.data.mapping.Alias;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project : clean-mongo-config
 * Created by IntelliJ IDEA
 * Author : DONGHWAN, KIM
 * DATE : 2020.03.11
 * DESCRIPTION :
 */
public class CleanTypeInformationMapper implements org.springframework.data.convert.TypeInformationMapper {

    private static final char PACKAGE_SEPARATOR = '.';

    private static final char INNER_CLASS_SEPARATOR = '$';

    private final Map<String, Optional<ClassTypeInformation<?>>> CACHE = new ConcurrentHashMap<>();

    @Nullable
    @Override
    public org.springframework.data.util.TypeInformation<?> resolveTypeFrom(Alias alias) {
        return null;
    }

    private static final Map<String, Class<?>> commonClassCache = new HashMap<>(64);

    @Override
    public Alias createAliasFor(org.springframework.data.util.TypeInformation<?> type) {
        String[] classSplit = type.getType().getName().split("\\.");
        return Alias.of(classSplit[classSplit.length-1]);
    }

    private static Optional<ClassTypeInformation<?>> loadClass(String typeName) {
        try {
            Class<?> clazz = findClassByCustomShortName(typeName);

            return Optional.of(ClassTypeInformation.from(ClassUtils.forName(typeName, null)));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    private static Class<?> findClassByCustomShortName(String shortName) throws ClassNotFoundException, LinkageError {
        ClassLoader clToUse = ClassUtils.getDefaultClassLoader();

        try {
            return Class.forName(shortName, false, clToUse);
        }
        catch (ClassNotFoundException ex) {
            int lastDotIndex = shortName.lastIndexOf(PACKAGE_SEPARATOR);
            if (lastDotIndex != -1) {
                String innerClassName =
                        shortName.substring(0, lastDotIndex) + INNER_CLASS_SEPARATOR + shortName.substring(lastDotIndex + 1);
                try {
                    return Class.forName(innerClassName, false, clToUse);
                }
                catch (ClassNotFoundException ex2) {
                    // Swallow - let original exception get through
                }
            }
            throw ex;
        }
    }


}
