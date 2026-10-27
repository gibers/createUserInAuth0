create table public.seating_capacity
(
    restaurateur_id  bigint not null,
    table_number     varchar(20) not null,
    capacity         integer not null check (capacity between 1 and 22),
    created_at       timestamp(6) with time zone not null default now(),
    updated_at       timestamp(6) with time zone not null default now(),
	color_code		 varchar(7),
    constraint pk_seating_capacity
        primary key (restaurateur_id, table_number),
    constraint fk_seating_capacity_restaurateur
        foreign key (restaurateur_id)
            references public.restaurateur (id)
            on delete cascade,
    constraint uq_seating_capacity_restaurateur_color_code
        unique (restaurateur_id, color_code)
);

alter table public.seating_capacity owner to "user";
