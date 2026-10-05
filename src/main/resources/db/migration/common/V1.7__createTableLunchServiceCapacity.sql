create table public.lunch_service_capacity
(
    template_id     bigint      not null,
    restaurateur_id bigint      not null,
    table_number    varchar(20) not null,
    constraint fk_lunch_service_capacity_template
        foreign key (template_id)
            references public.template (id)
            on delete cascade,
    constraint fk_lunch_service_capacity_seating_capacity
        foreign key (restaurateur_id, table_number)
            references public.seating_capacity (restaurateur_id, table_number)
            on delete cascade
);

alter table public.lunch_service_capacity owner to "user";

create table public.dinner_service_capacity
(
    template_id     bigint      not null,
    restaurateur_id bigint      not null,
    table_number    varchar(20) not null,
    constraint fk_dinner_service_capacity_template
        foreign key (template_id)
            references public.template (id)
            on delete cascade,
    constraint fk_dinner_service_capacity_seating_capacity
        foreign key (restaurateur_id, table_number)
            references public.seating_capacity (restaurateur_id, table_number)
            on delete cascade
);

alter table public.dinner_service_capacity owner to "user";
