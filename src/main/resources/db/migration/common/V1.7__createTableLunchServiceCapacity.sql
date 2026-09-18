create table public.service_capacity
(
    template_id     bigint      not null,
    restaurateur_id bigint      not null,
    table_number    varchar(10) not null,
    constraint pk_service_capacity
        primary key (template_id, restaurateur_id, table_number),
    constraint fk_service_capacity_template
        foreign key (template_id)
            references public.template (id)
            on delete cascade,
    constraint fk_service_capacity_seating_capacity
        foreign key (restaurateur_id, table_number)
            references public.seating_capacity (restaurateur_id, table_number)
            on delete cascade
);

alter table public.service_capacity owner to "user";

create table public.lunch_service_capacity
(
    template_id     bigint      not null,
    restaurateur_id bigint      not null,
    table_number    varchar(10) not null,
    constraint pk_lunch_service_capacity
        primary key (template_id, restaurateur_id, table_number),
    constraint fk_lunch_service_capacity_service_capacity
        foreign key (template_id, restaurateur_id, table_number)
            references public.service_capacity (template_id, restaurateur_id, table_number)
            on delete cascade
);

alter table public.lunch_service_capacity owner to "user";

create table public.dinner_service_capacity
(
    template_id     bigint      not null,
    restaurateur_id bigint      not null,
    table_number    varchar(10) not null,
    constraint pk_dinner_service_capacity
        primary key (template_id, restaurateur_id, table_number),
    constraint fk_dinner_service_capacity_service_capacity
        foreign key (template_id, restaurateur_id, table_number)
            references public.service_capacity (template_id, restaurateur_id, table_number)
            on delete cascade
);

alter table public.dinner_service_capacity owner to "user";