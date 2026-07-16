ALTER TABLE public.refresh_tokens ALTER COLUMN token TYPE character varying(2000);
ALTER TABLE public.email_verification_tokens ALTER COLUMN token TYPE character varying(2000);
