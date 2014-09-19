package gov.nysenate.seta.dao.base;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public abstract class BaseRowMapper<T> extends BaseMapper implements RowMapper<T> {


}
