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
-- Name: TempDataStore; Type: TABLE; Schema: ts; Owner: admin; Tablespace:
--

CREATE TABLE "TempDataStore" (
    "DataType" character(1) NOT NULL,
    "DataId" bigint NOT NULL,
    "Date" timestamp without time zone NOT NULL,
    "Id" integer NOT NULL
);


ALTER TABLE ts."TempDataStore" OWNER TO admin;

--
-- Name: TempDataStore_Id_seq; Type: SEQUENCE; Schema: ts; Owner: admin
--

CREATE SEQUENCE "TempDataStore_Id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ts."TempDataStore_Id_seq" OWNER TO admin;

--
-- Name: TempDataStore_Id_seq; Type: SEQUENCE OWNED BY; Schema: ts; Owner: admin
--

ALTER SEQUENCE "TempDataStore_Id_seq" OWNED BY "TempDataStore"."Id";


--
-- Name: Timesheet; Type: TABLE; Schema: ts; Owner: admin; Tablespace:
--

CREATE TABLE "Timesheet" (
    "TimesheetId" bigint NOT NULL,
    "EmpId" bigint NOT NULL,
    "TOriginalUserId" character(50) NOT NULL,
    "TUpdateUserId" character(50) NOT NULL,
    "TOriginalDate" timestamp without time zone NOT NULL,
    "TUpdateDate" timestamp without time zone NOT NULL,
    "Status" character(1) NOT NULL,
    "TSStatusId" character(2) NOT NULL,
    "BeginDate" date NOT NULL,
    "EndDate" date NOT NULL,
    "PayType" character(2) NOT NULL,
    "Remarks" text,
    "SupervisorId" bigint NOT NULL,
    "ExcDetails" text,
    "ProcDate" date NOT NULL
);


ALTER TABLE ts."Timesheet" OWNER TO admin;

--
-- Name: TimesheetByDay; Type: TABLE; Schema: ts; Owner: admin; Tablespace:
--

CREATE TABLE "TimesheetByDay" (
    "TSDayId" bigint NOT NULL,
    "TimesheetId" bigint NOT NULL,
    "EmpId" bigint NOT NULL,
    "DayDate" date NOT NULL,
    "WorkHR" numeric(4,2),
    "TravelHR" numeric(4,2),
    "HolidayHR" numeric(4,2),
    "SickEmpHR" numeric(4,2),
    "SickFamilyHR" numeric(4,2),
    "MiscHR" numeric(4,2),
    "MiscTypeId" character(1) NOT NULL,
    "TOrginalUserId" character(50) NOT NULL,
    "TUpdateUserId" character(50) NOT NULL,
    "TOriginalDate" timestamp without time zone NOT NULL,
    "TUpdateDate" timestamp without time zone NOT NULL,
    "Status" character(1) NOT NULL,
    "EmpComment" text,
    "PayType" character(2),
    "VacationHR" numeric(4,2),
    "PersonalHR" numeric(4,2)
);


ALTER TABLE ts."TimesheetByDay" OWNER TO admin;

--
-- Name: Id; Type: DEFAULT; Schema: ts; Owner: admin
--

ALTER TABLE ONLY "TempDataStore" ALTER COLUMN "Id" SET DEFAULT nextval('"TempDataStore_Id_seq"'::regclass);


--
-- Name: TimesheetByDay_pkey; Type: CONSTRAINT; Schema: ts; Owner: admin; Tablespace:
--

ALTER TABLE ONLY "TimesheetByDay"
    ADD CONSTRAINT "TimesheetByDay_pkey" PRIMARY KEY ("TSDayId");

ALTER TABLE "TimesheetByDay" CLUSTER ON "TimesheetByDay_pkey";


--
-- Name: Timesheet_pkey; Type: CONSTRAINT; Schema: ts; Owner: admin; Tablespace:
--

ALTER TABLE ONLY "Timesheet"
    ADD CONSTRAINT "Timesheet_pkey" PRIMARY KEY ("TimesheetId");

ALTER TABLE "Timesheet" CLUSTER ON "Timesheet_pkey";


--
-- Name: FKEY_TIMESHEET; Type: FK CONSTRAINT; Schema: ts; Owner: admin
--

ALTER TABLE ONLY "TimesheetByDay"
    ADD CONSTRAINT "FKEY_TIMESHEET" FOREIGN KEY ("TimesheetId") REFERENCES "Timesheet"("TimesheetId") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE;


--
-- PostgreSQL database dump complete
--
