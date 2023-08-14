dev/build:
	docker compose -f docker-compose.dev.yml build

dev/build/web:
	docker compose -f docker-compose.dev.yml build web

dev/up:
	docker compose -f docker-compose.dev.yml up -d

dev/down:
	docker compose -f docker-compose.dev.yml down --remove-orphans

dev/ps:
	docker compose -f docker-compose.dev.yml ps

dev/logs/web:
	docker compose -f docker-compose.dev.yml logs -f web

dev/db/migrate:
	docker compose -f docker-compose.dev.yml run --rm db_migrate

dev/setup: dev/build dev/up dev/db/migrate

setup:
	docker compose --profile app -f docker-compose.dev.yml build
	docker compose --profile app -f docker-compose.dev.yml up -d
	docker compose -f docker-compose.dev.yml run --rm db_migrate

