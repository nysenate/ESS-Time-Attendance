--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: ts; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA ts;


ALTER SCHEMA ts OWNER TO admin;

SET search_path = ts, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: timesheet; Type: TABLE; Schema: ts; Owner: admin; Tablespace: 
--

CREATE TABLE timesheet (
    timesheet_id numeric NOT NULL,
    emp_id numeric NOT NULL,
    t_original_user character(50) NOT NULL,
    t_update_user character(50) NOT NULL,
    t_original_date timestamp without time zone NOT NULL,
    t_update_date timestamp without time zone NOT NULL,
    status character(1) NOT NULL,
    ts_status_id character(2) NOT NULL,
    begin_date date NOT NULL,
    end_date date NOT NULL,
    remarks text,
    supervisor_id numeric NOT NULL,
    exc_details text,
    proc_date date
);


ALTER TABLE ts.timesheet OWNER TO admin;

--
-- Name: timesheet_by_day; Type: TABLE; Schema: ts; Owner: admin; Tablespace: 
--

CREATE TABLE timesheet_by_day (
    ts_day_id bigint NOT NULL,
    timesheet_id bigint NOT NULL,
    emp_id bigint NOT NULL,
    day_date date NOT NULL,
    work_hr numeric(4,2),
    travel_hr numeric(4,2),
    holiday_hr numeric(4,2),
    sick_emp_hr numeric(4,2),
    sick_family_hr numeric(4,2),
    misc_hr numeric(4,2),
    misc_type character(1) NOT NULL,
    t_original_user character(50) NOT NULL,
    t_update_user character(50) NOT NULL,
    t_original_date timestamp without time zone NOT NULL,
    t_update_date timestamp without time zone NOT NULL,
    status character(1) NOT NULL,
    emp_comment text,
    pay_type character(2),
    vacation_hr numeric(4,2),
    personal_hr numeric(4,2)
);


ALTER TABLE ts.timesheet_by_day OWNER TO admin;

--
-- Name: ts; Type: ACL; Schema: -; Owner: admin
--

REVOKE ALL ON SCHEMA ts FROM PUBLIC;


--
-- PostgreSQL database dump complete
--

