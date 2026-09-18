create table public.seating_capacity
(
    restaurateur_id  bigint not null,
    table_number     varchar(10) not null,
    capacity         integer not null check (capacity between 0 and 22),
    created_at       timestamp(6) with time zone not null default now(),
    updated_at       timestamp(6) with time zone not null default now(),
    constraint pk_seating_capacity
        primary key (restaurateur_id, table_number),
    constraint fk_seating_capacity_restaurateur
        foreign key (restaurateur_id)
            references public.restaurateur (id)
            on delete cascade
);

alter table public.seating_capacity owner to "user";
