--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: ts; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA ts;


ALTER SCHEMA ts OWNER TO postgres;

--
-- Name: SCHEMA ts; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA ts IS 'Timesheet Schema';


SET search_path = ts, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: sync_check; Type: TABLE; Schema: ts; Owner: postgres; Tablespace:
--

CREATE TABLE sync_check (
    check_id integer NOT NULL,
    data_type character(20),
    data_id numeric,
    data_date timestamp without time zone,
    data_side character(20)
);


ALTER TABLE ts.sync_check OWNER TO postgres;

--
-- Name: COLUMN sync_check.data_type; Type: COMMENT; Schema: ts; Owner: postgres
--

COMMENT ON COLUMN sync_check.data_type IS 'Time Record / Time Entry';


--
-- Name: COLUMN sync_check.data_side; Type: COMMENT; Schema: ts; Owner: postgres
--

COMMENT ON COLUMN sync_check.data_side IS 'Local / SFMS';


--
-- Name: sync_check_check_id_seq; Type: SEQUENCE; Schema: ts; Owner: postgres
--

CREATE SEQUENCE sync_check_check_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ts.sync_check_check_id_seq OWNER TO postgres;

--
-- Name: sync_check_check_id_seq; Type: SEQUENCE OWNED BY; Schema: ts; Owner: postgres
--

ALTER SEQUENCE sync_check_check_id_seq OWNED BY sync_check.check_id;


--
-- Name: time_entry; Type: TABLE; Schema: ts; Owner: postgres; Tablespace:
--

CREATE TABLE time_entry (
    id numeric NOT NULL,
    time_record_id numeric NOT NULL,
    emp_id numeric NOT NULL,
    day_date date NOT NULL,
    work_hr numeric(4,2),
    travel_hr numeric(4,2),
    holiday_hr numeric(4,2),
    sick_emp_hr numeric(4,2),
    sick_family_hr numeric(4,2),
    misc_hr numeric(4,2),
    misc_type character(1),
    tx_original_user character(50) NOT NULL,
    tx_update_user character(50) NOT NULL,
    tx_original_date timestamp without time zone NOT NULL,
    tx_update_date timestamp without time zone NOT NULL,
    status character(1) NOT NULL,
    pay_type character(2),
    vacation_hr numeric(4,2),
    personal_hr numeric(4,2)
);


ALTER TABLE ts.time_entry OWNER TO postgres;

--
-- Name: TABLE time_entry; Type: COMMENT; Schema: ts; Owner: postgres
--

COMMENT ON TABLE time_entry IS 'Time entry for individual days';


--
-- Name: time_entry_audit; Type: TABLE; Schema: ts; Owner: postgres; Tablespace:
--

CREATE TABLE time_entry_audit (
    time_entry_id bigint NOT NULL,
    time_record_id bigint NOT NULL,
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
    personal_hr numeric(4,2),
    audit_date timestamp without time zone,
    audit_name character(50),
    audit_id numeric
);


ALTER TABLE ts.time_entry_audit OWNER TO postgres;

--
-- Name: time_record; Type: TABLE; Schema: ts; Owner: postgres; Tablespace:
--

CREATE TABLE time_record (
    id numeric NOT NULL,
    emp_id numeric NOT NULL,
    tx_original_user character(50),
    tx_update_user character(50),
    tx_original_date timestamp without time zone,
    tx_update_date timestamp without time zone,
    status character(1) NOT NULL,
    ts_status_id character varying(2) NOT NULL,
    begin_date date NOT NULL,
    end_date date NOT NULL,
    remarks text,
    supervisor_id numeric,
    exc_details text,
    proc_date date
);


ALTER TABLE ts.time_record OWNER TO postgres;

--
-- Name: TABLE time_record; Type: COMMENT; Schema: ts; Owner: postgres
--

COMMENT ON TABLE time_record IS 'Bi-weekly attendance record';


--
-- Name: time_record_audit; Type: TABLE; Schema: ts; Owner: postgres; Tablespace:
--

CREATE TABLE time_record_audit (
    time_record_id numeric NOT NULL,
    emp_id numeric NOT NULL,
    tx_original_user character(50) NOT NULL,
    tx_update_user character(50) NOT NULL,
    tx_original_date timestamp without time zone NOT NULL,
    tx_update_date timestamp without time zone NOT NULL,
    status character(1) NOT NULL,
    ts_status_id character(2) NOT NULL,
    begin_date date NOT NULL,
    end_date date NOT NULL,
    remarks text,
    supervisor_id numeric,
    exc_details text,
    proc_date date,
    audit_date timestamp without time zone NOT NULL,
    audit_name character(50) NOT NULL,
    audit_id numeric
);


ALTER TABLE ts.time_record_audit OWNER TO postgres;

--
-- Name: timesheet_audit_auditor_name_seq; Type: SEQUENCE; Schema: ts; Owner: postgres
--

CREATE SEQUENCE timesheet_audit_auditor_name_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ts.timesheet_audit_auditor_name_seq OWNER TO postgres;

--
-- Name: timesheet_audit_auditor_name_seq; Type: SEQUENCE OWNED BY; Schema: ts; Owner: postgres
--

ALTER SEQUENCE timesheet_audit_auditor_name_seq OWNED BY time_record_audit.audit_name;


--
-- Name: check_id; Type: DEFAULT; Schema: ts; Owner: postgres
--

ALTER TABLE ONLY sync_check ALTER COLUMN check_id SET DEFAULT nextval('sync_check_check_id_seq'::regclass);


--
-- Name: ts; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA ts FROM PUBLIC;
REVOKE ALL ON SCHEMA ts FROM postgres;
GRANT ALL ON SCHEMA ts TO postgres;


--
-- PostgreSQL database dump complete
--
