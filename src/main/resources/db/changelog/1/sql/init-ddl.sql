CREATE TABLE public.drone
(
    serial_number    VARCHAR(100) NOT NULL,
    model            VARCHAR(25)  NOT NULL,
    weight_limit     DOUBLE       NOT NULL
        CHECK (weight_limit <= 500 AND weight_limit >= 0),
    battery_capacity SMALLINT     NOT NULL
        CHECK (battery_capacity <= 100 AND battery_capacity >= 0),
    state            VARCHAR(25)  NOT NULL,

    PRIMARY KEY (serial_number)
);

CREATE TABLE public.medication
(
    code   VARCHAR(100) NOT NULL,
    name   TEXT         NOT NULL,
    weight DOUBLE       NOT NULL
        CHECK (weight >= 0),
    image  TEXT,

    PRIMARY KEY (code)
);
