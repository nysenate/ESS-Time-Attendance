package gov.nysenate.seta.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a unit test class as having well written/reproducible tests.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface ProperTest
{
}
