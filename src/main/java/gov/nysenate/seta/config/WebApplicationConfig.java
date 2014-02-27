package gov.nysenate.seta.config;

import gov.nysenate.seta.web.CommonAttributeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Main configuration class that imports all the other config classes.
 */
@Configuration
@EnableWebMvc
@ComponentScan("gov.nysenate.seta")
@Import({PropertyConfig.class, SecurityConfig.class, DatabaseConfig.class})
public class WebApplicationConfig extends WebMvcConfigurerAdapter
{
    private static final Logger logger = LoggerFactory.getLogger(WebApplicationConfig.class);

    @Value("${resource.path}") private String resourcePath;
    @Value("${resource.location}") private String resourceLocation;

    /** Sets paths that should not be intercepted by a controller (e.g css/ js/). */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (resourcePath == null || resourceLocation == null) {
            logger.warn("Resource path/location for accessing public assets were not set!");
        }
        else {
            logger.info("Registering resource path {} for files under {}", resourcePath, resourceLocation);
            registry.addResourceHandler(resourcePath).addResourceLocations(resourceLocation);
        }
    }

    /**
     * This view resolver will map view names returned from the controllers to jsp files stored in the
     * configured 'prefix' path.
     */
    @Bean(name = "viewResolver")
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/view/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    /**
     * Filter implementation that sets commonly used request attributes for JSPs.
     */
    @Bean(name = "commonAttributeFilter")
    public CommonAttributeFilter commonAttributeFilter() {
        return new CommonAttributeFilter();
    }
}
