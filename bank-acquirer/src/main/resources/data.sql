--INSERT INTO public.card(id, account_number, available_funds, cvv, exp_date, pan) VALUES (1, '11111110001', 200000, '1234', '9/2020', '111111222222');
--INSERT INTO public.card(id, account_number, available_funds, cvv, exp_date, pan) VALUES (2, '11111110002', 300000, '4321', '9/2020', '111111222322');

--INSERT INTO public.card_owner(id, email, last_name, merchantid, merchant_pass, name, card_id) VALUES (1, 'maja@gmail.com', 'matkovski', 'marchantId1','password', 'maja', 1);
--INSERT INTO public.card_owner(id, email, last_name, merchantid, merchant_pass, name, card_id) VALUES (2, 'vesna@gmail.com', 'firma', 'marchantId2', 'password', 'firma', 2);


INSERT INTO public.card(id, account_number, available_funds, cvv, exp_date, pan) VALUES (1, '22222220001', 400000, '1222', '9/2020', '222222333333');

INSERT INTO public.card_owner(id, email, last_name, merchantid, merchant_pass, name, card_id) VALUES (1, 'bankar@gmail.com', 'matkovski', 'marchantId2','password', 'banka2', 1);