package yearnlune.lab.cleanmongo.config;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.convert.DefaultTypeMapper;
import org.springframework.data.convert.TypeAliasAccessor;
import org.springframework.data.convert.TypeInformationMapper;
import org.springframework.data.mapping.Alias;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Project : clean-mongo-config
 * Created by IntelliJ IDEA
 * Author : DONGHWAN, KIM
 * DATE : 2020.03.11
 * DESCRIPTION :
 */

public class CleanMongoTypeMapper extends DefaultTypeMapper<Bson> implements org.springframework.data.mongodb.core.convert.MongoTypeMapper {

    public static final String DEFAULT_TYPE_KEY = "_class";

    private final TypeAliasAccessor<Bson> accessor;

    private final @Nullable
    String typeKey;

    public CleanMongoTypeMapper() {
        this(DEFAULT_TYPE_KEY);
    }

    public CleanMongoTypeMapper(@Nullable String typeKey) {
        this(typeKey, Arrays.asList(new CleanTypeInformationMapper()));
    }

    public CleanMongoTypeMapper(@Nullable String typeKey, List<? extends TypeInformationMapper> mappers) {
        this(typeKey, new CleanMongoTypeMapper.DocumentTypeAliasAccessor(typeKey), null, mappers);
    }

    public CleanMongoTypeMapper(@Nullable String typeKey, TypeAliasAccessor<Bson> accessor, org.springframework.data.mapping.context.MappingContext<? extends PersistentEntity<?, ?>, ?> mappingContext, List<? extends TypeInformationMapper> additionalMappers) {
        super(accessor, mappingContext, additionalMappers);
        this.typeKey = typeKey;
        this.accessor = accessor;
    }

    @Override
    public boolean isTypeKey(String key) {
        return typeKey != null && typeKey.equals(key);
    }

    @Override
    public void writeTypeRestrictions(org.bson.Document result, Set<Class<?>> restrictedTypes) {
        if (ObjectUtils.isEmpty(restrictedTypes)) {
            return;
        }

        BasicDBList restrictedMappedTypes = new BasicDBList();

        for (Class<?> restrictedType : restrictedTypes) {

            Alias typeAlias = getAliasFor(ClassTypeInformation.from(restrictedType));

            if (!ObjectUtils.nullSafeEquals(Alias.NONE, typeAlias) && typeAlias.isPresent()) {
                restrictedMappedTypes.add(typeAlias.getValue());
            }
        }

        accessor.writeTypeTo(result, new org.bson.Document("$in", restrictedMappedTypes));
    }

    public static final class DocumentTypeAliasAccessor implements TypeAliasAccessor<Bson> {

        private final @Nullable
        String typeKey;

        public DocumentTypeAliasAccessor(@Nullable String typeKey) {
            this.typeKey = typeKey;
        }

        public Alias readAliasFrom(Bson source) {
            if (source instanceof List) {
                return Alias.NONE;
            }

            if (source instanceof org.bson.Document) {
                return Alias.ofNullable(((Document) source).get(typeKey));
            } else if (source instanceof com.mongodb.DBObject) {
                return Alias.ofNullable(((com.mongodb.DBObject) source).get(typeKey));
            }

            throw new IllegalArgumentException("Cannot read alias from " + source.getClass());
        }

        public void writeTypeTo(Bson sink, Object alias) {
            if (typeKey != null) {

                if (sink instanceof Document) {
                    ((Document) sink).put(typeKey, alias);
                } else if (sink instanceof DBObject) {
                    ((DBObject) sink).put(typeKey, alias);
                }
            }
        }
    }
}
