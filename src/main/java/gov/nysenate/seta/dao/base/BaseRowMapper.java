package gov.nysenate.seta.dao.base;

import org.springframework.jdbc.core.RowMapper;

public abstract class BaseRowMapper<T> extends BaseMapper implements RowMapper<T> {


}
