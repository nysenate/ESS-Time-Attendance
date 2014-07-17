package gov.nysenate.seta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Configures access to the app.properties config file through @Value annotations.
 */
@Configuration
public class PropertyConfig
{
    public static final String PROPERTY_FILENAME = "app.properties";
    public static final String TEST_PROPERTY_FILENAME = "test.app.properties";

    /**
     * This instance is necessary for Spring to load up the property file and allow access to
     * it through the @Value(${propertyName}) annotation. Also note that this bean must be static
     * in order to work properly with current Spring behavior.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        Resource[] resources = new ClassPathResource[] { new ClassPathResource(PROPERTY_FILENAME),
                                                         new ClassPathResource(TEST_PROPERTY_FILENAME) };
        pspc.setLocations(resources);
        pspc.setIgnoreUnresolvablePlaceholders(true);
        return pspc;
    }
}
