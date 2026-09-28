delete from public.restaurateur;

insert into public.restaurateur (
    name,
    adresse,
    telephone,
    email,
    created_at,
    updated_at,
    active,
    monthly_price,
    currency,
    users_id
)
values (
    'Restaurant Fictif',
    '10 rue de la République, 75001 Paris',
    '+33 1 23 45 67 89',
    'contact@restaurant-fictif.example',
    now(),
    now(),
    true,
    9.99,
    'EUR',
    16
);
