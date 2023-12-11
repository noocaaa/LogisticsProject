--
-- PostgreSQL database dump
--

-- Dumped from database version 15.4 (Ubuntu 15.4-2.pgdg20.04+1)
-- Dumped by pg_dump version 15.4 (Ubuntu 15.4-2.pgdg20.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: authorities; Type: TABLE; Schema: public; Owner: noelia
--

CREATE TABLE public.authorities (
    user_id bigint,
    authority character varying(50),
    id integer NOT NULL
);


ALTER TABLE public.authorities OWNER TO noelia;

--
-- Name: authorities_id_seq; Type: SEQUENCE; Schema: public; Owner: noelia
--

CREATE SEQUENCE public.authorities_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.authorities_id_seq OWNER TO noelia;

--
-- Name: authorities_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: noelia
--

ALTER SEQUENCE public.authorities_id_seq OWNED BY public.authorities.id;


--
-- Name: cargos; Type: TABLE; Schema: public; Owner: noelia
--

CREATE TABLE public.cargos (
    cargo_id integer NOT NULL,
    name character varying(50),
    weight integer,
    status character varying(15),
    CONSTRAINT cargos_status_check CHECK (((status)::text = ANY ((ARRAY['ready'::character varying, 'shipped'::character varying, 'delivered'::character varying])::text[])))
);


ALTER TABLE public.cargos OWNER TO noelia;

--
-- Name: cargos_cargo_id_seq; Type: SEQUENCE; Schema: public; Owner: noelia
--

CREATE SEQUENCE public.cargos_cargo_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.cargos_cargo_id_seq OWNER TO noelia;

--
-- Name: cargos_cargo_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: noelia
--

ALTER SEQUENCE public.cargos_cargo_id_seq OWNED BY public.cargos.cargo_id;


--
-- Name: cities; Type: TABLE; Schema: public; Owner: noelia
--

CREATE TABLE public.cities (
    city_id integer NOT NULL,
    name character varying(50),
    latitude double precision,
    longitude double precision
);


ALTER TABLE public.cities OWNER TO noelia;

--
-- Name: cities_city_id_seq; Type: SEQUENCE; Schema: public; Owner: noelia
--

CREATE SEQUENCE public.cities_city_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.cities_city_id_seq OWNER TO noelia;

--
-- Name: cities_city_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: noelia
--

ALTER SEQUENCE public.cities_city_id_seq OWNED BY public.cities.city_id;


--
-- Name: distances; Type: TABLE; Schema: public; Owner: noelia
--

CREATE TABLE public.distances (
    distance_id integer NOT NULL,
    city1_id integer,
    city2_id integer,
    distance integer,
    CONSTRAINT distances_check CHECK ((city1_id <> city2_id))
);


ALTER TABLE public.distances OWNER TO noelia;

--
-- Name: distances_distance_id_seq; Type: SEQUENCE; Schema: public; Owner: noelia
--

CREATE SEQUENCE public.distances_distance_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.distances_distance_id_seq OWNER TO noelia;

--
-- Name: distances_distance_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: noelia
--

ALTER SEQUENCE public.distances_distance_id_seq OWNED BY public.distances.distance_id;


--
-- Name: drivers; Type: TABLE; Schema: public; Owner: noelia
--

CREATE TABLE public.drivers (
    driver_id integer NOT NULL,
    name character varying(50),
    surname character varying(50),
    personal_number character varying(50),
    working_hours integer,
    status character varying(50),
    current_city character varying(50),
    current_truck_id integer,
    shift_start_time timestamp without time zone,
    shift_end_time timestamp without time zone,
    accumulated_minutes integer DEFAULT 0,
    CONSTRAINT drivers_status_check CHECK ((upper((status)::text) = ANY (ARRAY[('REST'::character varying)::text, ('DRIVING'::character varying)::text, ('SECOND_DRIVER'::character varying)::text, ('LOADING_UNLOADING'::character varying)::text]))),
    CONSTRAINT drivers_working_hours_check CHECK ((working_hours <= 176))
);


ALTER TABLE public.drivers OWNER TO noelia;

--
-- Name: drivers_driver_id_seq; Type: SEQUENCE; Schema: public; Owner: noelia
--

CREATE SEQUENCE public.drivers_driver_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.drivers_driver_id_seq OWNER TO noelia;

--
-- Name: drivers_driver_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: noelia
--

ALTER SEQUENCE public.drivers_driver_id_seq OWNED BY public.drivers.driver_id;


--
-- Name: driversorders; Type: TABLE; Schema: public; Owner: noelia
--

CREATE TABLE public.driversorders (
    driver_id integer NOT NULL,
    order_id integer NOT NULL
);


ALTER TABLE public.driversorders OWNER TO noelia;

--
-- Name: orders; Type: TABLE; Schema: public; Owner: noelia
--

CREATE TABLE public.orders (
    order_id integer NOT NULL,
    completed boolean,
    truck_id integer
);


ALTER TABLE public.orders OWNER TO noelia;

--
-- Name: orders_order_id_seq; Type: SEQUENCE; Schema: public; Owner: noelia
--

CREATE SEQUENCE public.orders_order_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.orders_order_id_seq OWNER TO noelia;

--
-- Name: orders_order_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: noelia
--

ALTER SEQUENCE public.orders_order_id_seq OWNED BY public.orders.order_id;


--
-- Name: trucks; Type: TABLE; Schema: public; Owner: noelia
--

CREATE TABLE public.trucks (
    truck_id integer NOT NULL,
    number character varying(7),
    capacity integer,
    status character varying(3),
    current_city character varying(50),
    CONSTRAINT trucks_status_check CHECK (((status)::text = ANY ((ARRAY['OK'::character varying, 'NOK'::character varying])::text[])))
);


ALTER TABLE public.trucks OWNER TO noelia;

--
-- Name: trucks_truck_id_seq; Type: SEQUENCE; Schema: public; Owner: noelia
--

CREATE SEQUENCE public.trucks_truck_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.trucks_truck_id_seq OWNER TO noelia;

--
-- Name: trucks_truck_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: noelia
--

ALTER SEQUENCE public.trucks_truck_id_seq OWNED BY public.trucks.truck_id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: noelia
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(100) NOT NULL,
    enabled boolean DEFAULT true NOT NULL
);


ALTER TABLE public.users OWNER TO noelia;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: noelia
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO noelia;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: noelia
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: waypoints; Type: TABLE; Schema: public; Owner: noelia
--

CREATE TABLE public.waypoints (
    waypoint_id integer NOT NULL,
    order_id integer,
    city_id integer,
    cargo_id integer,
    type character varying(15),
    CONSTRAINT waypoints_type_check CHECK (((type)::text = ANY ((ARRAY['loading'::character varying, 'unloading'::character varying])::text[])))
);


ALTER TABLE public.waypoints OWNER TO noelia;

--
-- Name: waypoints_waypoint_id_seq; Type: SEQUENCE; Schema: public; Owner: noelia
--

CREATE SEQUENCE public.waypoints_waypoint_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.waypoints_waypoint_id_seq OWNER TO noelia;

--
-- Name: waypoints_waypoint_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: noelia
--

ALTER SEQUENCE public.waypoints_waypoint_id_seq OWNED BY public.waypoints.waypoint_id;


--
-- Name: authorities id; Type: DEFAULT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.authorities ALTER COLUMN id SET DEFAULT nextval('public.authorities_id_seq'::regclass);


--
-- Name: cargos cargo_id; Type: DEFAULT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.cargos ALTER COLUMN cargo_id SET DEFAULT nextval('public.cargos_cargo_id_seq'::regclass);


--
-- Name: cities city_id; Type: DEFAULT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.cities ALTER COLUMN city_id SET DEFAULT nextval('public.cities_city_id_seq'::regclass);


--
-- Name: distances distance_id; Type: DEFAULT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.distances ALTER COLUMN distance_id SET DEFAULT nextval('public.distances_distance_id_seq'::regclass);


--
-- Name: drivers driver_id; Type: DEFAULT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.drivers ALTER COLUMN driver_id SET DEFAULT nextval('public.drivers_driver_id_seq'::regclass);


--
-- Name: orders order_id; Type: DEFAULT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.orders ALTER COLUMN order_id SET DEFAULT nextval('public.orders_order_id_seq'::regclass);


--
-- Name: trucks truck_id; Type: DEFAULT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.trucks ALTER COLUMN truck_id SET DEFAULT nextval('public.trucks_truck_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: waypoints waypoint_id; Type: DEFAULT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.waypoints ALTER COLUMN waypoint_id SET DEFAULT nextval('public.waypoints_waypoint_id_seq'::regclass);


--
-- Data for Name: authorities; Type: TABLE DATA; Schema: public; Owner: noelia
--

COPY public.authorities (user_id, authority, id) FROM stdin;
4	ROLE_DRIVER	4
5	ROLE_DRIVER	5
6	ROLE_DRIVER	6
7	ROLE_DRIVER	7
8	ROLE_DRIVER	8
9	ROLE_DRIVER	9
10	ROLE_DRIVER	10
11	ROLE_DRIVER	11
1	ROLE_EMPLOYEE	12
13	ROLE_DRIVER	13
\.


--
-- Data for Name: cargos; Type: TABLE DATA; Schema: public; Owner: noelia
--

COPY public.cargos (cargo_id, name, weight, status) FROM stdin;
2	Clothes	300	shipped
3	Food	400	delivered
1	Electronics	500	delivered
4	Shoes	1500	shipped
5	Flowers	700	shipped
6	Phones	1250	shipped
7	Chocolate	600	ready
\.


--
-- Data for Name: cities; Type: TABLE DATA; Schema: public; Owner: noelia
--

COPY public.cities (city_id, name, latitude, longitude) FROM stdin;
1	Granada	37.1773363	-3.5985571
2	Madrid	40.416775	-3.70379
3	Barcelona	41.385064	2.173404
4	Sevilla	37.389092	-5.984459
5	Valencia	39.469907	-0.376288
6	Málaga	36.7212737	-4.4213988
8	Murcia	37.9922399	-1.1306544
9	Bilbao	43.2630126	-2.9349852
10	Córdoba	37.8881751	-4.7793835
13	Vigo	42.240599	-8.720727
\.


--
-- Data for Name: distances; Type: TABLE DATA; Schema: public; Owner: noelia
--

COPY public.distances (distance_id, city1_id, city2_id, distance) FROM stdin;
12	1	2	417
13	1	3	831
14	1	4	251
15	1	5	484
16	2	3	620
17	2	4	530
18	2	5	357
19	3	4	998
20	3	5	350
21	4	5	659
22	9	1	810
23	9	2	395
24	9	3	609
25	9	4	859
26	9	5	611
27	9	13	666
28	9	8	782
29	13	1	1005
30	13	2	590
31	13	3	1146
32	13	4	771
33	13	5	947
34	13	8	986
35	8	1	278
36	8	2	396
37	8	3	566
38	8	4	527
39	8	5	220
40	10	1	159
41	10	2	393
42	10	3	860
43	10	4	143
44	10	5	521
45	10	13	830
46	10	8	433
47	10	9	787
48	10	6	157
49	6	1	124
50	6	2	529
51	6	3	953
52	6	4	210
53	6	5	607
54	6	13	979
55	6	8	400
56	6	9	923
\.


--
-- Data for Name: drivers; Type: TABLE DATA; Schema: public; Owner: noelia
--

COPY public.drivers (driver_id, name, surname, personal_number, working_hours, status, current_city, current_truck_id, shift_start_time, shift_end_time, accumulated_minutes) FROM stdin;
5	Juan	Pérez	123456789A	176	REST	Granada	\N	\N	\N	0
6	Maria	González	987654321B	35	DRIVING	Barcelona	2	\N	\N	0
7	Pedro	Sánchez	567890123C	176	REST	Madrid	\N	\N	\N	0
13	Ana	García	456	50	driving	Madrid	2	\N	\N	0
14	Carlos	López	789	30	rest	Barcelona	1	\N	\N	0
12	Juan	Peinado	123	0	SECOND_DRIVER	Granada	1	\N	2023-12-03 15:30:53.307945	8
10	Noelia	Carrasco	12345156	170	REST	Granada	13	\N	\N	0
8	Luisa	Martínez	345678901D	70	REST	Valencia	3	\N	\N	0
15	Jorge	Fernandez	12345678	34	REST	Granada	\N	\N	\N	\N
\.


--
-- Data for Name: driversorders; Type: TABLE DATA; Schema: public; Owner: noelia
--

COPY public.driversorders (driver_id, order_id) FROM stdin;
10	24
8	8
\.


--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: noelia
--

COPY public.orders (order_id, completed, truck_id) FROM stdin;
7	t	2
8	f	3
9	t	4
10	f	5
6	t	1
24	f	13
25	f	\N
26	f	\N
28	f	16
27	f	17
\.


--
-- Data for Name: trucks; Type: TABLE DATA; Schema: public; Owner: noelia
--

COPY public.trucks (truck_id, number, capacity, status, current_city) FROM stdin;
2	2345DEF	15	OK	Barcelona
3	3456GHI	20	NOK	Valencia
4	4567JKL	25	OK	Sevilla
5	5678MNO	30	NOK	Zaragoza
1	1234ABC	10	OK	Madrid
13	1234FDS	25	OK	Granada
15	4574	32	NOK	Barcelona
16	3465AAA	100	OK	Granada
17	2211FFF	12	OK	Granada
18	1489AVD	12	OK	Valencia
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: noelia
--

COPY public.users (id, username, password, enabled) FROM stdin;
1	jhondoe@gmail.com	$2a$10$GMsE3efcMYtqw6xbNrSMO.nx91FenkCu9o6zOXURypyS9tURfigH6	t
4	123456789A	$2a$10$54/jDdCr8UHd2d/vrdpUa.MsRGsE77sEl9pA48csKHkVQOOS6HeX6	t
5	987654321B	$2a$10$mF.99scJTN3zXDGFwx415eOj3fRHynLH/ilUzGXkDog9f8ME0e8/y	t
6	567890123C	$2a$10$NU.vx5SscBNLZMVBPVnd1uaKQzMF.bAVxIW6bi3rMKqRbrIhdkEk.	t
7	345678901D	$2a$10$CTLoztNsvmVzp8lJZReIAecpgIyLW9MkxNA7z0gu9nMxponieZeni	t
8	12345156	$2a$10$VSc0iQgpxdeN2ERd/wr2r.Eikxzl980hF.e39nwUuYv4c04OHalCe	t
9	123	$2a$10$/CPh5hdOu/UqkJLfgTzKTO6Zqh7cg9rlDpERZrQlPieygPtWyJxdq	t
10	456	$2a$10$MYocfXVurGBNHJN1P1A5fuXaBRbyElvBWKk0FgDXqB/VJ/86JtnFy	t
11	789	$2a$10$dyaexCuCxdK5jfqyBzj3m.UtGVvF4sNvF0S/.AjLB3upE9TIYObRO	t
12	1234523	$2a$10$PlV/TDIiCTJUluvGnAD/8Oh6ZCpbytoyxm9BsLb0fxJ4nJJtTojuu	t
13	9999	$2a$10$kufStL3ABRVeAWMOWMgeH.c57H7QaJJA9RSlRyvriH1GeWCRqzP.S	t
\.


--
-- Data for Name: waypoints; Type: TABLE DATA; Schema: public; Owner: noelia
--

COPY public.waypoints (waypoint_id, order_id, city_id, cargo_id, type) FROM stdin;
9	6	1	1	loading
10	6	1	1	unloading
11	7	2	2	loading
12	7	2	2	unloading
17	24	1	4	loading
18	24	2	4	unloading
19	25	2	4	unloading
20	25	1	4	loading
21	26	13	4	unloading
22	26	1	4	loading
24	27	13	5	unloading
25	27	1	5	loading
27	28	9	7	loading
28	28	8	7	unloading
\.


--
-- Name: authorities_id_seq; Type: SEQUENCE SET; Schema: public; Owner: noelia
--

SELECT pg_catalog.setval('public.authorities_id_seq', 13, true);


--
-- Name: cargos_cargo_id_seq; Type: SEQUENCE SET; Schema: public; Owner: noelia
--

SELECT pg_catalog.setval('public.cargos_cargo_id_seq', 7, true);


--
-- Name: cities_city_id_seq; Type: SEQUENCE SET; Schema: public; Owner: noelia
--

SELECT pg_catalog.setval('public.cities_city_id_seq', 24, true);


--
-- Name: distances_distance_id_seq; Type: SEQUENCE SET; Schema: public; Owner: noelia
--

SELECT pg_catalog.setval('public.distances_distance_id_seq', 56, true);


--
-- Name: drivers_driver_id_seq; Type: SEQUENCE SET; Schema: public; Owner: noelia
--

SELECT pg_catalog.setval('public.drivers_driver_id_seq', 18, true);


--
-- Name: orders_order_id_seq; Type: SEQUENCE SET; Schema: public; Owner: noelia
--

SELECT pg_catalog.setval('public.orders_order_id_seq', 28, true);


--
-- Name: trucks_truck_id_seq; Type: SEQUENCE SET; Schema: public; Owner: noelia
--

SELECT pg_catalog.setval('public.trucks_truck_id_seq', 18, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: noelia
--

SELECT pg_catalog.setval('public.users_id_seq', 13, true);


--
-- Name: waypoints_waypoint_id_seq; Type: SEQUENCE SET; Schema: public; Owner: noelia
--

SELECT pg_catalog.setval('public.waypoints_waypoint_id_seq', 28, true);


--
-- Name: authorities authorities_pkey; Type: CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.authorities
    ADD CONSTRAINT authorities_pkey PRIMARY KEY (id);


--
-- Name: cargos cargos_pkey; Type: CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.cargos
    ADD CONSTRAINT cargos_pkey PRIMARY KEY (cargo_id);


--
-- Name: cities cities_name_key; Type: CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.cities
    ADD CONSTRAINT cities_name_key UNIQUE (name);


--
-- Name: cities cities_pkey; Type: CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.cities
    ADD CONSTRAINT cities_pkey PRIMARY KEY (city_id);


--
-- Name: distances distances_pkey; Type: CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.distances
    ADD CONSTRAINT distances_pkey PRIMARY KEY (distance_id);


--
-- Name: drivers drivers_personal_number_key; Type: CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.drivers
    ADD CONSTRAINT drivers_personal_number_key UNIQUE (personal_number);


--
-- Name: drivers drivers_pkey; Type: CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.drivers
    ADD CONSTRAINT drivers_pkey PRIMARY KEY (driver_id);


--
-- Name: driversorders driversorders_pkey; Type: CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.driversorders
    ADD CONSTRAINT driversorders_pkey PRIMARY KEY (driver_id, order_id);


--
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (order_id);


--
-- Name: trucks trucks_number_key; Type: CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.trucks
    ADD CONSTRAINT trucks_number_key UNIQUE (number);


--
-- Name: trucks trucks_pkey; Type: CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.trucks
    ADD CONSTRAINT trucks_pkey PRIMARY KEY (truck_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: waypoints waypoints_pkey; Type: CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.waypoints
    ADD CONSTRAINT waypoints_pkey PRIMARY KEY (waypoint_id);


--
-- Name: authorities authorities_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.authorities
    ADD CONSTRAINT authorities_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: distances distances_city1_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.distances
    ADD CONSTRAINT distances_city1_id_fkey FOREIGN KEY (city1_id) REFERENCES public.cities(city_id);


--
-- Name: distances distances_city2_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.distances
    ADD CONSTRAINT distances_city2_id_fkey FOREIGN KEY (city2_id) REFERENCES public.cities(city_id);


--
-- Name: drivers drivers_current_truck_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.drivers
    ADD CONSTRAINT drivers_current_truck_id_fkey FOREIGN KEY (current_truck_id) REFERENCES public.trucks(truck_id);


--
-- Name: driversorders driversorders_driver_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.driversorders
    ADD CONSTRAINT driversorders_driver_id_fkey FOREIGN KEY (driver_id) REFERENCES public.drivers(driver_id);


--
-- Name: driversorders driversorders_order_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.driversorders
    ADD CONSTRAINT driversorders_order_id_fkey FOREIGN KEY (order_id) REFERENCES public.orders(order_id);


--
-- Name: orders orders_truck_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_truck_id_fkey FOREIGN KEY (truck_id) REFERENCES public.trucks(truck_id);


--
-- Name: waypoints waypoints_cargo_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.waypoints
    ADD CONSTRAINT waypoints_cargo_id_fkey FOREIGN KEY (cargo_id) REFERENCES public.cargos(cargo_id);


--
-- Name: waypoints waypoints_city_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.waypoints
    ADD CONSTRAINT waypoints_city_id_fkey FOREIGN KEY (city_id) REFERENCES public.cities(city_id);


--
-- Name: waypoints waypoints_order_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: noelia
--

ALTER TABLE ONLY public.waypoints
    ADD CONSTRAINT waypoints_order_id_fkey FOREIGN KEY (order_id) REFERENCES public.orders(order_id);


--
-- PostgreSQL database dump complete
--

