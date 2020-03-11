package yearnlune.lab.cleanmongo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * Project : clean-mongo-config
 * Created by IntelliJ IDEA
 * Author : DONGHWAN, KIM
 * DATE : 2020.03.11
 * DESCRIPTION :
 */

public class MongoConfig {
    @Autowired
    private MongoDbFactory mongoDbFactory;

    @Autowired
    private MongoMappingContext mongoMappingContext;


}
