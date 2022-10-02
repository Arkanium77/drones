CREATE TABLE public.load
(
    id         VARCHAR(100) NOT NULL,
    drone      VARCHAR(100) NOT NULL,
    medication VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (drone)
        REFERENCES public.drone (serial_number),
    FOREIGN KEY (medication)
        REFERENCES public.medication (code)
);